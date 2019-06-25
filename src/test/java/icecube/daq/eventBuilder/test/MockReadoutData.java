package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IReadoutDataPayload;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.IUTCTime;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class MockReadoutData
    implements IReadoutDataPayload
{
    private int uid;
    private ISourceID srcId;
    private IUTCTime startTime;
    private IUTCTime endTime;
    private boolean recycled;

    public MockReadoutData(int uid, int srcId, long startTime, long endTime)
    {
        if (startTime > endTime) {
            throw new Error("Starting time " + startTime +
                            " cannot be less than ending time " + endTime);
        }

        this.uid = uid;

        this.srcId = new MockSourceID(srcId);
        this.startTime = new MockUTCTime(startTime);
        this.endTime = new MockUTCTime(endTime);
    }

    @Override
    public Object deepCopy()
    {
        return new MockReadoutData(uid, srcId.getSourceID(),
                                   startTime.longValue(), endTime.longValue());
    }

    @Override
    public void dispose()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public List getDataPayloads()
    {
        return null;
    }

    @Override
    public IUTCTime getFirstTimeUTC()
    {
        return startTime;
    }

    @Override
    public List getHitList()
    {
        return null;
    }

    @Override
    public IUTCTime getLastTimeUTC()
    {
        return endTime;
    }

    @Override
    public int getNumHits()
    {
        return 0;
    }

    @Override
    public ByteBuffer getPayloadBacking()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int getPayloadInterfaceType()
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
    public List getPayloads()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int getReadoutDataPayloadNumber()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int getRequestUID()
    {
        return uid;
    }

    @Override
    public ISourceID getSourceID()
    {
        return srcId;
    }

    @Override
    public int getTriggerConfigID()
    {
        return -1;
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
    public boolean isLastPayloadOfGroup()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int length()
    {
        return 0;
    }

    @Override
    public void loadPayload()
    {
        // unneeded
    }

    @Override
    public void recycle()
    {
        if (recycled) {
            throw new Error("Payload has already been recycled");
        }

        recycled = true;
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
