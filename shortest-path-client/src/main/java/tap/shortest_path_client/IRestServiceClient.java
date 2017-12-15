package tap.shortest_path_client;

import java.io.IOException;

import com.google.gson.JsonElement;

public interface IRestServiceClient {
	public String doGet(int request, String args) throws IOException;
	

}
