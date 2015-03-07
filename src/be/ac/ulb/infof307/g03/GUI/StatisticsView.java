package be.ac.ulb.infof307.g03.GUI;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatisticsView extends JPanel {
	StatisticsController controller;
	JLabel labelHtml;

	public StatisticsView(StatisticsController newControler) {
		super(new GridLayout(1,1));
		this.controller = newControler;
		this.setOpaque(true);
        this.setBackground(Color.WHITE);
		String labelText ="-Empty-";
		labelHtml = new JLabel();
		labelHtml.setText(labelText);
		labelHtml.setVerticalTextPosition(JLabel.TOP);
		labelHtml.setHorizontalTextPosition(JLabel.CENTER);
		add(labelHtml);

	}
	
	public void editText(String html){
		this.labelHtml.setText(html);
	}

}
