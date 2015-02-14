/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.Where;
/**
 * Base class for indexed items
 * @author Titouan Christophe
 */
public abstract class Ordered extends Geometric {
	@DatabaseField(uniqueCombo = true)
	private int index = 0; 
	
	/**
	 * 
	 */
	public Ordered() {
	}
	
	/**
	 * @return The geometric's index
	 */
	public final int getIndex(){
		return index;
	}
	
	/**
	 * @param newIndex The new index
	 */
	public final void setIndex(int newIndex){
		index = newIndex;
	}
	
	/**
	 * @param dao An Ordered subclass DA
	 * @return A query suitable to get next ordered elements, in ascending order
	 * @throws SQLException
	 */
	public final <Subtype> PreparedQuery<Subtype> getQueryForFollowing(Dao<Subtype, Integer> dao) throws SQLException{
		return getWhereForUniqueness(dao.queryBuilder().orderBy("index", true).where()).gt("index", index).prepare();
	}
	
	/**
	 * @param dao An Ordered subclass DA
	 * @return A query suitable to get previous ordered elements, in ascending order
	 * @throws SQLException
	 */
	public final <Subtype> PreparedQuery<Subtype> getQueryForPreceeding(Dao<Subtype, Integer> dao) throws SQLException{
		return getWhereForUniqueness(dao.queryBuilder().orderBy("index", false).where()).lt("index", index).prepare();
	}
	
	/**
	 * @param dao An Ordered subclass DA
	 * @return A query suitable to get next all elements, starting from the last one in descending order
	 * @throws SQLException
	 */
	public final <Subtype> PreparedQuery<Subtype> getQueryForLast(Dao<Subtype, Integer> dao) throws SQLException{
		if (getId() != 0)
			return getWhereForUniqueness(dao.queryBuilder().orderBy("index", false).where().lt("index", index)).prepare();
		return getWhereForUniqueness(dao.queryBuilder().orderBy("index", false).where().isNotNull("index")).prepare();
	}
	
	/**
	 * @param dao An Ordered subclass DA
	 * @return A query suitable to get next all elements, starting from the first one in ascending order
	 * @throws SQLException
	 */
	public final <Subtype> PreparedQuery<Subtype> getQueryForFirst(Dao<Subtype, Integer> dao) throws SQLException{
		if (getId() != 0)
			return getWhereForUniqueness(dao.queryBuilder().orderBy("index", false).where().gt("index", index)).prepare();
		return getWhereForUniqueness(dao.queryBuilder().orderBy("index", true).where().isNotNull("index")).prepare();
	}
	
	/**
	 * User-definable restriction when getting previous/next elements
	 * @param initialClause
	 * @return
	 * @throws SQLException
	 */
	protected <Subtype> Where<Subtype, Integer> getWhereForUniqueness(Where<Subtype, Integer> initialClause) throws SQLException {
		return initialClause;
	}
}
