/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Scanner;

import junit.framework.Test;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 *
 */
public class BootController {
	static final private String HISTORY_FILE = ".openProjectPath";

	

	/**
	 * @return The path of the jar encoded in Utf8
	 * @throws UnsupportedEncodingException 
	 */
	public String getJarPath() throws UnsupportedEncodingException{
		String jarPath = Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(jarPath, "UTF-8");
		return decodedPath;
	}
	
	/**
	 * @param path 
	 * @throws IOException 
	 * 
	 */
	public void saveCurrentProjectPath(String path) throws IOException{
		// https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
		// windows <3
		String historyFile = getJarPath() + HISTORY_FILE;
		File file = new File (historyFile);
		PrintWriter out = new PrintWriter(file); // hope this will work with Windows
		out.flush();
		out.println(path);
		out.close();
	}
	
	/**
	 * @return A string containing the path of the last project opened
	 * @throws IOException 
	 */
	public String getLastProjectPath() throws IOException{
		String historyFile = getJarPath() + HISTORY_FILE;
		String path = new Scanner( new File(historyFile) ).useDelimiter("\\A").next();
		return path;
	}
	
	/**
	 * @return Return the last project opened
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Project loadLastProject() throws SQLException, IOException{
		Project proj = new Project();
		String lastProjectPath = getLastProjectPath(); 
		proj.create(lastProjectPath);
		return proj;	
	}
}
