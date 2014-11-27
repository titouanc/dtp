/**
 * 
 */
package be.ac.ulb.infof307.g03.utils;


import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * @author pierre
 *
 */
public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuffer buffer = new StringBuffer(1000);
        buffer.append(" ");
        buffer.append(record.getLevel());
        buffer.append(" ");
        buffer.append(record.getMessage());
        buffer.append("\n");
        return buffer.toString();
    }
    
    /**
     * @return Return the header of the message
     */
    public String getHead(Handler h) {
        return "[Log] " + (new Date());
      }
    

}