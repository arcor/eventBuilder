/*
 * class: SystemTestPayloadOutputEngine
 *
 * Version $Id: EventBuilderSPreqPayloadOutputEngine.java 2921 2008-04-14 21:23:54Z dglo $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.io.SimpleDestinationOutputEngine;

/**
 * This class ...does what?
 *
 * @author mcp
 * @version $Id: EventBuilderSPreqPayloadOutputEngine.java 2921 2008-04-14 21:23:54Z dglo $
 */
public class EventBuilderSPreqPayloadOutputEngine
    extends SimpleDestinationOutputEngine
    implements RequestPayloadOutputEngine
{
    /**
     * Create string processor request output engine.
     *
     * @param server MBean server
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
