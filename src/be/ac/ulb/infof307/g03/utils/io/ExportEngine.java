package be.ac.ulb.infof307.g03.utils.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Project;

public class ExportEngine {
  MasterDAO dao = null;
  Project project = null;
  Entity exportable = null;
  
  public ExportEngine(Project project, MasterDAO daoFactory) {
    this.dao = daoFactory;
    this.project = project;
  }
  
  private String getExtension(String fileName) {
    int i = fileName.lastIndexOf('.');
    if (i > 0) {
      return fileName.substring(i+1);
    }
    return "";
  }
  
  public void handleExport(Entity entity, String fileName, String path) {
    this.exportable = entity;
    String extension = getExtension(fileName);
    if (extension.equals("dae")) {
      handleDae(path+"/"+fileName);
    } else if (extension.equals("obj")) {
      // TODO handle obj import
    } else if (extension.equals("3ds")) {
      // TODO handle 3ds import
    } else if (extension.equals("kmz")) {
      // TODO handle kmz import
    }
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
  
  public void daeGeometry(PrintWriter file, Primitive primitive) {
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
      
      file.println(	"        <polylist count=\""+indexes.length+"\">"														);
      file.println(	"          <input semantic=\"VERTEX\" source=\"#"+primitive.getUID()+"-vertices\" offset=\"0\"/>"		);
      file.println(	"          <input semantic=\"NORMAL\" source=\"#"+primitive.getUID()+"-normals\" offset=\"1\"/>"		);
          //  <input semantic="TEXCOORD" source="#Cube_001-mesh-map-0" offset="2" set="0"/>
      file.print(	"          <vcount>");
                               for (int i=0; i<indexes.length/3; ++i ) {
                                 file.print("3 ");
                               }
      file.println(            "</vcount>"																					);
      file.print(	"          <p>");
                               for (int i=0; i<indexes.length-1; ++i) {
                                 file.print(String.valueOf(indexes[i])+" ");
                               }
      file.println(            String.valueOf(indexes[indexes.length-1])+"</p>"																						);
      file.println(	"        </polylist>"																					);
      file.println(	"      </mesh>"																							);
      file.println(	"    </geometry>"																						);

  }
  
  public void handleDae(String fileName) {
    try {
      PrintWriter file = new PrintWriter(fileName,"UTF-8");
      file.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>"												);
      file.println("<COLLADA xmlns=\"http://www.collada.org/2005/11/COLLADASchema\" version=\"1.4.1\">"		);
      daeAsset(file);
      file.println("  <library_geometries>"																	);
      for (Primitive primitive : this.exportable.getPrimitives()) {
        daeGeometry(file, primitive);
      }
      file.println("  </library_geometries>"																);
      file.println("</COLLADA>"																				);
      file.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
}
