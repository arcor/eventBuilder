package icecube.daq.eventBuilder.backend;

import icecube.daq.eventBuilder.SPDataAnalysis;
import icecube.daq.eventBuilder.test.MockAppender;
import icecube.daq.eventBuilder.test.MockBufferCache;
import icecube.daq.eventBuilder.test.MockDispatcher;
import icecube.daq.eventBuilder.test.MockFactory;
import icecube.daq.eventBuilder.test.MockHit;
import icecube.daq.eventBuilder.test.MockReadoutData;
import icecube.daq.eventBuilder.test.MockSplicer;
import icecube.daq.eventBuilder.test.MockTriggerRequest;
import icecube.daq.eventbuilder.IEventPayload;
import icecube.daq.trigger.ITriggerRequestPayload;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.log4j.BasicConfigurator;

public class EventBuilderBackEndTest
    extends TestCase
{
    private static final MockAppender appender = new MockAppender();

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
            fail("Couldn't load event");
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

        ITriggerRequestPayload evtReq = evt.getTriggerRequestPayload();
        assertNotNull("Null trigger request", evtReq);
        assertEquals("Bad trigger request", req, evtReq);

        List rdpList = evt.getReadoutDataPayloads();
        assertNotNull("Null data list", rdpList);
        assertEquals("Bad data list length", hitList.size(), rdpList.size());
    }

    public void testCreate()
    {
        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
    }

    public void testSetBadSubrunNumber()
    {
        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        final int badNum = 42;

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.prepareSubrun(badNum);

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
        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.prepareSubrun(1);
    }

    public void testMakeDataPayloadWithNullRequest()
    {
        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);

        backEnd.makeDataPayload(null, null);

        assertEquals("Bad number of log messages",
                     1, appender.getNumberOfMessages());

        final String expMsg = "No current request; cannot send data";
        assertEquals("Bad log message", expMsg, appender.getMessage(0));

        appender.clear();
    }

    public void testMakeDataPayloadEmpty()
    {
        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);

        long firstTime = 10000L;
        long lastTime = 20000L;
        int uid = 888;

        MockTriggerRequest req =
            new MockTriggerRequest(firstTime, lastTime, 999, uid);

        List hitList = new ArrayList();

        IEventPayload evt =
            (IEventPayload) backEnd.makeDataPayload(req, hitList);
        validateEvent(evt, 0, 0, uid, firstTime, lastTime, req, hitList);

        if (appender.getNumberOfMessages() > 0) {
            assertEquals("Bad number of log messages",
                         appender.getNumberOfMessages(), 1);

            final String expMsg = "Sending empty event for window [" +
                firstTime + " - " + lastTime + "]";
            assertEquals("Bad log message", expMsg, appender.getMessage(0));

            appender.clear();
        }
    }

    public void testMakeDataPayload()
    {
        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);

        long firstTime = 10000L;
        long lastTime = 20000L;
        int uid = 888;

        MockTriggerRequest req =
            new MockTriggerRequest(firstTime, lastTime, 999, uid);

        ArrayList hitList = new ArrayList();
        hitList.add(new MockHit());

        IEventPayload evt =
            (IEventPayload) backEnd.makeDataPayload(req, hitList);
        validateEvent(evt, 0, 0, uid, firstTime, lastTime, req, hitList);
    }

    /** Test the proper subrun numbering when making data payloads */
    public void testMakeDataPayloadSubruns()
    {
        appender.setVerbose(true);

        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        backEnd.reset();

        assertEquals("Bad subrun number", 0, backEnd.getSubrunNumber());

        final long timeStep = 10000L;

        final int runNum = 4;
        backEnd.setRunNumber(runNum);

        int numEvts = 0;

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
                    new MockTriggerRequest(firstTime, lastTime, 999, uid);

                ArrayList hitList = new ArrayList();
                hitList.add(new MockReadoutData(111, 222, firstTime + 1L,
                                                lastTime - 1L));

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
                while (backEnd.getNumOutputsQueued() > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        // do nothing
                    }
                }

                assertEquals("Bad number of dispatched events",
                             numEvts, dispatcher.getTotalDispatchedEvents());

                firstTime = lastTime;
                lastTime += substep;
            }

            subrun++;
        }

        backEnd.stopThread();

        /* checking event count */
        int numEvents = 0;
        for (int nextSubrun = 0; nextSubrun < subrun; nextSubrun++) {
            if ((nextSubrun & 1) == 0) {
                numEvents++;
            }

            assertEquals("Bad number of events for subrun " + nextSubrun,
                         numEvents, backEnd.getSubrunTotalEvents(nextSubrun));
        }
    }

    /**
     * Check that a sufficiently short and diabolical subruns do not cause
     * incorrect numbering of subruns.
     * Regression for issue #2318
     */
    public void testShortSubruns() {
        //System.out.println("Entering testShortSubruns():");
        //appender.setVerbose(true);
        //appender.setLevel(org.apache.log4j.Level.INFO);

        // Create a backend
        MockBufferCache     bufCache   = new MockBufferCache();
        MockFactory         factory    = new MockFactory();
        SPDataAnalysis      analysis   = new SPDataAnalysis(factory);
        MockSplicer         splicer    = new MockSplicer();
        MockDispatcher      dispatcher = new MockDispatcher();
        EventBuilderBackEnd backEnd    =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);

        final int runNum = 123456;
        backEnd.setRunNumber(runNum);

        // Bits for generating events
        Calendar now = new GregorianCalendar();
        Calendar startOfYear =
            new GregorianCalendar(now.get(Calendar.YEAR), 0, 1);
        long t0 = startOfYear.getTimeInMillis();

        // Fire a few events through the backend - subrun 0
        for (int i = 0; i < 10; i++) {
            final long firstTime = (System.currentTimeMillis() - t0) * 10000000L;
            final long lastTime = firstTime + 50L;
            final int uid = 888 + i;

            MockTriggerRequest req =
                new MockTriggerRequest(firstTime, lastTime, 999, uid);

            ArrayList hitList = new ArrayList();
            hitList.add(new MockHit());

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
        req = new MockTriggerRequest(firstTime1, lastTime1, 999, uid1);
        hitList = new ArrayList();
        hitList.add(new MockHit());

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

            req = new MockTriggerRequest(firstTime, lastTime, 999, uid);
            hitList = new ArrayList();
            hitList.add(new MockHit());

            evt = (IEventPayload) backEnd.makeDataPayload(req, hitList);
            validateEvent(evt, runNum, subrun, uid, firstTime, lastTime,
                          req, hitList);
            assertEquals("Bad subrun number in backend",
                         subrun, backEnd.getSubrunNumber());
        }

        //appender.setVerbose(false);
        //appender.setLevel(org.apache.log4j.Level.WARN);
        appender.clear();
        //System.out.println("Exiting testShortSubruns():");
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
