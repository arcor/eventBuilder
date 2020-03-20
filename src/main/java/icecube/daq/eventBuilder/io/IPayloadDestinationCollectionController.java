/*
 * interface: IPayloadDestinationCollectionController
 *
 * Version $Id: IPayloadDestinationCollectionController.java 17772 2020-03-20 14:31:55Z dglo $
 *
 * Date: October 19 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventbuilder.io;

import icecube.daq.payload.ISourceID;

/**
 * This interface defines an object the will create and control a
 *  PayloadDestinationCollection.
 *
 * @version $Id: IPayloadDestinationCollectionController.java 17772 2020-03-20 14:31:55Z dglo $
 * @author pat
 */
public interface IPayloadDestinationCollectionController
{

    /**
     * Get the PayloadDestinationCollection.
     * @return the PayloadDestinationCollection
     */
    IPayloadDestinationCollection getPayloadDestinationCollection();

    /**
     * Callback method that indicates that the PayloadDestination associated
     * with this SourceId has been closed by the user.
     * @param sourceId SourceId of closed PayloadDestination
     */
    void payloadDestinationClosed(ISourceID sourceId);

    /**
     * Callback method that indicates all PayloadDestinations have been closed.
     */
    void allPayloadDestinationsClosed();
}
