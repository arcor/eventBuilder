package icecube.daq.eventBuilder;

import icecube.daq.common.DAQCmdInterface;

import icecube.daq.io.Dispatcher;
import icecube.daq.io.FileDispatcher;

import icecube.daq.eventBuilder.backend.EventBuilderBackEnd;

import icecube.daq.eventBuilder.monitoring.MonitoringData;

import icecube.daq.io.PayloadOutputEngine;
import icecube.daq.io.PayloadTransmitChannel;
import icecube.daq.io.PushPayloadInputEngine;

import icecube.daq.juggler.component.DAQCompServer;
import icecube.daq.juggler.component.DAQCompException;
import icecube.daq.juggler.component.DAQComponent;
import icecube.daq.juggler.component.DAQConnector;

import icecube.daq.juggler.mbean.MemoryStatistics;

import icecube.daq.payload.ByteBufferCache;
import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.MasterPayloadFactory;

import icecube.daq.splicer.Splicer;
import icecube.daq.splicer.SplicerImpl;

import java.io.IOException;

import java.nio.ByteBuffer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

/**
 * Payload pass-through component.
 */
public class EBComponent
    extends DAQComponent
{
    /** Component name. */
    private static final String COMPONENT_NAME =
        DAQCmdInterface.DAQ_EVENTBUILDER;

    /** master payload factory. */
    private MasterPayloadFactory masterFactory;

    private EventBuilderGlobalTrigPayloadInputEngine gtInputProcess;
    private EventBuilderSPdataPayloadInputEngine spDataInputProcess;

    private EventBuilderSPreqPayloadOutputEngine spReqOutputProcess;
    private EventBuilderSPcachePayloadOutputEngine spFlushOutputProcess;

    private EventBuilderBackEnd backEnd;
    private SPDataAnalysis splicedAnalysis;

    private Dispatcher dispatcher;

    /**
     * Create a hit generator.
     */
    public EBComponent()
    {
        super(COMPONENT_NAME, 0);

        final int compId = 0;

        IByteBufferCache bufMgr =
            new ByteBufferCache(256, 50000000L, 5000000L, "EventBuilder");
        addCache(bufMgr);

        addMBean("memory", new MemoryStatistics());

        masterFactory = new MasterPayloadFactory(bufMgr);

        MonitoringData monData = new MonitoringData();
        addMBean("backEnd", monData);

        splicedAnalysis = new SPDataAnalysis(masterFactory);
        Splicer splicer = new SplicerImpl(splicedAnalysis);
        splicer.addSplicerListener(splicedAnalysis);
        addSplicer(splicer);

        dispatcher = new FileDispatcher("physics");

        backEnd =
            new EventBuilderBackEnd(masterFactory, bufMgr, splicer,
                                    splicedAnalysis, dispatcher);

        gtInputProcess =
            new EventBuilderGlobalTrigPayloadInputEngine(COMPONENT_NAME, compId,
                                                         "globalTrigInput",
                                                         backEnd, bufMgr,
                                                         masterFactory);
        addMonitoredEngine(DAQConnector.TYPE_GLOBAL_TRIGGER, gtInputProcess);

        spReqOutputProcess =
            new EventBuilderSPreqPayloadOutputEngine(COMPONENT_NAME, compId,
                                                     "spReqOutput");
        addEngine(DAQConnector.TYPE_READOUT_REQUEST, spReqOutputProcess, true);

        spDataInputProcess =
            new EventBuilderSPdataPayloadInputEngine(COMPONENT_NAME, compId,
                                                     "spDataInput", bufMgr,
                                                     masterFactory, splicer);
        addMonitoredEngine(DAQConnector.TYPE_READOUT_DATA, spDataInputProcess);

        spFlushOutputProcess =
            new EventBuilderSPcachePayloadOutputEngine(COMPONENT_NAME, compId,
                                                       "spFlushOutput");
        // TODO: don't add this output engine; it should go away

        // connect pieces together
        gtInputProcess.registerStringProcReqOutputEngine(spReqOutputProcess);
        backEnd.registerStringProcCacheOutputEngine(spFlushOutputProcess);
        spReqOutputProcess.registerBufferManager(bufMgr);
        spFlushOutputProcess.registerBufferManager(bufMgr);

        monData.setGlobalTriggerInputMonitor(gtInputProcess);
        monData.setBackEndMonitor(backEnd);
    }

    /**
     * Set the run number inside this component.
     *
     * @param runNumber run number
     */
    public void setRunNumber(int runNumber)
    {
        backEnd.reset();
        backEnd.setRunNumber(runNumber);
        splicedAnalysis.setRunNumber(runNumber);
    }

    /**
     * Set the destination directory where the dispatch files will be saved.
     *
     * @param dirName The absolute path of directory where the dispatch files will be stored.
     */
    public void setDispatchDestStorage(String dirName) {
        dispatcher.setDispatchDestStorage(dirName);
    }

    /**
     * Set the maximum size of the dispatch file.
     *
     * @param maxFileSize the maximum size of the dispatch file.
     */
    public void setMaxFileSize(long maxFileSize) {
        dispatcher.setMaxFileSize(maxFileSize);
    }

    /**
     * Run a DAQ component server.
     *
     * @param args command-line arguments
     *
     * @throws DAQCompException if there is a problem
     */
    public static void main(String[] args)
        throws DAQCompException
    {
        new DAQCompServer(new EBComponent(), args);
    }
}
