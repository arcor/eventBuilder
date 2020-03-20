/*
 * class: RequestPayloadOutputEngine
 *
 * Version $Id: RequestPayloadOutputEngine.java 17772 2020-03-20 14:31:55Z dglo $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.eventbuilder.io.IPayloadDestinationCollection;
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
