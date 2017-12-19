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
	
	/*
	public static void main(String[] args)
	{
		
		
		GUIpanel p=initFrame();
		GridFromServer mat=new GridFromServer(
				new int[][] {
					{1,0,0,0,1,0,0,0,1},
					{0,1,0,0,0,0,0,1,0},
					{1,0,0,0,1,0,0,0,1},
					{0,1,0,0,0,0,0,1,0},
					{1,0,0,0,1,0,0,0,1},
					{0,1,0,0,0,0,0,1,0},
					{1,0,0,0,1,0,0,0,1},
					{0,1,0,0,0,0,0,1,0},
				}
				);
		new GraphBuilder().makeGraph(mat, p);
		
		
	}
	private static GUIpanel initFrame() {
		JFrame frame=new JFrame();
		GUIpanel pan;
		frame.setLocation(50,50);
		frame.setSize(1024, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JPanel(),BorderLayout.WEST);
		frame.add(pan=new GUIpanel(30),BorderLayout.CENTER);
		frame.setVisible(true);
		return pan;
		
	}
	*/ 
}
