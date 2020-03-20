/*
 * class: PayloadDestinationCollection
 *
 * Version $Id: PayloadDestinationCollection.java 17772 2020-03-20 14:31:55Z dglo $
 *
 * Date: October 19 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventbuilder.io;

import icecube.daq.payload.IPayload;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.impl.SourceID;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a simple implementation of the IPayloadDestinationCollection
 * interface.
 *
 * @version $Id: PayloadDestinationCollection.java 17772 2020-03-20 14:31:55Z dglo $
 * @author pat
 */
public class PayloadDestinationCollection
    implements IPayloadDestinationCollection
{

    Map<ISourceID, IPayloadDestination> destinationMap =
        new HashMap<ISourceID, IPayloadDestination>();
    IPayloadDestinationCollectionController controller;

    /**
     * Default constructor.
     */
    public PayloadDestinationCollection()
    {
    }

    /**
     * Constructor for a collection with a single destination.
     * SourceID will be 0.
     *
     * @param destination PayloadDestination
     */
    public PayloadDestinationCollection(IPayloadDestination destination)
    {
        addPayloadDestination(new SourceID(0), destination);
    }

    /**
     * Constructor for a collection with a single destination.
     * @param sourceId    SourceId of the destination
     * @param destination PayloadDestination
     */
    public PayloadDestinationCollection(ISourceID sourceId,
                                        IPayloadDestination destination)
    {
        addPayloadDestination(sourceId, destination);
    }

    /**
     * Add a PayloadDestination associated with a SourceId to the collection.
     * @param sourceId     SourceId of this destination
     * @param destination  PayloadDestination
     */
    @Override
    public void addPayloadDestination(ISourceID sourceId,
                                      IPayloadDestination destination)
    {
        destinationMap.put(sourceId, destination);
    }

    /**
     * Get the PayloadDestination associated with a SourceId.
     * @param sourceId SourceId of the destination
     * @return a PayloadDestination, or null if one is not associated with
     *         the SourceId
     */
    @Override
    public IPayloadDestination getPayloadDestination(ISourceID sourceId)
    {
        return destinationMap.get(sourceId);
    }

    /**
     * Get all PayloadDestinations.
     * @return a Collection of PayloadDestinations
     */
    @Override
    public Collection<IPayloadDestination> getAllPayloadDestinations()
    {
        return destinationMap.values();
    }


    /**
     * Return the source IDs for all payload destinations in the collection.
     *
     * @return collection of source IDs
     */
    @Override
    public Collection<ISourceID> getAllSourceIDs()
    {
        return destinationMap.keySet();
    }

    /**
     * Write a Payload to a destination specified by SourceId.
     * @param sourceId SourceId of destination
     * @param payload  Payload to write
     * @return number of bytes written
     * @throws IOException if there is a write error from the underlying
     *                     PayloadDestination
     */
    @Override
    public int writePayload(ISourceID sourceId, IPayload payload)
        throws IOException
    {
        if (!destinationMap.containsKey(sourceId)) {
            final String errMsg = "No destination for source ID " +
                sourceId.getSourceID();
            throw new IllegalArgumentException(errMsg);
        }

        return ((IPayloadDestination) destinationMap.get(sourceId)).
            writePayload(false, payload);
    }

    /**
     * Write a Payload to all destinations.
     * @param payload Payload to write
     * @return total number of bytes written
     * @throws IOException if there is a write error from the underlying
     *                     PayloadDestination
     */
    @Override
    public int writePayload(IPayload payload)
        throws IOException
    {
        int nWrite = 0;
        Iterator destinationIter = destinationMap.keySet().iterator();
        while (destinationIter.hasNext()) {
            nWrite += writePayload((ISourceID) destinationIter.next(),
                                   payload);
        }
        return nWrite;
    }

    /**
     * Close the PayloadDestination for this SourceId.
     * @param sourceId SourceId of destination to close
     * @throws IOException if there is a close error from the underlying
     *                     PayloadDestination
     */
    @Override
    public void closePayloadDestination(ISourceID sourceId)
        throws IOException
    {
        ((IPayloadDestination) destinationMap.get(sourceId)).close();
        destinationMap.remove(sourceId);
        if (controller != null) {
            controller.payloadDestinationClosed(sourceId);
            if (destinationMap.isEmpty()) {
                controller.allPayloadDestinationsClosed();
            }
        }
    }

    /**
     * Close all PayloadDestinations.
     * @throws IOException if there is a close error from the underlying
     *                     PayloadDestination
     */
    @Override
    public void close()
        throws IOException
    {
        closeAllPayloadDestinations();
    }

    /**
     * Close all PayloadDestinations.
     * @throws IOException if there is a close error from the underlying
     *                     PayloadDestination
     */
    @Override
    public synchronized void closeAllPayloadDestinations()
        throws IOException
    {
        Iterator destinationIter = destinationMap.keySet().iterator();
        while (destinationIter.hasNext()) {
            closePayloadDestination((ISourceID) destinationIter.next());
        }
    }

    /**
     * Register a Controller object for this collection.
     * @param controller object that will control this collection
     */
    @Override
    public void registerController(IPayloadDestinationCollectionController controller)
    {
        this.controller = controller;
    }

    /**
     * Stop all PayloadDestinations.
     * @throws IOException
     */
    @Override
    public void stop()
        throws IOException
    {
        stopAllPayloadDestinations();
    }

    /**
     * Stop all PayloadDestinations.
     * @throws IOException
     */
    @Override
    public void stopAllPayloadDestinations()
        throws IOException
    {
        if (controller != null) {
            controller.allPayloadDestinationsClosed();
        }
    }

    /**
     * Stop a PayloadDestination that maps this sourceID.
     * @param sourceId SourceId of destination to stop
     * @throws IOException
     */
    @Override
    public void stopPayloadDestination(ISourceID sourceId)
        throws IOException
    {
        if (controller != null) {
            controller.payloadDestinationClosed(sourceId);
        }
    }
}
