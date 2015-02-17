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
		
		MasterDAO daoFactory = proj.getGeometryDAO();
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
			  p = new Point(-3,-15,0),
			  q = new Point(-3,20,0),
			  r = new Point(-5,0,0),
			  s = new Point(-3,0,0),
			  t = new Point(-3,-20,0),
			  u = new Point(-5,-20,0),
			  v = new Point(-5,20,0),
			  w = new Point(-5,-15,0),
			  x = new Point(-20,-15,0),
			  y = new Point(-20,20,0);

		Floor groundFloor = daoFactory.getDao(Floor.class).queryForEq("index", 0).get(0);
		proj.config("floor.current", groundFloor.getUID());
		
		Room r1 = createRoom(groundFloor, "Square room", a, e, f, d);
		showRoof(r1, daoFactory.getDao(Roof.class)); // nothing on the top of this room
		
		createRoom(groundFloor, "Irregular room", a, d, c, c, g,k, i,j, h);
		createRoom(groundFloor, "Rectangular room", f, d, c, l, m);
		
		createGround(groundFloor, "Jardin-gauche", "GrassFull", n , o , p ,q);
		createGround(groundFloor, "Jardin-droite", "GrassFull", v , w , x ,y);
		createGround(groundFloor, "Jardin-arrière", "GrassFull", q , s , r ,v);
		createGround(groundFloor, "Chemin", "Gray", r , s , t ,u);
		
		Floor firstFloor = new Floor();
		firstFloor.setBaseHeight(groundFloor.getHeight());
		firstFloor.setHeight(7);
		firstFloor.setIndex(1);
		daoFactory.getDao(Floor.class).create(firstFloor);
		daoFactory.getDao(Floor.class).refresh(firstFloor);
    	
		createRoom(firstFloor, "Irregular room 2", a, d, c, c, g,k, i,j, h);
		createRoom(firstFloor, "Rectangular room 2 ", f, d, c, l, m);
		
		/* Create a door object */
		Entity door = new Entity("Door");
		daoFactory.getDao(Entity.class).create(door);
		Primitive doorPrim = new Primitive(door, Primitive.CUBE);
		doorPrim.setScale(new Vector3f(2,1,4));
		doorPrim.setTexture("DoorFull");
		doorPrim.setTranslation(new Vector3f(0, 0, 2));
		daoFactory.getDao(Primitive.class).create(doorPrim);
		
		/* Place the door in the world */
		Item doorObject = new Item(groundFloor, door);
		doorObject.setPosition(new Vector3f(-4, 0, 0));
		daoFactory.getDao(Item.class).create(doorObject);
		
		/* Create a window object */
		Entity window = new Entity("Window");
		daoFactory.getDao(Entity.class).create(window);
		Primitive windowPrim = new Primitive(window, Primitive.CUBE);
		windowPrim.setRotation(new Vector3f(FastMath.PI/2,0,0));
		windowPrim.setScale(new Vector3f(1,4,8));
		windowPrim.setTexture("WindowFull");
		windowPrim.setTranslation(new Vector3f(0, 0, 5));
		daoFactory.getDao(Primitive.class).create(windowPrim);
		
		/* Place the window in the world */
		Item windowObject = new Item(groundFloor, window);
		windowObject.setPosition(new Vector3f(8, 6, 0));
		daoFactory.getDao(Item.class).create(windowObject);
		
		daoFactory.getDao(Floor.class).refresh(firstFloor);
		for	(Room room : firstFloor.getRooms()){
			showRoof(room, daoFactory.getDao(Roof.class)); // nothing on the top of this room
		}
		
		daoFactory.notifyObservers();
		return proj;
	}
	
	private static void showRoof(Room room, GeometricDAO<Roof> dao) throws SQLException{
		Roof roof = room.getRoof();
		roof.show();
		dao.update(roof);
	}
	
	private static Room createGround(Floor floor, String name,String texture ,Point...points) throws SQLException{
		Room room = new Room(name);
		
		Ground ground = new Ground();
		ground.setTexture(texture);
		room.setGround(ground);
		
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
