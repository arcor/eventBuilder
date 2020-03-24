package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IDOMID;
import icecube.daq.payload.IHitPayload;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.IUTCTime;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MockHit
    implements IHitPayload
{
    private static final int LENGTH = 13;

    public MockHit()
    {
    }

    @Override
    public Object deepCopy()
    {
        return new MockHit();
    }

    @Override
    public short getChannelID()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public IDOMID getDOMID()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public double getIntegratedCharge()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public ByteBuffer getPayloadBacking()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public IUTCTime getPayloadTimeUTC()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int getPayloadType()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public ISourceID getSourceID()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int getTriggerConfigID()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int getTriggerType()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public long getUTCTime()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public boolean hasChannelID()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int length()
    {
        return LENGTH;
    }

    @Override
    public void loadPayload()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public void recycle()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public void setCache(IByteBufferCache cache)
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int writePayload(boolean b0, int i1, ByteBuffer x2)
        throws IOException
    {
        throw new Error("Unimplemented");
    }
}
