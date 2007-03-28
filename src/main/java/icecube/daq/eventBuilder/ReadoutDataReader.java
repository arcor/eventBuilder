package icecube.daq.eventBuilder;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;

import icecube.daq.eventBuilder.exceptions.EventBuilderException;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.MasterPayloadFactory;

import icecube.daq.splicer.Splicer;

import icecube.daq.io.SpliceablePayloadReader;

import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;

public class ReadoutDataReader
    extends SpliceablePayloadReader
{
    private IByteBufferCache bufMgr;

    /**
     * Build input engine.
     *
     * @param name input engine name
     * @param id input engine ID
     * @param fcn input engine function(?)
     * @param bufMgr buffer cache manager
     * @param factory payload factory
     * @param splicer hit data splicer
     */
    public ReadoutDataReader(String name, Splicer splicer,
                             MasterPayloadFactory factory,
                             IByteBufferCache bufMgr)
        throws IOException
    {
        super(name, splicer, factory);

        if (bufMgr == null) {
            throw new IllegalArgumentException("Buffer cache manager cannot" +
                                               " be null");
        }
        this.bufMgr = bufMgr;
    }
}
