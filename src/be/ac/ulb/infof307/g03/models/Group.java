/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.Collection;

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
	private ForeignCollection<ShapeRecord> _shapes;

	public static void migrate(ConnectionSource database) throws SQLException {
		TableUtils.createTableIfNotExists(database, Group.class);
		ShapeRecord.migrate(database);
	}

	public void addShape(Shape shape) {
		ShapeRecord record = shape.getRecord();
		System.out.println(String.format("===== ADD SHAPE %d (UID %d)", shape.getId(), shape.getShapeId()));
		if (record.getId() == 0)
			_shapes.add(record);
		else
			record.setGroup(this);
		System.out.println(String.format("=== ADDED SHAPE %d (UID %d)", shape.getId(), shape.getShapeId()));
	}
	
	public Collection<ShapeRecord> getShapes(){
		return _shapes;
	}

	@Override
	public Boolean equalsByContent(Shape other) {
		return true;
		// TODO implement real comparison
	}
	
	public Group(){
		super();
	}
}
