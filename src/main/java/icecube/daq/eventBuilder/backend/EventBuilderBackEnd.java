package icecube.daq.eventBuilder.backend;

import icecube.daq.common.DAQCmdInterface;
import icecube.daq.common.EventVersion;
import icecube.daq.eventBuilder.SPDataAnalysis;
import icecube.daq.eventBuilder.monitoring.BackEndMonitor;
import icecube.daq.eventBuilder.exceptions.EventBuilderException;
import icecube.daq.io.DispatchException;
import icecube.daq.io.Dispatcher;
import icecube.daq.io.StreamMetaData;
import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IEventFactory;
import icecube.daq.payload.IEventHitRecord;
import icecube.daq.payload.IEventPayload;
import icecube.daq.payload.IHitRecordList;
import icecube.daq.payload.ILoadablePayload;
import icecube.daq.payload.IPayload;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.ITriggerRequestPayload;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.PayloadChecker;
import icecube.daq.payload.PayloadException;
import icecube.daq.payload.SourceIdRegistry;
import icecube.daq.payload.impl.EventFactory;
import icecube.daq.reqFiller.RequestFiller;
import icecube.daq.splicer.Spliceable;
import icecube.daq.splicer.Splicer;
import icecube.daq.util.IDOMRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Pull trigger requests from the front-end queue and readout data
 * from the splicer queue, and use those inputs to build events and send
 * them to DAQ dispatch.
 */
public class EventBuilderBackEnd
    extends RequestFiller
    implements BackEndMonitor, SPDataProcessor
{
    /**
     * Subrun event counts.
     */
    class SubrunEventCount
        implements Comparable
    {
        /** subrun number */
        private int num;
        /** number of events in subrun */
        private long count;

        /**
         * Create a subrun event count
         *
         * @param num subrun number
         * @param count number of events
         */
        SubrunEventCount(int num, long count)
        {
            this.num = num;
            this.count = count;
        }

        /**
         * Compare this object to another.
         *
         * @param obj compared cobject
         *
         * @return the usual comparison results
         */
        @Override
        public int compareTo(Object obj)
        {
            if (obj == null) {
                return -1;
            }

            if (!(obj instanceof SubrunEventCount)) {
                return getClass().getName().compareTo(obj.getClass().getName());
            }

            SubrunEventCount sd = (SubrunEventCount) obj;

            if (num == sd.num) {
                return 0;
            }

            if (num == -sd.num) {
                if (num < 0) {
                    return -1;
                }

                return 1;
            }

            int absNum = (num < 0 ? -num : num);
            int absSDNum = (sd.num < 0 ? -sd.num : sd.num);
            return absNum - absSDNum;
        }

        /**
         * Is this object equal to another object?
         *
         * @param obj compared object
         *
         * @return <tt>true</tt> if the compared object is equal to this object
         */
        @Override
        public boolean equals(Object obj)
        {
            return compareTo(obj) == 0;
        }

        /**
         * Get the number of events in this subrun
         *
         * @return number of events
         */
        long getCount()
        {
            return count;
        }

        /**
         * Get the hash code for this object
         *
         * @return subrun number
         */
        @Override
        public int hashCode()
        {
            return num;
        }
    }

    /**
     * Event totals for a run.
     */
    class EventRunData
    {
        private long numEvents;
        private long firstEventTime;
        private long lastEventTime;
        private long firstGoodTime;
        private long lastGoodTime;

        /**
         * Create an object holding the event totals for a run.
         *
         * @param numEvents - number of physics events dispatched
         * @param firstEventTime - starting time of first event
         * @param lastEventTime - ending time of last event
         * @param firstGoodTime - starting time of first good event
         * @param lastGoodTime - ending time of last good event
         */
        EventRunData(long numEvents, long firstEventTime, long lastEventTime,
                     long firstGoodTime, long lastGoodTime)
        {
            this.numEvents = numEvents;
            this.firstEventTime = firstEventTime;
            this.lastEventTime = lastEventTime;
            this.firstGoodTime = firstGoodTime;
            this.lastGoodTime = lastGoodTime;
        }

        public void setLastGoodTime(int runNumber, long time)
        {
            if (lastGoodTime > 0 && lastGoodTime != time) {
                LOG.error("Overwriting " + runNumber + " last good time " +
                          lastGoodTime + " with new value " + time);
            }

            lastGoodTime = time;
        }

        /**
         * Return run data as an array of <tt>long</tt> values.
         *
         * @return array of <tt>long</tt> values
         */
        public long[] toArray()
        {
            return new long[] { numEvents, firstEventTime, lastEventTime,
                                firstGoodTime, lastGoodTime };
        }

        /**
         * Return string representation of run data.
         *
         * @return string
         */
        @Override
        public String toString()
        {
            return "EventRunData[evts " + numEvents + ", first " +
                firstEventTime + ", last " + lastEventTime + ", firstGood " +
                firstGoodTime + ", lastGood " + lastGoodTime + "]";
        }
    }

    /** Message logger. */
    private static final Logger LOG =
        Logger.getLogger(EventBuilderBackEnd.class);

    /** Event builder source ID. */
    private static final ISourceID ME =
        SourceIdRegistry.getISourceIDFromNameAndId
        (DAQCmdInterface.DAQ_EVENTBUILDER, 0);

    /** If <tt>true</tt>, drop events outside firstGoodTime and lastGoodTime */
    private static final boolean DROP_NOT_GOOD = true;

    private Splicer splicer;
    private Dispatcher dispatcher;

    // Factory to make EventPayloads.
    private IEventFactory eventFactory;

    // per-run monitoring counters
    private int execListLen;
    private long numBadEvents;
    private long prevFirstTime;
    private long prevLastTime;

    // lifetime monitoring counters
    private long prevRunTotalEvents;
    private long totalEventsSent;

    /** Current run number. */
    private int runNumber;
    /** Previous run number. */
    private int prevRunNumber;
    /** Have we reported a bad run number yet? */
    private boolean reportedBadRunNumber;

    /** Current subrun number. */
    private int subrunNumber;
    /** Current subrun start time. */
    private long subrunStart;
    /** Number of events in current subrun. */
    private long subrunEventCount;
    /** If we have a new subrunStart value which has not yet been
     * reached (commitSubrun() has been called but not yet acted upon) */
    private boolean newSubrunStartTime;
    /** List of all subrun event counts. */
    private HashMap<SubrunEventCount, SubrunEventCount> subrunEventCountMap =
        new HashMap<SubrunEventCount, SubrunEventCount>();
    /** Last dispatched subrun number. */
    private int lastDispSubrunNumber;
    /** An object for synchronizing subrun data changes on */
    private Object subrunLock = new Object();

    /** Has the back end been reset? */
    private boolean isReset;
    /** should events be validated? */
    private boolean validateEvents;

    /** Current year. */
    private short year;
    private long prevYearTime;

    /** Output queue  -- ACCESS MUST BE SYNCHRONIZED. */
    private List<ILoadablePayload> outputQueue =
        new LinkedList<ILoadablePayload>();
    /** Output thread. */
    private OutputThread outputThread;

    /** New run number to be used when switching runs mid-stream */
    private int switchNumber;

    /** Track last event end to be used as the last good time when switching */
    private long prevEndTime;

    /** Map used to track start/stop/count data for each run */
    private HashMap<Integer, EventRunData> runData =
        new HashMap<Integer, EventRunData>();

    /** An object for synchronizing good time changes */
    private Object goodTimeLock = new Object();
    /** First time when all hubs have sent a hit */
    private long firstGoodTime;
    /** Last time when all hubs have sent a hit */
    private long lastGoodTime;
    /** Count the number of events sent before firstGoodTime was set */
    private long numUnknownBeforeFirst;
    /** Count the number of events dropped once firstGoodTime was set */
    private long numDroppedBeforeFirst;
    /** Count the number of events sent before lastGoodTime was set */
    private long numUnknownBeforeLast;
    /** Count the number of valid events seen once lastGoodTime was set */
    private long numKnownBeforeLast;
    /** Count the number of events dropped once lastGoodTime was set */
    private long numDroppedAfterLast;
    /** Count the total possible events */
    private long totalPossible;

    /**
     * Constructor
     *
     * @param eventCache event buffer cache manager
     * @param splicer data splicer
     * @param analysis data splicer analysis
     * @param dispatcher DAQ dispatch
     */
    public EventBuilderBackEnd(IByteBufferCache eventCache, Splicer splicer,
                               SPDataAnalysis analysis, Dispatcher dispatcher)
    {
        this(eventCache, splicer, analysis, dispatcher, false);
    }

    /**
     * Constructor
     *
     * @param eventCache event buffer cache manager
     * @param splicer data splicer
     * @param analysis data splicer analysis
     * @param dispatcher DAQ dispatch
     * @param validateEvents <tt>true</tt> if created events should be
     *                       checked for validity
     */
    public EventBuilderBackEnd(IByteBufferCache eventCache, Splicer splicer,
                               SPDataAnalysis analysis, Dispatcher dispatcher,
                               boolean validateEvents)
    {
        super("EventBuilderBackEnd", true);

        this.dispatcher = dispatcher;
        this.splicer = splicer;
        this.validateEvents = validateEvents;

        // register this object with splicer analysis
        analysis.setDataProcessor(this);

        //get factory object for event payloads
        try {
            eventFactory = new EventFactory(eventCache, EventVersion.VERSION);
        } catch (PayloadException pe) {
            throw new Error("Cannot create factory for Event V" +
                            EventVersion.VERSION);
        }
    }

    /**
     * Increment the count of splicer.execute() calls.
     */
    @Override
    public void addExecuteCall()
    {
        // XXX do nothing
    }

    /**
     * Extract hit records from separate HitRecordPayloads into a single list.
     * @param dataList list of HitRecordPayloads
     * @return unified list
     */
    private static List<IEventHitRecord> buildHitRecordList(List dataList)
    {
        List<IEventHitRecord> hitRecList = new ArrayList<IEventHitRecord>();
        for (Object obj : dataList) {
            IHitRecordList list = (IHitRecordList) obj;

            for (IEventHitRecord rec : list) {
                hitRecList.add(rec);
            }
        }
        return hitRecList;
    }

    /**
     * Do a simple comparison of the request and data payloads.
     *
     * @param reqPayload request payload
     * @param dataPayload data payload
     *
     * @return <tt>-1</tt> if data is "before" request
     *         <tt>0</tt> if data is "within" request
     *         <tt>1</tt> if data is "later than" request
     */
    @Override
    public int compareRequestAndData(IPayload reqPayload, IPayload dataPayload)
    {
        ITriggerRequestPayload req = (ITriggerRequestPayload) reqPayload;

        final long reqFirst, reqLast;
        if (req == null) {
            reqFirst = Integer.MAX_VALUE;
            reqLast = Integer.MAX_VALUE;
        } else {
            reqFirst = req.getFirstTimeUTC().longValue();
            reqLast = req.getLastTimeUTC().longValue();
        }

        final long time;
        if (dataPayload == null) {
            time = Long.MIN_VALUE;
        } else {
            time = ((IPayload) dataPayload).getUTCTime();
        }

        int rtnval;
        if (time < reqFirst) {
            rtnval = -1;
        } else if (time > reqLast) {
            rtnval = 1;
        } else {
            rtnval = 0;
        }

        return rtnval;
    }

    /**
     * Dispose of a data payload which is no longer needed.
     *
     * @param data payload
     */
    @Override
    public void disposeData(ILoadablePayload data)
    {
        data.recycle();
    }

    /**
     * Dispose of a list of data payloads which are no longer needed.
     *
     * @param dataList list of data payload
     */
    @Override
    public void disposeDataList(List dataList)
    {
        for (Object obj : dataList) {
            disposeData((ILoadablePayload) obj);
        }
    }

    /**
     * Finish any tasks to be done just before the thread exits.
     */
    @Override
    public void finishThreadCleanup()
    {
        if (runNumber < 0) {
            if (!reportedBadRunNumber) {
                LOG.error("Run number has not been set");
                reportedBadRunNumber = true;
            }
            return;
        }

        String message = Dispatcher.STOP_PREFIX + runNumber;
        try {
            dispatcher.dataBoundary(message);
        } catch (DispatchException de) {
            LOG.error("Couldn't stop dispatcher (" + message + ")", de);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Stopped dispatcher");
        }

        // save run data for later retrieval
        saveRunData();

        LOG.error("GoodTime Stats: UnknownBefore: " + numUnknownBeforeFirst +
                  "  DroppedBeforeFirst: " + numDroppedBeforeFirst +
                  "  UnknownBeforeLast: " + numUnknownBeforeLast +
                  "  KnownBeforeLast: " + numKnownBeforeLast +
                  "  DroppedAfterLast: " + numDroppedAfterLast +
                  "  TotalPossible: " + totalPossible);
    }

    /**
     * Get most recent splicer.execute() list length for this run.
     *
     * @return most recent splicer.execute() list length
     */
    @Override
    public long getCurrentExecuteListLength()
    {
        return execListLen;
    }

    /**
     * Returns the number of units still available in the disk (measured
     * in MB).  If it fails to check the disk space, then it returns -1.
     *
     * @return the number of units still available in the disk.
     */
    @Override
    public long getDiskAvailable()
    {
        return dispatcher.getDiskAvailable();
    }

    /**
     * Returns the total number of units in the disk (measured in MB).
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the total number of units in the disk.
     */
    @Override
    public long getDiskSize()
    {
        return dispatcher.getDiskSize();
    }

    /**
     * Return the number of events and the last event time as a list.
     *
     * @return event data
     */
    @Override
    public long[] getEventData()
    {
        StreamMetaData metadata = getMetaData();
        return new long[] {
            runNumber, metadata.getCount(), metadata.getTicks()
        };
    }

    /**
     * Return the first event time.
     *
     * @return first event time
     */
    @Override
    public long getFirstEventTime()
    {
        return getFirstOutputTime();
    }

    /**
     * Compute the subrun number which should follow the specified number.
     *
     * @param num current subrun number
     *
     * @return next subrun number
     */
    private static int getNextSubrunNumber(int num)
    {
        if (num < 0) {
            return -num;
        }

        return -num - 1;
    }

    /**
     * Get number of bad events for this run.
     *
     * @return number of bad events
     */
    @Override
    public long getNumBadEvents()
    {
        return numBadEvents;
    }

    /**
     * Returns the number of bytes written to disk by the event builder
     *
     * @return the number of bytes written to disk by the event builder
     */
    @Override
    public long getNumBytesWritten()
    {
        return dispatcher.getNumBytesWritten();
    }

    /**
     * Get number of events written by the dispatcher.
     *
     * @return number of events written to file
     */
    @Override
    public long getNumEventsDispatched()
    {
        return dispatcher.getNumDispatchedEvents();
    }

    /**
     * Get number of events sent.
     *
     * @return number of events sent
     */
    @Override
    public long getNumEventsSent()
    {
        return getNumOutputsSent();
    }

    /**
     * Get number of events queued for output.
     *
     * @return number of events queued
     */
    @Override
    public int getNumOutputsQueued()
    {
        return outputQueue.size();
    }

    /**
     * Get number of readouts cached for event being built
     *
     * @return number of cached readouts
     */
    @Override
    public int getNumReadoutsCached()
    {
        return getNumDataPayloadsCached();
    }

    /**
     * Get number of readouts queued for processing.
     *
     * @return number of readouts queued
     */
    @Override
    public int getNumReadoutsQueued()
    {
        return getNumDataPayloadsQueued();
    }

    /**
     * Get number of readouts received.
     *
     * @return number of readouts received
     */
    @Override
    public long getNumReadoutsReceived()
    {
        return getNumDataPayloadsReceived();
    }

    /**
     * Get number of trigger requests queued for the back end.
     *
     * @return number of trigger requests queued for the back end
     */
    @Override
    public int getNumTriggerRequestsQueued()
    {
        return getNumRequestsQueued();
    }

    /**
     * Get number of trigger requests received from the global trigger
     * for this run.
     *
     * @return number of trigger requests received for this run
     */
    @Override
    public long getNumTriggerRequestsReceived()
    {
        return getNumRequestsReceived();
    }

    /**
     * Get the total number of events from the previous run.
     *
     * @return total number of events sent during the previous run
     */
    @Override
    public long getPreviousRunTotalEvents()
    {
        return prevRunTotalEvents;
    }

    /**
     * Get the run data for the specified run.
     *
     * @param runNum run number
     * @param forcedSave if <tt>true</tt>, use current data
     *
     * @return array of <tt>long</tt> values:<ol>
     *    <li>number of events
     *    <li>starting time of first event in run
     *    <li>ending time of last event in run
     *    </ol>
     *
     * @throws EventBuilderException if no data is found for the run
     */
    public long[] getRunData(int runNum, boolean forcedSave)
        throws EventBuilderException
    {
        EventRunData evtData;
        if (runData.containsKey(runNum)) {
            evtData = runData.get(runNum);
        } else if (forcedSave) {
            evtData = saveRunData();
        } else {
            LOG.error("No data found for run " + runNum);
            throw new EventBuilderException("No data found for run " + runNum);
        }

        LOG.error("Run " + runNum + " EB data is " + evtData);
        return evtData.toArray();
    }

    /**
     * Get the current run number.
     */
    public int getRunNumber()
    {
        return runNumber;
    }

    /**
     * Get the current subrun number.
     *
     * @return current subrun number
     */
    @Override
    public int getSubrunNumber()
    {
        return subrunNumber;
    }

    /**
     * Get the total number of events for the specified subrun.
     *
     * @param subrun subrun number
     *
     * @return total number of events sent during the specified subrun
     */
    public long getSubrunTotalEvents(int subrun)
    {

        // return count from current subrun
        synchronized (subrunLock) {
            if (subrun == subrunNumber) {
                return subrunEventCount;
            }
        }

        SubrunEventCount question = new SubrunEventCount(subrun, 0L);
        SubrunEventCount answer;
        synchronized (subrunLock) {
            answer = subrunEventCountMap.get(question);
        }

        if (answer == null) {
            throw new RuntimeException("Illegal subrun " + subrun);
        }

        return answer.getCount();
    }

    /**
     * Total number of readouts received since last reset.
     *
     * @return total number of readouts received since last reset
     */
    @Override
    public long getTotalReadoutsReceived()
    {
        return getTotalDataPayloadsReceived();
    }

    /**
     * Does this data payload match one of the request criteria?
     *
     * @param reqPayload request
     * @param dataPayload data
     *
     * @return <tt>true</tt> if the data payload is part of the
     *         current request
     */
    @Override
    public boolean isRequested(IPayload reqPayload, IPayload dataPayload)
    {
        return true;
    }

    /**
     * Make an EventPayload out of the request in req and the data
     * in dataList.
     *
     * @param reqPayload trigger request
     * @param dataList list of readouts matching the trigger request
     *
     * @return The EventPayload created for the current TriggerRequest.
     */
    @Override
    public ILoadablePayload makeDataPayload(IPayload reqPayload,
                                            List dataList)
    {
        // remember that we need to be reset
        isReset = false;

        if (reqPayload == null) {
            try {
                throw new NullPointerException("No request");
            } catch (Exception ex) {
                LOG.error("No current request; cannot send data", ex);
            }

            return null;
        }

        if (runNumber < 0 && !reportedBadRunNumber) {
            LOG.error("Run number has not yet been set");
            reportedBadRunNumber = true;
            return null;
        }

        ITriggerRequestPayload req = (ITriggerRequestPayload) reqPayload;

        final int uid = req.getUID();
        final IUTCTime startUTC = req.getFirstTimeUTC();
        final IUTCTime endUTC = req.getLastTimeUTC();

        if (startUTC == null || endUTC == null) {
            LOG.error("Request may have been recycled; cannot send data");
            return null;
        }

        final long startTime = startUTC.longValue();

        long tmpFirstGood;
        long tmpLastGood;
        synchronized (goodTimeLock) {
            tmpFirstGood = firstGoodTime;
            tmpLastGood = lastGoodTime;
        }

        totalPossible++;
        if (tmpFirstGood == 0) {
            numUnknownBeforeFirst++;
        } else if (startTime < tmpFirstGood) {
            numDroppedBeforeFirst++;
            if (DROP_NOT_GOOD) {
                return DROPPED_PAYLOAD;
            }
        } else if (tmpLastGood == 0) {
            numUnknownBeforeLast++;
        } else if (endUTC.longValue() <= tmpLastGood) {
            numKnownBeforeLast++;
        } else {
            numDroppedAfterLast++;
            if (DROP_NOT_GOOD) {
                return DROPPED_PAYLOAD;
            }
        }

        // if this is the first event and we're supposed to switch to a
        // new run number, save the run data (but don't switch yet)
        if (uid == 1 && switchNumber > 0) {
            long[] tmpData = resetOutputData();
            runData.put(runNumber,
                        new EventRunData(tmpData[0], tmpData[1], tmpData[2],
                                         firstGoodTime, prevEndTime));

            prevRunNumber = runNumber;
            runNumber = switchNumber;
            switchNumber = 0;
            firstGoodTime = startTime;
            lastGoodTime = 0;
        }

        // remember this startTime in case we switch runs at the next event
        prevEndTime = startTime;

        // set the year if we haven't yet or if the time has wrapped
        if (year == 0 || startTime < prevYearTime) {
            final short oldYear = year;
            setCurrentYear();
            if (oldYear != 0) {
                LOG.error("Changed year from " + oldYear + " to " + year);
            }
            prevYearTime = startTime;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing Event " + uid + " [" + startUTC + " - " +
                      endUTC + "]");
        }
        if (LOG.isInfoEnabled() && dataList.size() == 0) {
            LOG.info("Sending empty event " + uid + " window [" + startUTC +
                     " - " + endUTC + "]");
        }

        int subnum;
        synchronized (subrunLock) {
            if (newSubrunStartTime && startTime >= subrunStart) {
                // commitSubrun was called & that subrun is here
                subrunNumber = getNextSubrunNumber(subrunNumber);
                newSubrunStartTime = false;
            }
            subnum = subrunNumber;
        }

        ILoadablePayload evt;
        if (EventVersion.VERSION < 5) {
            evt = eventFactory.createPayload(uid, ME, startUTC, endUTC, year,
                                             runNumber, subnum, req, dataList);
        } else {
            try {
                evt = eventFactory.createPayload(uid, startUTC, endUTC,
                                                 year, runNumber, subnum, req,
                                                 buildHitRecordList(dataList));
            } catch (PayloadException pe) {
                LOG.error("Cannot create event #" + uid, pe);
                evt = null;
            }
        }

        return evt;
    }

    /**
     * Reset the back end after it has been stopped.
     */
    @Override
    public void reset()
    {
        if (!isReset) {
            prevRunTotalEvents = getNumOutputsSent();

            execListLen = 0;
            numBadEvents = 0;
            prevFirstTime = 0L;
            prevLastTime = 0L;

            runNumber = Integer.MIN_VALUE;
            reportedBadRunNumber = false;

            switchNumber = 0;

            firstGoodTime = 0;
            lastGoodTime = 0;
            numUnknownBeforeFirst = 0;
            numDroppedBeforeFirst = 0;
            numUnknownBeforeLast = 0;
            numKnownBeforeLast = 0;
            numDroppedAfterLast = 0;
            totalPossible = 0;

            // clear the current year so it's set by the first event
            year = 0;

            if (outputQueue.size() > 0) {
                LOG.error("Unwritten events queued at reset");

                synchronized (outputQueue) {
                    outputQueue.clear();
                }
            }

            if (outputThread == null) {
                outputThread = new OutputThread("EventWriter");
                outputThread.start();
            }

            isReset = true;
        }

        super.reset();
    }

    /**
     * Reset the back end during startup.
     */
    public void resetAtStart()
    {
        synchronized (subrunLock) {
            subrunNumber = 0;
            subrunStart = 0L;
            subrunEventCount = 0L;
            subrunEventCountMap.clear();
            newSubrunStartTime = false;
            lastDispSubrunNumber = 0;
        }
        reset();
    }

    /**
     * Save the important data regarding this run.
     *
     * @return saved event count/time data
     */
    private EventRunData saveRunData()
    {
        StreamMetaData meta = getMetaData();
        EventRunData evtData =
            new EventRunData(meta.getCount(), getFirstOutputTime(),
                             meta.getTicks(), firstGoodTime, lastGoodTime);
        runData.put(runNumber, evtData);
        return evtData;
    }

    /**
     * Send an output payload.
     *
     * @param output payload being sent
     *
     * @return <tt>true</tt> if event was sent
     */
    @Override
    public boolean sendOutput(ILoadablePayload output)
    {
        if (outputThread == null) {
            throw new Error("Output thread is not running");
        } else if (outputThread.hasFailed()) {
            String errmsg;
            if (outputThread.hasMaxConsecutiveErrors()) {
                errmsg = "Output thread has failed after " +
                    outputThread.getNumConsecutiveErrors() +
                    " consecutive payload errors";
            } else if (outputThread.hasMaxTotalErrors()) {
                errmsg = "Output thread has failed after " +
                    outputThread.getNumTotalErrors() +
                    " total payload errors";
            } else {
                errmsg = "Output thread has failed";
            }
            try {
                throw new Error(errmsg);
            } catch (Error err) {
                LOG.error("Stack Trace", err);
            }
            return false;
        }

        synchronized (outputQueue) {
            outputQueue.add(output);
            outputQueue.notify();
        }

        return true;
    }

    /**
     * Set the current year.
     */
    public void setCurrentYear()
    {
        GregorianCalendar cal = new GregorianCalendar();
        year = (short) cal.get(GregorianCalendar.YEAR);
    }

    /**
     * Set the DOM registry used to translate hit DOM IDs to channel IDs
     * @param domRegistry DOM registry
     */
    public void setDOMRegistry(IDOMRegistry domRegistry)
    {
        eventFactory.setDOMRegistry(domRegistry);
    }

    /**
     * Record the length of the list passed to splicedAnalysis.execute().
     *
     * @param execListLen list length
     */
    @Override
    public void setExecuteListLength(int execListLen)
    {
        this.execListLen = execListLen;
    }

    /**
     * Set the first time when all hubs have sent a hit.
     *
     * @param firstTime time of first good hit in run
     */
    public void setFirstGoodTime(long firstTime)
    {
        synchronized (goodTimeLock) {
            firstGoodTime = firstTime;
        }
    }

    /**
     * Set the last time when all hubs have sent a hit.
     *
     * @param lastTime time of last good hit in run
     */
    public void setLastGoodTime(long lastTime)
    {
        synchronized (goodTimeLock) {
            lastGoodTime = lastTime;
        }

        if (runData.containsKey(runNumber)) {
            LOG.error("Last good time was set after the run finished!");
            runData.get(runNumber).setLastGoodTime(runNumber, lastTime);
        }
    }

    /**
     * Set the start and end times from the current request.
     *
     * @param payload request payload
     */
    @Override
    public void setRequestTimes(IPayload payload)
    {
        // do nothing (required by RequestFiller)
    }

    /**
     * Set the current run number.
     *
     * @param runNumber current run number
     */
    public void setRunNumber(int runNumber)
    {
        this.runNumber = runNumber;
    }

    /**
     * Should events be validated by PayloadChecker?
     * NOTE: This should not be enabled on SPS!!!
     * @param val <tt>true</tt> to enable event validation
     */
    public void setValidateEvents(boolean val)
    {
        validateEvents = val;
    }

    /**
     * Inform the dispatcher that a new run is starting.
     */
    @Override
    public void startDispatcher()
    {
        if (runNumber < 0) {
            if (!reportedBadRunNumber) {
                LOG.error("Run number has not been set");
                reportedBadRunNumber = true;
            }
            return;
        }

        LOG.info("Splicer entered STARTING state");
        String message = Dispatcher.START_PREFIX + runNumber;
        try {
            dispatcher.dataBoundary(message);
        } catch (DispatchException de) {
            LOG.error("Couldn't start dispatcher (" + message + ")", de);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Started dispatcher");
        }
    }

    /**
     * Switch to new file for events in switched run.
     */
    private void switchFile(int oldRun, int newRun)
    {
        LOG.error("Switching from run " + oldRun + " to " + newRun);

        try {
            dispatcher.dataBoundary(Dispatcher.STOP_PREFIX + oldRun);
        } catch (DispatchException de) {
            LOG.error("Could not inform dispatcher of run stop (switch" +
                      " from " + oldRun + " to " + newRun + ")", de);
        }

        try {
            dispatcher.dataBoundary(Dispatcher.START_PREFIX + newRun);
        } catch (DispatchException de) {
            LOG.error("Could not inform dispatcher of run start (switch" +
                      " from " + oldRun + " to " + newRun + ")", de);
        }
    }

    /**
     * Prepare for a new subrun starting soon.  This will cause
     * subsequent events to be marked with a new subrun number, which
     * will be the negative of the number provided here - until the
     * commitSubrun method is called and the timestamp provided there
     * (identifying the begining of the new subrun) has passed.
     *
     * Note that this method will log a warning if the subrun number
     * provided does not follow the current subrun number but will
     * accept that as the new subrun number and unset any pending
     * commitSubrun timestamp.
     *
     * @param subrunNumber the subrun number which will be starting
     */
    public void prepareSubrun(int subrunNumber)
    {
        subrunNumber = -subrunNumber;
        synchronized (subrunLock) {
            int tmpNum = getNextSubrunNumber(this.subrunNumber);
            if (subrunNumber != tmpNum) {
                LOG.warn("Preparing for subrun " + subrunNumber +
                         ", though current subrun is " + this.subrunNumber +
                         ". (Expected next subrun to be " + tmpNum + ")");
            }
            this.subrunNumber = subrunNumber;
            newSubrunStartTime = false;
        }
    }

    /**
     * Mark the begining timestamp for when the indicated subrun has
     * started.  This method is used in conjunction with the
     * prepareSubrun() method in that this identifies when events
     * should no longer be marked with the negative of the subrun
     * number.
     *
     * Note that this method will throw a RuntimeException if either
     * the subrun number provided does not follow the current subrun
     * number, or if it appears to have been called without a previous
     * call to prepareSubrun.
     *
     * @param subrunNumber the subrun number
     * @param startTime time of first good hit in subrun
     */
    public void commitSubrun(int subrunNumber, long startTime)
    {
        synchronized (subrunLock) {
            int tmpNum = getNextSubrunNumber(this.subrunNumber);
            if (subrunNumber != tmpNum) {
                throw new RuntimeException("Provided subrun # " +
                    subrunNumber + " does not follow subrun: " +
                        this.subrunNumber);
            }
            if (newSubrunStartTime) {
                throw new RuntimeException(
                    "subrun already has start time set.");
            }
            newSubrunStartTime = true;
            this.subrunStart = startTime;
        }
    }

    /**
     * Close out the current subrun by sending a subrun databoundary
     * to the dispatcher and storing the count for this ending subrun
     * in the subrunEventCountMap.
     *
     * @param oldSubrunNumber the subrun number of the ending subrun
     * @param newSubrunNumber the subrun number of the new subrun
     */
    private void rollSubRun(int oldSubrunNumber, int newSubrunNumber)
    {
        synchronized (subrunLock) {
            // save event count from ending subrun
            SubrunEventCount newData =
                new SubrunEventCount(oldSubrunNumber, subrunEventCount);
            if (subrunEventCountMap.containsKey(newData)) {
                LOG.error("Found multiple counts for subrun number " +
                          oldSubrunNumber);
            } else {
                subrunEventCountMap.put(newData, newData);
            }
            subrunEventCount = 0;

            String message = Dispatcher.SUBRUN_START_PREFIX + newSubrunNumber;
            if (LOG.isInfoEnabled()) {
                LOG.info("calling dataBoundary for subrun with the message: " +
                         message);
            }

            try {
                dispatcher.dataBoundary(message);
            } catch (DispatchException de) {
                LOG.error("Could not inform dispatcher of subrun change" +
                          " (" + oldSubrunNumber + " to " +
                          newSubrunNumber + ")", de);
            }
        }
    }

    /**
     * Set the event dispatcher.
     *
     * @param dispatcher event dispatcher
     */
    public void setDispatcher(Dispatcher dispatcher)
    {
        if (dispatcher == null) {
            LOG.error("Cannot set null dispatcher");
        } else {
            this.dispatcher = dispatcher;
        }
    }

    /**
     * Set the run number to be used when we start receiving
     * reset trigger UIDs.
     *
     * @param num run number for next run
     */
    public void setSwitchRunNumber(int num)
    {
        if (num < 0) {
            LOG.error("Ignoring invalid switch run number " + num);
            return;
        }

        if (switchNumber > 0) {
            LOG.error("Overriding previous switch run " + switchNumber +
                      " with new value " + num);
        }

        switchNumber = num;
    }

    /**
     * Inform back-end processor that the splicer has stopped.
     */
    @Override
    public void splicerStopped()
    {
        if (isRunning()) {
            try {
                addDataStop();
            } catch (IOException ioe) {
                LOG.error("Cannot send final data stop" +
                          " after splicer stopped", ioe);
            }
        }
    }

    /**
     * If the thread is running, stop it.
     */
    @Override
    public void stopThread()
        throws IOException
    {
        // run parent method first so any added data is processed
        super.stopThread();

        if (outputThread != null) {
            outputThread = null;

            synchronized (outputQueue) {
                outputQueue.notify();
            }
        }
    }

    /**
     * Class which writes events to file(s).
     */
    class OutputThread
        implements Runnable
    {
        /** consecutive number of dispatch errors to allow */
        private static final int MAX_CONSECUTIVE_ERRORS = 5;
        /** total number of dispatch errors to allow */
        private static final int MAX_TOTAL_ERRORS = 20;

        private Thread thread;
        private int dispatchErrs;
        private int totalDispatchErrs;
        private boolean failed;

        /**
         * Create and start output thread.
         *
         * @param name thread name
         */
        OutputThread(String name)
        {
            thread = new Thread(this);
            thread.setName(name);
        }

        /**
         * Get the number of consecutive payload errors
         *
         * @return number of errors
         */
        int getNumConsecutiveErrors()
        {
            return dispatchErrs;
        }

        /**
         * Get the total number of payload errors
         *
         * @return number of errors
         */
        int getNumTotalErrors()
        {
            return totalDispatchErrs;
        }

        /**
         * Has the output thread failed?
         *
         * @return true if the output thread died due to internal problems
         */
        boolean hasFailed()
        {
            return failed;
        }

        /**
         * Have we reached the maximum number of consecutive payload errors?
         *
         * @return <tt>true</tt> if we've seen the maximum number of errors
         */
        boolean hasMaxConsecutiveErrors()
        {
            return dispatchErrs == MAX_CONSECUTIVE_ERRORS;
        }

        /**
         * Have we reached the maximum number of payload errors?
         *
         * @return <tt>true</tt> if we've seen the maximum number of errors
         */
        boolean hasMaxTotalErrors()
        {
            return totalDispatchErrs == MAX_TOTAL_ERRORS;
        }

        /**
         * Main event dispatching loop.
         */
        @Override
        public void run()
        {
            ILoadablePayload event;
            while (outputThread != null) {
                synchronized (outputQueue) {
                    if (outputQueue.size() == 0) {
                        try {
                            outputQueue.wait();
                        } catch (InterruptedException ie) {
                            LOG.error("Interrupt while waiting for output" +
                                      " queue", ie);
                        }
                    }

                    if (outputQueue.size() == 0) {
                        event = null;
                    } else {
                        event = outputQueue.remove(0);
                    }
                }

                if (event != null) {
                    sendToDaqDispatch(event);
                }
            }
        }

        /**
         * Send an event to DAQ Dispatch.
         *
         * @param event event being sent
         *
         * @return <tt>true</tt> if event was sent
         */
        private boolean sendToDaqDispatch(ILoadablePayload event)
        {
            IEventPayload tmpEvent = (IEventPayload) event;

            int eventSubrunNumber = tmpEvent.getSubrunNumber();
            if (eventSubrunNumber != lastDispSubrunNumber) {
                rollSubRun(lastDispSubrunNumber, eventSubrunNumber);
                lastDispSubrunNumber = eventSubrunNumber;
            }

            if (validateEvents) {
                if (!PayloadChecker.validatePayload(tmpEvent, true)) {
                    numBadEvents++;
                } else if (prevLastTime >
                           tmpEvent.getFirstTimeUTC().longValue())
                {
                    LOG.error("Previous event time interval [" +
                              prevFirstTime + "-" + prevLastTime +
                              "] overlaps current event interval [" +
                              tmpEvent.getFirstTimeUTC() + "-" +
                              tmpEvent.getLastTimeUTC() + "]");
                    numBadEvents++;
                }

                prevFirstTime = tmpEvent.getFirstTimeUTC().longValue();
                prevLastTime = tmpEvent.getLastTimeUTC().longValue();
            }

            if (tmpEvent.getEventUID() == 1 && totalEventsSent > 0) {
                switchFile(prevRunNumber, runNumber);
            }

            boolean eventSent = false;
            try {
                dispatcher.dispatchEvent(tmpEvent);
                dispatchErrs = 0;
                totalEventsSent++;
                eventSent = true;
            } catch (DispatchException de) {
                Throwable cause = de.getCause();
                if (cause != null &&
                    cause instanceof IOException &&
                    cause.getMessage() != null &&
                    cause.getMessage().equals("Read-only file system"))
                {
                    failed = true;
                    throw new Error("Read-only filesystem for " + tmpEvent);
                }

                dispatchErrs++;
                totalDispatchErrs++;

                if (hasMaxConsecutiveErrors() || hasMaxTotalErrors()) {
                    failed = true;
                } else {
                    LOG.error("Could not dispatch event", de);
                }
            }

            if (eventSent && LOG.isDebugEnabled()) {
                LOG.debug("Event " + tmpEvent.getFirstTimeUTC() + "-" +
                          tmpEvent.getLastTimeUTC() +
                          " written to daq-dispatch");
            }

            subrunEventCount++;

            tmpEvent.recycle();

            return eventSent;
        }

        /**
         * Start the thread.
         */
        public void start()
        {
            thread.start();
        }
    }
}
