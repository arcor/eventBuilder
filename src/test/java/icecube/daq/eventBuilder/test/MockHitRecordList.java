package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IEventHitRecord;
import icecube.daq.payload.IHitPayload;
import icecube.daq.payload.IHitRecordList;
import icecube.daq.payload.ILoadablePayload;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.IWriteablePayload;
import icecube.daq.payload.PayloadException;
import icecube.daq.util.IDOMRegistry;

import java.io.IOException;
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

    @Override
    public short getChannelID()
    {
        return chanId;
    }

    @Override
    public long getHitTime()
    {
        return hitTime;
    }

    @Override
    public int length()
    {
        return 10;
    }

    @Override
    public boolean matches(IDOMRegistry domRegistry, IHitPayload pay)
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int writeRecord(ByteBuffer buf, int offset, long baseTime)
        throws PayloadException
    {
        throw new Error("Unimplemented");
    }
}

public class MockHitRecordList
    implements IHitRecordList, ILoadablePayload, IWriteablePayload
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

    @Override
    public Object deepCopy()
    {
        MockHitRecordList hrl = new MockHitRecordList(uid);
        for (IEventHitRecord hitRec : recList) {
            MockHitRecord mock = (MockHitRecord) hitRec;
            hrl.addRecord(mock.getChannelID(), mock.getHitTime());
        }
        return hrl;
    }

    @Override
    public void dispose()
    {
        throw new Error("Unimplemented");
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
    public int getUID()
    {
        return uid;
    }

    @Override
    public long getUTCTime()
    {
        throw new Error("Unimplemented");
    }

    public Iterator<IEventHitRecord> iterator()
    {
        return recList.iterator();
    }

    @Override
    public int length()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public void loadPayload()
    {
        throw new Error("Unimplemented");
    }

    @Override
    public void recycle()
    {
        uid = -1;

        if (recList != null) {
            recList.clear();
            recList = null;
        }
    }

    @Override
    public void setCache(IByteBufferCache x0)
    {
        throw new Error("Unimplemented");
    }

    @Override
    public int writePayload(boolean b0, int i1, ByteBuffer x2)
        throws IOException
    {
        throw new IOException("Unimplemented");
    }

    @Override
    public String toString()
    {
        return "MockHitRecordList[uid " + uid + ", " +
            recList.size() + " recs]";
    }
}
