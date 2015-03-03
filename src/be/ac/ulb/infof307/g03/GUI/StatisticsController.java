package be.ac.ulb.infof307.g03.GUI;

import java.util.Observable;
import java.util.Observer;

public class StatisticsController implements Observer {
	
	private StatisticsView view;

	public StatisticsController(){
		
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

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
}
