package icecube.daq.eventBuilder;

import icecube.daq.common.DAQCmdInterface;
import icecube.daq.eventBuilder.backend.EventBuilderBackEnd;
import icecube.daq.eventBuilder.exceptions.EventBuilderException;
import icecube.daq.eventBuilder.monitoring.MonitoringData;
import icecube.daq.io.DispatchException;
import icecube.daq.io.Dispatcher;
import icecube.daq.io.FileDispatcher;
import icecube.daq.io.SpliceablePayloadReader;
import icecube.daq.juggler.component.DAQCompException;
import icecube.daq.juggler.component.DAQCompServer;
import icecube.daq.juggler.component.DAQComponent;
import icecube.daq.juggler.component.DAQConnector;
import icecube.daq.juggler.mbean.MemoryStatistics;
import icecube.daq.juggler.mbean.SystemStatistics;
import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.PayloadChecker;
import icecube.daq.payload.impl.PayloadFactory;
import icecube.daq.payload.impl.ReadoutRequestFactory;
import icecube.daq.payload.impl.TriggerRequestFactory;
import icecube.daq.payload.impl.VitreousBufferCache;
import icecube.daq.splicer.HKN1Splicer;
import icecube.daq.splicer.Splicer;
import icecube.daq.util.DOMRegistry;
import icecube.daq.util.IDOMRegistry;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

/**
 * Event builder component.
 */
public class EBComponent
    extends DAQComponent
{
    /**
     * Name of property used to signal that events should be validated
     * before being written to the physics file.
     */
    public static final String PROP_VALIDATE_EVENTS =
            "icecube.daq.eventBuilder.validateEvents";

    /** Component name. */
    private static final String COMPONENT_NAME =
        DAQCmdInterface.DAQ_EVENTBUILDER;

    /** Message logger. */
    private static final Log LOG = LogFactory.getLog(EBComponent.class);

    private IByteBufferCache trigBufMgr;
    private GlobalTriggerReader gtInputProcess;

    private IByteBufferCache rdoutDataMgr;
    private SpliceablePayloadReader rdoutDataInputProcess;

    private EventBuilderSPreqPayloadOutputEngine spReqOutputProcess;

    private MonitoringData monData;

    private EventBuilderBackEnd backEnd;
    private SPDataAnalysis splicedAnalysis;
    private Splicer splicer;

    private Dispatcher dispatcher;

    private boolean validateEvents;
    private File configDir;

    /**
     * Create an event builder component.
     */
    public EBComponent()
    {
        this(false);
    }

    /**
     * Create an event builder component.
     *
     * @param validateEvents if <tt>true</tt>, use a validating dispatcher
     */
    public EBComponent(boolean validateEvents)
    {
        super(COMPONENT_NAME, 0);

        final int compId = 0;

        rdoutDataMgr = new VitreousBufferCache("EBRdOut", 250000000);
        addCache(DAQConnector.TYPE_READOUT_DATA, rdoutDataMgr);

        trigBufMgr = new VitreousBufferCache("EBTrig");
        addCache(DAQConnector.TYPE_GLOBAL_TRIGGER, trigBufMgr);
        TriggerRequestFactory trigFactory =
            new TriggerRequestFactory(trigBufMgr);

        IByteBufferCache evtDataMgr = new VitreousBufferCache("EBEvent");
        addCache(DAQConnector.TYPE_EVENT, evtDataMgr);

        IByteBufferCache genMgr = new VitreousBufferCache("EBGen");
        addCache(genMgr);

        addMBean("jvm", new MemoryStatistics());
        addMBean("system", new SystemStatistics());

        monData = new MonitoringData();
        addMBean("backEnd", monData);

        splicedAnalysis = new SPDataAnalysis();
        splicer = new HKN1Splicer(splicedAnalysis);
        splicer.addSplicerListener(splicedAnalysis);
        addSplicer(splicer);

        dispatcher = new FileDispatcher("physics", evtDataMgr);

        backEnd =
            new EventBuilderBackEnd(evtDataMgr, splicer, splicedAnalysis,
                                    dispatcher, validateEvents);

        ReadoutRequestFactory rdoutReqFactory = new ReadoutRequestFactory(null);

        EventBuilderTriggerRequestDemultiplexer demuxer =
            new EventBuilderTriggerRequestDemultiplexer(rdoutReqFactory);

        try {
            gtInputProcess =
                new GlobalTriggerReader(COMPONENT_NAME + "*GlblTrig", backEnd,
                                        trigFactory, trigBufMgr);
        } catch (IOException ioe) {
            throw new Error("Couldn't create GlobalTriggerReader", ioe);
        }
        addMonitoredEngine(DAQConnector.TYPE_GLOBAL_TRIGGER, gtInputProcess);

        spReqOutputProcess =
            new EventBuilderSPreqPayloadOutputEngine(COMPONENT_NAME + "*Req",
                                                     compId, "spReqOutput");
        addMonitoredEngine(DAQConnector.TYPE_READOUT_REQUEST,
                           spReqOutputProcess, true);

        PayloadFactory hitRecFactory =
            new PayloadFactory(rdoutDataMgr);

        try {
            rdoutDataInputProcess =
                new SpliceablePayloadReader(COMPONENT_NAME + "*RdoutData",
                                            50000, splicer, hitRecFactory);
        } catch (IOException ioe) {
            throw new Error("Couldn't create ReadoutDataReader", ioe);
        }
        addMonitoredEngine(DAQConnector.TYPE_READOUT_DATA,
                           rdoutDataInputProcess);

        // connect pieces together
        gtInputProcess.registerDemultiplexer(demuxer);

        demuxer.registerOutputEngine(spReqOutputProcess);

        spReqOutputProcess.registerBufferManager(genMgr);

        monData.setGlobalTriggerInputMonitor(gtInputProcess);
        monData.setBackEndMonitor(backEnd);

        this.validateEvents = validateEvents;
    }

    /**
     * Close all open files, sockets, etc.
     *
     * @throws IOException if there is a problem
     */
    public void closeAll()
        throws IOException
    {
        splicer.dispose();
        gtInputProcess.destroyProcessor();
        spReqOutputProcess.destroyProcessor();
        rdoutDataInputProcess.destroyProcessor();
        try {
            dispatcher.close();
        } catch (DispatchException de) {
            LOG.error("Cannot close dispatcher", de);
            throw new IOException("Cannot close dispatcher: " + de);
        }

        super.closeAll();
    }

    /**
     * Begin packaging events for the specified subrun.
     *
     * @param subrunNumber subrun number
     * @param startTime time of first good hit in subrun
     */
    public void commitSubrun(int subrunNumber, long startTime)
    {
        if (subrunNumber == 0) {
            throw new RuntimeException("Subrun number cannot be zero");
        }

        if (subrunNumber < 0) {
            LOG.error("Committed subrun number " + subrunNumber +
                      " should be not negative");
            subrunNumber = -subrunNumber;
        }

        backEnd.commitSubrun(subrunNumber, startTime);
    }

    public void configuring(String configName) throws DAQCompException
    {
        if (validateEvents) {
            PayloadChecker.configure(configDir, configName);
        }
    }

    public EventBuilderBackEnd getBackEnd()
    {
        return backEnd;
    }

    public IByteBufferCache getDataCache()
    {
        return rdoutDataMgr;
    }

    public SpliceablePayloadReader getDataReader()
    {
        return rdoutDataInputProcess;
    }

    public Splicer getDataSplicer()
    {
        return splicer;
    }

    public Dispatcher getDispatcher()
    {
        return dispatcher;
    }

    /**
     * Get the number of events for the given subrun.
     * NOTE: This should only be implemented by the event builder component.
     *
     * @param subrun subrun number
     *
     * @return number of events for the subrun
     *
     * @throws DAQCompException if the subrun number is not valid
     */
    public long getEvents(int subrun)
        throws DAQCompException
    {
        try {
            return backEnd.getSubrunTotalEvents(subrun);
        } catch (RuntimeException rte) {
            throw new DAQCompException(rte.getMessage(), rte);
        }
    }

    public long getEventsSent()
    {
        return dispatcher.getNumDispatchedEvents();
    }

    public MonitoringData getMonitoringData()
    {
        return monData;
    }

    public EventBuilderSPreqPayloadOutputEngine getRequestWriter()
    {
        return spReqOutputProcess;
    }

    public long getReadoutsReceived()
    {
        return backEnd.getTotalReadoutsReceived();
    }

    public long getRequestsSent()
    {
        return spReqOutputProcess.getTotalRecordsSent();
    }

    /**
     * Get the run data for the specified run.
     *
     * @return array of <tt>long</tt> values:<ol>
     *    <li>number of events
     *    <li>starting time of first event in run
     *    <li>ending time of last event in run
     *    </ol>
     *
     * @throw EventBuilderException if no data is found for the run
     */
    public long[] getRunData(int runNum)
        throws DAQCompException
    {
        try {
            return backEnd.getRunData(runNum);
        } catch (EventBuilderException ebe) {
            throw new DAQCompException("Cannot get run data", ebe);
        }
    }

    /**
     * Get the current run number.
     *
     * @return current run number
     */
    public int getRunNumber()
    {
        return backEnd.getRunNumber();
    }

    public IByteBufferCache getTriggerCache()
    {
        return trigBufMgr;
    }

    public GlobalTriggerReader getTriggerReader()
    {
        return gtInputProcess;
    }

    public long getTriggerRequestsReceived()
    {
        return backEnd.getNumTriggerRequestsReceived();
    }

    /**
     * Return this component's svn version id as a String.
     *
     * @return svn version id as a String
     */
    public String getVersionInfo()
    {
        return "$Id: EBComponent.java 14506 2013-05-16 19:23:08Z dglo $";
    }

    /**
     * Is the back end thread still processing data?
     */
    public boolean isBackEndRunning()
    {
        return backEnd.isRunning();
    }

    /**
     * Prepare for the subrun by marking events untrustworthy.
     *
     * @param subrunNumber subrun number
     */
    public void prepareSubrun(int subrunNumber)
    {
        if (subrunNumber == 0) {
            throw new RuntimeException("Subrun number cannot be zero");
        }

        if (subrunNumber < 0) {
            LOG.error("Preparatory subrun number " + subrunNumber +
                      " should be not negative");
            subrunNumber = -subrunNumber;
        }

        backEnd.prepareSubrun(subrunNumber);
    }

    /**
     * Set the destination directory where the dispatch files will be saved.
     *
     * @param dirName The absolute path of directory where the dispatch files
     *                will be stored.
     */
    public void setDispatchDestStorage(String dirName)
    {
        dispatcher.setDispatchDestStorage(dirName);
    }

    /**
     * Set the dispatcher to use.
     *
     * @param dispatcher object which deals with events
     */
    public void setDispatcher(Dispatcher dispatcher)
    {
        if (this.dispatcher != null) {
            try {
                this.dispatcher.close();
            } catch (DispatchException de) {
                LOG.error("Cannot close previous dispatcher", de);
            }
        }

        this.dispatcher = dispatcher;
        backEnd.setDispatcher(dispatcher);
    }

    /**
     * Set the first time when all hubs have sent a hit.
     *
     * @param firstTime time of first good hit in run
     */
    public void setFirstGoodTime(long firstTime)
    {
        backEnd.setFirstGoodTime(firstTime);
    }

    /**
     * Set the directory where the global configuration files can be found.
     *
     * @param dirName The absolute path of the global configuration directory
     */
    public void setGlobalConfigurationDir(String dirName)
    {
        configDir = new File(dirName);

        if (!configDir.exists()) {
            throw new Error("Configuration directory \"" + configDir +
                            "\" does not exist");
        }

        IDOMRegistry domRegistry;
        try {
            domRegistry = DOMRegistry.loadRegistry(dirName);
        } catch (ParserConfigurationException pce) {
            LOG.error("Cannot load DOM registry", pce);
            domRegistry = null;
        } catch (SAXException se) {
            LOG.error("Cannot load DOM registry", se);
            domRegistry = null;
        } catch (IOException ioe) {
            LOG.error("Cannot load DOM registry", ioe);
            domRegistry = null;
        }

        if (domRegistry != null) {
            backEnd.setDOMRegistry(domRegistry);
        }
    }

    /**
     * Set the last time when all hubs have sent a hit.
     *
     * @param lastTime time of last good hit in run
     */
    public void setLastGoodTime(long lastTime)
    {
        backEnd.setLastGoodTime(lastTime);
    }

    /**
     * Set the maximum size of the dispatch file.
     *
     * @param maxFileSize the maximum size of the dispatch file.
     */
    public void setMaxFileSize(long maxFileSize)
    {
        dispatcher.setMaxFileSize(maxFileSize);
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
    }

    /**
     * Perform any actions related to switching to a new run.
     *
     * @param runNumber new run number
     *
     * @throws DAQCompException if there is a problem switching the component
     */
    public void switching(int runNumber)
        throws DAQCompException
    {
        if (LOG.isInfoEnabled()){
            LOG.info("Setting runNumber = " + runNumber);
        }

        backEnd.setSwitchRunNumber(runNumber);
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
        boolean validateEvents =
            System.getProperty(PROP_VALIDATE_EVENTS) != null;

        DAQCompServer srvr;
        try {
            srvr = new DAQCompServer(new EBComponent(validateEvents), args);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            return;
        }
        srvr.startServing();
    }
}
