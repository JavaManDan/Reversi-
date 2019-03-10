
/**
 * FileIssuesException is used when a file has problem loading the file.
 *
 * @author DTK3 Daniel Knight
 * @version (version 7 29/04/18)
 */
public class FileIssueException extends Exception
{
    //file name
    private String file;

    /**
     * Constructor for objects of class FileIssuesException
     */
    public FileIssueException(String file)
    {
       this.file = file;       
    }
    
    /**
     *  Creates a string with the error message and file name that caused it.
     *  
     *  @return The string details.
     */
    public String toString(){
        return "There was a problem opening the File: " + file;
    }

}
