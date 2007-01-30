package icecube.daq.eventBuilder.monitoring;

/**
 * Wrapper for all monitored data objects.
 */
public class MonitoringData
    implements MonitoringDataMBean
{
    /** Back end monitoring. */
    private BackEndMonitor backEnd;
    /** Global trigger input monitoring. */
    private GlobalTriggerInputMonitor gtInput;

    /**
     * Wrapper for all monitored data objects.
     */
    public MonitoringData()
    {
    }

    /**
     * Get average millisecond time to dispatch event for this run.
     *
     * @return average dispatch time
     */
    public long getAverageDispatchTime()
    {
        if (backEnd == null) {
            return 0;
        }
        return backEnd.getAverageDispatchTime();
    }

    /**
     * Get average number of readouts per event.
     *
     * @return average readouts per event
     */
    public long getAverageReadoutsPerEvent()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getAverageReadoutsPerEvent();
    }

    /**
     * Get back-end state.
     *
     * @return back end state
     */
    public String getBackEndState()
    {
        if (backEnd == null) {
            return "<NO BACKEND>";
        }

        return backEnd.getBackEndState();
    }

    /**
     * Get back-end timing profile.
     *
     * @return back end timing
     */
    public String getBackEndTiming()
    {
        if (backEnd == null) {
            return "<NO BACKEND>";
        }

        return backEnd.getBackEndTiming();
    }

    /**
     * Get most recent millisecond time to dispatch event for this run.
     *
     * @return current dispatch time
     */
    public long getCurrentDispatchTime()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getCurrentDispatchTime();
    }

    /**
     * Get the end time for the event being built.
     *
     * @return current event end time
     */
    public long getCurrentEventEndTime()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getCurrentEventEndTime();
    }

    /**
     * Get the start time for the event being built.
     *
     * @return current event start time
     */
    public long getCurrentEventStartTime()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getCurrentEventStartTime();
    }

    /**
     * Get most recent splicer.execute() list length for this run.
     *
     * @return current execute list length
     */
    public long getCurrentExecuteListLength()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getCurrentExecuteListLength();
    }

    /**
     * Get current rate of events per second.
     *
     * @return events per second
     */
    public double getEventsPerSecond()
    {
        if (backEnd == null) {
            return 0.0;
        }

        return backEnd.getEventsPerSecond();
    }

    /**
     * Get maximum millisecond time to dispatch event for this run.
     *
     * @return maximum dispatch time
     */
    public long getMaximumDispatchTime()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getMaximumDispatchTime();
    }

    /**
     * Get maximum splicer.execute() list length for this run.
     *
     * @return maximum execute list length
     */
    public long getMaximumExecuteListLength()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getMaximumExecuteListLength();
    }

    /**
     * Get memory statistics.
     *
     * @return memory statistics
     */
    public String getMemoryStatistics()
    {
        if (backEnd == null) {
            return "<NO BACKEND>";
        }

        return backEnd.getMemoryStatistics();
    }

    /**
     * Get number of readouts which could not be loaded.
     *
     * @return num bad readouts
     */
    public long getNumBadReadouts()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumBadReadouts();
    }

    /**
     * Number of trigger requests which could not be loaded.
     *
     * @return num bad trigger requests
     */
    public long getNumBadTriggerRequests()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumBadTriggerRequests();
    }

    /**
     * Get the number of dispatch times accumulated for this run.
     *
     * @return num dispatch times
     */
    public long getNumDispatchTimes()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumDispatchTimes();
    }

    /**
     * Get number of passes through the main loop without a trigger request.
     *
     * @return num empty loops
     */
    public long getNumEmptyLoops()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumEmptyLoops();
    }

    /**
     * Get the number of events which could not be delivered for this run.
     *
     * @return num events failed
     */
    public long getNumEventsFailed()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumEventsFailed();
    }

    /**
     * Get number of empty events which were ignored.
     *
     * @return num events ignored
     */
    public long getNumEventsIgnored()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumEventsIgnored();
    }

    /**
     * Get the number of events delivered to daq-dispatch for this run.
     *
     * @return num events sent
     */
    public long getNumEventsSent()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumEventsSent();
    }

    /**
     * Get number of calls to SPDataAnalysis.execute().
     *
     * @return num execute calls
     */
    public int getNumExecuteCalls()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumExecuteCalls();
    }

    /**
     * Get number of events which could not be created since last reset.
     *
     * @return num null events
     */
    public long getNumNullEvents()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumNullEvents();
    }

    /**
     * Get number of null readouts received since last reset.
     *
     * @return num null readouts
     */
    public long getNumNullReadouts()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumNullReadouts();
    }

    /**
     * Get the number of readouts to be included in the event being built.
     *
     * @return num readouts cached
     */
    public int getNumReadoutsCached()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumReadoutsCached();
    }

    /**
     * Get the number of readouts not included in any event for this run.
     *
     * @return num readouts discarded
     */
    public long getNumReadoutsDiscarded()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumReadoutsDiscarded();
    }

    /**
     * Get number of readouts queued for processing.
     *
     * @return num readouts queued
     */
    public int getNumReadoutsQueued()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumReadoutsQueued();
    }

    /**
     * Get the number of readouts received from the string processors for this
     * run.
     *
     * @return num readouts received
     */
    public long getNumReadoutsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumReadoutsReceived();
    }

    /**
     * Get number of recycled payloads.
     *
     * @return num recycled
     */
    public long getNumRecycled()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumRecycled();
    }

    /**
     * Number of trigger requests dropped while stopping.
     *
     * @return num trigger requests dropped
     */
    public long getNumTriggerRequestsDropped()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumTriggerRequestsDropped();
    }

    /**
     * Get number of trigger requests queued for the back end.
     *
     * @return num trigger requests queued
     */
    public int getNumTriggerRequestsQueued()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumTriggerRequestsQueued();
    }

    /**
     * Get number of trigger requests received from the global trigger for this
     * run.
     *
     * @return num trigger requests received
     */
    public long getNumTriggerRequestsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumTriggerRequestsReceived();
    }

    /**
     * Get number of calls to SPDataAnalysis.truncate().
     *
     * @return num truncate calls
     */
    public int getNumTruncateCalls()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumTruncateCalls();
    }

    /**
     * Get number of readouts not used for an event since last reset.
     *
     * @return num unused readouts
     */
    public long getNumUnusedReadouts()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumUnusedReadouts();
    }

    /**
     * Get the total number of events from the previous run.
     *
     * @return previous run total events
     */
    public long getPreviousRunTotalEvents()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getPreviousRunTotalEvents();
    }

    /**
     * Get current rate of readouts per second.
     *
     * @return readouts per second
     */
    public double getReadoutsPerSecond()
    {
        if (backEnd == null) {
            return 0.0;
        }

        return backEnd.getReadoutsPerSecond();
    }

    /**
     * Get size of event at maximum millisecond time for this run.
     *
     * @return size of maximum dispatch time
     */
    public long getSizeOfMaximumDispatchTime()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getSizeOfMaximumDispatchTime();
    }

    /**
     * Get total number of readouts which could not be loaded since last reset.
     *
     * @return total bad readouts
     */
    public long getTotalBadReadouts()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalBadReadouts();
    }

    /**
     * Get the total dispatch time accumulated for this run.
     *
     * @return total dispatch time
     */
    public long getTotalDispatchTime()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalDispatchTime();
    }

    /**
     * Get the total number of events which could not be delivered since the
     * program began executing.
     *
     * @return total events failed
     */
    public long getTotalEventsFailed()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalEventsFailed();
    }

    /**
     * Total number of empty events which were ignored since last reset.
     *
     * @return total events ignored
     */
    public long getTotalEventsIgnored()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalEventsIgnored();
    }

    /**
     * Get the total number of events delivered to daq-dispatch since the
     * program began executing.
     *
     * @return total events sent
     */
    public long getTotalEventsSent()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalEventsSent();
    }

    /**
     * Get total number of stop messages received from the global trigger.
     *
     * @return total global trig stops received
     */
    public long getTotalGlobalTrigStopsReceived()
    {
        if (gtInput == null) {
            return 0;
        }

        return gtInput.getTotalGlobalTrigStopsReceived();
    }

    /**
     * Get the total number of readouts not included in any event since the
     * program began executing.
     *
     * @return total readouts discarded
     */
    public long getTotalReadoutsDiscarded()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalReadoutsDiscarded();
    }

    /**
     * Get the total number of readouts received from the string processors
     * since the program began executing.
     *
     * @return total readouts received
     */
    public long getTotalReadoutsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalReadoutsReceived();
    }

    /**
     * Get total number of stop messages received from the splicer.
     *
     * @return total splicer stops received
     */
    public long getTotalSplicerStopsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalSplicerStopsReceived();
    }

    /**
     * Get total number of stop messages sent to the string processor cache
     * output engine.
     *
     * @return total stops sent
     */
    public long getTotalStopsSent()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalStopsSent();
    }

    /**
     * Get total number of trigger requests received from the global trigger
     * since the program began executing.
     *
     * @return total trigger requests received
     */
    public long getTotalTriggerRequestsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalTriggerRequestsReceived();
    }

    /**
     * Get total number of stop messages received from the global trigger input
     * engine.
     *
     * @return total trigger stops received
     */
    public long getTotalTriggerStopsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getTotalTriggerStopsReceived();
    }

    /**
     * Get current rate of trigger requests per second.
     *
     * @return trigger requests per second
     */
    public double getTriggerRequestsPerSecond()
    {
        if (backEnd == null) {
            return 0.0;
        }

        return backEnd.getTriggerRequestsPerSecond();
    }

    /**
     * Set back end monitoring data object.
     *
     * @param mon monitoring data object
     */
    public void setBackEndMonitor(BackEndMonitor mon)
    {
        backEnd = mon;
    }

    /**
     * Set global trigger input monitoring data object.
     *
     * @param mon monitoring data object
     */
    public void setGlobalTriggerInputMonitor(GlobalTriggerInputMonitor mon)
    {
        gtInput = mon;
    }

    /**
     * Returns the number of units still available in the disk (measured in MB).
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the number of units still available in the disk.
     */
    public int getDiskAvailable(){
        if (backEnd == null) {
            return -1;
        }
        return backEnd.getDiskAvailable();
    }

    /**
     * Returns the total number of units in the disk (measured in MB).
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the total number of units in the disk.
     */
    public int getDiskSize(){
        if (backEnd == null) {
            return -1;
        }
        return backEnd.getDiskSize();
    }

    /**
     * String representation of monitoring data.
     *
     * @return monitored data
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("MonitoringData:");

        if (backEnd == null) {
            buf.append("\n  No backEnd monitoring data available");
        } else {
            buf.append("\n  averageDispatchTime ").
                append(getAverageDispatchTime());
            buf.append("\n  averageReadoutsPerEvt ").
                append(getAverageReadoutsPerEvent());
            buf.append("\n  backEndState ").append(getBackEndState());
            buf.append("\n  backEndTiming ").append(getBackEndTiming());
            buf.append("\n  curDispatchTime ").
                append(getCurrentDispatchTime());
            buf.append("\n  curEvtEndTime ").append(getCurrentEventEndTime());
            buf.append("\n  curEvtStartTime ").
                append(getCurrentEventStartTime());
            buf.append("\n  curExecuteListLength ").
                append(getCurrentExecuteListLength());
            buf.append("\n  evtsPerSecond ").append(getEventsPerSecond());
            buf.append("\n  maxDispatchTime ").
                append(getMaximumDispatchTime());
            buf.append("\n  maxExecuteListLength ").
                append(getMaximumExecuteListLength());
            buf.append("\n  memoryStatistics ").append(getMemoryStatistics());
            buf.append("\n  numBadReadouts ").append(getNumBadReadouts());
            buf.append("\n  numBadTRs ").append(getNumBadTriggerRequests());
            buf.append("\n  numDispatchTimes ").append(getNumDispatchTimes());
            buf.append("\n  numEmptyLoops ").append(getNumEmptyLoops());
            buf.append("\n  numEvtsFailed ").append(getNumEventsFailed());
            buf.append("\n  numEvtsIgnored ").append(getNumEventsIgnored());
            buf.append("\n  numEvtsSent ").append(getNumEventsSent());
            buf.append("\n  numExecuteCalls ").append(getNumExecuteCalls());
            buf.append("\n  numNullEvts ").append(getNumNullEvents());
            buf.append("\n  numNullReadouts ").append(getNumNullReadouts());
            buf.append("\n  numReadoutsCached ").
                append(getNumReadoutsCached());
            buf.append("\n  numReadoutsDiscarded ").
                append(getNumReadoutsDiscarded());
            buf.append("\n  numReadoutsQueued ").
                append(getNumReadoutsQueued());
            buf.append("\n  numReadoutsRcvd ").
                append(getNumReadoutsReceived());
            buf.append("\n  numRecycled ").append(getNumRecycled());
            buf.append("\n  numTRsDropped ").
                append(getNumTriggerRequestsDropped());
            buf.append("\n  numTRsQueued ").
                append(getNumTriggerRequestsQueued());
            buf.append("\n  numTRsRcvd ").
                append(getNumTriggerRequestsReceived());
            buf.append("\n  numTruncateCalls ").append(getNumTruncateCalls());
            buf.append("\n  numUnusedReadouts ").
                append(getNumUnusedReadouts());
            buf.append("\n  previousRunTotalEvts ").
                append(getPreviousRunTotalEvents());
            buf.append("\n  readoutsPerSecond ").
                append(getReadoutsPerSecond());
            buf.append("\n  sizeOfMaximumDispatchTime ").
                append(getSizeOfMaximumDispatchTime());
            buf.append("\n  totBadReadouts ").append(getTotalBadReadouts());
            buf.append("\n  totDispatchTime ").append(getTotalDispatchTime());
            buf.append("\n  totEvtsFailed ").append(getTotalEventsFailed());
            buf.append("\n  totEvtsIgnored ").append(getTotalEventsIgnored());
            buf.append("\n  totEvtsSent ").append(getTotalEventsSent());
            buf.append("\n  totReadoutsDiscarded ").
                append(getTotalReadoutsDiscarded());
            buf.append("\n  totReadoutsRcvd ").
                append(getTotalReadoutsReceived());
            buf.append("\n  totSplicerStopsRcvd ").
                append(getTotalSplicerStopsReceived());
            buf.append("\n  totStopsSent ").append(getTotalStopsSent());
            buf.append("\n  totTRsRcvd ").
                append(getTotalTriggerRequestsReceived());
            buf.append("\n  totTriggerStopsRcvd ").
                append(getTotalTriggerStopsReceived());
            buf.append("\n  tRsPerSecond ").
                append(getTriggerRequestsPerSecond());
            buf.append("\n  getDiskAvailable ").
                append(getDiskAvailable());
            buf.append("\n  getDiskSize ").
                append(getDiskSize());
        }
        if (gtInput == null) {
            buf.append("\n  No gtInput monitoring data available");
        } else {
            buf.append("\n  totGlobalTrigStopsRcvd ").
                append(getTotalGlobalTrigStopsReceived());
        }

        return buf.toString();
    }
}
