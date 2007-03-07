/*
 * class: EventBuilderSPdataPayloadInputEngine
 *
 * Version $Id: EventBuilderSPdataPayloadInputEngine.java,v 1.37.4.5 2006/11/02 16:41:39 dglo Exp $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;

import icecube.daq.eventBuilder.exceptions.EventBuilderException;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.MasterPayloadFactory;

import icecube.daq.splicer.Splicer;

import icecube.daq.io.SpliceablePayloadInputEngine;

import java.util.Collection;
import java.util.Iterator;

/**
 * Read requested data from string processor.
 * @version $Id: EventBuilderSPdataPayloadInputEngine.java,v 1.37.4.5 2006/11/02 16:41:39 dglo Exp $
 */
public class EventBuilderSPdataPayloadInputEngine
    extends SpliceablePayloadInputEngine
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
    public EventBuilderSPdataPayloadInputEngine(String name,
                                                int id,
                                                String fcn,
                                                IByteBufferCache bufMgr,
                                                MasterPayloadFactory factory,
                                                Splicer splicer)
    {
        super(name, id, fcn, splicer, factory);

        if (bufMgr == null) {
            throw new IllegalArgumentException("Buffer cache manager cannot" +
                                               " be null");
        }
        this.bufMgr = bufMgr;
    }

    /**
     * Pretend we're always healthy so that the run doesn't tip over,
     * causing readout data payloads to  back up in the string processor
     * as was originally designed.
     *
     * Return <tt>true</tt> always.
     */
    public boolean isHealthy()
    {
        return true;
    }
}
