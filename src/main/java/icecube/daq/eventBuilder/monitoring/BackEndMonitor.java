package icecube.daq.eventBuilder.monitoring;

/**
 * Interface between backend and monitoring MBean.
 */
public interface BackEndMonitor
{
    /**
     * Get most recent splicer.execute() list length for this run.
     *
     * @return most recent splicer.execute() list length
     */
    long getCurrentExecuteListLength();

    /**
     * Returns the number of units still available in the disk (measured
     * in MB).  If it fails to check the disk space, then it returns -1.
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
     * Returns the number of bytes written to disk by the event builder
     *
     * @return the number of bytes written to disk by the event builder
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
     * @return number of events delivered to daq-dispatch for this run
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
     * @return number of readouts to be included in the event being built
     */
    int getNumReadoutsCached();

    /**
     * Get number of readouts queued for processing.
     *
     * @return number of readouts queued
     */
    int getNumReadoutsQueued();

    /**
     * Get the number of readouts received from the string processors
     * for this run.
     *
     * @return number of readouts received from the string processors
     * for this run
     */
    long getNumReadoutsReceived();

    /**
     * Get number of trigger requests queued for the back end.
     *
     * @return number of trigger requests queued for the back end
     */
    int getNumTriggerRequestsQueued();

    /**
     * Get number of trigger requests received from the global trigger
     * for this run.
     *
     * @return number of trigger requests received for this run
     */
    long getNumTriggerRequestsReceived();

    /**
     * Get the total number of events from the previous run.
     *
     * @return total number of events sent during the previous run
     */
    long getPreviousRunTotalEvents();

    /**
     * Get the current subrun number.
     *
     * @return current subrun number
     */
    int getSubrunNumber();

    /**
     * Get the total number of readouts received from the string processors
     * since the program began executing.
     *
     * @return total number of readouts received from the string processors
     */
    long getTotalReadoutsReceived();
}
