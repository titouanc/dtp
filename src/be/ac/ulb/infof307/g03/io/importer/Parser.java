/**
 * 
 */
package be.ac.ulb.infof307.g03.io.importer;

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
	
	/**
	 * Constructor the parsers. Each specific parser extends this class
	 * @param filename : the filename containing the informations
	 * @param daoFactory : the DAO factory
	 * @throws IOException
	 * @throws SQLException
	 */
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
	
	/**
	 * Void constructor
	 */
	public Parser(){}

	/**
	 * Generic parser 
	 * @throws SQLException
	 * @throws IOException
	 */
	public abstract void parse() throws SQLException, IOException;
}
