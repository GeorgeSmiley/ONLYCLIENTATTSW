package tap.shortest_path_client;

public class Node {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((down == null) ? 0 : down.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((up == null) ? 0 : up.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (down == null) {
			if (other.down != null)
				return false;
		} else if (!down.equals(other.down))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (up == null) {
			if (other.up != null)
				return false;
		} else if (!up.equals(other.up))
			return false;
		return true;
	}
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
