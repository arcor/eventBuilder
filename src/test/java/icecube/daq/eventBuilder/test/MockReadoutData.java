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

    public Object deepCopy()
    {
        return new MockReadoutData(uid, srcId.getSourceID(),
                                   startTime.longValue(), endTime.longValue());
    }

    public void dispose()
    {
        throw new Error("Unimplemented");
    }

    public List getDataPayloads()
    {
        return null;
    }

    public IUTCTime getFirstTimeUTC()
    {
        return startTime;
    }

    public List getHitList()
    {
        return null;
    }

    public IUTCTime getLastTimeUTC()
    {
        return endTime;
    }

    public int getNumHits()
    {
        return 0;
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
        return length();
    }

    public IUTCTime getPayloadTimeUTC()
    {
        throw new Error("Unimplemented");
    }

    public int getPayloadType()
    {
        throw new Error("Unimplemented");
    }

    public List getPayloads()
    {
        throw new Error("Unimplemented");
    }

    public int getReadoutDataPayloadNumber()
    {
        throw new Error("Unimplemented");
    }

    public int getRequestUID()
    {
        return uid;
    }

    public ISourceID getSourceID()
    {
        return srcId;
    }

    public int getTriggerConfigID()
    {
        return -1;
    }

    public int getTriggerType()
    {
        throw new Error("Unimplemented");
    }

    public long getUTCTime()
    {
        throw new Error("Unimplemented");
    }

    public boolean isLastPayloadOfGroup()
    {
        throw new Error("Unimplemented");
    }

    public int length()
    {
        return 0;
    }

    public void loadPayload()
    {
        // unneeded
    }

    public void recycle()
    {
        if (recycled) {
            throw new Error("Payload has already been recycled");
        }

        recycled = true;
    }

    public void setCache(IByteBufferCache cache)
    {
        throw new Error("Unimplemented");
    }

    public int writePayload(boolean b0, int i1, ByteBuffer x2)
        throws IOException
    {
        throw new Error("Unimplemented");
    }
}
