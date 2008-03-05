package icecube.daq.eventBuilder;

import icecube.daq.eventBuilder.backend.EventBuilderBackEnd;
import icecube.daq.eventBuilder.monitoring.GlobalTriggerInputMonitor;
import icecube.daq.io.PushPayloadReader;
import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.ILoadablePayload;
import icecube.daq.payload.MasterPayloadFactory;
import icecube.daq.trigger.ITriggerRequestPayload;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlobalTriggerReader
    extends PushPayloadReader
    implements GlobalTriggerInputMonitor
{
    private static final Log LOG =
        LogFactory.getLog(GlobalTriggerReader.class);

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
    public GlobalTriggerReader(String name, EventBuilderBackEnd backEnd,
                               MasterPayloadFactory trigReqFactory,
                               IByteBufferCache bufMgr)
        throws IOException
    {
        super(name);

        if (backEnd == null) {
            throw new IllegalArgumentException("Back-end processor cannot" +
                                               " be null");
        }
        this.backEnd = backEnd;

        if (trigReqFactory == null) {
            throw new IllegalArgumentException("Trigger request factory" +
                                               " cannot be null");
        }
        this.trFactory = trigReqFactory;
        this.bufMgr = bufMgr;

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

    public void pushBuffer(ByteBuffer buf)
        throws IOException
    {
        // make a standalone copy of the original ByteBuffer
/*
        ByteBuffer newBuf = ByteBuffer.allocate(buf.limit());
        buf.position(0);
        newBuf.put(buf);
        bufMgr.returnBuffer(buf);
        newBuf.position(newBuf.capacity());
*/
ByteBuffer newBuf = buf;
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
        backEnd.addRequest((ILoadablePayload) pay);
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
        backEnd.resetAtStart();
    }
}
