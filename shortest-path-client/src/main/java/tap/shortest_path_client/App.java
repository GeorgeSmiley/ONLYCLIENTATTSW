package tap.shortest_path_client;

import java.awt.EventQueue;


import tap.shortes_path_client.gui.GUI;





public class App 
{
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				GUI.createGui();
				
			}});
	}
}
