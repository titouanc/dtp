package be.ac.ulb.infof307.g03.GUI;

import be.ac.ulb.infof307.g03.models.Project;



/**
 * @author pierre
 *
 */
public class StatisticsController {
	private StatisticsView view;
	private Project project;
	
	/**
	 * @param project The main project, for stat purpose
	 */
	public StatisticsController(Project project){
		this.project = project;
	}
	

	/**
	 * 
	 */
	public void run(){
		initView();
		this.view.display();
	}
	
	/**
	 * This method initiate the view
	 */
	public void initView(){
		this.view = new StatisticsView();
	}

}
