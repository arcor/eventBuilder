package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IEventHitRecord;
import icecube.daq.payload.IHitDataPayload;
import icecube.daq.payload.IHitRecordList;
import icecube.daq.payload.PayloadException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class MockHitRecord
    implements IEventHitRecord
{
    private short chanId;
    private long hitTime;

    MockHitRecord(short chanId, long hitTime)
    {
        this.chanId = chanId;
        this.hitTime = hitTime;
    }

    public long getHitTime()
    {
        return hitTime;
    }

    public int length()
    {
        return 10;
    }

    public boolean matches(IHitDataPayload pay)
    {
        throw new Error("Unimplemented");
    }

    public int writeRecord(ByteBuffer buf, int offset, long baseTime)
        throws PayloadException
    {
        throw new Error("Unimplemented");
    }
}

public class MockHitRecordList
    implements IHitRecordList
{
    private int uid;
    private List<IEventHitRecord> recList;

    public MockHitRecordList(int uid)
    {
        this.uid = uid;

        recList = new ArrayList<IEventHitRecord>();
    }

    public void addRecord(short chanId, long hitTime)
    {
        recList.add(new MockHitRecord(chanId, hitTime));
    }

    public int getUID()
    {
        return uid;
    }

    public Iterator<IEventHitRecord> iterator()
    {
        return recList.iterator();
    }
}
