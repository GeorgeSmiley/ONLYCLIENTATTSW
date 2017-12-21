package tap.shortes_path_client.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.JsonSyntaxException;

import tap.shortest_path_client.Client;
import tap.shortest_path_client.GridFromServer;
import tap.shortest_path_client.RestServiceClient;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GUIpanel panel;
	private JPanel NORTH,SOUTH;
	private JTextField server,port,urlToAll;
	private JButton perform,createConnector,reset;
	private JComboBox<String> actions,comboCity;
	private Client cl;
	private boolean GRID_ENABLED,PATH_ENABLED;
	public GUI() {
		super(Messages.getString("GUI.APP_TITLE"));
		initializeGui();
		initializeLocalFields();
		
	}
	private void initializeLocalFields() {
		cl=new Client();
		GRID_ENABLED=PATH_ENABLED=false;
	}
	private void initializeGui() {
		createWidgets();
		addWidgetsToFrame();
		graphicalAdjustements();
		createEvents();
	}
	private void repaintAllGraph() {
		setVisible(true);
		repaint();
		revalidate();
	}
	private void createEvents() {
		
		panel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if(arg0.getWheelRotation()==1) {
					panel.reduceCoordinatesOfPoints();
				}
				else
				{
					panel.enlargeCoordinatesOfPoints();
				}
				repaintAllGraph();
				
			}});
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.reset();
				PATH_ENABLED=false;
				comboCity.setEnabled(true);
				
			}
			
		});
		createConnector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String prefix=Messages.getString("GUI.HTTP")+server.getText()+":"+port.getText(); 
				String urltoall=prefix+urlToAll.getText();
				String user=JOptionPane.showInputDialog(null,Messages.getString("GUI.INSERT_USERNAME")); 
				String password=JOptionPane.showInputDialog(null,Messages.getString("GUI.INSERT_PASSWORD")); 
				try {
					cl.setRestServiceClient(new RestServiceClient(urltoall,user,password));
					JOptionPane.showMessageDialog(null, Messages.getString("GUI.LOGIN_OK")); 
				} catch (RuntimeException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					cl.setRestServiceClient(null);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, Messages.getString("GUI.SERVER_CONN_REFUSED")); 
					cl.setRestServiceClient(null);
				}
				
			}
		});
		perform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch(actions.getSelectedIndex()){
				case 0:{ 
					requestAll();
					break;
				}
				case 1:{ 
					if(!GRID_ENABLED) {
						JOptionPane.showMessageDialog(null, Messages.getString("GUI.CITIES_FIRST")); //$NON-NLS-1$
						break;
					}
					requestGrid();
					break;
				}
				case 2:{ 
					if(!PATH_ENABLED) {
						JOptionPane.showMessageDialog(null, Messages.getString("GUI.PATH_FIRST")); //$NON-NLS-1$
						break;
					}
						requestPath();
						
					
					break;
				}
				}
			}

			private void requestPath() {
				String from=JOptionPane.showInputDialog(null,Messages.getString("GUI.INSERT_SOURCE")); 
				String to=JOptionPane.showInputDialog(null,Messages.getString("GUI.INSERT_SINK")); 
				try {
					tryToHighlightPath(from, to);
				} catch (JsonSyntaxException e) {
					JOptionPane.showMessageDialog(null, Messages.getString("GUI.ERROR_JSON"));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP")); 
				}
			}

			private void tryToHighlightPath(String from, String to) throws IOException {
				panel.highlightPath(null);
				List<String> minPath=cl.getShortestPath(from,to,(String)comboCity.getSelectedItem());
				if(minPath.isEmpty()) JOptionPane.showMessageDialog(null, Messages.getString("GUI.NO_PATHS_FOUND"));
				panel.highlightPath(minPath);
			}

			private void requestGrid() {
				try {
					panel.reset();
					GridFromServer grid=cl.retrieveGrid((String)comboCity.getSelectedItem());
					GraphBuilder.makeGraph(grid, panel);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
				} 
				comboCity.setEnabled(false);
				PATH_ENABLED=true;
			}

			private void requestAll() {
				try {
					tryToGetAllTables();
				} catch (JsonSyntaxException e) {
					JOptionPane.showMessageDialog(null, Messages.getString("GUI.ERROR_JSON"));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP")); 
					
				} catch(NullPointerException e) {
					JOptionPane.showMessageDialog(null,Messages.getString("GUI.NO_CONNECTOR"));
				}
				GRID_ENABLED=true;
			}

			private void tryToGetAllTables() throws IOException {
				List<String> cities=cl.getAllTables();
				comboCity.removeAllItems();
				for(String e:cities) {
					comboCity.addItem(e);
				}
			}});
	}
	private void graphicalAdjustements() {
		setLocation(50,50);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		initFields(); 

	}
	private void initFields() {
		server.setText(Messages.getString("GUI.LOCALHOST")); 
		port.setText(Messages.getString("GUI.DEFAULT_PORT")); 
		urlToAll.setText(Messages.getString("GUI.DEFAULT_API_PATH"));
	}
	private void addWidgetsToFrame() {
		addToNorth();
		addToSouth();
		addToContentPane();
	}
	private void addToContentPane() {
		Container c=getContentPane();
		c.add(NORTH,BorderLayout.NORTH);
		c.add(panel,BorderLayout.CENTER);
		c.add(SOUTH,BorderLayout.SOUTH);
	}
	private void addToSouth() {
		SOUTH.add(actions);
		SOUTH.add(perform);
		SOUTH.add(comboCity);
		SOUTH.add(reset);
	}
	private void addToNorth() {
		NORTH.add(new JLabel("Host"));
		NORTH.add(server);
		NORTH.add(new JLabel("Port")); 
		NORTH.add(port);
		NORTH.add(new JLabel("Url to RESTful API"));
		NORTH.add(urlToAll);
		NORTH.add(createConnector);
	}
	private void createWidgets() {
		
		createPanels();
		createTextFields();
		createComboBoxes();
		createButtons(); 
	}
	private void createComboBoxes() {
		actions=new JComboBox<String>();
		for(String e:(Arrays.asList(Messages.getString("GUI.SHOW_GRIDS"),Messages.getString("GUI.REQUEST_GRID"),Messages.getString("GUI.REQUEST_PATH")))){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			actions.addItem(e);
		}
		comboCity=new JComboBox<String>();
	}
	private void createButtons() {
		perform=new JButton("Perform"); 
		createConnector=new JButton("Create connector");
		reset=new JButton("Reset");
	}
	private void createTextFields() {
		server=new JTextField(10);
		port=new JTextField(6);
		urlToAll=new JTextField(10);
	}
	private void createPanels() {
		NORTH=new JPanel();
		SOUTH=new JPanel();
		panel=new GUIpanel(20);
	}
	private static void createGui() {
		new GUI();
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				createGui();
				
			}});
	}

}
