package be.ac.ulb.infof307.g03.GUI;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Area;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.Ground;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Room;
import be.ac.ulb.infof307.g03.models.Wall;

public class StatisticsController implements Observer {
	
	private StatisticsView view;
	private Project project;
	private MasterDAO master;

	public StatisticsController(Project project){
		this.project = project;
		try {
			this.master = project.getGeometryDAO();
			this.master.addObserver(this);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public void run(){
		initView();
	}
	
	public void initView(){
		this.view = new StatisticsView(this);
		this.updateHTML();
	}
	
	public StatisticsView getView(){
		return view;
		
	}
	
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
	
	public void updateHTML(){
		Room rm = this.master.getRoomSelected();
		if (rm == null)
			view.editText(getGeneralStat());
		else
			view.editText(getRoomStat(rm));
		
	}

	@Override
	public void update(Observable obs, Object obj) {
		updateHTML();
	}
	
	
}
