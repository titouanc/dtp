/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author fhennecker
 *
 */
public class MainPaneController {
	private MainPaneView _view;
	
	MainPaneController(Project project){
		_view = new MainPaneView(project);
	}

	public MainPaneView view(){
		return _view;
	}
}
