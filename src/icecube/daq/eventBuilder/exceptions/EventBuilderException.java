package icecube.daq.eventBuilder.exceptions;

/**
 * User: nehar
 * Date: Aug 29, 2005
 * Time: 9:00:39 PM
 *
 * The base Event Builder exception class. This forms the root of the
 * eventbuilder exception hierarchy.
 *
 */

public class EventBuilderException
    extends Exception
{
    /**
     * default constructor
     */
    public EventBuilderException()
    {
    }

    /**
     * constructor taking a message
     * @param message message associated with this exception
     */
    public EventBuilderException(String message)
    {
        super(message);
    }

    /**
     * constructor taking an exception
     * @param exception the exception
     */
    public EventBuilderException(Exception exception)
    {
        super(exception);
    }

    /**
     * constructor taking a message and exception
     * @param message message associated with this exception
     * @param exception the exception
     */
    public EventBuilderException(String message, Exception exception)
    {
        super(message, exception);
    }
}
