package icecube.daq.eventBuilder.backend;

import icecube.daq.eventBuilder.SPDataAnalysis;

import icecube.daq.eventbuilder.IEventPayload;

import icecube.daq.eventBuilder.test.MockAppender;
import icecube.daq.eventBuilder.test.MockBufferCache;
import icecube.daq.eventBuilder.test.MockDispatcher;
import icecube.daq.eventBuilder.test.MockFactory;
import icecube.daq.eventBuilder.test.MockHit;
import icecube.daq.eventBuilder.test.MockSplicer;
import icecube.daq.eventBuilder.test.MockTriggerRequest;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import org.apache.log4j.BasicConfigurator;

public class EventBuilderBackEndTest
    extends TestCase
{
    private static final MockAppender appender = new MockAppender();

    public EventBuilderBackEndTest(String name)
    {
        super(name);
    }

    private static int getNextSubrun(int subrun)
    {
        if (subrun < 0) {
            return -subrun;
        }

        return subrun = -subrun - 1;
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
        backEnd.setSubrunNumber(badNum, 123456L);

        assertEquals("Bad number of log messages",
                     1, appender.getNumberOfMessages());

        final String badMsg =
            "Expected subrun number 0 to be followed by -1, not " + badNum;
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
        backEnd.setSubrunNumber(-1, 123456L);
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

        MockTriggerRequest req =
            new MockTriggerRequest(firstTime, lastTime, 999, 888);

        backEnd.makeDataPayload(req, new ArrayList());

        assertEquals("Bad number of log messages",
                     1, appender.getNumberOfMessages());

        final String expMsg = "Sending empty event for window [" + firstTime +
            " - " + lastTime + "]";
        assertEquals("Bad log message", expMsg, appender.getMessage(0));

        appender.clear();
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

        MockTriggerRequest req =
            new MockTriggerRequest(firstTime, lastTime, 999, 888);

        ArrayList hitList = new ArrayList();
        hitList.add(new MockHit());

        backEnd.makeDataPayload(req, hitList);
    }

    public void testMakeDataPayloadSubruns()
    {
        MockBufferCache bufCache = new MockBufferCache();
        MockFactory factory = new MockFactory();

        SPDataAnalysis analysis = new SPDataAnalysis(factory);
        MockSplicer splicer = new MockSplicer();

        MockDispatcher dispatcher = new MockDispatcher();

        EventBuilderBackEnd backEnd =
            new EventBuilderBackEnd(bufCache, splicer, analysis, dispatcher);
        assertEquals("Bad subrun number", 0, backEnd.getSubrunNumber());

        final long timeStep = 10000L;

        int subrun = 0;
        for (int i = 0; i < 10; i++) {
            final long firstTime = (long) (i + 1) * timeStep;
            if (i != 0) {
                backEnd.setSubrunNumber(subrun, firstTime);
            }

            final long substep = timeStep / (i + 1);

            long lastTime = firstTime;
            for (int j = 0; j < i + 1; j++) {
                long tmpTime = lastTime + substep;

                MockTriggerRequest req =
                    new MockTriggerRequest(lastTime, tmpTime, 999, 888 + i);

                lastTime = tmpTime;

                ArrayList hitList = new ArrayList();
                hitList.add(new MockHit());

                IEventPayload evt =
                    (IEventPayload) backEnd.makeDataPayload(req, hitList);
                assertEquals("Bad subrun number",
                             subrun, evt.getSubrunNumber());
            }

            subrun = getNextSubrun(subrun);
        }

        int nextSubrun = 0;
        for (int n = 1; nextSubrun != subrun; n++) {
            assertEquals("Bad number of events for subrun " + nextSubrun,
                         n, backEnd.getSubrunTotalEvents(nextSubrun));
            nextSubrun = getNextSubrun(nextSubrun);
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
