package icecube.daq.eventBuilder.test;

import icecube.daq.splicer.Spliceable;
import icecube.daq.splicer.SpliceableFactory;

import java.nio.ByteBuffer;
import java.util.List;

public class MockFactory
    implements SpliceableFactory
{
    public MockFactory()
    {
    }

    public void backingBufferShift(List x0, int i1, int i2)
    {
        throw new Error("Unimplemented");
    }

    public Spliceable createCurrentPlaceSpliceable()
    {
        throw new Error("Unimplemented");
    }

    public Spliceable createSpliceable(ByteBuffer x0)
    {
        throw new Error("Unimplemented");
    }

    public void invalidateSpliceables(List x0)
    {
        throw new Error("Unimplemented");
    }

    public boolean skipSpliceable(ByteBuffer x0)
    {
        throw new Error("Unimplemented");
    }
}
