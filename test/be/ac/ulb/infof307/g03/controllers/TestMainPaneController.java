/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;
import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import be.ac.ulb.infof307.g03.models.Project;


/**
 * @author fhennecker
 *
 */
public class TestMainPaneController {
	
	private MainPaneController _controller;
	private Project _project;
	
	/**
	 * @throws SQLException
	 */
	@Before
	public void setUp() throws SQLException{
        _project = new Project();
        _project.create(":memory:");
        
        _controller = new MainPaneController(_project);
	}
	
	/**
	 * 
	 */
	@Test
	public void testCreation(){
		assertNotNull(_controller.getView());
	}
}
