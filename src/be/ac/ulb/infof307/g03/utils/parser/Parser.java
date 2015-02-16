/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;

/**
 * @author brochape
 *
 */
public abstract class Parser {
	protected MasterDAO daoFactory;
	protected Primitive primitive;
	
	public Parser(String filename, MasterDAO daoFactory) throws IOException, SQLException {
		File f = new File(filename);
		assert f.exists() && f.isFile();
		
		int lastSlash = filename.lastIndexOf("/");
		lastSlash++;
		// {{ Invariant: lastSlash >= 0 }}
		int dot = filename.indexOf(".", lastSlash);
		String name = filename.substring(lastSlash, dot);
		Entity entity = new Entity(name);
		daoFactory.getDao(Entity.class).insert(entity);
		
		this.primitive = new Primitive(entity, Primitive.IMPORTED);
		daoFactory.getDao(Primitive.class).create(primitive);
		this.daoFactory = daoFactory;
	}
	
	public Parser(){}

	public abstract void parse() throws SQLException, IOException;
}
