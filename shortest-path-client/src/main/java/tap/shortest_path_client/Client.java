package tap.shortest_path_client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Client implements IClient {

	private IRestServiceClient restclient;
	private Gson gson;
    public Client() {
    	this.gson=new Gson();
    }
    public Client(IRestServiceClient restclient) {
    	this.gson=new Gson();
    	this.restclient=restclient;
    }
	public void setRestServiceClient(IRestServiceClient restclient) {
		this.restclient=restclient;
		
	}


	public List<String> getAllTables() throws JsonSyntaxException, IOException {
		String rcv=(restclient.doGet(Request.REQUEST_ALL,null));
		return gson.fromJson(rcv,List.class);
	}

	public List<String> getShortestPath(String fromName, String toName) throws JsonSyntaxException, IOException  {
		String rcv=(restclient.doGet(Request.REQUEST_PATH, fromName+"TO"+toName));
		return gson.fromJson(rcv, List.class);
	}
	@Override
	public GridFromServer retrieveGrid(String name) throws IOException {
		GridFromServer retrieved = gson.fromJson(restclient.doGet(Request.REQUEST_GRID, name), GridFromServer.class);
		return retrieved;
	}

}
