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
	public MainPaneView view;
	
	MainPaneController(Project project){
		view = new MainPaneView(project);
	}
}
