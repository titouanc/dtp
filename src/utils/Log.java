package utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author pierre
 *
 */
public class Log {
    static Logger _logger;
    private ConsoleHandler _consoleHandler;
    private LogFormatter _plainTextFormatter;

    private Log() {
    	_logger = Logger.getLogger(Log.class.getName());
    	_logger.setLevel(Level.ALL);
        _consoleHandler = new ConsoleHandler();
        _plainTextFormatter = new LogFormatter();
        _consoleHandler.setFormatter(_plainTextFormatter);
        _consoleHandler.setLevel(Level.ALL);
        _logger.addHandler(_consoleHandler);

    }
    
    private static Logger getLogger(){
        if(_logger == null){
        	_logger = Logger.getLogger(Log.class.getName());
        }
        return _logger;
    }
    
    /**
     * @param level The level of the message OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
     * @param msg The message to be display
     */
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
        System.out.println(msg);
    }
}
