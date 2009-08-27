package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IPayloadDestination;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.SourceIdRegistry;
import icecube.daq.trigger.IReadoutRequest;
import icecube.daq.trigger.ITriggerRequestPayload;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class MockTriggerRequest
    implements Comparable, ITriggerRequestPayload
{
    private static final int LENGTH = 41;

    private IUTCTime startTime;
    private IUTCTime endTime;
    private int type;
    private int uid;
    private int srcId = SourceIdRegistry.GLOBAL_TRIGGER_SOURCE_ID;
    private IReadoutRequest rdoutReq;
    private boolean recycled;

    public MockTriggerRequest(long startVal, long endVal, int type, int uid)
    {
        if (startVal > endVal) {
            throw new Error("Starting time " + startVal +
                            " cannot be less than ending time " + endVal);
        }

        startTime = new MockUTCTime(startVal);
        endTime = new MockUTCTime(endVal);
        this.type = type;
        this.uid = uid;
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

    public Object deepCopy()
    {
        return new MockTriggerRequest(startTime.longValue(),
                                      endTime.longValue(), type, uid);
    }

    public void dispose()
    {
        throw new Error("Unimplemented");
    }

    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    public IUTCTime getFirstTimeUTC()
    {
        return startTime;
    }

    public List getHitList()
    {
        throw new Error("Unimplemented");
    }

    public IUTCTime getLastTimeUTC()
    {
        return endTime;
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

    public List getPayloads()
        throws DataFormatException
    {
        return new ArrayList();
    }

    public IReadoutRequest getReadoutRequest()
    {
        return rdoutReq;
    }

    public ISourceID getSourceID()
    {
        return new MockSourceID(srcId);
    }

    public int getTriggerConfigID()
    {
        throw new Error("Unimplemented");
    }

    public int getTriggerType()
    {
        return type;
    }

    public int getUID()
    {
        return uid;
    }

    public int hashCode()
    {
        return uid + type +
            (int) (startTime.longValue() % (long) Integer.MAX_VALUE) +
            (int) (endTime.longValue() % (long) Integer.MAX_VALUE);
    }

    public void loadPayload()
        throws IOException, DataFormatException
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
