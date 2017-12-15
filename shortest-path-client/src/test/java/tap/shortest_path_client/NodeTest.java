package tap.shortest_path_client;

import static org.junit.Assert.*;


import org.junit.Test;

public class NodeTest {
	private Node node;
	
	@Test
	public void testNameNotNull() {
		node = new Node();
		assertNotNull(node.getName());
	}
	@Test
	public void testHasNoParents() {
		node = new Node();
		assertNull(node.getLeft());
		assertNull(node.getRight());
		assertNull(node.getUp());
		assertNull(node.getDown());
	}
	@Test
	public void testHasAllParents() {
		node = new Node("",new Node(),new Node(), new Node(), new Node());
		assertNotNull(node.getLeft());
		assertNotNull(node.getRight());
		assertNotNull(node.getDown());
		assertNotNull(node.getUp());
	}
	@Test(expected=IllegalArgumentException.class)
	public void testIsNotSelfParentDown() {
		node=new Node();
		node.setDown(node);
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void testIsNotSelfParentUp() {
		node=new Node();
		node.setUp(node);
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void testIsNotSelfParentLeft() {
		node=new Node();
		node.setRight(node);
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void testIsNotSelfParentRight() {
		node=new Node();
		node.setRight(node);
		
	}
	@Test
	public void testCorrectNameWhenPassedToConstructor() {
		node=new Node("node1");
		assertEquals("node1",node.getName());
	}
	
	
	
	
	

}
