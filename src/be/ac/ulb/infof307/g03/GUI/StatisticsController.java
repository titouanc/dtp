package be.ac.ulb.infof307.g03.GUI;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.j256.ormlite.dao.ForeignCollection;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.Ground;
import be.ac.ulb.infof307.g03.models.Item;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Room;
import be.ac.ulb.infof307.g03.models.Selectionable;
import be.ac.ulb.infof307.g03.models.Wall;
import be.ac.ulb.infof307.g03.utils.Log;

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
			this.master = project.getMasterDAO();
			this.master.addObserver(this);
			this.project.addObserver(this);
		} catch (SQLException e) {
			Log.exception(e);
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
	
	
	private StringBuffer createHeader(){
		StringBuffer html = new StringBuffer();
		html.append("<html><head><style type='text/css'>");
	    //html.append("body { background-color: #fffffff; }");
		html.append("</style></head>");
		html.append("<h3>Statistics</h3>");
		
		return html;
		
	}
	
	/**
	 * Return html formated string of
	 * general statistics of the project
	 * @return Html formated string
	 */
	public String getGeneralStat(){
		GeometricDAO<Floor> daoFloor;
		GeometricDAO<Room> daoRoom;
		GeometricDAO<Item> daoItem;
		List<Floor> floorList = null;
		List<Room> roomList = null;
		List<Item> itemList = null;
		try {
			daoFloor = this.master.getDao(Floor.class);
			floorList = daoFloor.queryForAll();
			daoRoom = this.master.getDao(Room.class);
			roomList = daoRoom.queryForAll();
			daoItem = this.master.getDao(Item.class);
			itemList = daoItem.queryForAll();
		} catch (SQLException e) {
			Log.exception(e);
		}
		double habitableSurface = 0;
		double surfaceTotal = 0;
		double wallSurface = 0;
		double roomsVolume = 0;
		for (Room room : roomList) {
			Ground gr = room.getGround();
			Wall wl = room.getWall();
			if (gr != null )
				surfaceTotal += room.getGround().getSurface();
			if (gr != null && wl != null)
				habitableSurface += room.getGround().getSurface();
			if (wl != null)
				wallSurface += room.getWall().getSurface();
			roomsVolume += room.getVolume();
			
		}
		StringBuffer html = this.createHeader();
		html.append("<h4>General Statistics</h4>");
		html.append("<p>Number of object : ");
		html.append(itemList.size());
		html.append("</p>");
		html.append("<p>Living surface : ");
		html.append(round(habitableSurface,1));
		html.append("</p>");
		html.append("<p>Total surface : ");
		html.append(round(surfaceTotal,1));
		html.append("</p>");
		html.append("<p>Walls surface : ");
		html.append(round(wallSurface,1));
		html.append("</p>");
		html.append("<p>Floors number : ");
		html.append(floorList.size());
		html.append("</p>");
		html.append("<p>Total volume : ");
		html.append(round(roomsVolume,1));
		html.append("</p>");
		html.append("</html>");
		
		return html.toString();
		
	}
	
	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	/**
	 * Return html formated string of
	 *  statistics of the selected room
	 * @param selectedRoom The room selected for stat
	 * @return Html fromated string containing stat of the room
	 */
	public String getRoomStat(Room selectedRoom){
		StringBuffer html = this.createHeader();
		html.append("<h4>");
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

		html.append("<p>Surface : ");
		html.append(round(habitableSurface,1));
		html.append("</p>");
		html.append("<p>Walls surface : ");
		html.append(round(wallSurface,1));
		html.append("</p>");
		html.append("</p>");
		html.append("<p>Room volume : ");
		html.append(round(roomsVolume,1));
		html.append("</p>");
		
		Floor fl = selectedRoom.getFloor();
		ForeignCollection<Room> roomList = fl.getRooms();
		
		double habitableSurfaceFloor = 0;
		double totalSurfaceFloor = 0;
		double wallSurfaceFloor = 0;
		double roomsVolumeVolume = 0;
		for (Room room : roomList) {
			Ground grf = room.getGround();
			Wall wlf = room.getWall();
			if (grf != null)
				totalSurfaceFloor += grf.getSurface();
			if (grf != null && wlf != null)
				habitableSurfaceFloor += grf.getSurface();
			if (wlf != null)
				wallSurfaceFloor += wlf.getSurface();
			roomsVolumeVolume += room.getVolume();
			
		}
		html.append("</style></head>");
		html.append("<h4>");
		html.append(fl.toString());
		html.append("</h4>");
		html.append("<p>Living surface : ");
		html.append(round(habitableSurfaceFloor,1));
		html.append("</p>");
		html.append("<p>Total surface : ");
		html.append(round(totalSurfaceFloor,1));
		html.append("</p>");
		html.append("<p>Walls surface : ");
		html.append(round(wallSurfaceFloor,1));
		html.append("</p>");
		html.append("<p>Total volume : ");
		html.append(round(roomsVolumeVolume,1));
		html.append("</p>");
		html.append("</html>");
		
		
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
			entity = (Entity) project.getMasterDAO().getByUID(project.config("entity.current"));
		} catch (SQLException e) {
			Log.exception(e);
		}
		int numberVertices = 0;
		int numberFaces = 0;
		if (entity != null){
			if (entity.getPrimitives() != null){
				for (Primitive primitive : entity.getPrimitives()) {
					numberVertices += primitive.getVerticesNumber();
					numberFaces += primitive.getFaceNumber();
				}
			}
		}
		StringBuffer html = this.createHeader();
		html.append("<h4>Object</h4>");
		html.append("<p>Vertices number : ");
		html.append(numberVertices);
		html.append("</p>");
		
		html.append("<p>Face number : ");
		html.append(numberFaces);
		html.append("</p>");
		
		if (entity != null){
			if (entity.getPrimitives() != null){
				html.append("<p>Primitive number : ");
				html.append(entity.getPrimitives().size());
				html.append("</p>");
			}
		}
		
		html.append("</html>");
		return html.toString();
		
	}
	
	/**
	 * Update the statistics hmtl according on
	 * what is selected (room or nothing) in world view
	 */
	public void updateHTMLWorld(){
		Selectionable selected = this.project.getSelectionManager().selected();
		if (selected != null && selected instanceof Room){
			Room rm = (Room) selected;
			view.editText(getRoomStat(rm));
		} else {
			view.editText(getGeneralStat());
		}
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
			}else if (project.config("edition.mode").equals("world")){
				updateHTMLWorld();
			}
		}
		else{
			updateHTMLWorld();
		}
	}
	
	
}
