/*
 * class: RequestPayloadOutputEngine
 *
 * Version $Id: RequestPayloadOutputEngine.java,v 1.3 2005/11/28 23:18:56 dglo Exp $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.payload.IByteBufferCache;
import icecube.daq.payload.IPayloadDestinationCollection;

/**
 * Request payload interface.
 */
public interface RequestPayloadOutputEngine
{
    /**
     * Get collection of payload destinations.
     *
     * @return collection of payload destinations
     */
    IPayloadDestinationCollection getPayloadDestinationCollection();

    /**
     * Register the buffer cache manager.
     *
     * @param bufMgr buffer cache manager
     */
    void registerBufferManager(IByteBufferCache bufMgr);

    /**
     * Send STOP message.
     */
    void sendLastAndStop();
}
