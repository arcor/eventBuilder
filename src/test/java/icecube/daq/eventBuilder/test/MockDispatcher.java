package icecube.daq.eventBuilder.test;

import icecube.daq.common.DAQCmdInterface;

import icecube.daq.io.Dispatcher;
import icecube.daq.io.DispatchException;

import icecube.daq.eventbuilder.IEventPayload;
import icecube.daq.eventbuilder.IReadoutDataPayload;

import icecube.daq.eventbuilder.impl.EventPayload_v2;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.ILoadablePayload;
import icecube.daq.payload.MasterPayloadFactory;

import icecube.daq.payload.splicer.Payload;

import java.io.IOException;
import java.io.FileOutputStream;

import java.nio.channels.WritableByteChannel;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockDispatcher
    implements Dispatcher
{
    private static final Log log = LogFactory.getLog(MockDispatcher.class);

    private static final String START_PREFIX =
        DAQCmdInterface.DAQ_ONLINE_RUNSTART_FLAG;
    private static final String STOP_PREFIX =
        DAQCmdInterface.DAQ_ONLINE_RUNSTOP_FLAG;

    private MasterPayloadFactory masterFactory;

    private ArrayList events = new ArrayList();
    private boolean saveEvents = true;
    private boolean running;
    private int expRunNum;

    public MockDispatcher(MasterPayloadFactory masterFactory)
    {
        this.masterFactory = masterFactory;
    }

    public void dataBoundary()
        throws DispatchException
    {
        throw new DispatchException("dataBoundary() called with no argument");
    }

    public void dataBoundary(String message)
       throws DispatchException
    {
        if (message == null) {
            final String errMsg =
                "dataBoundary() called with null argument";
            throw new DispatchException(errMsg);
        }

        String prefix = null;
        String errMsg = null;

        if (message.startsWith(START_PREFIX)) {
            if (!running) {
                running = true;
                prefix = START_PREFIX;
            } else {
                errMsg = "MockDispatcher started while running";
            }
        } else if (message.startsWith(STOP_PREFIX)) {
            if (running) {
                running = false;
                prefix = STOP_PREFIX;
            } else {
                errMsg = "MockDispatcher stopped while not running";
            }
        } else {
            errMsg = "Unknown dispatcher message: " + message;
        }

        if (errMsg != null) {
            try {
                throw new Exception("StackTrace");
            } catch (Exception ex) {
                log.error(errMsg, ex);
            }
        } else if (prefix != null) {
            int runNum;
            try {
                runNum = Integer.parseInt(message.substring(prefix.length()));
                if (runNum != expRunNum) {
                    throw new NumberFormatException("Mismatch");
                }
            } catch (NumberFormatException nfe) {
                throw new Error("Expected run number " + expRunNum + ", not " +
                                message.substring(prefix.length()));
            }
        }
    }

    public void dispatchEvent(ByteBuffer buffer)
        throws DispatchException
    {
        if (!running) {
            throw new DispatchException("dispatcher is not running");
        }

        buffer.position(0);
        Payload payload;
        try {
            payload = masterFactory.createPayload(0, buffer);
            payload.loadPayload();
            if (!(payload instanceof IEventPayload)) {
                throw new Error("Unexpected payload class " +
                                payload.getClass().getName());
            }

            loadEvent((IEventPayload) payload);
            if (saveEvents) {
                events.add(payload);
            }
        } catch (Exception ex) {
            throw new DispatchException("Couldn't create payload", ex);
        }
    }

    /**
     * Dispatch a Payload event object
     *
     * @param event A payload object.
     * @throws DispatchException is there is a problem in the Dispatch system.
     */
    public void dispatchEvent(Payload event) throws DispatchException {
        throw new UnsupportedOperationException("Unimplemented");
    }
    
    public void dispatchEvents(ByteBuffer buffer, int[] indices)
        throws DispatchException
    {
        throw new UnsupportedOperationException("Unimplemented");
    }

    public void dispatchEvents(ByteBuffer buffer, int[] indices, int count)
        throws DispatchException
    {
        throw new UnsupportedOperationException("Unimplemented");
    }

    public Iterator events()
    {
        return events.iterator();
    }

    public EventPayload_v2 getEvent(int index)
    {
        if (index < 0 || index >= events.size()) {
            throw new Error("Illegal event index #" + index);
        }

        return (EventPayload_v2) events.get(index);
    }

    public boolean getSaveEvents()
    {
        return saveEvents;
    }

    public int getNumberOfEvents()
    {
        return events.size();
    }

    public long getTotalDispatchedEvents(){
        return events.size();
    }

    /**
     * Set the destination directory where the dispatch files will be saved.
     *
     * @param dirName The absolute path of directory where the dispatch files will be stored.
     */
    public void setDispatchDestStorage(String dirName) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    /**
     * Set the maximum size of the dispatch file.
     *
     * @param maxFileSize the maximum size of the dispatch file.
     */
    public void setMaxFileSize(long maxFileSize) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    /**
     * Returns the number of units still available in the disk (measured in MB).
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the number of units still available in the disk.
     */
    public int getDiskAvailable(){
        throw new UnsupportedOperationException("Unimplemented");
    }

    /**
     * Returns the total number of units in the disk (measured in MB).
     * If it fails to check the disk space, then it returns -1.
     *
     * @return the total number of units in the disk.
     */
    public int getDiskSize(){
        throw new UnsupportedOperationException("Unimplemented");
    }

    /**
     * Load all payload data in case underlying ByteBuffer gets reused.
     */
    private static final void loadEvent(IEventPayload evt)
    {
        try {
            ((ILoadablePayload) evt.getTriggerRequestPayload()).loadPayload();
        } catch (Exception ex) {
            throw new Error("Couldn't load trigger request payload" +
                            " for event #" + evt.getEventUID(), ex);
        }

        Iterator iter = evt.getReadoutDataPayloads().iterator();
        while (iter.hasNext()) {
            IReadoutDataPayload roData =
                (IReadoutDataPayload) iter.next();
            try {
                ((ILoadablePayload) roData).loadPayload();
            } catch (Exception ex) {
                throw new Error("Couldn't load readout data payload#" +
                                roData.getReadoutDataPayloadNumber() +
                                " from event #" + evt.getEventUID(),
                                ex);
            }

            Iterator dIter = roData.getDataPayloads().iterator();
            while (dIter.hasNext()) {
                try {
                    ((ILoadablePayload) dIter.next()).loadPayload();
                } catch (Exception ex) {
                    throw new Error("Couldn't load data for readout" +
                                    " data payload#" +
                                    roData.getReadoutDataPayloadNumber() +
                                    " from event #" + evt.getEventUID(),
                                    ex);
                }
            }
        }
    }

    void reset()
    {
        if (running) {
            log.error("Dispatcher reset while running");
        }

        events.clear();
    }

    void setExpectedRunNumber(int runNum)
    {
        expRunNum = runNum;
    }

    void setSaveEvents(boolean val)
    {
        saveEvents = val;
    }
}
