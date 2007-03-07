package icecube.daq.eventBuilder.monitoring;

/**
 * Interface between global trigger input engine and monitoring MBean.
 */
public interface GlobalTriggerInputMonitor
{
    /**
     * Get total number of stop messages received from the global trigger.
     *
     * @return total number of stop messages received from the global trigger
     */
    long getTotalGlobalTrigStopsReceived();
}
