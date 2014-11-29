package be.ac.ulb.infof307.g03.utils;

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
        	new Log();
        }
        return _logger;
    }
    
    /**
     * @param level The level of the message OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
     * @param msg The message to be display
     */
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
    }
    
    /**
     * Shortcut for Log.log(Level.FINE, String.format(msg, args...))
     * @param msg The message printf-like format string
     * @param args The arguments for format string
     */
    public static void debug(String msg, Object...args){
    	getLogger().log(Level.FINE, String.format(msg, args));
    }
    
    /**
     * Shortcut for Log.log(Level.INFO, String.format(msg, args...))
     * @param msg The message printf-like format string
     * @param args The arguments for format string
     */
    public static void info(String msg, Object...args){
    	getLogger().log(Level.INFO, String.format(msg, args));
    }
    
    /**
     * Shortcut for Log.log(Level.WARNING, String.format(msg, args...))
     * @param msg The message printf-like format string
     * @param args The arguments for format string
     */
    public static void warn(String msg, Object...args){
    	getLogger().log(Level.WARNING, String.format(msg, args));
    }
    
    /**
     * Shortcut for Log.log(Level.SEVERE, String.format(msg, args...))
     * @param msg The message printf-like format string
     * @param args The arguments for format string
     */
    public static void error(String msg, Object...args){
    	getLogger().log(Level.SEVERE, String.format(msg, args));
    }
}
