package be.ac.ulb.infof307.g03.views;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
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
			} else if (cmd.equals(_EXPORT)) {
				_controller.onExport(selectedEntity);
			}
		}
		
	}
	
	private ObjectListController _controller = null;
	private GeometryDAO _dao = null;
	private JFileChooser chooser;
	
	private static final String _NEW = "PAL_new";
	private static final String _RENAME = "PAL_rename";
	private static final String _EDIT = "PAL_edit";
	private static final String _DELETE = "PAL_delete";
	private static final String _INSERT = "PAL_insert";
	private static final String _EXPORT = "PAL_export";
	
	JFrame frameExport;
	
	public ObjectListView(ObjectListController controller, Project project) {
		super();
		this.chooser = new JFileChooser();
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
	
	public JPopupMenu createPopupMenu(){
		PopupActionListener listener = new PopupActionListener();
		JPopupMenu res = new JPopupMenu();
		res.add(createPopupMenuItem("Export ..", _EXPORT, listener));
		res.add(new JPopupMenu.Separator());
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
		List<Change> changes = (List<Change>) arg;
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
