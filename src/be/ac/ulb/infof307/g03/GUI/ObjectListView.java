package be.ac.ulb.infof307.g03.GUI;

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

import be.ac.ulb.infof307.g03.models.Change;
import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.MasterDAO;
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
			if (cmd.equals(NEW)) {
				String name = JOptionPane.showInputDialog("New object name ?");
				controller.onNewAction(name);
			} else if (cmd.equals(RENAME)) {
				String name = JOptionPane.showInputDialog("New object name ?");
				controller.onRenameAction(selectedEntity, name);
			} else if (cmd.equals(EDIT)) {
				controller.onEditAction(selectedEntity);
			} else if (cmd.equals(DELETE)) {
				controller.onDeleteAction(selectedEntity);
			} else if (cmd.equals(INSERT)) {
				controller.onInsertAction(selectedEntity);
			} else if (cmd.equals(EXPORT)) {
				controller.onExport(selectedEntity);
			} else if (cmd.equals(IMPORT)) {
				controller.onImport();
			}
		}
		
	}
	
	private ObjectListController controller = null;
	private MasterDAO daoFactory = null;
	
	private static final String NEW = "PAL_new";
	private static final String RENAME = "PAL_rename";
	private static final String EDIT = "PAL_edit";
	private static final String DELETE = "PAL_delete";
	private static final String INSERT = "PAL_insert";
	private static final String EXPORT = "PAL_export";
	private static final String IMPORT = "PAL_import";
	
	/**
	 * The object list view constructor
	 * @param controller The view's controller
	 * @param project The main project
	 */
	public ObjectListView(ObjectListController controller, Project project) {
		super();
		this.controller = controller;
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(-1);
		setCellRenderer(new MyCellRenderer());
		try {
			daoFactory = project.getMasterDAO();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
		addMouseListener(controller);
		daoFactory.addObserver(this);
		createList();
	}
	
	private void createList() {
		try {
			List<Entity> entities = daoFactory.getDao(Entity.class).queryForAll();
			setListData(new Vector<Entity>(entities));
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
	private String getFloorName(){
		Floor floor = this.controller.getCurrentFloor();
		return (floor == null) ? "current floor" : floor.toString();
	}
	
	/**
	 * Create a pop up when user right click on the object list
	 * @return Which option the user click on
	 */
	public JPopupMenu createPopupMenu(){
		PopupActionListener listener = new PopupActionListener();
		JPopupMenu res = new JPopupMenu();
		if (!isSelectionEmpty()) {
			res.add(createPopupMenuItem("Export...", EXPORT, listener));
		}
		res.add(createPopupMenuItem("Import...", IMPORT, listener));
		
		res.add(new JPopupMenu.Separator());
		
		res.add(createPopupMenuItem("New", NEW, listener));
		
		if (!isSelectionEmpty()) {
			res.add(new JPopupMenu.Separator());
			res.add(createPopupMenuItem("Rename", RENAME, listener));
			res.add(createPopupMenuItem("Edit", EDIT, listener));
			res.add(createPopupMenuItem("Delete", DELETE, listener));
			res.add(new JPopupMenu.Separator());
			res.add(createPopupMenuItem("Create on " + this.getFloorName(), INSERT, listener));
		}
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
