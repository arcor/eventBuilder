package icecube.daq.eventBuilder.backend;

import icecube.daq.common.EventVersion;
import icecube.daq.eventBuilder.SPDataAnalysis;
import icecube.daq.eventBuilder.test.MockAppender;
import icecube.daq.eventBuilder.test.MockBufferCache;
import icecube.daq.eventBuilder.test.MockDispatcher;
import icecube.daq.eventBuilder.test.MockHitRecordList;
import icecube.daq.eventBuilder.test.MockReadoutData;
import icecube.daq.eventBuilder.test.MockSplicer;
import icecube.daq.eventBuilder.test.MockTriggerRequest;
import icecube.daq.payload.IEventPayload;
import icecube.daq.payload.ITriggerRequestPayload;
import icecube.daq.util.DeployedDOM;
import icecube.daq.util.IDOMRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.log4j.BasicConfigurator;

public class EventBuilderBackEndTest
    extends TestCase
{
    class MockDOMRegistry
        implements IDOMRegistry
    {
        public double distanceBetweenDOMs(long mbid0, long mbid1)
        {
            throw new Error("Unimplemented");
        }

        public short getChannelId(long mbid)
        {
            throw new Error("Unimplemented");
        }

        public DeployedDOM getDom(long mbId)
        {
            throw new Error("Unimplemented");
        }

        public DeployedDOM getDom(short chanid)
        {
            throw new Error("Unimplemented");
        }

        public Set<DeployedDOM> getDomsOnHub(int hubId)
        {
            throw new Error("Unimplemented");
        }

        public Set<DeployedDOM> getDomsOnString(int string)
        {
            throw new Error("Unimplemented");
        }

        public int getStringMajor(long mbid)
        {
            throw new Error("Unimplemented");
        }

        public int getStringMinor(long mbid)
        {
            throw new Error("Unimplemented");
        }

        public Set<Long> keys()
        {
            throw new Error("Unimplemented");
        }

        public int size()
        {
            throw new Error("Unimplemented");
        }
    }

    private static final MockAppender appender =
        //new MockAppender(org.apache.log4j.Level.ALL).setVerbose(true);
        new MockAppender();

    private static short year;

    static {
        GregorianCalendar cal = new GregorianCalendar();
        year = (short) cal.get(GregorianCalendar.YEAR);
    }


    public EventBuilderBackEndTest(String name)
    {
        super(name);
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();

        appender.clear();

        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure(appender);
    }

    public static Test suite()
    {
        return new TestSuite(EventBuilderBackEndTest.class);
    }

    protected void tearDown()
        throws Exception
    {
        assertEquals("Bad number of log messages",
                     0, appender.getNumberOfMessages());

        super.tearDown();
    }

    private static void validateEvent(IEventPayload evt, int runNum,
                                      int subrunNum, int uid, long firstTime,
                                      long lastTime, MockTriggerRequest req,
                                      List hitList)
    {
        try {
            evt.loadPayload();
        } catch (Exception ex) {
            fail("Couldn't load event: " + ex);
        }

        assertEquals("Bad type", -1, evt.getEventType());
        assertEquals("Bad config ID", -1, evt.getEventConfigID());
        assertEquals("Bad run number", runNum, evt.getRunNumber());
        assertEquals("Bad subrun number", subrunNum, evt.getSubrunNumber());
        assertEquals("Bad UID", uid, evt.getEventUID());
        assertEquals("Bad year", year, evt.getYear());
        assertNotNull("Null first time", evt.getFirstTimeUTC());
        assertEquals("Bad first time",
                     firstTime, evt.getFirstTimeUTC().longValue());
        assertNotNull("Null last time", evt.getLastTimeUTC());
        assertEquals("Bad last time",
                     lastTime, evt.getLastTimeUTC().longValue());

/*
        ITriggerRequestPayload evtReq = evt.getTriggerRequestPayload();
        assertNotNull("Null trigger request", evtReq);
        assertEquals("Bad trigger request", req, evtReq);

        List rdpList = evt.getReadoutDataPayloads();
        assertNotNull("Null data list", rdpList);
        assertEquals("Bad data list length", hitList.size(), rdpList.size());
*/
    }

    public void testCreate()
    {
        MockBufferCache bufCache = new MockBufferCache("Cre");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
    }

    public void testSetBadSubrunNumber()
    {
        MockBufferCache bufCache = new MockBufferCache("SetBadSub");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        final int badNum = 42;

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.prepareSubrun(badNum);

        waitForLogMessages(1);
        assertEquals("Bad number of log messages",
                     1, appender.getNumberOfMessages());

        final String badMsg =
            "Preparing for subrun " + -badNum +
            ", though current subrun is 0. (Expected next subrun to be -1)";
        assertEquals("Bad log message", badMsg, appender.getMessage(0));

        appender.clear();
    }

    public void testSetSubrunNumber()
    {
        MockBufferCache bufCache = new MockBufferCache("SetSub");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.prepareSubrun(1);
    }

    public void testMakeDataPayloadWithNullRequest()
    {
        MockBufferCache bufCache = new MockBufferCache("MakeNull");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setCurrentYear();

        backEnd.makeDataPayload(null, null);

        waitForLogMessages(1);
        assertEquals("Bad number of log messages",
                     1, appender.getNumberOfMessages());

        final String expMsg = "No current request; cannot send data";
        assertEquals("Bad log message", expMsg, appender.getMessage(0));

        appender.clear();
    }

    public void testMakeDataPayloadEmpty()
    {
        MockBufferCache bufCache = new MockBufferCache("MakeEmpty");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setCurrentYear();
        backEnd.setDOMRegistry(new MockDOMRegistry());

        final int runNum = 1;
        backEnd.setRunNumber(runNum);

        final long firstTime = 10000L;
        final long lastTime = 20000L;
        final int uid = 888;
        final int cfgId = 444;

        MockTriggerRequest req =
            new MockTriggerRequest(uid, 999, cfgId, firstTime, lastTime);

        List hitList = new ArrayList();

        IEventPayload evt =
            (IEventPayload) backEnd.makeDataPayload(req, hitList);
        validateEvent(evt, runNum, 0, uid, firstTime, lastTime, req, hitList);

        if (appender.getNumberOfMessages() > 0) {
for (int i=0;i<appender.getNumberOfMessages();i++)System.err.println("LogMsg#"+i+": "+appender.getMessage(i));
            assertEquals("Bad number of log messages",
                         1, appender.getNumberOfMessages());

            final String expMsg = "Sending empty event for window [" +
                firstTime + " - " + lastTime + "]";
            assertEquals("Bad log message", expMsg, appender.getMessage(0));

            appender.clear();
        }
    }

    public void testMakeDataPayload()
    {
        MockBufferCache bufCache = new MockBufferCache("Make");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setCurrentYear();
        backEnd.setDOMRegistry(new MockDOMRegistry());

        final int runNum = 2;
        backEnd.setRunNumber(runNum);

        final long firstTime = 10000L;
        final long lastTime = 20000L;
        final int uid = 888;
        final int cfgId = 444;

        MockTriggerRequest req =
            new MockTriggerRequest(uid, 999, cfgId, firstTime, lastTime);

        ArrayList hitList = new ArrayList();
        if (EventVersion.VERSION < 5) {
            hitList.add(new MockReadoutData(111, 222, firstTime + 1L,
                                            lastTime - 1L));
        } else {
            MockHitRecordList recList = new MockHitRecordList(uid);
            recList.addRecord((short) 101, firstTime + 1);

            hitList.add(recList);
        }

        IEventPayload evt =
            (IEventPayload) backEnd.makeDataPayload(req, hitList);
        validateEvent(evt, runNum, 0, uid, firstTime, lastTime, req, hitList);

        waitForLogMessages(0);
    }

    /** Test the proper subrun numbering when making data payloads */
    public void testMakeDataPayloadSubruns()
    {
        MockBufferCache bufCache = new MockBufferCache("MakeSub");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setCurrentYear();
        backEnd.setDOMRegistry(new MockDOMRegistry());

        backEnd.reset();

        assertEquals("Bad subrun number", 0, backEnd.getSubrunNumber());

        final long timeStep = 10000L;

        final int runNum = 4;
        backEnd.setRunNumber(runNum);

        final int cfgId = 444;

        int numEvts = 0;

        backEnd.startDispatcher();

        int subrun = 0;
        for (int i = 0; i < 10; i++) {
            final long startTime = (long) (i + 1) * timeStep;
            final long substep = timeStep / (i + 1);
            final long commitTime = startTime + (i * substep)/2;

            if (i != 0) {
                backEnd.prepareSubrun(subrun);
                backEnd.commitSubrun(subrun, commitTime);
            }

            long firstTime = startTime;
            long lastTime = firstTime + substep;

            for (int j = 0; j < i + 1; j++) {
                int uid = 888 + numEvts;

                MockTriggerRequest req =
                    new MockTriggerRequest(uid, 999, cfgId, firstTime, lastTime);

                ArrayList hitList = new ArrayList();
                if (EventVersion.VERSION < 5) {
                    hitList.add(new MockReadoutData(111, 222, firstTime + 1L,
                                                    lastTime - 1L));
                } else {
                    MockHitRecordList recList = new MockHitRecordList(uid);
                    recList.addRecord((short) (j + 101), firstTime + 1);

                    hitList.add(recList);
                }

                IEventPayload evt =
                    (IEventPayload) backEnd.makeDataPayload(req, hitList);

                numEvts++;

                int expSubrun;
                if (firstTime >= commitTime) {
                    expSubrun = subrun;
                } else {
                    expSubrun = -subrun;
                }

                validateEvent(evt, runNum, expSubrun, uid, firstTime, lastTime,
                              req, hitList);

                backEnd.sendOutput(evt);
                for (int o = 0; o < 10 && backEnd.getNumOutputsQueued() > 0;
                     o++)
                {
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        // do nothing
                    }
                }
                assertEquals("Still have " + backEnd.getNumOutputsQueued() +
                             " outputs queued",
                             0, backEnd.getNumOutputsQueued());

                for (int o = 0;
                     o < 10 && dispatcher.getNumDispatchedEvents() < numEvts;
                     o++)
                {
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        // do nothing
                    }
                }
                assertEquals("Bad number of dispatched events",
                             numEvts, dispatcher.getNumDispatchedEvents());

                firstTime = lastTime;
                lastTime += substep;
            }

            subrun++;
        }

        try {
            backEnd.stopThread();
        } catch (IOException ioe) {
            fail("Caught " + ioe);
        }

        /* checking event count */
        int numEvents = 0;
        for (int nextSubrun = 0; nextSubrun < subrun; nextSubrun++) {
            if ((nextSubrun & 1) == 0) {
                numEvents++;
            }

            assertEquals("Bad number of events for subrun " + nextSubrun,
                         numEvents, backEnd.getSubrunTotalEvents(nextSubrun));
        }

        waitForDispatcher(dispatcher);
        waitForLogMessages(1);

        assertNotNull("Null log message ", appender.getMessage(0));
        final String logMsg = appender.getMessage(0).toString();
        assertTrue("Bad log message " + logMsg,
                   logMsg.startsWith("GoodTime Stats"));

        appender.clear();
    }

    /**
     * Check that a sufficiently short and diabolical subruns do not cause
     * incorrect numbering of subruns.
     * Regression for issue #2318
     */
    public void testShortSubruns()
    {
        // Create a backend
        MockBufferCache     bufCache   = new MockBufferCache("ShortSub");
        SPDataAnalysis      analysis   = new SPDataAnalysis();
        MockSplicer         splicer    = new MockSplicer();
        MockDispatcher      dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd    =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setDOMRegistry(new MockDOMRegistry());

        final int runNum = 123456;
        backEnd.setRunNumber(runNum);

        // Bits for generating events
        Calendar now = new GregorianCalendar();
        Calendar startOfYear =
            new GregorianCalendar(now.get(Calendar.YEAR), 0, 1);
        long t0 = startOfYear.getTimeInMillis();

        final int cfgId = 444;

        // Fire a few events through the backend - subrun 0
        for (int i = 0; i < 10; i++) {
            final long firstTime =
                (System.currentTimeMillis() - t0) * 10000000L;
            final long lastTime = firstTime + 50L;
            final int uid = 888 + i;

            MockTriggerRequest req =
                new MockTriggerRequest(uid, 999, cfgId, firstTime, lastTime);

            ArrayList hitList = new ArrayList();
            hitList.add(new MockHitRecordList(uid));

            IEventPayload evt =
                (IEventPayload) backEnd.makeDataPayload(req, hitList);
            validateEvent(evt, runNum, 0, uid, firstTime, lastTime,
                          req, hitList);

            assertEquals("Bad subrun number in backend",
                         0, backEnd.getSubrunNumber());
        }

        final long startTime = (System.currentTimeMillis() - t0) * 10000000L;
        final long commitTime1 = startTime + 50L;

        MockTriggerRequest req;
        ArrayList hitList;
        IEventPayload evt;

        // prep for subrun 1
        backEnd.prepareSubrun(1);
        backEnd.commitSubrun(1, commitTime1);

        final long firstTime1 = startTime;
        final long lastTime1 = firstTime1 + 5L;
        final int uid1 = 900;

        // a transitional event: subrun -1
        req = new MockTriggerRequest(uid1, 999, cfgId, firstTime1, lastTime1);
        hitList = new ArrayList();
        hitList.add(new MockHitRecordList(uid1));

        evt = (IEventPayload) backEnd.makeDataPayload(req, hitList);
        validateEvent(evt, runNum, -1, uid1, firstTime1, lastTime1,
                      req, hitList);

        assertEquals("Bad subrun number in backend",
                     -1, backEnd.getSubrunNumber());

        // prep for subrun 2
        backEnd.prepareSubrun(2);

        final long commitTime2 = commitTime1 + 150L;

        long baseTime = commitTime1;

        for (int i = 0; i < 3; i++) {

            // Now send event after subrun 1's start time but before we
            // set subrun 2's start time.  Events should be part of subrun -2
            final long firstTime = baseTime + 50L;
            final long lastTime = firstTime + 20L;
            int uid = uid1 + i + 1;

            baseTime += 50L;

            if (i == 1) {
                backEnd.commitSubrun(2, commitTime2);
            }

            int subrun;
            if (firstTime < commitTime2) {
                subrun = -2;
            } else {
                subrun = 2;
            }

            req = new MockTriggerRequest(uid, 999, cfgId, firstTime, lastTime);
            hitList = new ArrayList();
            hitList.add(new MockHitRecordList(uid));

            evt = (IEventPayload) backEnd.makeDataPayload(req, hitList);
            validateEvent(evt, runNum, subrun, uid, firstTime, lastTime,
                          req, hitList);
            assertEquals("Bad subrun number in backend",
                         subrun, backEnd.getSubrunNumber());
        }

        waitForDispatcher(dispatcher);

        waitForLogMessages(1);
        final String expMsg = "Preparing for subrun -2, though current" +
            " subrun is -1. (Expected next subrun to be 1)";
        assertEquals("Bad log message", expMsg, appender.getMessage(0));

        appender.clear();
    }

    public void testReadOnlyFilesystem()
    {
        MockBufferCache bufCache = new MockBufferCache("MakeSub");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setCurrentYear();
        backEnd.setDOMRegistry(new MockDOMRegistry());

        backEnd.reset();
        backEnd.setMaximumOutputFailures(5);

        assertEquals("Bad subrun number", 0, backEnd.getSubrunNumber());

        final long timeStep = 10000L;

        final int runNum = 4;
        backEnd.setRunNumber(runNum);

        final int cfgId = 444;

        int numEvts = 0;

        long firstTime = timeStep;
        long lastTime = firstTime + timeStep;

        short recNum = 777;

        int numGood = 5;
        boolean readOnly = false;

        backEnd.startDispatcher();

        for (int i = 0; i < 10; i++) {
            int uid = 888 + numEvts;

            if (i == numGood) {
                dispatcher.setReadOnly(true);
            } else if (i == numGood + 1) {
                readOnly = true;
            }

            MockTriggerRequest req =
                new MockTriggerRequest(uid, 999, cfgId, firstTime, lastTime);

            ArrayList hitList = new ArrayList();
            if (EventVersion.VERSION < 5) {
                hitList.add(new MockReadoutData(111, 222, firstTime + 1L,
                                                lastTime - 1L));
            } else {
                MockHitRecordList recList = new MockHitRecordList(uid);
                recList.addRecord(recNum, firstTime + 1);
                recNum++;

                hitList.add(recList);
            }

            IEventPayload evt =
                (IEventPayload) backEnd.makeDataPayload(req, hitList);

            numEvts++;

            validateEvent(evt, runNum, 0, uid, firstTime, lastTime, req,
                          hitList);

            boolean outputSent = backEnd.sendOutput(evt);
            if (!outputSent && !readOnly) {
                throw new Error("Send failed for evt#" + numEvts);
            } else if (outputSent && readOnly) {
                throw new Error("Send should have failed for evt#" + numEvts);
            }

            for (int o = 0; o < 10 && backEnd.getNumOutputsQueued() > 0;
                 o++)
            {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    // do nothing
                }
            }
            assertEquals("Still have " + backEnd.getNumOutputsQueued() +
                         " outputs queued",
                         0, backEnd.getNumOutputsQueued());

            for (int o = 0;
                 o < 10 && dispatcher.getNumDispatchedEvents() < numEvts;
                 o++)
            {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    // do nothing
                }
            }

            int expEvts;
            if (numEvts <= numGood) {
                expEvts = numEvts;
            } else {
                expEvts = numGood + 1;
            }

            assertEquals("Bad number of dispatched events",
                         expEvts, dispatcher.getNumDispatchedEvents());

            firstTime = lastTime;
            lastTime += timeStep;
        }

        try {
            backEnd.stopThread();
        } catch (IOException ioe) {
            fail("Caught " + ioe);
        }

        waitForDispatcher(dispatcher);
        waitForLogMessages(5);

        final String badMsg = "Stack Trace";
        for (int i = 0; i < 4; i++) {
            assertEquals("Bad log message", badMsg, appender.getMessage(i));
        }

        assertNotNull("Null log message ", appender.getMessage(4));
        final String logMsg = appender.getMessage(4).toString();
        assertTrue("Bad log message " + logMsg,
                   logMsg.startsWith("GoodTime Stats"));

        appender.clear();
    }

    public void testMakeDataPayloadSwitchRun()
    {
        MockBufferCache bufCache = new MockBufferCache("Make");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setCurrentYear();
        backEnd.setDOMRegistry(new MockDOMRegistry());

        int runNum = 987654;
        backEnd.setRunNumber(runNum);

        final int cfgId = 444;

        long firstTime = 10000L;
        long lastTime = 20000L;
        int trUID = 888;
        int rdUID = 111;

        final int switchNum = runNum + 100;

        backEnd.startDispatcher();

        for (int i = 0; i < 3; i++) {
            String expMessage = null;

            if (i == 1) {
                backEnd.setSwitchRunNumber(switchNum);
            } else if (i == 2) {
                trUID = 1;
                runNum = switchNum;
            }

            MockTriggerRequest req =
                new MockTriggerRequest(trUID, 999, cfgId, firstTime, lastTime);

            ArrayList hitList = new ArrayList();
            if (EventVersion.VERSION < 5) {
                hitList.add(new MockReadoutData(111, rdUID, firstTime + 1L,
                                                lastTime - 1L));
            } else {
                MockHitRecordList recList = new MockHitRecordList(trUID);
                recList.addRecord((short) 101, firstTime + 1);

                hitList.add(recList);
            }

            IEventPayload evt =
                (IEventPayload) backEnd.makeDataPayload(req, hitList);
            validateEvent(evt, runNum, 0, trUID, firstTime, lastTime, req,
                          hitList);

            waitForLogMessages(0);

            firstTime = lastTime + 1000L;
            lastTime = firstTime + 10000L;
            trUID++;
            rdUID++;
        }
    }

    public void testDispatchFailure()
    {
        MockBufferCache bufCache = new MockBufferCache("MakeSub");

        SPDataAnalysis analysis = new SPDataAnalysis();
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.setCurrentYear();
        backEnd.setDOMRegistry(new MockDOMRegistry());

        backEnd.reset();
        backEnd.setMaximumOutputFailures(5);

        assertEquals("Bad subrun number", 0, backEnd.getSubrunNumber());

        final long timeStep = 10000L;

        final int runNum = 4;
        backEnd.setRunNumber(runNum);

        final int cfgId = 444;

        int numEvts = 0;

        long firstTime = timeStep;
        long lastTime = firstTime + timeStep;

        short recNum = 777;

        int numGood = 5;
        boolean dispatchError = false;

        backEnd.startDispatcher();

        for (int i = 0; i < 10; i++) {
            int uid = 888 + numEvts;

            if (i == numGood) {
                dispatcher.setDispatchError(true);
            } else if (i == numGood + 1) {
                dispatchError = true;
            }

            MockTriggerRequest req =
                new MockTriggerRequest(uid, 999, cfgId, firstTime, lastTime);

            ArrayList hitList = new ArrayList();
            if (EventVersion.VERSION < 5) {
                hitList.add(new MockReadoutData(111, 222, firstTime + 1L,
                                                lastTime - 1L));
            } else {
                MockHitRecordList recList = new MockHitRecordList(uid);
                recList.addRecord(recNum, firstTime + 1);
                recNum++;

                hitList.add(recList);
            }

            IEventPayload evt =
                (IEventPayload) backEnd.makeDataPayload(req, hitList);

            numEvts++;

            validateEvent(evt, runNum, 0, uid, firstTime, lastTime, req,
                          hitList);

            boolean outputSent = backEnd.sendOutput(evt);
            if (!outputSent && !dispatchError) {
                throw new Error("Send failed for evt#" + numEvts);
            }

            for (int o = 0; o < 10 && backEnd.getNumOutputsQueued() > 0;
                 o++)
            {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    // do nothing
                }
            }
            assertEquals("Still have " + backEnd.getNumOutputsQueued() +
                         " outputs queued",
                         0, backEnd.getNumOutputsQueued());

            for (int o = 0;
                 o < 10 && dispatcher.getNumDispatchedEvents() < numEvts;
                 o++)
            {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    // do nothing
                }
            }

            int expEvts = numEvts;

            assertEquals("Bad number of dispatched events",
                         expEvts, dispatcher.getNumDispatchedEvents());

            firstTime = lastTime;
            lastTime += timeStep;
        }

        try {
            backEnd.stopThread();
        } catch (IOException ioe) {
            fail("Caught " + ioe);
        }

        waitForDispatcher(dispatcher);
        waitForLogMessages(5);

        final String badMsg = "Could not dispatch event";
        for (int i = 0; i < 4; i++) {
            assertEquals("Bad log message", badMsg, appender.getMessage(i));
        }

        assertNotNull("Null log message ", appender.getMessage(4));
        final String logMsg = appender.getMessage(4).toString();
        assertTrue("Bad log message " + logMsg,
                   logMsg.startsWith("GoodTime Stats"));

        appender.clear();
    }

    private static void waitForDispatcher(MockDispatcher dispatcher)
    {
        for (int i = 0; i < 100 && dispatcher.isStarted(); i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                // ignore interrupts
            }
        }
        assertFalse("Dispatcher has not stopped", dispatcher.isStarted());
    }

    private void waitForLogMessages(int numMsgs)
    {
        for (int i = 0; i < 100 && appender.getNumberOfMessages() < numMsgs;
             i++)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                // ignore interrupts
            }
        }

        if (appender.getNumberOfMessages() > numMsgs){
            for (int i = 0; i < appender.getNumberOfMessages(); i++) {
                System.out.println("MSG#" + i + ": " + appender.getMessage(i));
            }
        }
        assertEquals("Bad number of log messages",
                     numMsgs, appender.getNumberOfMessages());
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
