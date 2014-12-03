/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;


/**
 * @author pierre
 *
 */
public class DemoProject {
	
	/**
	 * Create a basic house
	 * in a demo project (not saved on disk)
	 * @return The created project
	 * @throws SQLException
	 */
	public static Project create() throws SQLException {
		Project proj = new Project();
		proj.create(":memory:");
		
		GeometryDAO dao = proj.getGeometryDAO();
		Point a = new Point(0, 0, 0),
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
			  m = new Point(-8, 14, 0),
			  n = new Point(20,20,0),
			  o = new Point(20,-15,0),
			  p = new Point(-20,-15,0),
			  q = new Point(-20,20,0),
			  r = new Point(-5,0,0),
			  s = new Point(-3,0,0),
			  t = new Point(-3,-20,0),
			  u = new Point(-5,-20,0);

		Floor groundFloor = dao.getFloors().get(0);
		proj.config("floor.current", groundFloor.getUID());
		
		Room r1 = createRoom(groundFloor, "Square room", a, e, f, d);
		showRoof(r1,dao); // nothing on the top of this room
		
		createRoom(groundFloor, "Irregular room", a, d, c, c, g,k, i,j, h);
		createRoom(groundFloor, "Rectangular room", f, d, c, l, m);
		
		createGround(groundFloor, "Jardin", "GrassFull", n , o , p ,q);
		createGround(groundFloor, "Chemin", "Gray", r , s , t ,u);
		
		dao.createFloorOnTop(7);
		String currentFloorUID = proj.config("floor.current");
		Floor floor = (Floor) dao.getByUID(currentFloorUID);
    	Floor firstFloor = dao.getNextFloor(floor);
    	firstFloor.setHeight(3);
    	
		//createRoom(firstFloor, "Square room 2", a, e, f, d);
		createRoom(firstFloor, "Irregular room 2", a, d, c, c, g,k, i,j, h);
		createRoom(firstFloor, "Rectangular room 2 ", f, d, c, l, m);
		/*
		Entity entity = new Entity("Cube");
		dao.create(entity);
		Primitive prim = new Primitive(entity, Primitive.CUBE);
		dao.create(prim);
		
		dao.refresh(firstFloor);
		for	(Room room : firstFloor.getRooms()){
			showRoof(room,dao); // nothing on the top of this room
		}
		
		Item placedObject = new Item(firstFloor, entity);
		placedObject.setPosition(new Vector3f(-10, -10, 3));
		dao.create(placedObject);
		*/
		
		Entity door = new Entity("Door");
		dao.create(door);
		Primitive doorPrim = new Primitive(door, Primitive.CUBE);
		doorPrim.setScale(new Vector3f(2,1,4));
		doorPrim.setTexture("DoorFull");
		dao.create(doorPrim);
		
		
		Item doorObject = new Item(groundFloor, door);
		doorObject.setPosition(new Vector3f(-4, 0, 2));
		dao.create(doorObject);
		
		Entity window = new Entity("Window");
		dao.create(window);
		Primitive windowPrim = new Primitive(window, Primitive.CUBE);
		windowPrim.setRotation(new Vector3f(FastMath.PI/2,0,0));
		windowPrim.setScale(new Vector3f(1,4,8));
		windowPrim.setTexture("WindowFull");
		dao.create(windowPrim);
		
		
		Item windowObject = new Item(groundFloor, window);
		windowObject.setPosition(new Vector3f(8, 6, 5));
		dao.create(windowObject);
		
		
		
		dao.refresh(firstFloor);
		for	(Room room : firstFloor.getRooms()){
			showRoof(room,dao); // nothing on the top of this room
		}
		
		dao.notifyObservers();
		return proj;
	}
	
	private static void showRoof(Room room,GeometryDAO dao) throws SQLException{
		Roof roof = room.getRoof();
		roof.show();
		dao.update(roof);
	}
	
	private static Room createGround(Floor floor, String name,String texture ,Point...points) throws SQLException{
		Room room = new Room(name);
		
		//Wall wall = new Wall();
		//wall.setTexture("BrickFull");
		//room.setWall(wall);
		
		Ground ground = new Ground();
		ground.setTexture(texture);
		room.setGround(ground);
		
		//Roof roof = new Roof();
		//roof.setTexture("WoodFull");
		//room.setRoof(roof);
		
		floor.getRooms().add(room);
		floor.getRooms().refresh(room);
		
		room.addPoints(points);
		room.addPoints(points[0]);
		floor.getRooms().update(room);
		return room;
	}
	
	/**
	 * Create a room in a project
	 * @param floor Floor where rooms will be added
	 * @param name The name of this room.
	 * @param points Contour of this room, in order
	 * @return A group which is a room.
	 * @throws SQLException
	 */
	private static Room createRoom(Floor floor, String name, Point...points) throws SQLException{
		Room room = new Room(name);
		
		Wall wall = new Wall();
		wall.setTexture("BrickFull");
		room.setWall(wall);
		
		Ground ground = new Ground();
		ground.setTexture("ParquetFull");
		room.setGround(ground);
		
		Roof roof = new Roof();
		roof.setTexture("WoodFull");
		room.setRoof(roof);
		
		floor.getRooms().add(room);
		floor.getRooms().refresh(room);
		
		room.addPoints(points);
		room.addPoints(points[0]);
		floor.getRooms().update(room);
		return room;
	}
}
