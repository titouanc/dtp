package be.ac.ulb.infof307.g03.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author pierre
 *
 */
public class Log {
	private static Level _level = Level.ALL;
    static Logger logger;
    private ConsoleHandler consoleHandler;
    private LogFormatter plainTextFormatter;

    private Log() {
    	logger = Logger.getLogger(Log.class.getName());
    	logger.setLevel(_level);
        consoleHandler = new ConsoleHandler();
        plainTextFormatter = new LogFormatter();
        consoleHandler.setFormatter(plainTextFormatter);
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

    }
    
    private static Logger getLogger(){
        if(logger == null){
        	new Log();
        }
        return logger;
    }
    
    /**
     * Set the logging level for the program
     * @param lvl The logging level
     */
    public static void setLevel(Level lvl){
    	_level = lvl;
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
    
    /**
     * @param ex
     * @param msg The message printf-like format string
     * @param args The arguments for format string
     */
    public static void exception(Exception ex, String msg,  Object...args){
    	getLogger().log(Level.SEVERE, String.format(msg, args),ex);
    }
    
    /**
     * @param ex
     * @param args The arguments for format string
     */
    public static void exception(Exception ex,  Object...args){
    	getLogger().log(Level.SEVERE, String.format(ex.getMessage(), args),ex);
    }
}
