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
		this.updateHTML();
	}
	
	public StatisticsView getView(){
		return view;
		
	}
	
	public void updateHTML(){
		StringBuffer html = new StringBuffer();
		html.append("<html><head><style type='text/css'>");
		html.append("li { font-style: italic; font-size: 30pt; }");
		html.append("li { font-family: serif; color: #ff5555; }");
		html.append("ul { border-width: 4px; border-style: solid;border-color: #ff0000; } ");
	    html.append("ul { background-color: #ffeeee; }");
		html.append("</style></head>");
		html.append("<h3>H3 Header</h3>");
		html.append("<ul><li>large serifed text</li><li>as list items</li>");
		html.append("</html>");
		
		view.editText(html.toString());
		
	}

	@Override
	public void update(Observable obs, Object obj) {
		System.out.println("____UPDATE____");
	}
	
	
}
