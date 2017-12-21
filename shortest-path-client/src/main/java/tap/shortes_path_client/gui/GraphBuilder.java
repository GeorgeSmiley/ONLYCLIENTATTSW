package tap.shortes_path_client.gui;




import java.awt.Color;

import tap.shortest_path_client.GridFromServer;

public class GraphBuilder {
	
	public static void makeGraph(GridFromServer mat, GUIpanel p) {
		int n=mat.getN();
		for(int i=0; i<n;i++)
		{
			
			for(int j=0; j<n;j++) {
				if(mat.isEnabled(i, j))p.enablePoint(mat.getName(i,j),i,j,Color.RED);
				else p.enablePoint("", i, j,Color.BLACK);
			}
			
		}
	    
	}
	
	
}
