/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

/**
 * @author pierre
 *
 */
public class DemoProject {
	
	/**
	 * Create a basic irregular 4-sided polygon and a triangle,
	 * build a wall and a ground on them,
	 * in a demo project (not saved on disk)
	 * @return The created project
	 * @throws SQLException
	 */
	public Project createDemoProject() throws SQLException{
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
	 * @param dao A geometric Data Acces Object.
	 * @param name The name of this room.
	 * @param points Contour of this room, in order
	 * @return A group which is a room.
	 * @throws SQLException
	 */
	public Group createRoom(GeometryDAO dao, String name, Point...points) throws SQLException{
		Group room = new Group(name);
		dao.create(room);
		dao.create(new Wall(room));
		dao.create(new Ground(room));
		
		for (int i=0; i<points.length; i++)
			dao.addShapeToGroup(room, new Line(points[i], points[(i+1)%points.length]));
		return room;
	}

}
