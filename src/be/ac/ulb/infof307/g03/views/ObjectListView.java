package be.ac.ulb.infof307.g03.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import be.ac.ulb.infof307.g03.controllers.ObjectListController;
import be.ac.ulb.infof307.g03.views.ObjectTreeView.PopupListener;

public class ObjectListView extends JList {

	class MyCellRenderer extends JLabel implements ListCellRenderer {
	     //final static ImageIcon longIcon = new ImageIcon("long.gif");
	     //final static ImageIcon shortIcon = new ImageIcon("short.gif");
		
		/**
		 * This is the only method defined by ListCellRenderer.
		 * We just reconfigure the JLabel each time we're called.
		 */ @Override
	     public Component getListCellRendererComponent(
	       JList list,           // the list
	       Object value,            // value to display
	       int index,               // cell index
	       boolean isSelected,      // is the cell selected
	       boolean cellHasFocus)    // does the cell have focus
	     {
	         String s = value.toString();
	         setText(s);
	        // setIcon((s.length() > 10) ? longIcon : shortIcon);
	         if (isSelected) {
	             setBackground(list.getSelectionBackground());
	             setForeground(list.getSelectionForeground());
	         } else {
	             setBackground(list.getBackground());
	             setForeground(list.getForeground());
	         }
	         setEnabled(list.isEnabled());
	         setFont(list.getFont());
	         setOpaque(true);
	         return this;
	     }
	 }
	
	class PopupActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(_NEW)) {
				String name = JOptionPane.showInputDialog("New object name ?");
				_controller.onNewAction(name);
			} else if (cmd.equals(_RENAME)) {
				String name = JOptionPane.showInputDialog("New object name ?");
				_controller.onRenameAction();
			} else if (cmd.equals(_EDIT)) {
				_controller.onEditAction(/*TODO*/);
			} else if (cmd.equals(_DELETE)) {
				_controller.onDeleteAction();
			}
 			
		}
		
	}
	
	private ObjectListController _controller = null;
	private static String[] data = {"one", "two", "three", "four"};
	
	private static final String _NEW = "PAL_new";
	private static final String _RENAME = "PAL_rename";
	private static final String _EDIT = "PAL_edit";
	private static final String _DELETE = "PAL_delete";
	
	public ObjectListView(ObjectListController controller) {
		super(data);
		_controller = controller;
		createList();
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL_WRAP);
		setVisibleRowCount(-1);
		setCellRenderer(new MyCellRenderer());
		
		addMouseListener(_controller);
	} 
	
	private void createList() { 
		
	}
	
	public JPopupMenu createPopupMenu(){
		PopupActionListener listener = new PopupActionListener();
		JPopupMenu res = new JPopupMenu();
		res.add(createPopupMenuItem("New", _NEW, listener));
		res.add(createPopupMenuItem("Rename", _RENAME, listener));
		res.add(createPopupMenuItem("Edit", _EDIT, listener));
		res.add(createPopupMenuItem("Delete", _DELETE, listener));
		return res;
	}

	private JMenuItem createPopupMenuItem(String label, String action, PopupActionListener listener) {
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.addActionListener(listener);
		menuItem.setActionCommand(action);
		return menuItem;
	}
	
}