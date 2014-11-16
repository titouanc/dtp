package be.ac.ulb.infof307.g03.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import be.ac.ulb.infof307.g03.models.Project;


/**
 * @author pierre
 *
 */
public class TestBootController {
	
	private BootController _controller;
	private static final String RANDOM_PATH = "/home/tester/myproject.hpj";
	//private Project _project;
	
	
	/**
	 * 
	 */
	@Before
	public void setUp(){
		_controller = new BootController();
		_controller.saveCurrentProjectPath(RANDOM_PATH);
		
	}
	
	/**
	 * 
	 */
	@Test
	public void testCreation(){
		assertEquals(_controller.getLastProjectPath(), RANDOM_PATH);
		
	}

}
