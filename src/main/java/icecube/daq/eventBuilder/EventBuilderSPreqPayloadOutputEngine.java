/*
 * class: SystemTestPayloadOutputEngine
 *
 * Version $Id: EventBuilderSPreqPayloadOutputEngine.java 2751 2008-03-05 01:47:18Z ksb $
 *
 * Date: May 23 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.eventBuilder;

import icecube.daq.io.MultiDestinationOutputEngine;

/**
 * This class ...does what?
 *
 * @author mcp
 * @version $Id: EventBuilderSPreqPayloadOutputEngine.java 2751 2008-03-05 01:47:18Z ksb $
 */
public class EventBuilderSPreqPayloadOutputEngine
    extends MultiDestinationOutputEngine
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
