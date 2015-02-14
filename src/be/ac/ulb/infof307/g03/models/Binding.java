/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 * @brief A point bound to a Room
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class Binding extends Ordered {
	/**
	 * First point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
	private Room room = null;
	/**
	 * Second point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point point = null;
	
	/**
	 * Empty constructor of the Binding class.
	 */
	public Binding(){
		super();
	}
	
	/**
	 * @param room First point of line
	 * @param point Second point of line
	 * @param index In ordered collection
	 */
	public Binding(Room room, Point point, int index) {
		super();
		setRoom(room);
		setPoint(point);
		setIndex(index);
	}

	/**
	 * @param point The second point of the line.
	 */
	public void setPoint(Point point) {
		this.point = point;
	}
	
	/**
	 * @return The second point of the line.
	 */
	public Point getPoint(){
		return this.point;
	}

	/**
	 * @param room The room of the binding. First point of line.
	 */
	public void setRoom(Room room) {
		this.room = room;
	}
	
	/**
	 * @return The room of the binding. First point of line.
	 */
	public Room getRoom(){
		return this.room;
	}
	
	@Override
	public String toString(){
		return String.format("Binding %d", getId());
	}

	@Override
	public String getUIDPrefix() {
		return "bind";
	}
	
	protected <Subtype> Where<Subtype, Integer> getWhereForUniqueness(Where<Subtype, Integer> initialClause) throws SQLException {
		return initialClause.and().eq("room_id", this.room.getId());
	}
}
