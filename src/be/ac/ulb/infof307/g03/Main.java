package be.ac.ulb.infof307.g03;

import java.sql.SQLException;
import com.j256.ormlite.logger.LocalLog;
import be.ac.ulb.infof307.g03.models.*;
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
		// Mac OS X specific configuration
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HomePlans");
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "info");
		
		// Enqueue a new GUI in main dispatcher
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run(){
				try {
					Project proj = createDemoProject();
					new GUI(proj);
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
		Point a = new Point(0, 0, 1),
			  b = new Point(3, 0, 1),
			  c = new Point(7, 8, 1),
			  d = new Point(0, 12, 1),
			  e = new Point(-5, -1, 1);
		
		Floor groundFloor = new Floor(7);
		geo.create(groundFloor);
		
		geo.addGroupToFloor(groundFloor, createRoom(geo, "Irregular room", a, b, c, d));
		geo.addGroupToFloor(groundFloor, createRoom(geo, "Triangular room", a, e, d));
		geo.notifyObservers();
		return proj;
	}
	
	/**
	 * Create a room in a project
	 * @param dao A geometric Data Acces Object
	 * @param name The name of this room
	 * @param points Contour of this room, in order
	 * @throws SQLException
	 */
	public static Group createRoom(GeometryDAO dao, String name, Point...points) throws SQLException{
		Group room = new Group(name);
		dao.create(room);
		dao.create(new Wall(room));
		dao.create(new Ground(room));
		
		for (int i=0; i<points.length; i++)
			dao.addShapeToGroup(room, new Line(points[i], points[(i+1)%points.length]));
		return room;
	}
}
