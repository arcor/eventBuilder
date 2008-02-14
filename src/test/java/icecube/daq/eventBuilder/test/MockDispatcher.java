package icecube.daq.eventBuilder.test;

import icecube.daq.io.DispatchException;
import icecube.daq.io.Dispatcher;
import icecube.daq.payload.IWriteablePayload;

import java.nio.ByteBuffer;

public class MockDispatcher
    implements Dispatcher
{
    public MockDispatcher()
    {
    }

    public void dataBoundary()
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dataBoundary(String s0)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dispatchEvent(ByteBuffer buf)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dispatchEvent(IWriteablePayload pay)
        throws DispatchException
    {
        System.err.println("DISP " + pay);
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

    public int getDiskAvailable()
    {
        return 0;
    }

    public int getDiskSize()
    {
        return 0;
    }

    public long getTotalDispatchedEvents()
    {
        return 0;
    }

    public void setDispatchDestStorage(String s0)
    {
        throw new Error("Unimplemented");
    }

    public void setMaxFileSize(long x0)
    {
        throw new Error("Unimplemented");
    }
}
