package be.ac.ulb.infof307.g03;

import java.sql.SQLException;
import java.util.logging.Level;

import com.j256.ormlite.logger.LocalLog;

import be.ac.ulb.infof307.g03.controllers.BootController;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.*;

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
		
		// logger level and output
		
		
		// first log
		Log.log(Level.FINE, "The program started");
		
		// Mac OS X specific configuration
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HomePlans");
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "info");
		
		// Enqueue a new GUI in main dispatcher
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run(){
				try {
					BootController bc = new BootController();
					Project proj = bc.initProject();
					if (proj == null)
						Log.log(Level.SEVERE, "Unable to initialize project");
					new GUI(proj);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
