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
				System.out.println("Woué");
				displayExport(null); // TODO trouvé un parent autre que null
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
	
	class FormatButtonListener implements ItemListener {
		 
        public void itemStateChanged(ItemEvent ev) {
            AbstractButton button = (AbstractButton) ev.getItemSelectable();
            String command = button.getActionCommand();
            if (command.equals("OBJ")) {
                displayExport(null,"obj"); //TODO trouvé un parent mieux que null
            } else if (command.equals("DAE")) {
            	displayExport(null,"dae"); //TODO trouvé un parent mieux que null
            } else if (command.equals("3DS")) {
            	displayExport(null,"3ds"); //TODO trouvé un parent mieux que null
            } else if (command.equals("KMZ")) {
            	displayExport(null,"kmz"); //TODO trouvé un parent mieux que null
            }
            frameExport.setVisible(false);
        }
    }



	
	/**
	 * @param parent The parent of the frame/window where you choose the extension
	 */
	public void displayExport(Component parent){
	    // creates radio button and set corresponding action
	    // commands
		frameExport = new JFrame("Export Format");
		
		
		// format OBJ, DAE, 3DS, KMZ
		
	    JRadioButton objButton = new JRadioButton("OBJ");
	    objButton.setActionCommand("OBJ");

	    JRadioButton daeButton = new JRadioButton("DAE");
	    daeButton.setActionCommand("DAE");

	    JRadioButton dsButton = new JRadioButton("3DS");
	    dsButton.setActionCommand("3DS");
	    
	    JRadioButton kmzButton = new JRadioButton("KMZ");
	    kmzButton.setActionCommand("KMZ");

	    // add event handler
	    FormatButtonListener myItemListener = new FormatButtonListener();
	    objButton.addItemListener(myItemListener);
	    daeButton.addItemListener(myItemListener);
	    dsButton.addItemListener(myItemListener);
	    kmzButton.addItemListener(myItemListener);

	    // add radio buttons to a ButtonGroup
	    final ButtonGroup group = new ButtonGroup();
	    group.add(objButton);
	    group.add(daeButton);
	    group.add(kmzButton);

	    // Frame setting
	    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    frameExport.setSize(300, 200);
	    Container cont = frameExport.getContentPane();

	    cont.setLayout(new GridLayout(0, 1));
	    cont.add(new JLabel("Please choose the export format:"));
	    cont.add(objButton);
	    cont.add(daeButton);
	    cont.add(dsButton);
	    cont.add(kmzButton);

	    frameExport.setVisible(true);
		
		
	}
	
	/**
	 * @param parent The parent of the frame/window
	 * @param extension The extension of the file to be exported
	 */
	public void displayExport(Component parent, String extension){
		this.chooser.setSelectedFile(new File("*." + extension));
	    int returnVal = this.chooser.showDialog(parent, "Export As..");
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this._controller.exportAs(this.chooser.getSelectedFile(), extension);
	    }
		
	}
}
