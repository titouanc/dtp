package be.ac.ulb.infof307.g03.GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

class ModificationFrame extends JFrame implements ActionListener, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Primitive primitive = null;
	private JFormattedTextField scalex, scaley, scalez, posz, rotx, roty, rotz;
	private JSlider sliderRotx, sliderRoty, sliderRotz;
	private MasterDAO dao = null;
	
	private static final String OK = "ok";
	private static final String CANCEL = "cancel";
	
	public ModificationFrame(Primitive prim, MasterDAO dao) {
		super("Modification Panel");
		this.primitive = prim;
		this.dao = dao;
		
		JPanel panel = new JPanel();
		this.add(panel);
		Dimension dimension = new Dimension(400,300);
		this.setPreferredSize(dimension);
		this.setMaximumSize(dimension);
		this.setMinimumSize(dimension);
		this.setResizable(false);
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		
		panel.add(new JLabel("Scale x : "),constraints);
		++constraints.gridy;
		panel.add(new JLabel("Scale y : "), constraints);
		++constraints.gridy;
		panel.add(new JLabel("Scale z : "), constraints);
		++constraints.gridy;
		panel.add(new JLabel("Translation z : "), constraints);
		constraints.gridwidth = 1;
		++constraints.gridy;
		panel.add(new JLabel("Rotation x : "), constraints);
		++constraints.gridy;
		panel.add(new JLabel("Rotation y : "), constraints);
		++constraints.gridy;
		panel.add(new JLabel("Rotation z : "), constraints);
		
		constraints.gridx = 2;
		constraints.gridy = 0;
		scalex = new JFormattedTextField(prim.getScale().x);
		scalex.setColumns(3);
		panel.add(scalex, constraints);
		scalex.addPropertyChangeListener(this);
		++constraints.gridy;
		scaley = new JFormattedTextField(prim.getScale().y);
		scaley.setColumns(3);
		panel.add(scaley,constraints);
		scaley.addPropertyChangeListener(this);
		++constraints.gridy;
		scalez = new JFormattedTextField(prim.getScale().z);
		scalez.setColumns(3);
		panel.add(scalez,constraints);
		scalez.addPropertyChangeListener(this);
		++constraints.gridy;
		posz = new JFormattedTextField(prim.getTranslation().z);
		posz.setColumns(3);
		panel.add(posz,constraints);
		posz.addPropertyChangeListener(this);
		++constraints.gridy;
		rotx = new JFormattedTextField(prim.getRotation().x);
		rotx.setColumns(3);
		panel.add(rotx,constraints);
		rotx.addPropertyChangeListener(this);
		++constraints.gridy;
		roty = new JFormattedTextField(prim.getRotation().y);
		roty.setColumns(3);
		panel.add(roty,constraints);
		roty.addPropertyChangeListener(this);
		++constraints.gridy;
		rotz = new JFormattedTextField(prim.getRotation().z);
		rotz.setColumns(3);
		panel.add(rotz,constraints);
		rotz.addPropertyChangeListener(this);
		
		constraints.gridx = 1;
		constraints.gridy = 4;
		sliderRotx = new JSlider(JSlider.HORIZONTAL, -180, 180,(int)prim.getRotation().x);
		sliderRotx.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				
				rotx.setValue(sliderRotx.getValue());
			}
		});
		panel.add(sliderRotx ,constraints);
		++constraints.gridy;
		sliderRoty = new JSlider(JSlider.HORIZONTAL, -180, 180, (int)prim.getRotation().y);
		sliderRoty.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				roty.setValue(sliderRoty.getValue());
			}
		});
		panel.add(sliderRoty ,constraints);
		++constraints.gridy;
		sliderRotz = new JSlider(JSlider.HORIZONTAL, -180, 180, (int)prim.getRotation().z);
		sliderRotz.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				rotz.setValue(sliderRotz.getValue());
			}
		});
		panel.add(sliderRotz ,constraints);
		
		++constraints.gridy;
		JButton applyButton = new JButton("Ok");
		applyButton.setActionCommand(OK);
		applyButton.addActionListener(this);
		panel.add(applyButton, constraints);
		++constraints.gridx;
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(CANCEL);
		cancelButton.addActionListener(this);
		panel.add(cancelButton, constraints);
	}
	
	public void applyModif() {
		primitive.setScale(new Vector3f(((Number)scalex.getValue()).floatValue(),
				((Number)scaley.getValue()).floatValue(),
				((Number)scalez.getValue()).floatValue()));
		primitive.setTranslation(new Vector3f(	primitive.getTranslation().getX(),
				primitive.getTranslation().getY(),
				((Number)posz.getValue()).floatValue()));
		primitive.setRotation(new Vector3f(	((Number)rotx.getValue()).floatValue()*FastMath.PI/180,
				((Number)roty.getValue()).floatValue()*FastMath.PI/180,
				((Number)rotz.getValue()).floatValue()*FastMath.PI/180));
		try {
			dao.getDao(Primitive.class).modify(primitive);
			dao.notifyObservers(primitive);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(OK)) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else if (cmd.equals(CANCEL)) {
			// restore
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		applyModif();
	}

}