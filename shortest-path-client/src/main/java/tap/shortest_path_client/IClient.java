package tap.shortest_path_client;

import java.io.IOException;

public interface IClient {
	public Node getAllNodes() throws IOException;
	public Node getShortestPath(String fromName, String toName) throws IOException;

}
