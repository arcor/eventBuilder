/*
 * interface: IPayloadDestinationCollection
 *
 * Version $Id: IPayloadDestinationCollection.java 17772 2020-03-20 14:31:55Z dglo $
 *
 * Date: October 19 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventbuilder.io;

import icecube.daq.payload.IPayload;
import icecube.daq.payload.ISourceID;

import java.io.IOException;
import java.util.Collection;

/**
 * This interface defines a collection of PayloadDestinations.
 *
 * @version $Id: IPayloadDestinationCollection.java 17772 2020-03-20 14:31:55Z dglo $
 * @author pat
 */
public interface IPayloadDestinationCollection
{
    /**
     * Add a PayloadDestination associated with a SourceId to the collection.
     * @param sourceId     SourceId of this destination
     * @param destination  PayloadDestination
     */
    void addPayloadDestination(ISourceID sourceId,
                               IPayloadDestination destination);

    /**
     * Close the output object.
     *
     * @throws IOException if there is a close error
     */
    void close()
        throws IOException;

    /**
     * Close the PayloadDestination for this SourceId.
     * @param sourceId SourceId of destination to close
     * @throws IOException if there is a close error from the underlying
     *                     PayloadDestination
     */
    void closePayloadDestination(ISourceID sourceId)
        throws IOException;

    /**
     * Close all PayloadDestinations.
     * @throws IOException if there is a close error from the underlying
     *                     PayloadDestination
     */
    void closeAllPayloadDestinations()
        throws IOException;

    /**
     * Get the PayloadDestination associated with a SourceId.
     * @param sourceId SourceId of the destination
     * @return a PayloadDestination, or null if one is not associated with the
     *         SourceId
     */
    IPayloadDestination getPayloadDestination(ISourceID sourceId);

    /**
     * Get all PayloadDestinations.
     * @return a Collection of PayloadDestinations
     */
    Collection<IPayloadDestination> getAllPayloadDestinations();

    /**
     * Get all PayloadDestination SourceIds.
     * @return a Collection of SourceIds
     */
    Collection<ISourceID> getAllSourceIDs();

    /**
     * Register a Controller object for this collection.
     * @param controller object that will control this collection
     */
    void registerController(IPayloadDestinationCollectionController controller);

    /**
     * Stop the output object.
     *
     * @throws IOException if there is a stop error
     */
    void stop()
        throws IOException;

    /**
     * Stop all PayloadDestinations.
     * @throws IOException is one or more destinations cannot be stopped
     */
    void stopAllPayloadDestinations()
        throws IOException;

    /**
     * Stop a PayloadDestination that maps this sourceID.
     * @param sourceId SourceId of destination to stop
     * @throws IOException if destination cannot be stopped
     */
    void stopPayloadDestination(ISourceID sourceId)
        throws IOException;

    /**
     * Write a Payload.
     *
     * @param payload Payload to write
     *
     * @return total number of bytes written
     *
     * @throws IOException if there is a write error
     */
    int writePayload(IPayload payload)
        throws IOException;

    /**
     * Write a Payload to a destination specified by SourceId.
     * @param sourceId SourceId of destination
     * @param payload  Payload to write
     * @return number of bytes written
     * @throws IOException if there is a write error from the underlying
     *                     PayloadDestination
     */
    int writePayload(ISourceID sourceId, IPayload payload)
        throws IOException;
}
