package be.ac.ulb.infof307.g03;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.GUI.*;

public class Main {

	/**
	 * @brief Main entry point of the program
	 */
	public static void main(String[] args) {
		// Mac Os X : Menu name configuration
		System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HomePlans");
		
		// Call GUI
		GUI gui = new GUI();
	}
}
