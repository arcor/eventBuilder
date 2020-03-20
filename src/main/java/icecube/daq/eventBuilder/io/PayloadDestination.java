package icecube.daq.eventbuilder.io;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *  This class is meant to be a null implementation
 *  of the PayloadDestination.
 */
public abstract class PayloadDestination
    implements IPayloadDestination
{
    /**
     * Utility function to throw an exception for un-implemented methods.
     * @param sMethod String holding the method name from which this method
     *                was called.
     */
    protected static void errorUnimplementedMethod(String sMethod)
        throws IOException
    {
        throw new IOException("DataOutputAdapter: "+sMethod+"not implemented");
    }

    /**
     * Tells whether or not this channel is open.
     *
     * @return <tt>true</tt> if, and only if, this channel is open
     */
    @Override
    public boolean isOpen() {
        return true;
    }

    /**
     * Closes this channel.
     *
     * <p> After a channel is closed, any further attempt to invoke I/O
     * operations upon it will cause a {@link java.nio.channels.ClosedChannelException} to be
     * thrown.
     *
     * <p> If this channel is already closed then invoking this method has no
     * effect.
     *
     * <p> This method may be invoked at any time.  If some other thread has
     * already invoked it, however, then another invocation will block until
     * the first invocation is complete, after which it will return without
     * effect. </p>
     *
     * @throws  IOException  If an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        // errorUnimplementedMethod("close()");
    }
}
