package icecube.daq.eventBuilder;

import icecube.daq.eventBuilder.backend.SPDataProcessor;
import icecube.daq.splicer.Spliceable;
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
    implements SplicedAnalysis, SplicerListener
{
    private static final Log LOG = LogFactory.getLog(SPDataAnalysis.class);

    /** Interface for event builder back end. */
    private SPDataProcessor dataProc;
    /** Track progress through splicer data. */
    private int listOffset;

    /**
     * Create splicer analysis.
     */
    public SPDataAnalysis()
    {
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
            try {
                dataProc.addData(list, addIndex);
            } catch (IOException ioe) {
                LOG.error("Could not add data (len=" + list.size() + ", dec=" +
                          decrement + ")", ioe);
            }
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
        dataProc.startDispatcher();
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
