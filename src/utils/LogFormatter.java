/**
 * 
 */
package utils;


import java.util.logging.Formatter;
import java.util.logging.LogRecord;


/**
 * @author pierre
 *
 */
public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(record.getMessage());
        return buffer.toString();
    }

}