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
	
	public Binding(){
		super();
	}
	
	public Binding(Room room, Point point, int index) {
		super();
		setRoom(room);
		setPoint(point);
		setIndex(index);
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	public Point getPoint(){
		return this.point;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
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
