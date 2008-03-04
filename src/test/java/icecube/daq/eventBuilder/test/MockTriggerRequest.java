package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IPayloadDestination;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.IUTCTime;
import icecube.daq.trigger.IReadoutRequest;
import icecube.daq.trigger.ITriggerRequestPayload;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.zip.DataFormatException;

public class MockTriggerRequest
    implements ITriggerRequestPayload
{
    private static final int LENGTH = 41;

    private IUTCTime startTime;
    private IUTCTime endTime;
    private int type;
    private int uid;

    public MockTriggerRequest(long startVal, long endVal, int type, int uid)
    {
        startTime = new MockUTCTime(startVal);
        endTime = new MockUTCTime(endVal);
        this.type = type;
        this.uid = uid;
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

    public IUTCTime getFirstTimeUTC()
    {
        return startTime;
    }

    public Vector getHitList()
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

    public Vector getPayloads()
        throws IOException, DataFormatException
    {
        throw new Error("Unimplemented");
    }

    public IReadoutRequest getReadoutRequest()
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
        return type;
    }

    public int getUID()
    {
        return uid;
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
