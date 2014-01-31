package icecube.daq.eventBuilder.monitoring;

/**
 * MBean interface for monitored back-end data.
 */
public interface MonitoringDataMBean
{
    /**
     * Get average number of readouts per event.
     *
     * @return average readouts per event
     */
    //long getAverageReadoutsPerEvent();

    /**
     * Get most recent splicer.execute() list length for this run.
     *
     * @return current execute list length
     */
    long getCurrentExecuteListLength();

    String getDebugMsg();

    /**
     * Returns the number of units still available in the disk (measured in MB).
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the number of units still available in the disk.
     */
    long getDiskAvailable();

    /**
     * Returns the total number of units in the disk (measured in MB).
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the total number of units in the disk.
     */
    long getDiskSize();

    /**
     * Return the number of events and the last event time as a list.
     *
     * @return event data
     */
    long[] getEventData();

    /**
     * Get current rate of events per second.
     *
     * @return events per second
     */
    //double getEventsPerSecond();

    /**
     * Return the first event time.
     *
     * @return first event time
     */
    long getFirstEventTime();

    /**
     * Get internal state.
     *
     * @return internal state
     */
    //String getInternalState();

    /**
     * Get internal timing profile.
     *
     * @return internal timing
     */
    //String getInternalTiming();

    /**
     * Get the amount of time the current event spent in the system.
     *
     * @return current latency
     */
    double getLatency();

    /**
     * Get number of bad events for this run.
     *
     * @return number of bad events
     */
    long getNumBadEvents();

    /**
     * Get number of readouts which could not be loaded.
     *
     * @return num bad readouts
     */
    //long getNumBadReadouts();

    /**
     * Number of trigger requests which could not be loaded.
     *
     * @return num bad trigger requests
     */
    //long getNumBadTriggerRequests();

    /**
     * Returns the number of bytes written to disk
     *
     * @return the total number of bytes written to disk
     */
    long getNumBytesWritten();

    /**
     * Get number of passes through the main loop without a trigger request.
     *
     * @return num empty loops
     */
    //long getNumEmptyLoops();

    /**
     * Get the number of events written to disk by the dispatcher.
     *
     * @return num events dispatched
     */
    long getNumEventsDispatched();

    /**
     * Get the number of events which could not be delivered for this run.
     *
     * @return num events failed
     */
    //long getNumEventsFailed();

    /**
     * Get number of empty events which were ignored.
     *
     * @return num events ignored
     */
    //long getNumEventsIgnored();

    /**
     * Get the number of events delivered to daq-dispatch for this run.
     *
     * @return num events sent
     */
    long getNumEventsSent();

    /**
     * Get number of events which could not be created since last reset.
     *
     * @return num null events
     */
    //long getNumNullEvents();

    /**
     * Get number of null readouts received since last reset.
     *
     * @return num null readouts
     */
    //long getNumNullReadouts();

    /**
     * Get number of events queued for output.
     *
     * @return number of events queued
     */
    int getNumOutputsQueued();

    /**
     * Get the number of readouts to be included in the event being built.
     *
     * @return num readouts cached
     */
    int getNumReadoutsCached();

    /**
     * Get the number of readouts not included in any event for this run.
     *
     * @return num readouts discarded
     */
    //long getNumReadoutsDiscarded();

    /**
     * Get number of readouts dropped while stopping.
     *
     * @return num readouts dropped
     */
    //long getNumReadoutsDropped();

    /**
     * Get number of readouts queued for processing.
     *
     * @return num readouts queued
     */
    int getNumReadoutsQueued();

    /**
     * Get the number of readouts received from the string processors for this
     * run.
     *
     * @return num readouts received
     */
    long getNumReadoutsReceived();

    /**
     * Number of trigger requests dropped while stopping.
     *
     * @return num trigger requests dropped
     */
    //long getNumTriggerRequestsDropped();

    /**
     * Get number of trigger requests queued for the back end.
     *
     * @return num trigger requests queued
     */
    int getNumTriggerRequestsQueued();

    /**
     * Get number of trigger requests received from the global trigger for this
     * run.
     *
     * @return num trigger requests received
     */
    long getNumTriggerRequestsReceived();

    /**
     * Get the total number of events from the previous run.
     *
     * @return previous run total events
     */
    long getPreviousRunTotalEvents();

    /**
     * Get current rate of readouts per second.
     *
     * @return readouts per second
     */
    //double getReadoutsPerSecond();

    /**
     * Get the current subrun number.
     *
     * @return current subrun number
     */
    int getSubrunNumber();

    /**
     * Get total number of readouts which could not be loaded since last reset.
     *
     * @return total bad readouts
     */
    //long getTotalBadReadouts();

    /**
     * Get the total number of events which could not be delivered since the
     * program began executing.
     *
     * @return total events failed
     */
    //long getTotalEventsFailed();

    /**
     * Total number of empty events which were ignored since last reset.
     *
     * @return total events ignored
     */
    //long getTotalEventsIgnored();

    /**
     * Get the total number of events delivered to daq-dispatch since the
     * program began executing.
     *
     * @return total events sent
     */
    //long getTotalEventsSent();

    /**
     * Get total number of stop messages received from the global trigger.
     *
     * @return total global trig stops received
     */
    //long getTotalGlobalTrigStopsReceived();

    /**
     * Get the total number of readouts not included in any event since the
     * program began executing.
     *
     * @return total readouts discarded
     */
    //long getTotalReadoutsDiscarded();

    /**
     * Get the total number of readouts received from the string processors
     * since the program began executing.
     *
     * @return total readouts received
     */
    //long getTotalReadoutsReceived();

    /**
     * Get total number of stop messages received from the splicer.
     *
     * @return total splicer stops received
     */
    //long getTotalSplicerStopsReceived();

    /**
     * Get total number of stop messages sent to the string processor cache
     * output engine.
     *
     * @return total stops sent
     */
    //long getTotalStopsSent();

    /**
     * Get total number of trigger requests received from the global trigger
     * since the program began executing.
     *
     * @return total trigger requests received
     */
    //long getTotalTriggerRequestsReceived();

    /**
     * Get total number of stop messages received from the global trigger input
     * engine.
     *
     * @return total trigger stops received
     */
    //long getTotalTriggerStopsReceived();

    /**
     * Get current rate of trigger requests per second.
     *
     * @return trigger requests per second
     */
    //double getTriggerRequestsPerSecond();
}
