package be.ac.ulb.infof307.g03.utils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.nio.FloatBuffer;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.logging.Level;


import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;

import be.ac.ulb.infof307.g03.models.Project;

import be.ac.ulb.infof307.g03.utils.Log;


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
  
  public void handleExport(Entity entity, File fileToExport) {
    this.exportable = entity;
    String fileName = fileToExport.getName();
    String path = fileToExport.getParent();
    String extension = getExtension(fileName);
    if (extension.equals("dae")) {
      handleDae(path+"/"+fileName);
    } else if (extension.equals("obj")) {
		handleObj(path+"/"+fileName);
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
  
  private void daeScene(PrintWriter file) {
	  file.println(		"    <library_visual_scenes>");
	  file.println(		"      <visual_scene id=\"Scene\" name=\"Scene\">");
	  for (Primitive primitive : this.exportable.getPrimitives()) {
		  file.println(	"        <node id=\""+primitive.getUID()+"\" name=\""+primitive.getUID()+"\" type=\"NODE\">");
	      file.print(	"          <matrix sid=\"transform\">");
	      FloatBuffer fb =  primitive.getRotMatrix();
	      for (int i=0; i<fb.capacity(); ++i) {
	    	  file.print(String.valueOf(fb.get(i))+" ");
	      }
	      file.println(	           "</matrix>");
	      file.println(	"          <instance_geometry url=\"#"+primitive.getUID()+"\">");
	      file.println(	"            <bind_material>");
	      file.println(	"              <technique_common>");
	      //file.println(	"                <instance_material symbol=\"Material_001-material\" target=\"#Material_001-material\"/>");
	      file.println(	"              </technique_common>");
	      file.println(	"            </bind_material>");
	      file.println(	"          </instance_geometry>");
	      file.println(	"        </node>");
	  }
	  file.println(	"      </visual_scene>");
	  file.println(	"    </library_visual_scenes>");
	  file.println(	"    <scene>");
	  file.println(	"      <instance_visual_scene url=\"#Scene\"/>");
	  file.println(	"    </scene>");
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
      file.println("  <library_controllers/>");
      daeScene(file);
      file.println("</COLLADA>"																				);
      file.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

	
	public ExportEngine(MasterDAO daoFactory) {
		this.dao = daoFactory;
	}
	


	
	public void handleObj(String fileName){
		try {
			PrintWriter file = new PrintWriter(fileName,"UTF-8");
			file.print("# Created with HomePlans");
			file.println();
			file.print("# www.supayr.ninja");
			file.println();
			for (Primitive primitive : this.exportable.getPrimitives()) {
				//Vectrices
				float[] vertices = primitive.getVertices();
				int verticesNumber = vertices.length/3;
				for (int i=0; i<verticesNumber; i+=3) {
					file.print("v ");
					file.print(String.valueOf(vertices[i])+" ");
					file.print(String.valueOf(vertices[i+1])+" ");
					file.print(String.valueOf(vertices[i+2])+" ");
					file.println();
				}
				file.println();
				//Faces
				int[] faces = primitive.getIndexes();
				int facesNumber = faces.length/3;
				for (int i=0; i<facesNumber; i+=3) {
					file.print("f ");
					file.print(String.valueOf(faces[i])+" ");
					file.print(String.valueOf(faces[i+1])+" ");
					file.print(String.valueOf(faces[i+2])+" ");
					file.println();
				}			
			}
			file.close();
		} catch (FileNotFoundException e) {
			Log.log(Level.FINEST, "[ERROR] File couldn't be exported");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.log(Level.FINEST, "[ERROR] File couldn't be exported - Encoding error");
			e.printStackTrace();
		}
		
	}
}
