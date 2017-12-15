package tap.shortest_path_client;

import java.io.IOException;

public interface IClient {
	public void addNode(Node n) throws IOException;
	public void remNode(String name) throws IOException;
	public Node getAllNodes() throws IOException;
	public Node getShortestPath(Node from, Node to) throws IOException;

}
