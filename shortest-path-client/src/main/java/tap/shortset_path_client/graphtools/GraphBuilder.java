package tap.shortset_path_client.graphtools;



import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import tap.shortes_path_client.gui.GUIpanel;
import tap.shortest_path_client.Node;

public class GraphBuilder {
	private LinkedList<Node> visited=new LinkedList<Node>();
	private LinkedList<String[]> nameMov= new LinkedList<String[]>();
	public  void makeGraph(Node node) {
		if(node==null) {
			nameMov.add(new String[] {"ext_node","backedge"});
			return;
		}
	    
	    visited.add(node);
	    
	    if(!visited(node.getLeft())) {
	    
	    	nameMov.add(new String[] {node.getName(),"left"});
	    	makeGraph(node.getLeft());
	    }
	    if(!visited(node.getRight())) {
	    	
	    	nameMov.add(new String[] {node.getName(),"right"});
	    	makeGraph(node.getRight());
	    }
	    if(!visited(node.getUp())) {
	    
	    	nameMov.add(new String[] {node.getName(),"up"});
	    	makeGraph(node.getUp());
	    }
	    if(!visited(node.getDown())) {
	    	nameMov.add(new String[] {node.getName(),"down"});
	    	makeGraph(node.getDown());
	    }
	    
	}
	
	private boolean visited(Node node) {
		return visited.contains(node);
	}
	public static void alert(String args) {
		JOptionPane.showMessageDialog(null, args);
	}
	public static void main(String[] args)
	{
		Node root=new Node("root");
		Node left=new Node("rtlf");
		Node right=new Node("rtrg");
		Node leftup=new Node("rtlfu");
		Node rightdown=new Node("rtrgdo");
		root.setLeft(left);
		left.setRight(root);
		root.setRight(right);
		right.setLeft(root);
		left.setUp(leftup);
		leftup.setDown(left);
		right.setDown(rightdown);
		rightdown.setUp(right);
		
		GraphBuilder b=new GraphBuilder();
		b.makeGraph(rightdown);
		
		GUIpanel p=initFrame();
		
		for(String[] e:b.nameMov) {
			//System.out.print(e[0]+" "+e[1]+" ");
			
			if(e[0].equals("ext_node")) {
				p.undoLast();
			}
			else
			{
				p.enablePoint(e[0]);
				switch(e[1]) {
				case "left": {
					p.moveLeft();
					break;
				}
				case "right": {
					p.moveRight();
					break;
				}
				case "down": {
					p.moveDown();
					break;
				}
				case "up": {
					p.moveUp();
					break;
				}
				}
			}
		}
		
		
		
	}
	private static GUIpanel initFrame() {
		JFrame frame=new JFrame();
		GUIpanel pan;
		frame.setLocation(50,50);
		frame.setSize(1024, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pan=new GUIpanel(70));
		frame.setVisible(true);
		return pan;
		
	}
}
