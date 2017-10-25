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

    /**
     * Wrapper for all monitored data objects.
     */
    public MonitoringData()
    {
    }

    /**
     * Get most recent splicer.execute() list length for this run.
     *
     * @return current execute list length
     */
    @Override
    public long getCurrentExecuteListLength()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getCurrentExecuteListLength();
    }

    /**
     * Returns the number of units still available in the disk (measured in MB)
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the number of units still available in the disk.
     */
    @Override
    public long getDiskAvailable()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getDiskAvailable();
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
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getDiskSize();
    }

    /**
     * Return the number of events and the last event time as a list.
     *
     * @return event data
     */
    @Override
    public long[] getEventData()
    {
        if (backEnd == null) {
            return new long[] {-1L, -1L };
        }

        return backEnd.getEventData();
    }

    /**
     * Return the first event time.
     *
     * @return first event time
     */
    @Override
    public long getFirstEventTime()
    {
        if (backEnd == null) {
            return -1L;
        }

        return backEnd.getFirstEventTime();
    }

    /**
     * Get the amount of time the current event spent in the system.
     *
     * @return current latency
     */
    @Override
    public double getLatency()
    {
        if (backEnd == null) {
            return Long.MAX_VALUE;
        }

        return backEnd.getLatency();
    }

    /**
     * Get number of bad events for this run.
     *
     * @return number of bad events
     */
    @Override
    public long getNumBadEvents()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumBadEvents();
    }

    /**
     * Returns the number of bytes written to disk by the event builder
     *
     * @return the number of bytes written to disk by the event builder
     */
    @Override
    public long getNumBytesWritten()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumBytesWritten();
    }

    /**
     * Get the number of events written to disk by the dispatcher.
     *
     * @return num events dispatched
     */
    @Override
    public long getNumEventsDispatched()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumEventsDispatched();
    }

    /**
     * Get the number of events delivered to daq-dispatch for this run.
     *
     * @return num events sent
     */
    @Override
    public long getNumEventsSent()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumEventsSent();
    }

    /**
     * Get number of events queued for output.
     *
     * @return number of events queued
     */
    @Override
    public int getNumOutputsQueued()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumOutputsQueued();
    }

    /**
     * Get the number of readouts to be included in the event being built.
     *
     * @return num readouts cached
     */
    @Override
    public int getNumReadoutsCached()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumReadoutsCached();
    }

    /**
     * Get number of readouts queued for processing.
     *
     * @return num readouts queued
     */
    @Override
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
    @Override
    public long getNumReadoutsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumReadoutsReceived();
    }

    /**
     * Get number of trigger requests queued for the back end.
     *
     * @return num trigger requests queued
     */
    @Override
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
    @Override
    public long getNumTriggerRequestsReceived()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getNumTriggerRequestsReceived();
    }

    /**
     * Get the total number of events from the previous run.
     *
     * @return previous run total events
     */
    @Override
    public long getPreviousRunTotalEvents()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getPreviousRunTotalEvents();
    }

    /**
     * Get the current subrun number.
     *
     * @return current subrun number
     */
    @Override
    public int getSubrunNumber()
    {
        if (backEnd == null) {
            return 0;
        }

        return backEnd.getSubrunNumber();
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
     * String representation of monitoring data.
     *
     * @return monitored data
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder("MonitoringData:");

        if (backEnd == null) {
            buf.append("\n  No backEnd monitoring data available");
        } else {
            buf.append("\n  curExecuteListLength ").
                append(getCurrentExecuteListLength());
            buf.append("\n  diskAvailable ").
                append(getDiskAvailable());
            buf.append("\n  diskSize ").
                append(getDiskSize());
            buf.append("\n  numEvtsSent ").append(getNumEventsSent());
            buf.append("\n  numReadoutsCached ").
                append(getNumReadoutsCached());
            buf.append("\n  numReadoutsQueued ").
                append(getNumReadoutsQueued());
            buf.append("\n  numReadoutsRcvd ").
                append(getNumReadoutsReceived());
            buf.append("\n  numTRsQueued ").
                append(getNumTriggerRequestsQueued());
            buf.append("\n  numTRsRcvd ").
                append(getNumTriggerRequestsReceived());
            buf.append("\n  previousRunTotalEvts ").
                append(getPreviousRunTotalEvents());
        }

        return buf.toString();
    }
}
