package icecube.daq.eventBuilder;

import icecube.daq.payload.IPayloadDestinationCollection;
import icecube.daq.payload.IReadoutRequest;
import icecube.daq.payload.IReadoutRequestElement;
import icecube.daq.payload.ITriggerRequestPayload;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.IWriteablePayload;
import icecube.daq.payload.SourceIdRegistry;
import icecube.daq.payload.impl.ReadoutRequestFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: nehar
 * Date: Aug 17, 2005
 * Time: 12:34:37 PM
 *
 * This class handles routing the readout requests to
 * the appropriate String processors (and IDH's at
 * some point) in response to a received Trigger
 * request payload from the Global Trigger.
 */
public class EventBuilderTriggerRequestDemultiplexer
{
    private static final Log LOG =
        LogFactory.getLog(EventBuilderTriggerRequestDemultiplexer.class);

    /**
     *  The Generator object used to get a vector of readout requests
     *  to send to sources that make up this event.
     */
    private EventBuilderReadoutRequestGenerator readoutGenerator;

    /** <tt>true</tt> if generator has been initialized */
    private boolean generatorInitialized;

    /** The output engine used to send Readout Requests to StringHubs. */
    private RequestPayloadOutputEngine payloadDest;

    /**
     * Constructor
     *
     * @param factory master payload factory
     */
    public EventBuilderTriggerRequestDemultiplexer
        (ReadoutRequestFactory factory)
    {
        readoutGenerator = new EventBuilderReadoutRequestGenerator(factory);
    }

    /**
     * Does the actual routing of readout requests to string procs
     * according to the ITriggerRequestPayload sent by the global trigger.
     *
     * @param inputTriggerRequest the request from the global trigger.
     *
     * @return returns <tt>false</tt> if payload is discarded,
     *         <tt>true</tt> otherwise.
     */
    public boolean demux(ITriggerRequestPayload inputTriggerRequest)
    {
        if (payloadDest == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("StringProcRequestOutputEngine is null.");
            }

            return false;
        }

        IPayloadDestinationCollection dests =
            payloadDest.getPayloadDestinationCollection();

        if (!generatorInitialized) {
            readoutGenerator.setDestinations(dests.getAllSourceIDs());
            generatorInitialized = true;
        }

        final int inSrcId = inputTriggerRequest.getSourceID().getSourceID();
        if (inSrcId != SourceIdRegistry.GLOBAL_TRIGGER_SOURCE_ID) {
            // ...ditto
            if (LOG.isErrorEnabled()) {
                LOG.error("Source#" + inSrcId + " is not GlobaTrigger.");
            }

            return false;
        }

        // looks like a valid trigger request payload judging by the type.
        int eventId = inputTriggerRequest.getUID();

        // We need to get the Payload time stamp to put in the readout
        // requests.
        IUTCTime utcTime = inputTriggerRequest.getFirstTimeUTC();

        final IReadoutRequest tmpReq = inputTriggerRequest.getReadoutRequest();
        List readoutElements = tmpReq.getReadoutRequestElements();

        // Get readout Request payloads to send from the Generator object.
        Collection readouts =
            readoutGenerator.generator(readoutElements, eventId, utcTime);
        if (readouts == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Generator gave back a null list");
            }

            return false;
        } else if (readouts.size() == 0) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Generator gave back an empty list");
            }

            return false;
        }

        // Let's dump these readouts to logs and to the output destination.
        Iterator iter = readouts.iterator();
        while (iter.hasNext()) {

            IReadoutRequest tmpRRQ = (IReadoutRequest) iter.next();

            // this is where we send out the payload using the
            // payloadDest payload output engine.
            //  We try to demux the readouts to different files.

            List elemVec = tmpRRQ.getReadoutRequestElements();

            if (LOG.isErrorEnabled() && elemVec.size() != 1) {
                LOG.error("Expected one element in readout request #" +
                          tmpRRQ.getUID() + ", not " +  elemVec.size());
            }

            //this works coz there's only one element in each readoutRequest
            IReadoutRequestElement tmpReadout =
                (IReadoutRequestElement) elemVec.get(0);

            //DO the actual demuxing...
            try {
                IWriteablePayload reqPay = (IWriteablePayload) tmpRRQ;
                dests.writePayload(tmpReadout.getSourceID(), reqPay);
                reqPay.recycle();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Problem while writing readout request" +
                              " to source #" + tmpReadout.getSourceID(), e);
                }
            }
        }

        return true;
    }

    /**
     * Register the String Processor request output engine.
     *
     * @param oe output engine
     */
    public void registerOutputEngine(RequestPayloadOutputEngine oe)
    {
        if (oe == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Null payload output engine.");
            }

            return;
        }

        payloadDest = oe;
    }

    /**
     * Send STOP message to output engine.
     */
    public void sendStopMessage()
    {
        payloadDest.sendLastAndStop();
    }
}
