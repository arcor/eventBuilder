package icecube.daq.eventBuilder.test;

import icecube.daq.io.DispatchException;
import icecube.daq.io.Dispatcher;
import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IEventPayload;
import icecube.daq.payload.IWriteablePayload;
import icecube.daq.payload.PayloadChecker;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MockDispatcher
    implements Dispatcher
{
    private int numSeen;
    private int numBad;
    private int readOnlyTrigger;
    private boolean readOnly;
    private boolean started;

    public MockDispatcher()
    {
    }

    public void close()
        throws DispatchException
    {
        // do nothing
    }

    public void dataBoundary()
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dataBoundary(String msg)
        throws DispatchException
    {
        if (msg.startsWith(START_PREFIX)) {
            if (started) {
                throw new Error("Dispatcher has already been started");
            }

            started = true;
        } else if (msg.startsWith(STOP_PREFIX)) {
            if (!started) {
                throw new Error("Dispatcher is already stopped");
            }

            started = false;
        }
    }

    public void dispatchEvent(ByteBuffer buf)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dispatchEvent(IWriteablePayload pay)
        throws DispatchException
    {
        numSeen++;

        if (readOnlyTrigger > 0 && numSeen >= readOnlyTrigger) {
            readOnly = true;
        }

        if (!PayloadChecker.validateEvent((IEventPayload) pay, true)) {
            numBad++;
        }

        if (readOnly) {
            throw new DispatchException("Could not dispatch event",
                                        new IOException("Read-only file system"));
        }
    }

    public void dispatchEvents(ByteBuffer buf, int[] il1)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dispatchEvents(ByteBuffer buf, int[] il1, int i2)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public IByteBufferCache getByteBufferCache()
    {
        throw new Error("Unimplemented");
    }

    public long getDiskAvailable()
    {
        return 0;
    }

    public long getDiskSize()
    {
        return 0;
    }

    public long getNumBytesWritten() 
    {
        return 0;
    }

    public int getNumberOfBadEvents()
    {
        return numBad;
    }

    public long getTotalDispatchedEvents()
    {
        return numSeen;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setDispatchDestStorage(String destDir)
    {
        // do nothing
    }

    public void setMaxFileSize(long x0)
    {
        throw new Error("Unimplemented");
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /**
     * Trigger a read-only filesystem event after <tt>eventCount</tt> events.
     *
     * @param eventCount number of events needed to trigger a read-only filesystem
     */
    public void setReadOnlyTrigger(int eventCount)
    {
        this.readOnlyTrigger = eventCount;
    }

    public String toString()
    {
        if (numBad == 0) {
            return "Dispatcher saw " + numSeen + " payloads";
        }

        return "Dispatcher saw " + numBad + " bad payloads (of " + numSeen +
            ")";
    }
}
