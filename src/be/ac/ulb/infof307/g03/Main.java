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
			// Mac Os X : Menu name configuration
			System.setProperty("apple.laf.useScreenMenuBar", "true");
	        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HomePlans");
			
			// Call GUI
			new GUI(createDemoProject());
		} catch (Exception err){
			System.out.println("[DEBUG] Exception catched while creating GUI");
			System.out.println("[DEBUG] Error is : "+ err);
		}
	}
	
	/**
	 * Create a basic irregular 4-sided polygon in a demo project (not saved on disk)
	 * @return The created project
	 * @throws SQLException
	 */
	public static Project createDemoProject() throws SQLException{
		Project proj = new Project();
		proj.create(":memory:");
		
		proj.config("canvas.width", "1024");
		proj.config("canvas.height", "768");
		
		GeometryDAO geo = proj.getGeometryDAO();
		Point a = new Point(0, 0, 0),
			  b = new Point(3, 0, 0),
			  c = new Point(7, 8, 0),
			  d = new Point(0, 12, 0),
			  e = new Point(-5, -1, 0);
		
		createRoom(geo, "Irregular room", a, b, c, d);
		createRoom(geo, "Triangular room", a, e, d);
		return proj;
	}
	
	/**
	 * Create a room in a project
	 * @param dao A geometric Data Acces Object
	 * @param name The name of this room
	 * @param points Contour of this room, in order
	 * @throws SQLException
	 */
	public static void createRoom(GeometryDAO dao, String name, Point...points) throws SQLException{
		Group room = new Group(name);
		dao.create(room);
		
		for (int i=0; i<points.length; i++)
			dao.addShapeToGroup(room, new Line(points[i], points[(i+1)%points.length]));
	}
}
