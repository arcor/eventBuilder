package icecube.daq.eventBuilder;

import icecube.daq.payload.IPayloadDestinationCollection;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.IWriteablePayload;
import icecube.daq.payload.MasterPayloadFactory;
import icecube.daq.payload.PayloadRegistry;
import icecube.daq.payload.SourceIdRegistry;
import icecube.daq.trigger.IReadoutRequest;
import icecube.daq.trigger.IReadoutRequestElement;
import icecube.daq.trigger.ITriggerRequestPayload;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

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

    /*
     * The unique ID for the Event generated in response
     * to the Trigger Request that invoked this object.
     * This is gotted from the ITriggerRequestPayload.
     */
    private int eventId;

    /* Vector of all readout reqeust elements associated with
     * this event. Obtained by calling getReadoutRequest()
     * on mt_rrq.
     */
    private Vector readoutElements;

    /**
     * Every Payload has to have a time stamp. This field is used to
     * generate the time stamps on the ReadoutRequestPayload's sent out
     * to the SP/IDH's. comes from the TriggerRequestPaylaod with which
     * we got invoked.
     */
    private IUTCTime utcTime;

    // The output engine used to send Readout Requests to String procs.
    private RequestPayloadOutputEngine payloadDest;

    /**
     * Constructor
     *
     * @param master master payload factory
     */
    public EventBuilderTriggerRequestDemultiplexer(MasterPayloadFactory master)
    {
        readoutGenerator = new EventBuilderReadoutRequestGenerator(master);
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

        if (inputTriggerRequest.getPayloadType() !=
            PayloadRegistry.PAYLOAD_ID_TRIGGER_REQUEST)
        {
            //Something sucks - Global Trigger sent a weird payload. Bailout.
            if (LOG.isErrorEnabled()) {
                LOG.error("Not a triggerRequestPayload.");
            }

            return false;
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
        eventId = inputTriggerRequest.getUID();

        // We need to get the Payload time stamp to put in the readout
        // requests.
        utcTime = inputTriggerRequest.getPayloadTimeUTC();

        final IReadoutRequest tmpReq = inputTriggerRequest.getReadoutRequest();
        readoutElements =
            tmpReq.getReadoutRequestElements();

        if (!generatorInitialized) {
            IPayloadDestinationCollection coll =
                payloadDest.getPayloadDestinationCollection();
            readoutGenerator.setDestinations(coll.getAllSourceIDs());
            generatorInitialized = true;
        }

        // Get readout Request payloads to send from the Generator object.
        Collection readouts =
            readoutGenerator.generator(readoutElements, eventId, utcTime);
        if (readouts == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Generator gave back a null vector");
            }

            return false;
        }

        IPayloadDestinationCollection dests =
            payloadDest.getPayloadDestinationCollection();

        // Let's dump these readouts to logs and to the output destination.
        Iterator iter = readouts.iterator();
        while (iter.hasNext()) {

            IReadoutRequest tmpRRQ = (IReadoutRequest) iter.next();

            // this is where we send out the payload using the
            // payloadDest payload output engine.
            //  We try to demux the readouts to different files.

            Vector elemVec = tmpRRQ.getReadoutRequestElements();

            if (LOG.isErrorEnabled() && elemVec.size() != 1) {
                LOG.error("Expected one element in readout request #" +
                          tmpRRQ.getUID() + ", not " +  elemVec.size());
            }

            //this works coz there's only one element in each readoutRequest
            IReadoutRequestElement tmpReadout =
                (IReadoutRequestElement) elemVec.elementAt(0);

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
