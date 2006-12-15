package icecube.daq.eventBuilder.monitoring;

/**
 * MBean wrapper for monitored data objects.
 */
public class MonitoringMBean
{
    /** Monitoring data. */
    private MonitoringData data;

    /**
     * MBean wrapper for monitored data objects.
     *
     * @param data monitored data object
     */
    public MonitoringMBean(MonitoringData data)
    {
        this.data = data;
    }

    /**
     * Get average millisecond time to dispatch event for this run.
     *
     * @return average dispatch time
     */
    public long getAverageDispatchTime()
    {
        if (data == null) {
            return 0;
        }

        return data.getAverageDispatchTime();
    }

    /**
     * Get average number of readouts per event.
     *
     * @return average readouts per event
     */
    public long getAverageReadoutsPerEvent()
    {
        if (data == null) {
            return 0;
        }

        return data.getAverageReadoutsPerEvent();
    }

    /**
     * Get back-end state.
     *
     * @return back end state
     */
    public String getBackEndState()
    {
        if (data == null) {
            return "<NO DATA>";
        }

        return data.getBackEndState();
    }

    /**
     * Get back-end timing profile.
     *
     * @return back end timing
     */
    public String getBackEndTiming()
    {
        if (data == null) {
            return "<NO DATA>";
        }

        return data.getBackEndTiming();
    }

    /**
     * Get most recent millisecond time to dispatch event for this run.
     *
     * @return current dispatch time
     */
    public long getCurrentDispatchTime()
    {
        if (data == null) {
            return 0;
        }

        return data.getCurrentDispatchTime();
    }

    /**
     * Get the end time for the event being built.
     *
     * @return current event end time
     */
    public long getCurrentEventEndTime()
    {
        if (data == null) {
            return 0;
        }

        return data.getCurrentEventEndTime();
    }

    /**
     * Get the start time for the event being built.
     *
     * @return current event start time
     */
    public long getCurrentEventStartTime()
    {
        if (data == null) {
            return 0;
        }

        return data.getCurrentEventStartTime();
    }

    /**
     * Get most recent splicer.execute() list length for this run.
     *
     * @return current execute list length
     */
    public long getCurrentExecuteListLength()
    {
        if (data == null) {
            return 0;
        }

        return data.getCurrentExecuteListLength();
    }

    /**
     * Get current rate of events per second.
     *
     * @return events per second
     */
    public double getEventsPerSecond()
    {
        if (data == null) {
            return 0.0;
        }

        return data.getEventsPerSecond();
    }

    /**
     * Get maximum millisecond time to dispatch event for this run.
     *
     * @return maximum dispatch time
     */
    public long getMaximumDispatchTime()
    {
        if (data == null) {
            return 0;
        }

        return data.getMaximumDispatchTime();
    }

    /**
     * Get maximum splicer.execute() list length for this run.
     *
     * @return maximum execute list length
     */
    public long getMaximumExecuteListLength()
    {
        if (data == null) {
            return 0;
        }

        return data.getMaximumExecuteListLength();
    }

    /**
     * Get memory statistics.
     *
     * @return memory statistics
     */
    public String getMemoryStatistics()
    {
        if (data == null) {
            return "<NO DATA>";
        }

        return data.getMemoryStatistics();
    }

    /**
     * Get number of readouts which could not be loaded.
     *
     * @return num bad readouts
     */
    public long getNumBadReadouts()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumBadReadouts();
    }

    /**
     * Number of trigger requests which could not be loaded.
     *
     * @return num bad trigger requests
     */
    public long getNumBadTriggerRequests()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumBadTriggerRequests();
    }

    /**
     * Get the number of dispatch times accumulated for this run.
     *
     * @return num dispatch times
     */
    public long getNumDispatchTimes()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumDispatchTimes();
    }

    /**
     * Get number of passes through the main loop without a trigger request.
     *
     * @return num empty loops
     */
    public long getNumEmptyLoops()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumEmptyLoops();
    }

    /**
     * Get the number of events which could not be delivered for this run.
     *
     * @return num events failed
     */
    public long getNumEventsFailed()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumEventsFailed();
    }

    /**
     * Get number of empty events which were ignored.
     *
     * @return num events ignored
     */
    public long getNumEventsIgnored()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumEventsIgnored();
    }

    /**
     * Get the number of events delivered to daq-dispatch for this run.
     *
     * @return num events sent
     */
    public long getNumEventsSent()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumEventsSent();
    }

    /**
     * Get number of calls to SPDataAnalysis.execute().
     *
     * @return num execute calls
     */
    public int getNumExecuteCalls()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumExecuteCalls();
    }

    /**
     * Get number of events which could not be created since last reset.
     *
     * @return num null events
     */
    public long getNumNullEvents()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumNullEvents();
    }

    /**
     * Get number of null readouts received since last reset.
     *
     * @return num null readouts
     */
    public long getNumNullReadouts()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumNullReadouts();
    }

    /**
     * Get the number of readouts to be included in the event being built.
     *
     * @return num readouts cached
     */
    public int getNumReadoutsCached()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumReadoutsCached();
    }

    /**
     * Get the number of readouts not included in any event for this run.
     *
     * @return num readouts discarded
     */
    public long getNumReadoutsDiscarded()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumReadoutsDiscarded();
    }

    /**
     * Get number of readouts queued for processing.
     *
     * @return num readouts queued
     */
    public int getNumReadoutsQueued()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumReadoutsQueued();
    }

    /**
     * Get the number of readouts received from the string processors for this
     * run.
     *
     * @return num readouts received
     */
    public long getNumReadoutsReceived()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumReadoutsReceived();
    }

    /**
     * Get number of recycled payloads.
     *
     * @return num recycled
     */
    public long getNumRecycled()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumRecycled();
    }

    /**
     * Number of trigger requests dropped while stopping.
     *
     * @return num trigger requests dropped
     */
    public long getNumTriggerRequestsDropped()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumTriggerRequestsDropped();
    }

    /**
     * Get number of trigger requests queued for the back end.
     *
     * @return num trigger requests queued
     */
    public int getNumTriggerRequestsQueued()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumTriggerRequestsQueued();
    }

    /**
     * Get number of trigger requests received from the global trigger for this
     * run.
     *
     * @return num trigger requests received
     */
    public long getNumTriggerRequestsReceived()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumTriggerRequestsReceived();
    }

    /**
     * Get number of calls to SPDataAnalysis.truncate().
     *
     * @return num truncate calls
     */
    public int getNumTruncateCalls()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumTruncateCalls();
    }

    /**
     * Get number of readouts not used for an event since last reset.
     *
     * @return num unused readouts
     */
    public long getNumUnusedReadouts()
    {
        if (data == null) {
            return 0;
        }

        return data.getNumUnusedReadouts();
    }

    /**
     * Get the total number of events from the previous run.
     *
     * @return previous run total events
     */
    public long getPreviousRunTotalEvents()
    {
        if (data == null) {
            return 0;
        }

        return data.getPreviousRunTotalEvents();
    }

    /**
     * Get current rate of readouts per second.
     *
     * @return readouts per second
     */
    public double getReadoutsPerSecond()
    {
        if (data == null) {
            return 0.0;
        }

        return data.getReadoutsPerSecond();
    }

    /**
     * Get size of event at maximum millisecond time for this run.
     *
     * @return size of maximum dispatch time
     */
    public long getSizeOfMaximumDispatchTime()
    {
        if (data == null) {
            return 0;
        }

        return data.getSizeOfMaximumDispatchTime();
    }

    /**
     * Get total number of readouts which could not be loaded since last reset.
     *
     * @return total bad readouts
     */
    public long getTotalBadReadouts()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalBadReadouts();
    }

    /**
     * Get the total dispatch time accumulated for this run.
     *
     * @return total dispatch time
     */
    public long getTotalDispatchTime()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalDispatchTime();
    }

    /**
     * Get the total number of events which could not be delivered since the
     * program began executing.
     *
     * @return total events failed
     */
    public long getTotalEventsFailed()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalEventsFailed();
    }

    /**
     * Total number of empty events which were ignored since last reset.
     *
     * @return total events ignored
     */
    public long getTotalEventsIgnored()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalEventsIgnored();
    }

    /**
     * Get the total number of events delivered to daq-dispatch since the
     * program began executing.
     *
     * @return total events sent
     */
    public long getTotalEventsSent()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalEventsSent();
    }

    /**
     * Get total number of stop messages received from the global trigger.
     *
     * @return total global trig stops received
     */
    public long getTotalGlobalTrigStopsReceived()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalGlobalTrigStopsReceived();
    }

    /**
     * Get the total number of readouts not included in any event since the
     * program began executing.
     *
     * @return total readouts discarded
     */
    public long getTotalReadoutsDiscarded()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalReadoutsDiscarded();
    }

    /**
     * Get the total number of readouts received from the string processors
     * since the program began executing.
     *
     * @return total readouts received
     */
    public long getTotalReadoutsReceived()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalReadoutsReceived();
    }

    /**
     * Get total number of stop messages received from the splicer.
     *
     * @return total splicer stops received
     */
    public long getTotalSplicerStopsReceived()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalSplicerStopsReceived();
    }

    /**
     * Get total number of stop messages sent to the string processor cache
     * output engine.
     *
     * @return total stops sent
     */
    public long getTotalStopsSent()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalStopsSent();
    }

    /**
     * Get total number of trigger requests received from the global trigger
     * since the program began executing.
     *
     * @return total trigger requests received
     */
    public long getTotalTriggerRequestsReceived()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalTriggerRequestsReceived();
    }

    /**
     * Get total number of stop messages received from the global trigger input
     * engine.
     *
     * @return total trigger stops received
     */
    public long getTotalTriggerStopsReceived()
    {
        if (data == null) {
            return 0;
        }

        return data.getTotalTriggerStopsReceived();
    }

    /**
     * Get current rate of trigger requests per second.
     *
     * @return trigger requests per second
     */
    public double getTriggerRequestsPerSecond()
    {
        if (data == null) {
            return 0.0;
        }

        return data.getTriggerRequestsPerSecond();
    }

    /**
     * Set Set monitoring data object. data object.
     *
     * @param mon monitoring data object
     */
    public void setData(MonitoringData mon)
    {
        data = mon;
    }
}
