package be.ac.ulb.infof307.g03.models;

import java.io.File;


import com.j256.ormlite.field.DatabaseField;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

/**
 * @author titou
 *
 */
public abstract class Meshable extends Geometric {

	@DatabaseField
	private Boolean visible = true;
	@DatabaseField
	private String texture = "Gray";
	
	private static ColorRGBA selectedColor = new ColorRGBA(0f, 1.2f, 0f, 0.33f);

	private String classPath = getClass().getResource("Meshable.class").toString();

	
	/**
	 *  Constructor of meshable
	 */
	public Meshable() {
		super();
	}

	/**
	 * Set visibility to false;
	 */
	public final void hide() {
		this.visible = false;
	}

	/**
	 * @return current texture
	 */
	public final String getTexture() {
		return this.texture;
	}

	/**
	 * @param newTexture
	 */
	public final void setTexture(String newTexture) {
		this.texture=newTexture;
	}

	/**
	 * Set visibility to true;
	 */
	public final void show() {
		this.visible = true;
	}

	/**
	 * Status of visibility
	 * @return True if the Shape is visible
	 */
	public final Boolean isVisible() {
		return this.visible;
	}

	public final String toString() {
		String prefix = isVisible() ? "" : "*";
		return prefix + innerToString();
	}

	protected abstract String innerToString();
	protected abstract Boolean drawAsSelected();
	
	/**
	 * Convert a meshable object into a jmonkey mesh-like
	 * @param material The material to be applied to the result mesh
	 * @return A new Spatial object, that could be attached to a jMonkey context.
	 * @note The Spatial name is the Meshable UID.
	 */
	public abstract Spatial toSpatial(Material material);
	
	private final Material loadMaterial(AssetManager assetManager){
		if (texture.equals("Gray") || texture.equals("")){
			texture = "GrayColor";
		}
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat.setBoolean("UseMaterialColors", true);
		
		mat.setColor("Diffuse", ColorRGBA.Gray);
		mat.setColor("Ambient", this.drawAsSelected() ? selectedColor : ColorRGBA.Gray);
		mat.setColor("Specular", ColorRGBA.Gray);
		
		try {
			if((classPath.subSequence(0, 3).equals("rsr"))){
				if (texture.contains("Colors/")){
					texture=texture.replace("Colors/", "");					
				}
				if (!(texture.contains("Full")) && !(texture.contains("Color"))){
					texture=texture+"Color";
				}
				if (texture.equals("/Textures/Full")){
					texture="";
				}
				if (texture.contains(File.separator)){
					String[] parts = texture.split(File.separator);
					String path = "";
					for ( int i = 0;i<parts.length-1;++i){
						path +=File.separator+ parts[i];
					}
					texture = parts[parts.length-1];
					assetManager.registerLocator(path, FileLocator.class);
				}
			}
			else{
				if(!(texture.endsWith("Color")) && !(texture.endsWith("Full")))
					texture += "Color";
			}
		} catch (AssetNotFoundException ex){
			texture = "GrayColor";
		}
		mat.setTexture("DiffuseMap", assetManager.loadTexture(texture+".png"));
		return mat;
	}
	
	/**
	 * Load texture, then toSpatial(Material)
	 * @param assetManager an assetManager to load textures
	 * @return A new Spatial object, that could be attached to a jMonkey context.
	 */
	public Spatial toSpatial(AssetManager assetManager){
		return this.toSpatial(loadMaterial(assetManager));
	}

}
