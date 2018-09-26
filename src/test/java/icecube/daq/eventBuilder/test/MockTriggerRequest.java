package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IReadoutRequest;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.ITriggerRequestPayload;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.SourceIdRegistry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MockTriggerRequest
    implements Comparable, ITriggerRequestPayload
{
    private static final int LENGTH = 41;

    private int uid;
    private int type;
    private int cfgId;
    private int srcId = SourceIdRegistry.GLOBAL_TRIGGER_SOURCE_ID;
    private IUTCTime startTime;
    private IUTCTime endTime;
    private IReadoutRequest rdoutReq;
    private boolean recycled;

    public MockTriggerRequest(int uid, int type, int cfgId, long startVal,
                              long endVal)
    {
        if (startVal > endVal) {
            throw new Error("Starting time " + startVal +
                            " cannot be less than ending time " + endVal);
        }

        this.uid = uid;
        this.type = type;
        this.cfgId = cfgId;
        startTime = new MockUTCTime(startVal);
        endTime = new MockUTCTime(endVal);
    }

    private static int compareTimes(IUTCTime a, IUTCTime b)
    {
        if (a == null) {
            if (b == null) {
                return 0;
            }

            return 1;
        } else if (b == null) {
            return -1;
        }

        return (int) (a.longValue() - b.longValue());
    }

    @Override
    public int compareTo(Object obj)
    {
        if (!(obj instanceof ITriggerRequestPayload)) {
            return getClass().getName().compareTo(obj.getClass().getName());
        }

        ITriggerRequestPayload req = (ITriggerRequestPayload) obj;
        int val = uid - req.getUID();
        if (val != 0) {
            val = compareTimes(startTime, req.getFirstTimeUTC());
            if (val != 0) {
                val = compareTimes(endTime, req.getLastTimeUTC());
            }
        }

        return val;
    }

    @Override
    public Object deepCopy()
    {
        return new MockTriggerRequest(uid, type, cfgId, startTime.longValue(),
                                      endTime.longValue());
    }

    @Override
    public void dispose()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    @Override
    public IUTCTime getFirstTimeUTC()
    {
        return startTime;
    }

    @Override
    public List getHitList()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public IUTCTime getLastTimeUTC()
    {
        return endTime;
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
        return new ArrayList();
    }

    @Override
    public IReadoutRequest getReadoutRequest()
    {
        return rdoutReq;
    }

    @Override
    public ISourceID getSourceID()
    {
        return new MockSourceID(srcId);
    }

    @Override
    public int getTriggerConfigID()
    {
        return cfgId;
    }

    @Override
    public String getTriggerName()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int getTriggerType()
    {
        return type;
    }

    @Override
    public int getUID()
    {
        return uid;
    }

    @Override
    public long getUTCTime()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int hashCode()
    {
        return uid + type +
            (int) (startTime.longValue() % (long) Integer.MAX_VALUE) +
            (int) (endTime.longValue() % (long) Integer.MAX_VALUE);
    }

    @Override
    public boolean isMerged()
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

    /**
     * Set the universal ID for global requests which will become events.
     *
     * @param uid new UID
     */
    @Override
    public void setUID(int uid)
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
