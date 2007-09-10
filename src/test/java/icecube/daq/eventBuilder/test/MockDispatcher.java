package icecube.daq.eventBuilder.test;

import icecube.daq.io.DispatchException;
import icecube.daq.io.Dispatcher;
import icecube.daq.payload.splicer.Payload;

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

    public void dispatchEvent(ByteBuffer x0)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dispatchEvent(Payload x0)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dispatchEvents(ByteBuffer x0, int[] il1)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public void dispatchEvents(ByteBuffer x0, int[] il1, int i2)
        throws DispatchException
    {
        throw new Error("Unimplemented");
    }

    public int getDiskAvailable()
    {
        throw new Error("Unimplemented");
    }

    public int getDiskSize()
    {
        throw new Error("Unimplemented");
    }

    public long getTotalDispatchedEvents()
    {
        throw new Error("Unimplemented");
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
