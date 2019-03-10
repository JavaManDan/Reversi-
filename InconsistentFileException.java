
/**
 * InconsistentFileException is thrown when a file contents do not match.
 *
 * @author DTK3 Daniel Knight
 * @version (version 7 29/04/18)
 */
public class InconsistentFileException extends Exception
{
    // file name
    private String file;

    /**
     * Constructor for objects of class InconsistentFileException
     */
    public InconsistentFileException(String file)
    {
        this.file = file;
    }
    
     /**
     *  Creates a string with the error message and file name that caused it.
     *  
     *  @return The string details.
     */
    public String toString(){
        return "Could not open file due to inconsistent piece count: " + file;
    }
}
