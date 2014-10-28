/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 * @brief A group is a shape constituted of multiple shapes
 */
public class Group extends Shape {
	@ForeignCollectionField
	public ForeignCollection<ShapeRecord> _shapes;

	public static void migrate(ConnectionSource database) throws SQLException {
		TableUtils.createTableIfNotExists(database, Group.class);
		ShapeRecord.migrate(database);
	}

	void addShape(Shape shape) {
		_shapes.add(shape.getRecord());
	}

	@Override
	public Boolean equalsByContent(Shape other) {
		return true;
		// TODO implement real comparison
	}
}
