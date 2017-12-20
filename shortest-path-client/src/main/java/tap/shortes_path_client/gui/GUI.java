package tap.shortes_path_client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private JTextField server,port,urlToPath,urlToAll,urlToGrid;
	private JButton perform,createConnector,reset;
	private JComboBox<String> actions,comboCity;
	private Client cl;
	private boolean GRID_ENABLED,PATH_ENABLED;
	public GUI() {
		super("ATTSW Project 17/18");
		createWidgets();
		addWidgetsToFrame();
		graphicalAdjustements();
		createEvents();
		cl=new Client();
		GRID_ENABLED=PATH_ENABLED=false;
		
	}
	private void createEvents() {
		
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
				String serverurl=server.getText();
				String _port=port.getText();
				String prefix="http://"+serverurl+":"+_port;
				String urltopath=prefix+urlToPath.getText();
				String urltoall=prefix+urlToAll.getText();
				String urltogrid=prefix+urlToGrid.getText();
				cl.setRestServiceClient(new RestServiceClient(urltoall,urltopath,urltogrid));
				JOptionPane.showMessageDialog(null, "Connector created");
			}
		});
		perform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd=(String) actions.getSelectedItem();
				switch(cmd){
				case "Show cities":{
					try {
						List<String> cities=cl.getAllTables();
						comboCity.removeAllItems();
						for(String e:cities) {
							comboCity.addItem(e);
						}
					} catch (JsonSyntaxException e) {
						JOptionPane.showMessageDialog(null, "Error while parsing json object!");
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Error while connecting to server!");
						
					} catch(NullPointerException e) {
						JOptionPane.showMessageDialog(null,"You must create connector before performing operations!");
					}
					GRID_ENABLED=true;
					break;
				}
				case "Request city":{
					if(!GRID_ENABLED) {
						JOptionPane.showMessageDialog(null, "Must retrieve all cities first!");
						break;
					}
					try {
						panel.reset();
						GridFromServer grid=cl.retrieveGrid((String)comboCity.getSelectedItem());
						GraphBuilder.makeGraph(grid, panel);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					comboCity.setEnabled(false);
					PATH_ENABLED=true;
					break;
				}
				case "Request path":{
					if(!PATH_ENABLED) {
						JOptionPane.showMessageDialog(null, "Must retrieve a grid first!");
						break;
					}
						String from=JOptionPane.showInputDialog(null,"Insert source node");
						String to=JOptionPane.showInputDialog(null,"Insert sink node");
						try {
							panel.highlightPath(null);
							List<String> minPath=cl.getShortestPath(from,to,(String)comboCity.getSelectedItem());
							if(minPath.isEmpty()) JOptionPane.showMessageDialog(null, "No paths found from source node to sink!");
							panel.highlightPath(minPath);
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					
					break;
				}
				}
				
			}});
	}
	private void graphicalAdjustements() {
		setLocation(50,50);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		server.setText("localhost");
		port.setText("8080");
		urlToAll.setText("/api/");
		urlToPath.setText("/api/path");
		urlToGrid.setText("/api/city");
	}
	private void addWidgetsToFrame() {
		NORTH.add(new JLabel("Host"));
		NORTH.add(server);
		NORTH.add(new JLabel("port"));
		NORTH.add(port);
		NORTH.add(new JLabel("Url to all"));
		NORTH.add(urlToAll);
		NORTH.add(new JLabel("Url to city"));
		NORTH.add(urlToGrid);
		NORTH.add(new JLabel("Url to path"));
		NORTH.add(urlToPath);
		NORTH.add(createConnector);
		SOUTH.add(actions);
		SOUTH.add(perform);
		SOUTH.add(comboCity);
		SOUTH.add(reset);
		Container c=getContentPane();
		
		c.add(NORTH,BorderLayout.NORTH);
		c.add(panel,BorderLayout.CENTER);
		c.add(SOUTH,BorderLayout.SOUTH);
	}
	private void createWidgets() {
		NORTH=new JPanel();
		SOUTH=new JPanel();
		panel=new GUIpanel(20,300,150);
		server=new JTextField(10);
		port=new JTextField(6);
		urlToPath=new JTextField(10);
		urlToAll=new JTextField(10);
		urlToGrid=new JTextField(10);
		perform=new JButton("Perform");
		actions=new JComboBox<String>();
		for(String e:(Arrays.asList("Show cities","Request city","Request path"))){
			actions.addItem(e);
		}
		createConnector=new JButton("Create connector");
		reset=new JButton("Reset");
		comboCity=new JComboBox<String>();
	}
	public static void main(String[] args) {
		new GUI();
	}

}
