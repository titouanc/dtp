package be.ac.ulb.infof307.g03.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import be.ac.ulb.infof307.g03.controllers.ObjectListController;
import be.ac.ulb.infof307.g03.models.Change;
import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author titouan
 *
 */
public class ObjectListView extends JList implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	class MyCellRenderer extends JLabel implements ListCellRenderer {
	     //final static ImageIcon longIcon = new ImageIcon("long.gif");
	     //final static ImageIcon shortIcon = new ImageIcon("short.gif");
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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
			Entity selectedEntity = (Entity) getSelectedValue();
			if (cmd.equals(_NEW)) {
				String name = JOptionPane.showInputDialog("New object name ?");
				_controller.onNewAction(name);
			} else if (cmd.equals(_RENAME)) {
				String name = JOptionPane.showInputDialog("New object name ?");
				_controller.onRenameAction(selectedEntity, name);
			} else if (cmd.equals(_EDIT)) {
				_controller.onEditAction(selectedEntity);
			} else if (cmd.equals(_DELETE)) {
				_controller.onDeleteAction(selectedEntity);
			} else if (cmd.equals(_INSERT)) {
				_controller.onInsertAction(selectedEntity);
			}
		}
		
	}
	
	private ObjectListController _controller = null;
	private GeometryDAO _dao = null;
	
	private static final String _NEW = "PAL_new";
	private static final String _RENAME = "PAL_rename";
	private static final String _EDIT = "PAL_edit";
	private static final String _DELETE = "PAL_delete";
	private static final String _INSERT = "PAL_insert";
	
	/**
	 * @param controller The view's controller
	 * @param project The main project
	 */
	public ObjectListView(ObjectListController controller, Project project) {
		super();
		_controller = controller;
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL_WRAP);
		setVisibleRowCount(-1);
		setCellRenderer(new MyCellRenderer());
		try {
			_dao = project.getGeometryDAO();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
		addMouseListener(_controller);
		_dao.addObserver(this);
		createList();
	}
	
	private void createList() {
		try {
			List<Entity> entities = _dao.getEntities();
			setListData(new Vector<Entity>(entities));
		} catch (SQLException ex) {
			Log.exception(ex);
		}
		
	}
	
	private String getFloorName(){
		Floor floor = this._controller.getCurrentFloor();
		return (floor == null) ? "current floor" : floor.toString();
	}
	
	/**
	 * Create the right click popup menu
	 * @return the menu selected by the user.
	 */
	public JPopupMenu createPopupMenu(){
		PopupActionListener listener = new PopupActionListener();
		JPopupMenu res = new JPopupMenu();
		res.add(createPopupMenuItem("New", _NEW, listener));
		res.add(new JPopupMenu.Separator());
		res.add(createPopupMenuItem("Rename", _RENAME, listener));
		res.add(createPopupMenuItem("Edit", _EDIT, listener));
		res.add(createPopupMenuItem("Delete", _DELETE, listener));
		res.add(new JPopupMenu.Separator());
		res.add(createPopupMenuItem("Create on " + this.getFloorName(), _INSERT, listener));
		return res;
	}

	private JMenuItem createPopupMenuItem(String label, String action, PopupActionListener listener) {
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.addActionListener(listener);
		menuItem.setActionCommand(action);
		return menuItem;
	}
	
	@Override
	public void update(Observable obs, Object arg) {
		List<Change> changes = ((List<Change>) arg);
		boolean mustRedraw = false;
		for (Change change : changes) {
			if (change.getItem() instanceof Entity) {
				mustRedraw = true;
			}
		}
		
		if (mustRedraw) {
			createList();
		}
	}
}
