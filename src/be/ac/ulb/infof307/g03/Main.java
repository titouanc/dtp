package be.ac.ulb.infof307.g03;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.GUI.*;

public class Main {

	/**
	 * @brief Main entry point of the program
	 */
	public static void main(String[] args) {
		try {
			Project p = createDemoProject();
			
			// Mac Os X : Menu name configuration
			System.setProperty("apple.laf.useScreenMenuBar", "true");
	        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HomePlans");
			
			// Call GUI
			new GUI();
		} catch (Exception err){}
	}
	
	public static Project createDemoProject() throws SQLException{
		Project proj = new Project();
		proj.create(":memory:");
		
		Geometry geo = proj.getGeometry();
		Point o = new Point(0, 0, 0), x = new Point(1, 0, 0), y = new Point(0, 1, 0);
		Point xy = new Point(1, 1, 0);
		
		Group room = new Group();
		geo.create(room);
		
		geo.addShapeToGroup(room, new Line(o, x));
		geo.addShapeToGroup(room, new Line(x, xy));
		geo.addShapeToGroup(room, new Line(xy, y));
		geo.addShapeToGroup(room, new Line(y, o));
		
		return proj;
	}
}
