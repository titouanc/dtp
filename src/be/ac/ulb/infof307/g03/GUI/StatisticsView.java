package be.ac.ulb.infof307.g03.GUI;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatisticsView extends JPanel {
	StatisticsController controller;
	JLabel labelHtml;

	public StatisticsView(StatisticsController newControler) {
		super(new GridLayout(1,1));
		this.controller = newControler;
		String labelText ="<html><FONT COLOR=RED>Red</FONT> and <FONT COLOR=BLUE>Blue</FONT> Text</html>";
		labelHtml = new JLabel();
		labelHtml.setText(labelText);
		labelHtml.setVerticalTextPosition(JLabel.BOTTOM);
		labelHtml.setHorizontalTextPosition(JLabel.CENTER);
		add(labelHtml);

	}
	
	public void editText(String html){
		this.labelHtml.setText(html);
	}

}
