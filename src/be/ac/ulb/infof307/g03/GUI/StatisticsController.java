package be.ac.ulb.infof307.g03.GUI;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Area;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Wall;

public class StatisticsController implements Observer {
	
	private StatisticsView view;
	private Project project;

	public StatisticsController(Project project){
		this.project = project;
		try {
			project.getGeometryDAO().addObserver(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		initView();
	}
	
	public void initView(){
		this.view = new StatisticsView(this);
	}
	
	public StatisticsView getView(){
		return view;
		
	}
	
	public void updateHTML(){
		
	}

	@Override
	public void update(Observable obs, Object obj) {
		System.out.println("____UPDATE____");
	}
	
	
}
