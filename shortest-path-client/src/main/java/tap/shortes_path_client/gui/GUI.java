package tap.shortes_path_client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.google.gson.JsonSyntaxException;

import tap.shortest_path_client.Client;
import tap.shortest_path_client.GridFromServer;
import tap.shortest_path_client.IClient;
import tap.shortest_path_client.RestServiceClient;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GUIpanel panel;
	private JPanel NORTH,SOUTH;
	private JTextField server,port,urlToAll,userField,txtsource,txtsink;
	private JButton perform,createConnector,reset;
	private JComboBox<String> actions,comboCity;
	private JLabel lblout;
	private IClient cl;
	private boolean GRID_ENABLED,PATH_ENABLED,CONN_CREATED;
	private JPasswordField passField;
	public GUI() {
		super(Messages.getString("GUI.APP_TITLE")); //$NON-NLS-1$
		initializeGui();
		initializeLocalFields();
		
	}
	private void initializeLocalFields() {
		cl=new Client();
		GRID_ENABLED=PATH_ENABLED=CONN_CREATED=false;
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
	private void alert(String message) {
		lblout.setText(message);
	}
	public void resizeGrid(int rotation) {
		if(rotation>0) {
			panel.reduceCoordinatesOfPoints();
		}
		else
		{
			panel.enlargeCoordinatesOfPoints();
		}
		repaintAllGraph();
	}
	public void resetPane() {
		panel.reset();
		PATH_ENABLED=false;
		comboCity.setEnabled(true);
	}
	public void createConnection() {
		String prefix=Messages.getString("GUI.HTTP")+server.getText()+":"+port.getText();  //$NON-NLS-1$ //$NON-NLS-2$
		String urltoall=prefix+urlToAll.getText();
		String user=userField.getText();
		if(user.length()==0) {
			alert(Messages.getString("GUI.INSERT_USERNAME")); //$NON-NLS-1$
			return;
		}
		
		String password=new String(passField.getPassword());
		if(password.length()==0) {
			alert(Messages.getString("GUI.INSERT_PASSWORD")); //$NON-NLS-1$
			return;
		}
		RestServiceClient rsc=(new RestServiceClient(urltoall,user,password));
		try {
			
			cl.setRestServiceClient(rsc);
			cl.doLogin();
			CONN_CREATED=true;
			alert(Messages.getString("GUI.LOGIN_OK"));  //$NON-NLS-1$
		} catch (RuntimeException e) {
			alert( Messages.getString("GUI.LOGIN_NOK"));
			cl.setRestServiceClient(null);
		} catch (IOException e) {
			alert( Messages.getString("GUI.SERVER_CONN_REFUSED"));  //$NON-NLS-1$
			cl.setRestServiceClient(null);
		}
	}
	public void requestPath() {
		String from=txtsource.getText();
		if(from.length()==0) {
			alert(Messages.getString("GUI.INSERT_SOURCE"));  //$NON-NLS-1$
			return;
		}
		String to=txtsink.getText();
		if(to.length()==0) {
			alert (Messages.getString("GUI.INSERT_SINK"));  //$NON-NLS-1$
			return;
		}
		try {
			tryToHighlightPath(from, to);
		} catch (JsonSyntaxException e) {
			alert(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP")); //$NON-NLS-1$
		} catch (IOException e) {
			alert(Messages.getString("GUI.SERVER_CONN_REFUSED"));  //$NON-NLS-1$
		} catch(NullPointerException e) {
			alert(Messages.getString("GUI.NO_CONNECTOR"));
		}
	}

	private void tryToHighlightPath(String from, String to) throws IOException {
		panel.highlightPath(null);
		List<String> minPath=cl.getShortestPath(from,to,(String)comboCity.getSelectedItem());
		alert(Messages.getString("GUI.QUERY_OK"));
		if(minPath.isEmpty()) alert(Messages.getString("GUI.NO_PATHS_FOUND")); //$NON-NLS-1$
		panel.highlightPath(minPath);
		
	}

	public void requestGrid() {
		try {
			panel.reset();
			GridFromServer grid=cl.retrieveGrid((String)comboCity.getSelectedItem());
			GraphBuilder.makeGraph(grid, panel);
			alert(Messages.getString("GUI.QUERY_OK"));
		} catch (IOException e) {
			alert(Messages.getString("GUI.SERVER_CONN_REFUSED")); //$NON-NLS-1$
		}  catch(JsonSyntaxException e) {
			alert(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
		} catch(NullPointerException e) {
			alert(Messages.getString("GUI.NO_CONNECTOR"));
		}
		comboCity.setEnabled(false);
		PATH_ENABLED=true;
	}

	public void requestAll() {
		try {
			tryToGetAllTables();
			alert(Messages.getString("GUI.QUERY_OK"));
		} catch (JsonSyntaxException e) {
			alert(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP")); //$NON-NLS-1$
		} catch (IOException e) {
			alert(Messages.getString("GUI.SERVER_CONN_REFUSED"));  //$NON-NLS-1$
			
		} catch(NullPointerException e) {
			alert(Messages.getString("GUI.NO_CONNECTOR")); //$NON-NLS-1$
		}
		GRID_ENABLED=true;
	}

	private void tryToGetAllTables() throws IOException {
		List<String> cities=cl.getAllTables();
		comboCity.removeAllItems();
		for(String e:cities) {
			comboCity.addItem(e);
		}
	}
	private void createEvents() {
		
		panel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				resizeGrid(arg0.getWheelRotation());
				
			}

			});
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetPane();
				
			}

			
			
		});
		createConnector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createConnection(); 
				
				
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
					
					if(!GRID_ENABLED && CONN_CREATED) {
						alert(Messages.getString("GUI.GRIDS_FIRST")); //$NON-NLS-1$
						break;
					}
					requestGrid();
					break;
				}
				case 2:{ 
					if(!PATH_ENABLED && CONN_CREATED) {
						alert(Messages.getString("GUI.REQAGRID_FIRST")); //$NON-NLS-1$
						break;
					}
						requestPath();
						txtsource.setText("");
						txtsink.setText("");
					
					break;
				}
				}
			}

			});
	}
	private void graphicalAdjustements() {
		setLocation(50,50);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		initFields(); 
		lblout.setFont(new Font("",Font.BOLD,16)); //$NON-NLS-1$
		lblout.setForeground(Color.BLUE);
		
	}
	private void initFields() {
		server.setText(Messages.getString("GUI.LOCALHOST"));  //$NON-NLS-1$
		port.setText(Messages.getString("GUI.DEFAULT_PORT"));  //$NON-NLS-1$
		urlToAll.setText(Messages.getString("GUI.DEFAULT_API_PATH")); //$NON-NLS-1$
		userField.setText(Messages.getString("GUI.0")); //$NON-NLS-1$
		passField.setText(Messages.getString("GUI.1")); //$NON-NLS-1$
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
		
		SOUTH.add(comboCity);
		SOUTH.add(new JLabel(Messages.getString("GUI.9")));
		SOUTH.add(txtsource);
		SOUTH.add(new JLabel(Messages.getString("GUI.10")));
		SOUTH.add(txtsink);
		SOUTH.add(perform);
		SOUTH.add(reset);
		SOUTH.add(lblout);
	}
	private void addToNorth() {
		NORTH.add(new JLabel(Messages.getString("GUI.2"))); //$NON-NLS-1$
		NORTH.add(server);
		NORTH.add(new JLabel(Messages.getString("GUI.3")));  //$NON-NLS-1$
		NORTH.add(port);
		NORTH.add(new JLabel(Messages.getString("GUI.4"))); //$NON-NLS-1$
		NORTH.add(urlToAll);
		NORTH.add(new JLabel(Messages.getString("GUI.7")));
		NORTH.add(userField);
		NORTH.add(new JLabel(Messages.getString("GUI.8")));
		NORTH.add(passField);
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
		actions.setName("actionsComboBox"); //$NON-NLS-1$
		for(String e:(Arrays.asList(Messages.getString("GUI.SHOW_GRIDS"),Messages.getString("GUI.REQUEST_GRID"),Messages.getString("GUI.REQUEST_PATH")))){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			actions.addItem(e);
		}
		comboCity=new JComboBox<String>();
		comboCity.setName("gridComboBox"); //$NON-NLS-1$
	}
	private void createButtons() {
		perform=new JButton("Perform");  //$NON-NLS-1$
		perform.setName("performButton"); //$NON-NLS-1$
		createConnector=new JButton("Create connector"); //$NON-NLS-1$
		createConnector.setName("createConnectorButton"); //$NON-NLS-1$
		reset=new JButton("Reset"); //$NON-NLS-1$
		reset.setName("resetButton"); //$NON-NLS-1$
	}
	private void createTextFields() {
		server=new JTextField(10);
		server.setName("serverField"); //$NON-NLS-1$
		port=new JTextField(6);
		port.setName("portField"); //$NON-NLS-1$
		urlToAll=new JTextField(10);
		urlToAll.setName("urlToApiField"); //$NON-NLS-1$
		lblout=new JLabel();
		lblout.setName("outputLabel"); //$NON-NLS-1$
		txtsource=new JTextField(4);
		txtsink=new JTextField(4);
		txtsource.setName("sourceField");
		txtsink.setName("sinkField");
		passField=new JPasswordField(10);
		passField.setName("passField");
		userField=new JTextField(10);
		userField.setName("userField");
	}
	private void createPanels() {
		NORTH=new JPanel();
		NORTH.setName("North Panel"); //$NON-NLS-1$
		SOUTH=new JPanel();
		SOUTH.setName("South Panel"); //$NON-NLS-1$
		panel=new GUIpanel(20);
		panel.setName("Gui Panel"); //$NON-NLS-1$
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
	public void mockClient(IClient mock) {
		this.cl=mock;
		CONN_CREATED=(mock!=null);
		
	}

}
