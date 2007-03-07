/*
 * class: EventBuilderGlobalTrigPayloadInputEngine
 *
 * Version $Id: EventBuilderGlobalTrigPayloadInputEngine.java,v 1.61.2.6 2006/11/02 17:50:01 dglo Exp $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.eventBuilder.backend.EventBuilderBackEnd;

import icecube.daq.eventBuilder.monitoring.GlobalTriggerInputMonitor;

import icecube.daq.io.PushPayloadInputEngine;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.ILoadablePayload;
import icecube.daq.payload.MasterPayloadFactory;

import icecube.daq.payload.splicer.Payload;

import icecube.daq.trigger.ITriggerRequestPayload;

import java.io.IOException;

import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is an an implementation of the PayloadInputEngine
 * for the Main Data Collection Partition.
 *
 * @author mcp
 * @version $Id: EventBuilderGlobalTrigPayloadInputEngine.java,v 1.61.2.6 2006/11/02 17:50:01 dglo Exp $
 */
public class EventBuilderGlobalTrigPayloadInputEngine
    extends PushPayloadInputEngine
    implements GlobalTriggerInputMonitor
{
    private static final Log LOG =
        LogFactory.getLog(EventBuilderGlobalTrigPayloadInputEngine.class);

    /** back-end processor which digests trigger requests. */
    private EventBuilderBackEnd backEnd;

    /** main buffer cache. */
    private IByteBufferCache bufMgr;

    /**
     * The Engine that does the actual demultiplexing
     * of the trigger requests to the SPs.
     */
    private EventBuilderTriggerRequestDemultiplexer demuxer;

    /** standalone trigger/readout request factory. */
    private MasterPayloadFactory trFactory;

    /**
     * Create an instance of this class.
     */
    public EventBuilderGlobalTrigPayloadInputEngine(String name, int id,
                                                    String fcn,
                                                    EventBuilderBackEnd backEnd,
                                                    IByteBufferCache bufMgr,
                                                    MasterPayloadFactory factory)
    {
        // parent constructor wants same args
        super(name, id, fcn, "GlblTrig", bufMgr);

        if (backEnd == null) {
            throw new IllegalArgumentException("Back-end processor cannot" +
                                               " be null");
        }
        this.backEnd = backEnd;
        this.bufMgr = bufMgr;

        // create a payload factory which is not attached to the
        // ByteBufferCache
        // XXX this should have some sort of metering so we don't fill memory
        trFactory = new MasterPayloadFactory();

        demuxer = new EventBuilderTriggerRequestDemultiplexer(trFactory);
    }

    public long getReceivedMessages()
    {
        return getDequeuedMessages();
    }

    public long getTotalGlobalTrigStopsReceived()
    {
        return getTotalStopsReceived();
    }

    public boolean isHealthy()
    {
        return true;
    }

    public void pushBuffer(ByteBuffer buf)
        throws IOException
    {
        // make a standalone copy of the original ByteBuffer
        ByteBuffer newBuf = ByteBuffer.allocate(buf.limit());
        buf.position(0);
        newBuf.put(buf);
        bufMgr.returnBuffer(buf);
        newBuf.position(newBuf.capacity());

        ITriggerRequestPayload pay;
        try {
            pay = (ITriggerRequestPayload) trFactory.createPayload(0, newBuf);
        } catch (Exception ex) {
            LOG.error("Cannot create trigger request", ex);
            throw new IOException("Cannot create trigger request");
        }

        if (pay == null) {
            return;
        }

        try {
            ((ILoadablePayload) pay).loadPayload();
        } catch (Exception ex) {
            LOG.error("Cannot load trigger request", ex);
            throw new IOException("Cannot load trigger request");
        }

        // send readout requests
        demuxer.demux(pay);

        // add trigger request to back-end queue
        backEnd.addRequest(pay);
    }

    public void registerStringProcCacheOutputEngine(EventBuilderSPcachePayloadOutputEngine oe)
    {
        if (oe != null) {
            oe.registerBufferManager(bufMgr);
        }
    }

    public void registerStringProcReqOutputEngine(RequestPayloadOutputEngine oe)
    {
        oe.registerBufferManager(bufMgr);

        // Register the output engine
        // with the global trigger to string proc demultiplexer
        demuxer.registerOutputEngine(oe);
    }

    public void sendStop()
    {
        backEnd.addRequestStop();

        demuxer.sendStopMessage();
    }

    public void startProcessing()
    {
        super.startProcessing();
        backEnd.reset();
    }
}
