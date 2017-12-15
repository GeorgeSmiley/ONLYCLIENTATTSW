package tap.shortest_path_client;

public class Node {

	private String name;
	private Node up,down,left,right;
	public Node getUp() {
		return up;
	}
	public void setUp(Node up) throws IllegalArgumentException {
		if(up==this) throw new IllegalArgumentException("A node cannot be parent of itself");
		this.up = up;
	}
	public Node getDown() {
		return down;
	}
	public void setDown(Node down) throws IllegalArgumentException {
		if(down==this) throw new IllegalArgumentException("A node cannot be parent of itself");
		this.down = down;
	}
	public Node getLeft() {
		return left;
	}
	public void setLeft(Node left) throws IllegalArgumentException {
		if(left==this) throw new IllegalArgumentException("A node cannot be parent of itself");
		this.left = left;
	}
	public Node getRight() {
		return right;
	}
	public void setRight(Node right) throws IllegalArgumentException {
		if(right==this) throw new IllegalArgumentException("A node cannot be parent of itself");
		this.right = right;
	}
	public Node() {
		this.name="";
	}
	public Node(String name) {
		this.name=name;
	}
	public Node(String name, Node up, Node down, Node left, Node right) {
		this.name=name;
		this.up=up;
		this.down=down;
		this.left=left;
		this.right=right;
	}
	public void setName(String name) {
		this.name=name;
	}
	public String getName() {
		return name;
	}

}
