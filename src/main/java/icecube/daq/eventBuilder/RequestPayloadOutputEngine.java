/*
 * class: RequestPayloadOutputEngine
 *
 * Version $Id: RequestPayloadOutputEngine.java 17846 2020-08-14 17:14:18Z dglo $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.eventBuilder.io.IPayloadDestinationCollection;
import icecube.daq.payload.IByteBufferCache;

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
