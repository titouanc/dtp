package be.ac.ulb.infof307.g03;

import java.sql.SQLException;

import com.j256.ormlite.logger.LocalLog;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.views.*;

public class Main {

	/**
	 * Main entry point of the program
	 * @param args Command line parameters
	 * @see http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:swing_canvas
	 */
	public static void main(String[] args) {
		// Mac OS X specific configuration
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HomePlans");
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "info");
		
		// Enqueue a new GUI in main dispatcher
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run(){
				try {
					new GUI(createDemoProject());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}
	
	/**
	 * Create a basic irregular 4-sided polygon and a triangle,
	 * build a wall and a ground on them,
	 * in a demo project (not saved on disk)
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
		dao.create(new Wall(room, 7));
		dao.create(new Ground(room));
		
		for (int i=0; i<points.length; i++)
			dao.addShapeToGroup(room, new Line(points[i], points[(i+1)%points.length]));
	}
}
