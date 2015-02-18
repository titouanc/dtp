/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.j256.ormlite.dao.DaoManager;

/**
 * @author Titouan Christophe, Walter Moulart
 */
public class MasterDAO extends Observable {
	private ConnectionSource database = null;
	private Map<Class<? extends Geometric>,GeometricDAO<? extends Geometric>> daos = null;
	private List<Change> changes = null;
	private Map<String,Class<? extends Geometric>> uidMap = null;
	private static Class managedTypes[] = {
		Floor.class, Point.class, Binding.class, Wall.class, Ground.class,
		Roof.class, Room.class, Entity.class, Primitive.class, Item.class,
		Vertex.class, Triangle.class
	};
	public static Class areaClasses[] = {Roof.class, Ground.class, Wall.class};
	
	/**
	 * Migrate all needed tables to a database
	 * @param database The database connection
	 * @throws SQLException
	 */
	public static void migrate(ConnectionSource database) throws SQLException{
		for (Class<?> klass : managedTypes){
			TableUtils.createTableIfNotExists(database, klass);
		}
	}
	
	/**
	 * Create a new geometric data access object that relies on given database
	 * @param database A valid connection for ORMLite
	 * @throws SQLException
	 */
	public MasterDAO(ConnectionSource database) throws SQLException{
		super();
		this.daos = new HashMap<Class<? extends Geometric>,GeometricDAO<? extends Geometric>>();
		resetConnection(database);
		this.uidMap = new HashMap<String,Class<? extends Geometric>>();
		for (Class<? extends Geometric> klass : managedTypes){
			/* Build an association UID prefix (object short name type) 
			 * -> class which should be used to template the DAO to access them */
			try {
				String prefix = klass.newInstance().getUIDPrefix();
				this.uidMap.put(prefix, klass);
			}
			catch (InstantiationException e) {e.printStackTrace();} 
			catch (IllegalAccessException e) {e.printStackTrace();}
		}
	}
	
	public Geometric getByUID(String uid){
		String parts[] = uid.split("-");
		Geometric res = null;
		if (parts.length == 2){
			Class<? extends Geometric> klass = this.uidMap.get(parts[0]);
			Integer id = new Integer(parts[1]);
			try {
				GeometricDAO<? extends Geometric> dao = this.getDao(klass);
				res = dao.queryForId(id);
			} catch (SQLException err){
				err.printStackTrace();
			}
		}
		return res;
	}
	
	/**
	 * Reset the inner database connection
	 * @param newConnection The new database connection to use
	 * @throws SQLException
	 */
	public final void resetConnection(ConnectionSource newConnection) throws SQLException {
		this.daos.clear();
		this.changes = new LinkedList<Change>();
		this.database = newConnection;
	}
	
	/**
	 * Create a DAO for a certain type of Geometric object
	 * @param forType a Geometric subclass
	 * @return A DAO for a Geometric subclass
	 * @throws SQLException
	 */
	public final <T extends Geometric> GeometricDAO<T> getDao(final Class<T> forType) throws SQLException{
		GeometricDAO<T> dao = null;
		if (this.daos.containsKey(forType)){
			dao = (GeometricDAO<T>) this.daos.get(forType);
		} else {
			Boolean found = false;
			for (Class klass : managedTypes){
				if (klass == forType){
					found = true;
					break;
				}
			}
			if (! found)
				throw new SQLException("This class cannot be managed by a MasterDAO !");
			dao = DaoManager.createDao(this.database, forType);
			dao.setMaster(this);
			dao.setObjectCache(true);
			this.daos.put(forType, dao);
		}
		return dao;
	}
	
	/**
	 * Copy all content from another DAO
	 * @param other the original DAO
	 * @throws SQLException
	 */
	public void copyFrom(MasterDAO other) throws SQLException{
		for (Class<? extends Geometric> klass : managedTypes){
			GeometricDAO<? extends Geometric> dao = other.getDao(klass);
			GeometricDAO<? extends Geometric> myDao = this.getDao(klass);
			for (Geometric g : dao.queryForAll()){
				myDao.insert(g);
			}
		}
	}
	
	@Override
	public void notifyObservers(){
		List<Change> changes = new LinkedList<Change>();
		for (Change chg : this.changes){
			Change found = null;
			for (Change last : changes){
				if (last.getItem().getUID().equals(chg.getItem().getUID()))
					found = last;
			}
			
			/* 2 consecutive updates on the same obj -> keep the last update only */
			if (found != null && chg.isUpdate() && (found.isUpdate() || found.isCreation())){
				found.setItem(chg.getItem());
			}
			else {changes.add(chg);}
		}
		this.changes = new LinkedList<Change>();
		super.notifyObservers(changes);
	}
	
	@Override
	public void notifyObservers(Object arg){
		notifyObservers();
	}

	public void addChange(Change chg) {
		setChanged();
		this.changes.add(chg);
	}
}
