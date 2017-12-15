package tap.shortest_path_client;

import java.io.IOException;

import com.google.gson.JsonElement;

public interface IRestServiceClient {
	public void connect() throws IOException;
	public JsonElement doPost(int request, String args) throws IOException;
	public void disconnect() throws IOException;

}
