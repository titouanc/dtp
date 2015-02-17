/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;
import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.world.WorldController;
import be.ac.ulb.infof307.g03.world.WorldView;

import com.jme3.system.AppSettings;

/**
 * @author fhennecker
 *
 */
public class TestWorldController {
	
	private WorldView world;
	private Project project;
	
	@Before
	public void setUp() throws SQLException{
		AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        this.project = new Project();
        this.project.create(":memory:");
        this.world = new WorldView(project, settings);
	}
	
	@Test
	public void testCreation(){
		world.createCanvas();
		assertNotNull(world);
		assertEquals(world.getProject(), project);
	}
}
