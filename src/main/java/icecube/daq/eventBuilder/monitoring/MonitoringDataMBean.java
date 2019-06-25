package icecube.daq.eventBuilder.monitoring;

/**
 * MBean interface for monitored back-end data.
 */
public interface MonitoringDataMBean
{
    /**
     * Get most recent splicer.execute() list length for this run.
     *
     * @return current execute list length
     */
    long getCurrentExecuteListLength();

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
     * Return the first event time.
     *
     * @return first event time
     */
    long getFirstEventTime();

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
     * Returns the number of bytes written to disk
     *
     * @return the total number of bytes written to disk
     */
    long getNumBytesWritten();

    /**
     * Get the number of events written to disk by the dispatcher.
     *
     * @return num events dispatched
     */
    long getNumEventsDispatched();

    /**
     * Get the number of events delivered to daq-dispatch for this run.
     *
     * @return num events sent
     */
    long getNumEventsSent();

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
     * Get the current subrun number.
     *
     * @return current subrun number
     */
    int getSubrunNumber();
}
