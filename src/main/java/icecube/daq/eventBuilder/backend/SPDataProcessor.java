package icecube.daq.eventBuilder.backend;

import icecube.daq.io.DispatchException;
import icecube.daq.payload.ILoadablePayload;

import java.util.Collection;
import java.util.List;

/**
 * Interface between spliced analysis and back end.
 */
public interface SPDataProcessor
{
    /**
     * Receive data from splicer.
     *
     * @param newData list of new data
     * @param offset number of previously-seen data at front of list
     */
    void addData(List<ILoadablePayload> newData, int offset);

    /**
     * Increment the count of splicer.execute() calls.
     */
    void addExecuteCall();

    /**
     * Add data received while splicer is stopping.
     *
     * @param coll collection of final data payloads
     */
    void addFinalData(Collection coll);

    /**
     * Increment the count of splicer.truncate() calls.
     */
    void addTruncateCall();

    /**
     * Mark data boundary between runs.
     *
     * @param message run message
     *
     * @throws DispatchException if there is a problem changing the run
     */
    void dataBoundary(String message) throws DispatchException;

    /**
     * Recycle all payloads in the list.
     *
     * @param payloadList list of payloads
     */
    void recycleAll(Collection payloadList);

    /**
     * Record the length of the list passed to splicedAnalysis.execute().
     *
     * @param execListLen list length
     */
    void setExecuteListLength(int execListLen);

    /**
     * Inform processor that the splicer has stopped.
     */
    void splicerStopped();
}
