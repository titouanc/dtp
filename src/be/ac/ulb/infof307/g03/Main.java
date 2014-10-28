package be.ac.ulb.infof307.g03;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import be.ac.ulb.infof307.g03.models.*;

public class Main {

	/**
	 * @brief Main entry point of the program
	 */
	public static void main(String[] args) {
		modelsDemo();
	}
	
	public static void modelsDemo(){
		try {
			Project p = new Project();
			p.create("hello.hpj");
			
			Geometry g = p.getGeometry();
			for (Line l : g.getLines()){
				System.out.println(l);
			}
			
			Point o=new Point(), x=new Point(1, 0, 0), y=new Point(0, 1, 0), z=new Point(0, 0, 1);
			g.addLine(new Line(o, x));
			g.addLine(new Line(o, y));
			g.addLine(new Line(o, z));
		} catch (SQLException err){
			System.out.println(err);
		}
	}
}
