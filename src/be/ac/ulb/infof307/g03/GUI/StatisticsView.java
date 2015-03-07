package be.ac.ulb.infof307.g03.GUI;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatisticsView extends JPanel {
	StatisticsController controller;
	JLabel label;

	public StatisticsView(StatisticsController newControler) {
		super(new GridLayout(1,1));
		this.controller = newControler;
		String labelText ="<html><FONT COLOR=RED>Red</FONT> and <FONT COLOR=BLUE>Blue</FONT> Text</html>";
		label = new JLabel();
		label.setText(labelText);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		add(label);

	}
	
	public void editText(String html){
		this.label.setText(html);
	}

}
