package icecube.daq.eventBuilder;

import icecube.daq.common.DAQCmdInterface;
import icecube.daq.eventBuilder.backend.EventBuilderBackEnd;
import icecube.daq.eventBuilder.exceptions.EventBuilderException;
import icecube.daq.eventBuilder.monitoring.MonitoringData;
import icecube.daq.io.DispatchException;
import icecube.daq.io.Dispatcher;
import icecube.daq.io.FileDispatcher;
import icecube.daq.io.SpliceableStreamReader;
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
import icecube.daq.splicer.PrioritySplicer;
import icecube.daq.splicer.Spliceable;
import icecube.daq.splicer.SpliceableComparator;
import icecube.daq.splicer.SpliceableFactory;
import icecube.daq.splicer.Splicer;
import icecube.daq.splicer.SplicerException;
import icecube.daq.util.DOMRegistryException;
import icecube.daq.util.DOMRegistryFactory;
import icecube.daq.util.IDOMRegistry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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

    private static final Spliceable LAST_SPLICEABLE =
        SpliceableFactory.LAST_POSSIBLE_SPLICEABLE;

    /** Message logger. */
    private static final Log LOG = LogFactory.getLog(EBComponent.class);

    private IByteBufferCache trigBufMgr;
    private GlobalTriggerReader gtInputProcess;

    private IByteBufferCache rdoutDataMgr;
    private SpliceableStreamReader rdoutDataInputProcess;

    private EventBuilderSPreqPayloadOutputEngine spReqOutputProcess;

    private MonitoringData monData;

    private EventBuilderBackEnd backEnd;
    private SPDataAnalysis splicedAnalysis;
    private Splicer<Spliceable> splicer;

    private String dispatcherDir;
    private Dispatcher dispatcher;

    private boolean validateEvents;
    private File configDir;
    private IDOMRegistry domRegistry;

    /**
     * Create an event builder component.
     *
     * @throws DAQCompException if component cannot be created
     */
    public EBComponent()
        throws DAQCompException
    {
        super(COMPONENT_NAME, 0);

        validateEvents =
            System.getProperty(PROP_VALIDATE_EVENTS) != null;
    }

    /**
     * Initialize event builder component
     */
    @Override
    public void initialize()
        throws DAQCompException
    {
        final int compId = 0;

        rdoutDataMgr = new VitreousBufferCache("EBRdOut", 2000000000);
        addCache(DAQConnector.TYPE_READOUT_DATA, rdoutDataMgr);

        trigBufMgr = new VitreousBufferCache("EBTrig", 1500000000);
        addCache(DAQConnector.TYPE_GLOBAL_TRIGGER, trigBufMgr);
        TriggerRequestFactory trigFactory =
            new TriggerRequestFactory(trigBufMgr);

        IByteBufferCache evtDataMgr =
            new VitreousBufferCache("EBEvent", 400000000);
        addCache(DAQConnector.TYPE_EVENT, evtDataMgr);

        IByteBufferCache genMgr = new VitreousBufferCache("EBGen", 400000000);
        addCache(genMgr);

        addMBean("jvm", new MemoryStatistics());
        addMBean("system", new SystemStatistics());

        monData = new MonitoringData();
        addMBean("backEnd", monData);

        splicedAnalysis = new SPDataAnalysis();

        SpliceableComparator splCmp =
            new SpliceableComparator(LAST_SPLICEABLE);
        if (System.getProperty("usePrioritySplicer") == null) {
            splicer = new HKN1Splicer<Spliceable>(splicedAnalysis, splCmp,
                                                  LAST_SPLICEABLE);
        } else {
            final int totChannels = DAQCmdInterface.DAQ_MAX_NUM_STRINGS +
                DAQCmdInterface.DAQ_MAX_NUM_IDH;
            try {
                splicer = new PrioritySplicer<Spliceable>("EBSorter",
                                                          splicedAnalysis,
                                                          splCmp,
                                                          LAST_SPLICEABLE,
                                                          totChannels);
            } catch (SplicerException se) {
                throw new DAQCompException("Cannot create splicer", se);
            }
            addMBean("EBSorter", splicer);
        }
        splicer.addSplicerListener(splicedAnalysis);
        addSplicer(splicer);

        dispatcher = new FileDispatcher("physics", evtDataMgr);
        if (dispatcherDir != null) {
            dispatcher.setDispatchDestStorage(dispatcherDir);
        }

        backEnd =
            new EventBuilderBackEnd(evtDataMgr, splicer, splicedAnalysis,
                                    dispatcher, validateEvents);
        if (domRegistry != null) {
            backEnd.setDOMRegistry(domRegistry);
        }

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
                new SpliceableStreamReader(COMPONENT_NAME + "*RdoutData",
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

        monData.setBackEndMonitor(backEnd);
    }

    /**
     * Close all open files, sockets, etc.
     *
     * @throws IOException if there is a problem
     */
    @Override
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
            throw new IOException("Cannot close dispatcher", de);
        }

        super.closeAll();
    }

    /**
     * Begin packaging events for the specified subrun.
     *
     * @param subrunNumber subrun number
     * @param startTime time of first good hit in subrun
     */
    @Override
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

    @Override
    public void configuring(String configName) throws DAQCompException
    {
        if (validateEvents) {
            if (configDir == null) {
                throw new Error("Configuration directory has not been set");
            }

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

    public SpliceableStreamReader getDataReader()
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
    @Override
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
     * @throws DAQCompException if no data is found for the run
     */
    @Override
    public long[] getRunData(int runNum)
        throws DAQCompException
    {
        try {
            return backEnd.getRunData(runNum);
        } catch (EventBuilderException ebe) {
            throw new DAQCompException("No final counts found for run " +
                                       runNum + "; state is " + getState(),
                                       ebe);
        }
    }

    /**
     * Get the current run number.
     *
     * @return current run number
     */
    @Override
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
    @Override
    public String getVersionInfo()
    {
        return "$Id: EBComponent.java 17124 2018-10-04 15:56:01Z dglo $";
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
    @Override
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
    @Override
    public void setDispatchDestStorage(String dirName)
    {
        dispatcherDir = dirName;
        if (dispatcher != null) {
            dispatcher.setDispatchDestStorage(dirName);
        }
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
    @Override
    public void setFirstGoodTime(long firstTime)
    {
        backEnd.setFirstGoodTime(firstTime);
    }

    /**
     * Set the directory where the global configuration files can be found.
     *
     * @param dirName The absolute path of the global configuration directory
     */
    @Override
    public void setGlobalConfigurationDir(String dirName)
    {
        configDir = new File(dirName);

        if (!configDir.exists()) {
            throw new Error("Configuration directory \"" + configDir +
                            "\" does not exist");
        }

        try {
            domRegistry = DOMRegistryFactory.load(dirName);
        } catch (DOMRegistryException dre) {
            LOG.error("Cannot load DOM registry", dre);
            domRegistry = null;
        }

        if (backEnd != null && domRegistry != null) {
            backEnd.setDOMRegistry(domRegistry);
        }
    }

    /**
     * Set the last time when all hubs have sent a hit.
     *
     * @param lastTime time of last good hit in run
     */
    @Override
    public void setLastGoodTime(long lastTime)
    {
        backEnd.setLastGoodTime(lastTime);
    }

    /**
     * Set the maximum size of the dispatch file.
     *
     * @param maxFileSize the maximum size of the dispatch file.
     */
    @Override
    public void setMaxFileSize(long maxFileSize)
    {
        dispatcher.setMaxFileSize(maxFileSize);
    }

    /**
     * Should events be validated by PayloadChecker?
     * NOTE: This should not be enabled on SPS!!!
     * @param val <tt>true</tt> to enable event validation
     */
    public void setValidateEvents(boolean val)
    {
        validateEvents = val;
        if (backEnd != null) {
            backEnd.setValidateEvents(val);
        }
    }

    /**
     * Set the run number at the start of the run.
     *
     * @param runNumber run number
     */
    @Override
    public void starting(int runNumber)
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
    @Override
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
        ConsoleAppender appender = new ConsoleAppender();
        appender.setWriter(new PrintWriter(System.out));
        appender.setLayout(new PatternLayout("%p[%t] %L - %m%n"));
        appender.setName("console");
        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel(Level.INFO);
        DAQCompServer srvr;
        try {
            srvr = new DAQCompServer(new EBComponent(), args);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            return;
        }
        srvr.startServing();
    }
}
