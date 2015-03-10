package be.ac.ulb.infof307.g03;

import java.sql.SQLException;
import java.util.logging.Level;

import com.j256.ormlite.logger.LocalLog;

import be.ac.ulb.infof307.g03.GUI.*;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author all
 * This is the Main class of the Home Plans program
 * It's first call at execution
 */
public class Main {
	

	/**
	 * Main entry point of the program
	 * @param args Command line parameters
	 * @see <a href=" http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:swing_canvas">Jmonkey doc</a>
	 */
	public static void main(String[] args) {
		/* Configure log level (should not be above INFO in production) */
		// Mac OS X specific configuration
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HomePlans");
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "info");
		
		Log.setLevel(Level.ALL);
		
		// Enqueue a new GUI in main dispatcher
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run(){
				Log.debug("The program started");
				BootController bc = new BootController();
				Project proj = bc.initProject();
				if (proj == null)
					Log.error("Unable to initialize project !");
				try {
					new GUI(proj);
				} catch (SQLException sqlEx) {
					Log.exception(sqlEx);
				}
			}
		});

	}

}
