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
	public static Project create() throws SQLException {
		Project proj = new Project();
		proj.create(":memory:");
		
		GeometryDAO geo = proj.getGeometryDAO();
		Point a = new Point(0, 0, 0),
			  b = new Point(8, 0, 0),
			  c = new Point(8, 8, 0),
			  d = new Point(0, 8, 0),
			  e = new Point(-8, 0, 0),
			  f = new Point(-8, 8, 0),
			  g = new Point(8, -2, 0),
			  h = new Point(0, -2, 0),
			  i = new Point(4, -4.5, 0),
			  j = new Point(2, -4, 0),
			  k = new Point(6, -4, 0),
			  l = new Point(8, 14, 0),
			  m = new Point(-8, 14, 0);
		
		Floor groundFloor = geo.getFloors().get(0);
		proj.config("floor.current", groundFloor.getUID());
		
		createRoom(groundFloor, "Square room", a, e, f, d);
		createRoom(groundFloor, "Irregular room", a, d, c, c, g,k, i,j, h);
		createRoom(groundFloor, "Rectangular room", f, d, c, l, m);
		//createRoom(groundFloor, "Irregular room", g,k, i,j, h);
		geo.notifyObservers();
		return proj;
	}
	
	/**
	 * Create a room in a project
	 * @param floor Floor where rooms will be added
	 * @param name The name of this room.
	 * @param points Contour of this room, in order
	 * @return A group which is a room.
	 * @throws SQLException
	 */
	public static Room createRoom(Floor floor, String name, Point...points) throws SQLException{
		Room room = new Room(name);
		room.setWall(new Wall());
		room.setGround(new Ground());
		room.setRoof(new Roof());
		floor.getRooms().add(room);
		floor.getRooms().refresh(room);
		
		room.addPoints(points);
		room.addPoints(points[0]);
		floor.getRooms().update(room);
		return room;
	}
}
