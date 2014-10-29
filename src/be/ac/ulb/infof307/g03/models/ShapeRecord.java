/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 *
 */
@DatabaseTable()
public class ShapeRecord {
	/**
	 * @brief Shape id
	 */
	@DatabaseField(generatedId = true)
	private int _id = 0;
	
	@DatabaseField(canBeNull = true, foreign = true)
	private Group _group;
	
	public int getId(){
		return _id;
	}
		
	public static void migrate(ConnectionSource database) throws SQLException {
		TableUtils.createTableIfNotExists(database, ShapeRecord.class);
	}

	public void setGroup(Group group) {
		_group = group;
	}
}
