package be.ac.ulb.infof307.g03.utils.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Project;


/**
 * @author pierre
 *
 */
public class DAEExporter {

	private Project project;

	/**
	 *  Constructor of DAEExporter
	 * @param project The main project
	 */
	public DAEExporter(Project project){
		this.project = project;
	}

	/**
	 * Export to file
	 * @param fileToExport The file in which the object will be write
	 * @param entity The entity to be exported
	 */
	public void export(File fileToExport, Entity entity){
		try {
			PrintWriter file = new PrintWriter(fileToExport,"UTF-8");
			file.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>"												);
			file.println("<COLLADA xmlns=\"http://www.collada.org/2005/11/COLLADASchema\" version=\"1.4.1\">"		);
			daeAsset(file);
			file.println("  <library_geometries>"																	);
			for (Primitive primitive : entity.getPrimitives()) {
				daeGeometry(file, primitive);
			}
			file.println("  </library_geometries>"																);
			file.println("  <library_controllers/>");
			daeScene(file, entity);
			file.println("</COLLADA>"																				);
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void daeGeometry(PrintWriter file, Primitive primitive) {
		float[] vertices = primitive.getVertices();
		int[] indexes = primitive.getIndexes();


		file.println(	"    <geometry id=\""+primitive.getUID()+"\" name=\""+primitive.getType()+"\">"							);
		file.println(	"      <mesh>"																							);
		file.println(	"        <source id=\""+primitive.getUID()+"-positions\">"												);
		file.print(	"          <float_array id=\""+primitive.getUID()+"-positions-array\" count=\""+vertices.length+"\">"	);
		for (int i=0; i<vertices.length-1; ++i) {
			file.print(String.valueOf(vertices[i])+" ");
		}
		file.println(            String.valueOf(vertices[vertices.length-1])+"</float_array>"									);
		file.println(	"          <technique_common>"																			);
		file.println(	"            <accessor source=\"#"+primitive.getUID()+"-positions-array\" "
				+ "count=\""+String.valueOf(new Integer(vertices.length/3))+"\" stride=\"3\">"				);
		file.println(	"              <param name=\"X\" type=\"float\"/>"														);
		file.println(	"              <param name=\"Y\" type=\"float\"/>"														);
		file.println(	"              <param name=\"Z\" type=\"float\"/>"														);
		file.println(	"            </accessor>"																				);
		file.println(	"          </technique_common>"																			);
		file.println(	"        </source>"																						);

		file.println(	"        <vertices id=\""+primitive.getUID()+"-vertices\">"												);
		file.println(	"          <input semantic=\"POSITION\" source=\"#"+primitive.getUID()+"-positions\"/>"					);
		file.println(	"        </vertices>"																					);

		file.println(	"        <triangles count=\""+indexes.length+"\">"														);
		file.println(	"          <input semantic=\"VERTEX\" source=\"#"+primitive.getUID()+"-vertices\" offset=\"0\"/>"		);
		file.print(	"          <p>");
		for (int i=0; i<indexes.length-1; ++i) {
			file.print(String.valueOf(indexes[i])+" ");
		}
		file.println(            String.valueOf(indexes[indexes.length-1])+"</p>"																						);
		file.println(	"        </triangles>"																					);
		file.println(	"      </mesh>"																							);
		file.println(	"    </geometry>"																						);

	}

	private void daeScene(PrintWriter file, Entity entity) {
		file.println(		"  <library_visual_scenes>"																		);
		file.println(		"    <visual_scene id=\"Scene\" name=\"Scene\">"												);
		for (Primitive primitive : entity.getPrimitives()) {
			file.println(	"      <node id=\""+primitive.getUID()+"\" name=\""+primitive.getUID()+"\" type=\"NODE\">"		);
			file.println(	"        <instance_geometry url=\"#"+primitive.getUID()+"\">"									);
			file.println(	"          <bind_material>"																		);
			file.println(	"            <technique_common/>"																);
			file.println(	"          </bind_material>"																	);
			file.println(	"        </instance_geometry>"																	);
			file.println(	"      </node>"																					);
		}
		file.println(		"    </visual_scene>"																			);
		file.println(		"  </library_visual_scenes>"																	);
		file.println(		"  <scene>"																						);
		file.println(		"    <instance_visual_scene url=\"#Scene\"/>"													);
		file.println(		"  </scene>"																					);
	}


	private void daeAsset(PrintWriter file){
		file.println("  <asset>"																						);
		file.println("    <contributor>"																				);
		file.println("      <author>Homeplan User</author>"																);
		file.println("      <authoring_tool>HomePlan "+this.project.config("version.current")+"</authoring_tool>"		);
		file.println("    </contributor>"																				);
		file.println("    <created>"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())+"</created>"		);
		file.println("    <modified>"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())+"</modified>"	);
		file.println("    <unit name=\"meter\" meter=\"1\"/>"															);
		file.println("    <up_axis>Z_UP</up_axis>"																		);
		file.println("  </asset>"																						);
	}
}