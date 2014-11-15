/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;
import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import be.ac.ulb.infof307.g03.controllers.WorldController;
import be.ac.ulb.infof307.g03.models.Project;

import com.jme3.system.AppSettings;

/**
 * @author fhennecker
 *
 */
public class TestWorldController {
	
	private WorldController _world;
	private Project _project;
	
	@Before
	public void setUp() throws SQLException{
		AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        _project = new Project();
        _project.create(":memory:");
        
        _world = new WorldController(settings, _project);
	}
	
	@Test
	public void testCreation(){
		assertNotNull(_world.getView());
		assertEquals(_world.getProject(), _project);
	}
}
