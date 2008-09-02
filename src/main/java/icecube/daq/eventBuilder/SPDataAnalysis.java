package icecube.daq.eventBuilder;

import icecube.daq.common.DAQCmdInterface;
import icecube.daq.eventBuilder.backend.SPDataProcessor;
import icecube.daq.io.DispatchException;
import icecube.daq.splicer.Spliceable;
import icecube.daq.splicer.SpliceableFactory;
import icecube.daq.splicer.SplicedAnalysis;
import icecube.daq.splicer.Splicer;
import icecube.daq.splicer.SplicerChangedEvent;
import icecube.daq.splicer.SplicerListener;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Splicer analysis of string processor data.
 */
public class SPDataAnalysis
    implements SplicedAnalysis, SplicerListener
{
    private static final Log LOG = LogFactory.getLog(SPDataAnalysis.class);

    /** Factory used to build spliceable objects. */
    private SpliceableFactory factory;
    /** Interface for event builder back end. */
    private SPDataProcessor dataProc;
    /** Track progress through splicer data. */
    private int listOffset;

    /** Current run number. */
    private int runNumber;
    /** Have we reported a bad run number yet? */
    private boolean reportedBadRunNumber;

    /**
     * Create splicer analysis.
     *
     * @param factory spliceable factory
     */
    public SPDataAnalysis(SpliceableFactory factory)
    {
        this.factory = factory;
    }

    /**
     * Called when the {@link Splicer Splicer} enters the disposed state.
     *
     * @param event the event encapsulating this state change.
     */
    public void disposed(SplicerChangedEvent event)
    {
        // ignored
    }

    /**
     * Called by the {@link Splicer Splicer} to analyze the
     * List of Spliceable objects provided.
     *
     * @param list a List of Spliceable objects.
     * @param decrement the number of items deleted from the front of the list
     *                  since the last execute() call
     */
    public void execute(List list, int decrement)
    {
        final int listLen = list.size();

        dataProc.addExecuteCall();
        dataProc.setExecuteListLength(listLen);

        int addIndex = listOffset - decrement;
        if (listLen > addIndex) {
            dataProc.addData(list, addIndex);
        }

        listOffset = listLen;
    }

    /**
     * Called when the {@link Splicer Splicer} enters the failed state.
     *
     * @param event the event encapsulating this state change.
     */
    public void failed(SplicerChangedEvent event)
    {
        // ignored
    }

   /**
     * Returns the {@link SpliceableFactory} that should be used to create the
     * {@link Spliceable Spliceable} objects used by this
     * object.
     *
     * @return the SpliceableFactory that creates Spliceable objects.
     */
    public SpliceableFactory getFactory()
    {
        return factory;
    }

    /**
     * Set the string processor data handler.
     *
     * @param dataProc data processor
     */
    public void setDataProcessor(SPDataProcessor dataProc)
    {
        this.dataProc = dataProc;
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
     * Called when the {@link Splicer Splicer} enters the started state.
     *
     * @param event the event encapsulating this state change.
     */
    public void started(SplicerChangedEvent event)
    {
        LOG.info("Splicer entered STARTED state");
    }

    /**
     * Called when the {@link Splicer Splicer} enters the starting state.
     *
     * @param event the event encapsulating this state change.
     */
    public void starting(SplicerChangedEvent event)
    {
        if (runNumber < 0) {
            if (!reportedBadRunNumber) {
                LOG.error("Run number has not been set");
                reportedBadRunNumber = true;
            }
            return;
        }

        LOG.info("Splicer entered STARTING state");
        String message =
            DAQCmdInterface.DAQ_ONLINE_RUNSTART_FLAG + runNumber;
        try {
            dataProc.dataBoundary(message);
        } catch (DispatchException de) {
            LOG.error("Couldn't start dispatcher (" + message + ")", de);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("called dataBoundary on STARTING with the message: " +
                     message);
        }
    }

    /**
     * Called when the {@link Splicer Splicer} enters the stopped state.
     *
     * @param event the event encapsulating this state change.
     */
    public void stopped(SplicerChangedEvent event)
    {
        dataProc.splicerStopped();
        LOG.info("Splicer entered STOPPED state");
    }

    /**
     * Notify dispatcher that we've stopped.
     */
    public void stopDispatcher()
    {
        if (runNumber < 0) {
            LOG.error("Run number has not been set");
            return;
        }

        String message = DAQCmdInterface.DAQ_ONLINE_RUNSTOP_FLAG + runNumber;
        try {
            dataProc.dataBoundary(message);
        } catch (DispatchException de) {
            LOG.error("Couldn't stop dispatcher (" + message + ")", de);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("called dataBoundary on STOPPED with the message: " +
                     message);
        }

        runNumber = Integer.MIN_VALUE;
        reportedBadRunNumber = false;
    }

    /**
     * Called when the {@link Splicer Splicer} enters the stopping state.
     *
     * @param event the event encapsulating this state change.
     */
    public void stopping(SplicerChangedEvent event)
    {
        LOG.info("Splicer entered STOPPING state");
    }

    /**
     * Called when the {@link Splicer Splicer} has truncated its "rope". This
     * method is called whenever the "rope" is cut, for example to make a clean
     * start from the frayed beginning of a "rope" or cutting the rope when
     * reaching the Stopped state. This is not only invoked as the result of
     * the {@link Splicer#truncate(Spliceable)} method being invoked.
     * <p/>
     * This enables the client to be notified as to which Spliceable are never
     * going to be accessed again by the Splicer.
     * <p/>
     * When entering the Stopped state the
     * {@link SplicerChangedEvent#getSpliceable()}
     * method will return the {@link Splicer#LAST_POSSIBLE_SPLICEABLE} object.
     *
     * @param event the event encapsulating this truncation.
     */
    public void truncated(SplicerChangedEvent event)
    {
        dataProc.addTruncateCall();

        Spliceable spl = event.getSpliceable();
        if (spl == Splicer.LAST_POSSIBLE_SPLICEABLE) {
            // splicer is stopping; save these payloads until backend is done
            dataProc.addFinalData(event.getAllSpliceables());
        } else {
            dataProc.recycleAll(event.getAllSpliceables());
        }
    }
}
