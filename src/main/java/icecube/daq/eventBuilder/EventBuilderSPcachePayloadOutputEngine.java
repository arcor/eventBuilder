/*
 * Version $Id: EventBuilderSPcachePayloadOutputEngine.java,v 1.7 2005/12/15 17:00:28 dglo Exp $
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
