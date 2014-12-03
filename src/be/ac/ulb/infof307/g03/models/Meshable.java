package be.ac.ulb.infof307.g03.models;

import java.io.File;

import be.ac.ulb.infof307.g03.utils.Log;

import com.j256.ormlite.field.DatabaseField;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

public abstract class Meshable extends Geometric {

	@DatabaseField
	private Boolean visible = true;
	@DatabaseField
	
	private Boolean selected = false;
	@DatabaseField
	private String texture = "Gray";

	private String classPath = getClass().getResource("Meshable.class").toString();

	
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
	 * Select object
	 */
	public final void select() {
		this.selected = true;
	}

	/**
	 * Deselect object
	 */
	public final void deselect() {
		this.selected = false;
	}

	/**
	 * Selects object if it is deselected
	 * Deselects object if it is selected
	 */
	public final void toggleSelect() {
		this.selected = !this.selected;
	}

	/**
	 * Status of visibility
	 * @return True if the Shape is visible
	 */
	public final Boolean isVisible() {
		return this.visible;
	}

	/**
	 * Is the object selected?
	 * @return True if the Shape is selected
	 */
	public final Boolean isSelected() {
		return this.selected;
	}

	public final String toString() {
		String prefix = isVisible() ? "" : "*";
		String suffix = isSelected() ? " [S]" : "";
		return prefix + innerToString() + suffix;
	}

	protected abstract String innerToString();

	/**
	 * Convert a meshable object into a jmonkey mesh-like
	 * @param material The material to be applied to the result mesh
	 * @return A new Spatial object, that could be attached to a jMonkey context.
	 * @note The Spatial name is the Meshable UID.
	 */
	public abstract Spatial toSpatial(Material material);
	
	private final Material loadTexture(AssetManager assetManager){
		if (texture.equals("Gray") || texture.equals("")){
			texture = "GrayColors";
		}
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat.setBoolean("UseMaterialColors", true);
		ColorRGBA color = new ColorRGBA(ColorRGBA.Gray);
		mat.setColor("Diffuse", color);
		mat.setColor("Ambient", color);
		mat.setColor("Specular",color);
		if (isSelected()){
			mat.setColor("Ambient", new ColorRGBA(220,220,255,1f));
		}
		try{
			if((classPath.subSequence(0, 3).equals("rsr"))){
				if (texture.contains("Colors/")){
					texture=texture.replace("Colors/", "");					
				}
				if (!(texture.contains("Full"))){
					texture=texture+"Color";
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
		
		mat.setTexture("DiffuseMap",assetManager.loadTexture(texture+".png"));
		} catch (AssetNotFoundException ex){
			texture = "GrayColor";
			mat.setTexture("DiffuseMap",assetManager.loadTexture(texture+".png"));
		}
		return mat;
	}
	
	/**
	 * Load texture, then toSpatial(Material)
	 * @param assetManager an assetManager to load textures
	 * @return A new Spatial object, that could be attached to a jMonkey context.
	 */
	public Spatial toSpatial(AssetManager assetManager){
		return this.toSpatial(loadTexture(assetManager));
	}

}
