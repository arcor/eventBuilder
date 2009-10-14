package icecube.daq.eventBuilder.test;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IEventHitRecord;
import icecube.daq.payload.IHitDataPayload;
import icecube.daq.payload.IHitRecordList;
import icecube.daq.payload.ILoadablePayload;
import icecube.daq.payload.IPayloadDestination;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.IWriteablePayload;
import icecube.daq.payload.PayloadException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

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

    public short getChannelId()
    {
        return chanId;
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

    public Object deepCopy()
    {
        MockHitRecordList hrl = new MockHitRecordList(uid);
        for (IEventHitRecord hitRec : recList) {
            MockHitRecord mock = (MockHitRecord) hitRec;
            hrl.addRecord(mock.getChannelId(), mock.getHitTime());
        }
        return hrl;
    }

    public void dispose()
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
        throw new Error("Unimplemented");
    }

    public IUTCTime getPayloadTimeUTC()
    {
        throw new Error("Unimplemented");
    }

    public int getPayloadType()
    {
        throw new Error("Unimplemented");
    }

    public int getUID()
    {
        return uid;
    }

    public Iterator<IEventHitRecord> iterator()
    {
        return recList.iterator();
    }

    public void loadPayload()
        throws IOException, DataFormatException
    {
        throw new IOException("Unimplemented");
    }

    public void recycle()
    {
        uid = -1;

        if (recList != null) {
            recList.clear();
            recList = null;
        }
    }

    public void setCache(IByteBufferCache x0)
    {
        throw new Error("Unimplemented");
    }

    public int writePayload(boolean b0, IPayloadDestination x1)
        throws IOException
    {
        throw new IOException("Unimplemented");
    }

    public int writePayload(boolean b0, int i1, ByteBuffer x2)
        throws IOException
    {
        throw new IOException("Unimplemented");
    }

    public String toString()
    {
        return "MockHitRecordList[uid " + uid + ", " +
            recList.size() + " recs]";
    }
}
