package be.ac.ulb.infof307.g03.GUI;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author pierre
 *
 */
public class StatisticsView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	StatisticsController controller;
	JLabel labelHtml;

	/**
	 * The constructor of the class StatisticsView
	 * @param newControler The view's controller
	 */
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
	
	
	/**
	 * Update the html contained in the jpanel
	 * @param html The html to be set in the jpanel
	 */
	public void editText(String html){
		this.labelHtml.setText(html);
	}

}
