package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

public abstract class Meshable extends Geometric {

	@DatabaseField
	private Boolean visible = true;
	@DatabaseField
	private Boolean selected = false;
	@DatabaseField
	private String texture = "Gray";

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
	 * @param material TODO
	 * @return Return a new Spatial object, that could be attached to a jMonkey context.
	 * @note The Spatial name is the Meshable UID.
	 */
	public abstract Spatial toSpatial(Material material);

}