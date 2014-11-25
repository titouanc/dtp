/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Base class for indexed items
 * @author Titouan Christophe
 */
public abstract class Ordered<Subtype> extends Geometric {
	@DatabaseField(uniqueCombo = true)
	private int _index = 0;
	
	/**
	 * 
	 */
	public Ordered() {
	}
	
	public final int getIndex(){
		return _index;
	}
	
	public final void setIndex(int index){
		_index = index;
	}
	
	public final List<Subtype> getFollowing(Dao<Subtype, Integer> dao) throws SQLException{
		return dao.query(dao.queryBuilder().where().gt("_index", _index).prepare());
	}
	
	public final List<Subtype> getPreceding(Dao<Subtype, Integer> dao) throws SQLException{
		return dao.query(dao.queryBuilder().where().lt("_index", _index).prepare());
	}
}
