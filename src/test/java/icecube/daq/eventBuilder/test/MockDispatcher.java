package icecube.daq.eventBuilder.test;

import icecube.daq.io.DispatchException;
import icecube.daq.io.Dispatcher;
import icecube.daq.payload.IEventPayload;
import icecube.daq.payload.IWriteablePayload;
import icecube.daq.payload.PayloadChecker;

import java.nio.ByteBuffer;

public class MockDispatcher
    implements Dispatcher
{
    private int numSeen = 0;
    private int numBad = 0;

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

    public void dataBoundary(String s0)
        throws DispatchException
    {
        // ignored
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
        if (!PayloadChecker.validateEvent((IEventPayload) pay, true)) {
            numBad++;
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

    public long getDiskAvailable()
    {
        return 0;
    }

    public long getDiskSize()
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

    public void setDispatchDestStorage(String s0)
    {
        throw new Error("Unimplemented");
    }

    public void setMaxFileSize(long x0)
    {
        throw new Error("Unimplemented");
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
