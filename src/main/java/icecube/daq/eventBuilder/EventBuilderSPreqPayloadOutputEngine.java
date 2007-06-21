/*
 * class: SystemTestPayloadOutputEngine
 *
 * Version $Id: EventBuilderSPreqPayloadOutputEngine.java,v 1.10 2005/11/18 20:42:54 dglo Exp $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.io.PayloadDestinationOutputEngine;

/**
 * This class ...does what?
 *
 * @author mcp
 * @version $Id: EventBuilderSPreqPayloadOutputEngine.java,v 1.10 2005/11/18 20:42:54 dglo Exp $
 */
public class EventBuilderSPreqPayloadOutputEngine
    extends PayloadDestinationOutputEngine
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
