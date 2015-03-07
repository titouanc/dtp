package be.ac.ulb.infof307.g03.GUI;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.Ground;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Room;
import be.ac.ulb.infof307.g03.models.Wall;

/**
 * @author pierre
 *
 */
public class StatisticsController implements Observer {
	
	private StatisticsView view;
	private Project project;
	private MasterDAO master;

	/**
	 * Constructor of the class StatisticsController
	 * @param project The main project
	 */
	public StatisticsController(Project project){
		this.project = project;
		try {
			this.master = project.getGeometryDAO();
			this.master.addObserver(this);
			this.project.addObserver(this);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 *  Run the statistic view
	 */
	public void run(){
		initView();
	}
	
	private void initView(){
		this.view = new StatisticsView(this);
		this.updateHTMLWorld();
	}
	
	/**
	 * @return The controller's view
	 */
	public StatisticsView getView(){
		return view;
		
	}
	
	/**
	 * Return html formated string of
	 * general statistics of the project
	 * @return Html formated string
	 */
	public String getGeneralStat(){
		GeometricDAO<Floor> daoFloor;
		GeometricDAO<Room> daoRoom;
		List<Floor> floorList = null;
		List<Room> roomList = null;
		try {
			daoFloor = this.master.getDao(Floor.class);
			floorList = daoFloor.queryForAll();
			daoRoom = this.master.getDao(Room.class);
			roomList = daoRoom.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		double habitableSurface = 0;
		double wallSurface = 0;
		double roomsVolume = 0;
		for (Room room : roomList) {
			Ground gr = room.getGround();
			if (gr != null)
				habitableSurface += room.getGround().getSurface();
			Wall wl = room.getWall();
			if (wl != null)
				wallSurface += room.getWall().getSurface();
			roomsVolume += room.getVolume();
			
		}
		StringBuffer html = new StringBuffer();
		html.append("<html><head><style type='text/css'>");
	    //html.append("body { background-color: #fffffff; }");
		html.append("</style></head>");
		html.append("<h4>General Statistics</h4>");
		html.append("<p>Surface habitable : ");
		html.append(habitableSurface);
		html.append("</p>");
		html.append("<p>Surface des murs : ");
		html.append(wallSurface);
		html.append("</p>");
		html.append("<p>Nombre d'étage : ");
		html.append(floorList.size());
		html.append("</p>");
		html.append("<p>Volume total : ");
		html.append(roomsVolume);
		html.append("</p>");
		html.append("</html>");
		
		return html.toString();
		
	}
	
	/**
	 * Return html formated string of
	 *  statistics of the selected room
	 * @param selectedRoom The room selected for stat
	 * @return Html fromated string containing stat of the room
	 */
	public String getRoomStat(Room selectedRoom){
		StringBuffer html = new StringBuffer();
		html.append("<html><head><style type='text/css'>");
	    //html.append("body { background-color: #fffffff; }");
		html.append("</style></head>");
		html.append("<h4>Statistics : ");
		html.append(selectedRoom.getName());
		html.append("</h4>");
		double habitableSurface = 0;
		double wallSurface = 0;
		double roomsVolume = 0;
		Ground gr = selectedRoom.getGround();
		if (gr != null)
			habitableSurface += selectedRoom.getGround().getSurface();
		Wall wl = selectedRoom.getWall();
		if (wl != null)
			wallSurface += selectedRoom.getWall().getSurface();
		roomsVolume += selectedRoom.getVolume();

		html.append("<html><head><style type='text/css'>");
	    //html.append("body { background-color: #fffffff; }");
		html.append("</style></head>");
		html.append("<p>Surface habitable : ");
		html.append(habitableSurface);
		html.append("</p>");
		html.append("<p>Surface des murs : ");
		html.append(wallSurface);
		html.append("</p>");
		html.append("</p>");
		html.append("<p>Volume de la pièce : ");
		html.append(roomsVolume);
		html.append("</p>");
		html.append("</html>");
		
		return html.toString();
		
	}
	
	/**
	 * Return html formated string of
	 *  statistics of the object 
	 * @return Html fromated string containing stat of the object
	 */
	public String getObjectStat() {
		Entity entity =null;
		try {
			entity = (Entity) project.getGeometryDAO().getByUID(project.config("entity.current"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numberVertices = 0;
		int numberFaces = 0;
		for (Primitive primitive : entity.getPrimitives()) {
			numberVertices += primitive.getVerticesNumber();
			numberFaces += primitive.getFaceNumber();
		}
		StringBuffer html = new StringBuffer();
		html.append("<html><head><style type='text/css'>");
	    //html.append("body { background-color: #fffffff; }");
		html.append("</style></head>");
		html.append("<h4>Object Statistics</h4>");
		html.append("<p>Vertices number : ");
		html.append(numberVertices);
		html.append("</p>");
		
		html.append("<p>Face number : ");
		html.append(numberFaces);
		html.append("</p>");
		html.append("</html>");
		
		return html.toString();
		
	}
	
	/**
	 * Update the statistics hmtl according on
	 * what is selected (room or nothing) in world view
	 */
	public void updateHTMLWorld(){
		Room rm = this.master.getRoomSelected();
		if (rm == null)
			view.editText(getGeneralStat());
		else
			view.editText(getRoomStat(rm));
		
	}
	
	
	
	/**
	 * Update the statistics hmtl according on
	 * what is selected (room or nothing) in world view
	 */
	public void updateHTMLobject(){
		view.editText( getObjectStat());
		
	}

	@Override
	public void update(Observable obs, Object obj) {
		if( obs instanceof Project){
			if (project.config("edition.mode").equals("object")){
				updateHTMLobject();
			}
		}
		else{
			updateHTMLWorld();
		}
	}
	
	
}
