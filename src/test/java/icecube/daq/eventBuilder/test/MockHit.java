package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IDOMID;
import icecube.daq.payload.IHitPayload;
import icecube.daq.payload.IPayloadDestination;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.IUTCTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

public class MockHit
    implements IHitPayload
{
    private static final int LENGTH = 13;

    public MockHit()
    {
    }

    public Object deepCopy()
    {
        return new MockHit();
    }

    public void dispose()
    {
        throw new Error("Unimplemented");
    }

    public IDOMID getDOMID()
    {
        throw new Error("Unimplemented");
    }

    public IUTCTime getHitTimeUTC()
    {
        throw new Error("Unimplemented");
    }

    public double getIntegratedCharge()
    {
        throw new Error("Unimplemented");
    }

    public ByteBuffer getPayloadBacking()
    {
        throw new Error("Unimplemented");
    }

    public int getPayloadInterfaceType()
    {
        throw new Error("Unimplemented");
    }

    public int getPayloadLength()
    {
        return LENGTH;
    }

    public IUTCTime getPayloadTimeUTC()
    {
        throw new Error("Unimplemented");
    }

    public int getPayloadType()
    {
        throw new Error("Unimplemented");
    }

    public ISourceID getSourceID()
    {
        throw new Error("Unimplemented");
    }

    public int getTriggerConfigID()
    {
        throw new Error("Unimplemented");
    }

    public int getTriggerType()
    {
        throw new Error("Unimplemented");
    }

    public long getUTCTime()
    {
        throw new Error("Unimplemented");
    }

    public void loadPayload()
        throws IOException, DataFormatException
    {
        throw new Error("Unimplemented");
    }

    public void recycle()
    {
        throw new Error("Unimplemented");
    }

    public void setCache(IByteBufferCache cache)
    {
        throw new Error("Unimplemented");
    }

    public int writePayload(boolean b0, IPayloadDestination x1)
        throws IOException
    {
        throw new Error("Unimplemented");
    }

    public int writePayload(boolean b0, int i1, ByteBuffer x2)
        throws IOException
    {
        throw new Error("Unimplemented");
    }
}
