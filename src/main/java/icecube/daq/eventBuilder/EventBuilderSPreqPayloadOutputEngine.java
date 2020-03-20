/*
 * class: EventBuilderSPreqPayloadOutputEngine
 *
 * Version $Id: EventBuilderSPreqPayloadOutputEngine.java
 *     3433 2008-08-31 16:19:12Z dglo $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.eventbuilder.io.SimpleDestinationOutputEngine;

/**
 * This class ...does what?
 *
 * @author mcp
 * @version $Id: EventBuilderSPreqPayloadOutputEngine.java
 *     3433 2008-08-31 16:19:12Z dglo $
 */
public class EventBuilderSPreqPayloadOutputEngine
    extends SimpleDestinationOutputEngine
    implements RequestPayloadOutputEngine
{
    /**
     * Create string processor request output engine.
     *
     * @param type engine type
     * @param id engine ID
     * @param fcn engine function
     */
    public EventBuilderSPreqPayloadOutputEngine(String type, int id,
                                                String fcn)
    {
        super(type, id, fcn);
    }
}
