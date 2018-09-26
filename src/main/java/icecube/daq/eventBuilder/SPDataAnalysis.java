package icecube.daq.eventBuilder;

import icecube.daq.eventBuilder.backend.SPDataProcessor;
import icecube.daq.splicer.Spliceable;
import icecube.daq.splicer.SpliceableFactory;
import icecube.daq.splicer.SplicedAnalysis;
import icecube.daq.splicer.Splicer;
import icecube.daq.splicer.SplicerChangedEvent;
import icecube.daq.splicer.SplicerListener;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Splicer analysis of string processor data.
 */
public class SPDataAnalysis
    implements SplicedAnalysis<Spliceable>, SplicerListener<Spliceable>
{
    private static final Log LOG = LogFactory.getLog(SPDataAnalysis.class);

    /** Interface for event builder back end. */
    private SPDataProcessor dataProc;

    /**
     * Create splicer analysis.
     */
    public SPDataAnalysis()
    {
    }

    /**
     * Called by the {@link Splicer Splicer} to analyze the
     * List of Spliceable objects provided.
     *
     * @param list a List of Spliceable objects.
     */
    public void analyze(List list)
    {
        dataProc.addExecuteCall();
        dataProc.setExecuteListLength(list.size());

        try {
            dataProc.addData(list, 0);
        } catch (IOException ioe) {
            LOG.error("Could not add data (len=" + list.size() + ")", ioe);
        }
    }

    /**
     * Called when the {@link Splicer Splicer} enters the disposed state.
     *
     * @param event the event encapsulating this state change.
     */
    public void disposed(SplicerChangedEvent<Spliceable> event)
    {
        // ignored
    }

    /**
     * Called when the {@link Splicer Splicer} enters the failed state.
     *
     * @param event the event encapsulating this state change.
     */
    public void failed(SplicerChangedEvent<Spliceable> event)
    {
        // ignored
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
     * Called when the {@link Splicer Splicer} enters the started state.
     *
     * @param event the event encapsulating this state change.
     */
    public void started(SplicerChangedEvent<Spliceable> event)
    {
        LOG.info("Splicer entered STARTED state");
    }

    /**
     * Called when the {@link Splicer Splicer} enters the starting state.
     *
     * @param event the event encapsulating this state change.
     */
    public void starting(SplicerChangedEvent<Spliceable> event)
    {
        dataProc.startDispatcher();
    }

    /**
     * Called when the {@link Splicer Splicer} enters the stopped state.
     *
     * @param event the event encapsulating this state change.
     */
    public void stopped(SplicerChangedEvent<Spliceable> event)
    {
        dataProc.splicerStopped();
        LOG.info("Splicer entered STOPPED state");
    }

    /**
     * Called when the {@link Splicer Splicer} enters the stopping state.
     *
     * @param event the event encapsulating this state change.
     */
    public void stopping(SplicerChangedEvent<Spliceable> event)
    {
        LOG.info("Splicer entered STOPPING state");
    }
}
