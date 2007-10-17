/*
 * Version $Id: EventBuilderSPcachePayloadOutputEngine.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.io.PayloadDestinationOutputEngine;

/**
 * Send cache flush messages to string processor.
 */
public class EventBuilderSPcachePayloadOutputEngine
    extends PayloadDestinationOutputEngine
{
    /**
     * Create an instance of this class.
     *
     * @param type engine type
     * @param id engine ID
     * @param fcn engine function
     */
    public EventBuilderSPcachePayloadOutputEngine(String type, int id,
                                                  String fcn)
    {
        super(type, id, fcn);
    }
}
