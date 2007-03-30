package icecube.daq.eventBuilder;

import icecube.daq.common.DAQCmdInterface;

import icecube.daq.payload.IDOMID;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.MasterPayloadFactory;
import icecube.daq.payload.PayloadRegistry;
import icecube.daq.payload.SourceIdRegistry;

import icecube.daq.trigger.IReadoutRequest;
import icecube.daq.trigger.IReadoutRequestElement;

import icecube.daq.trigger.impl.DOMID8B;
import icecube.daq.trigger.impl.ReadoutRequestPayload;
import icecube.daq.trigger.impl.ReadoutRequestPayloadFactory;
import icecube.daq.trigger.impl.TriggerRequestPayloadFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: nehar
 * Date: Sep 4, 2005
 * Time: 3:18:10 PM
 *
 * Utility class used by EventBuidlerTriggerRequestDemultiplexer to create
 * a list of ReadoutRequestPayload's in response to a list of
 * ReadoutRequestElements. A TriggerRequestDemultiplexer object would own one
 * of these and use these to get at the readoutr requests that are sent in
 * response to all the readout elements for a single trigger request.
 *
 * Note that the event builder acccepts all possible readout types and
 * translates them into either STRING or DOM requests for
 * the string processors.
 */
public class EventBuilderReadoutRequestGenerator
{
    //convenient mnemonics for the readout types
    private static final int GLOBAL =
        IReadoutRequestElement.READOUT_TYPE_IIIT_GLOBAL;
    private static final int INICE =
        IReadoutRequestElement.READOUT_TYPE_II_GLOBAL;
    private static final int ICETOP =
        IReadoutRequestElement.READOUT_TYPE_IT_GLOBAL;
    private static final int STRING =
        IReadoutRequestElement.READOUT_TYPE_II_STRING;
    private static final int DOM =
        IReadoutRequestElement.READOUT_TYPE_II_MODULE;
    private static final int OLD_DOM =
        IReadoutRequestElement.READOUT_TYPE_IT_MODULE;

    //TODO: source id should come from configuration stuff
    private static final ISourceID ME =
        SourceIdRegistry.getISourceIDFromNameAndId
        (DAQCmdInterface.DAQ_EVENTBUILDER, 0);

    private static final Log LOG =
        LogFactory.getLog(EventBuilderReadoutRequestGenerator.class);

    //Factory object used to create readout requests.
    private ReadoutRequestPayloadFactory readoutFactory;

    // collection of ISourceIDs of all InIce string procs.
    private Collection inIceSources;

    // collection of Source id's of all IceTop IDHs
    private Collection iceTopSources;

    /**
     * Create a readout request generator.
     *
     * @param master master payload factory
     */
    public EventBuilderReadoutRequestGenerator(MasterPayloadFactory master)
    {
        if (master == null) {
            throw new Error("Master payload factory is null");
        }

        final int reqType = PayloadRegistry.PAYLOAD_ID_READOUT_REQUEST;
        readoutFactory =
            (ReadoutRequestPayloadFactory) master.getPayloadFactory(reqType);
    }

    /**
     * Generate a request for a single DOM  for the string proc
     * (or IDH) specified by the source id.
     *
     * @param stringID The source id of the String proc to send out created
     *                 request.
     * @param domID The DOMID of the Dom.
     * @param rtype The readout type to be sent for current string.
     *
     * @return the readout request for the DOM.
     */
    private void generateDomRequest(Collection requests,
                                    int eventId,
                                    ISourceID stringID,
                                    IDOMID domID,
                                    IUTCTime firstTime,
                                    IUTCTime lastTime,
                                    IUTCTime timeStamp)
    {
        IReadoutRequestElement elem =
            TriggerRequestPayloadFactory.createReadoutRequestElement
            (DOM, firstTime, lastTime, domID, stringID);

        Vector readoutElements = new Vector();
        readoutElements.add(0, elem);

        // so this gives me an IReadoutRequest
        IReadoutRequest req =
            (IReadoutRequest) ReadoutRequestPayloadFactory.createReadoutRequest
            (ME, eventId, readoutElements);

        ReadoutRequestPayload payload = null;
        try {
            payload =
                (ReadoutRequestPayload) readoutFactory.createPayload
                (timeStamp, req);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Couldn't get ReadoutRequestPayload" +
                         " from IReadoutRequest", e);
            }
        }

        try {
            payload.loadPayload();
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("ReadoutRequestGenerator", e);
            }
        }

        requests.add(payload);
    }

    /**
     *  Generate a global readout request, i.e. generate
     *  ReadoutRequestPayloads to go to all String processors and IDHs,
     *  in response to a Global request from a ReadoutRequestElement that
     *  came in the incoming TriggerRequestPayload.
     */
    private void generateGlobalRequest(Collection requests,
                                       int eventId,
                                       IUTCTime firstTime,
                                       IUTCTime lastTime,
                                       IUTCTime timeStamp)
    {
        if (requests.size() > 0 && LOG.isErrorEnabled()) {
            // TODO: this doesn't look right
            LOG.error("Throwing away " + requests.size() +
                      " requests when adding global request");
        }

        //reset the list of requests to send out, just in case.
        requests.clear();
        // all inice
        generateStringGlobalRequest(requests, inIceSources, eventId,
                                    firstTime, lastTime, timeStamp);
        //all icetop
        generateStringGlobalRequest(requests, iceTopSources, eventId,
                                    firstTime, lastTime, timeStamp);
    }

    /**
     *  Generate ReadoutRequests for all strings of a given type.
     */
    private void generateStringGlobalRequest(Collection requests,
                                             Collection sources,
                                             int eventId,
                                             IUTCTime firstTime,
                                             IUTCTime lastTime,
                                             IUTCTime timeStamp)
    {
        // create readoutRequests for each string in icetop
        Iterator iter = sources.iterator();
        while (iter.hasNext()) {
            ISourceID curr = (ISourceID) iter.next();

            generateStringRequest(requests, eventId, curr,
                                  firstTime, lastTime, timeStamp);
        }
    }

    /**
     * Generate an all-string request for the string proc (or IDH)
     * specified by the source id.
     *
     * @param stringid The source id of the String proc to send out create
     *                 request.
     * @param rtype The readout type to be sent for current string.
     *
     * @return the readout request for the string.
     */
    private void generateStringRequest(Collection requests,
                                       int eventId,
                                       ISourceID stringid,
                                       IUTCTime firstUTC,
                                       IUTCTime lastUTC,
                                       IUTCTime timeStamp)
    {
        //all string request
        DOMID8B domId = new DOMID8B();

        IReadoutRequestElement elem = (IReadoutRequestElement)
            ReadoutRequestPayloadFactory.createReadoutRequestElement
            (STRING, firstUTC, lastUTC, domId, stringid);

        Vector readoutElements = new Vector();
        readoutElements.add(elem);

        // so this gives me an IReadoutRequest
        IReadoutRequest req =
            (IReadoutRequest) ReadoutRequestPayloadFactory.createReadoutRequest
            (ME, eventId, readoutElements);

        ReadoutRequestPayload payload = null;
        try {
            payload = (ReadoutRequestPayload)
                readoutFactory.createPayload(timeStamp, req);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Couldn't get ReadoutRequestPayload" +
                         " from IReadoutRequest", e);
            }
        }
        try {
            payload.loadPayload();
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("ReadoutRequestGenerator", e);
            }
            return;
        }

        requests.add(payload);
    }

    /**
     * The main processing method for the requests
     *
     * @param readoutElements list of readout requests
     * @param eventId global trigger request UID
     * @param timeStamp global trigger request timestamp
     *
     * @return list of targetted requests
     */
    public Collection generator(Collection readoutElements, int eventId,
                                IUTCTime timeStamp)
    {
        //initialize to empty vectors to avoid Null pointer exceptions.
        ArrayList eventReadoutRequests = new ArrayList();

        // Go through the element list
        Iterator iter = readoutElements.iterator();
        while (iter.hasNext()) {

            //  Some initialization stuff for this readout element
            IReadoutRequestElement tmp = (IReadoutRequestElement) iter.next();

            //readout type for the current element.
            int elementType = tmp.getReadoutType();
            ISourceID sid = tmp.getSourceID();
            IDOMID domid = tmp.getDomID();
            IUTCTime firstTime = tmp.getFirstTimeUTC();
            IUTCTime lastTime = tmp.getLastTimeUTC();

            switch(elementType) {

            case GLOBAL:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saw request GLOBAL for current Trigger");
                }

                generateGlobalRequest(eventReadoutRequests, eventId,
                                      firstTime, lastTime, timeStamp);

                return eventReadoutRequests;

                //----------------------------------------------------
            case INICE:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saw request INICE for current Trigger");
                }

                generateStringGlobalRequest(eventReadoutRequests,
                                            inIceSources, eventId,
                                            firstTime, lastTime, timeStamp);
                break;

                //-----------------------------------------------------
            case ICETOP:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saw request ICETOP for current Trigger");
                }

                generateStringGlobalRequest(eventReadoutRequests,
                                            iceTopSources, eventId,
                                            firstTime, lastTime, timeStamp);
                break;

                //-----------------------------------------------------
            case STRING:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saw request STRING for current trigger");
                }


                //add  a string request to the vector to be returned.
                generateStringRequest(eventReadoutRequests, eventId, sid,
                                      firstTime, lastTime, timeStamp);
                break;

                //-----------------------------------------------------
                // Logically these  cases are the same deal

            case DOM:
            case OLD_DOM:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saw request DOM for current Trigger");
                }

                generateDomRequest(eventReadoutRequests, eventId, sid, domid,
                                   firstTime, lastTime, timeStamp);
                break;

                //-----------------------------------------------------
            default:
                // weird DOM type, just ignore and go on

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Got weird readout request " + elementType +
                             " in current Trigger");
                }

                break;
            }
        }

        return eventReadoutRequests;
    }

    /**
     * Use the set of connected source IDs to create lists of in-ice and
     * icetop destinations.
     *
     * @param sourceIds set of connected source IDs
     */
    public void setDestinations(Collection sourceIds)
    {
        if (sourceIds == null || sourceIds.size() == 0) {
            throw new Error("No source IDs specified");
        }

        if (iceTopSources == null) {
            iceTopSources = new ArrayList();
        } else {
            iceTopSources.clear();
        }

        if (inIceSources == null) {
            inIceSources = new ArrayList();
        } else {
            inIceSources.clear();
        }

        Iterator iter = sourceIds.iterator();
        while (iter.hasNext()) {
            ISourceID srcId = (ISourceID) iter.next();

            if (SourceIdRegistry.isIniceHubSourceID(srcId)) {
                inIceSources.add(srcId);
            } else if (SourceIdRegistry.isIcetopHubSourceID(srcId)) {
                iceTopSources.add(srcId);
            } else {
                String typeStr;
                if (SourceIdRegistry.isAnyHubSourceID(srcId)) {
                    typeStr = "hub";
                } else {
                    typeStr = "non-hub";
                }

                LOG.error("Ignoring " + typeStr + " target #" +
                          srcId.getSourceID());
            }
        }
    }
}
