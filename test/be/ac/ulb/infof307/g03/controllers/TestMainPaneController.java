/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;
import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import be.ac.ulb.infof307.g03.GUI.MainPaneController;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.world.WorldController;

import com.jme3.system.AppSettings;

/**
 * @author fhennecker
 *
 */
public class TestMainPaneController {
	
	private MainPaneController _controller;
	private Project _project;
	
	@Before
	public void setUp() throws SQLException{
        _project = new Project();
        _project.create(":memory:");
        _controller = new MainPaneController(_project);
	}
	
	@Test
	public void testCreation(){
		_controller.run();
		assertNotNull(_controller.getView());
	}
}
