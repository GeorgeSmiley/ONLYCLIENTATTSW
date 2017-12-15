package tap.shortest_path_client;

import java.io.IOException;

public interface IRestServiceClient {
	public String doGet(int request, String args) throws IOException;
	

}
