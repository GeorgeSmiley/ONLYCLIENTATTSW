package tap.shortest_path_client;

import java.io.IOException;

import com.google.gson.Gson;

public class Client implements IClient {

	private IRestServiceClient restclient;
    private Gson gson;
    public Client() {
    	gson=new Gson();
    }
	public void setRestServiceClient(IRestServiceClient restclient) {
		this.restclient=restclient;
		
	}


	public Node getAllNodes() throws IOException {
		Node main=gson.fromJson(restclient.doGet(Request.REQUEST_ALL,null), Node.class);
		
		return main;
	}

	public Node getShortestPath(String fromName, String toName) throws IOException {
		Node initial=gson.fromJson(restclient.doGet(Request.REQUEST_PATH, fromName+"TO"+toName),Node.class);
		
		return initial;
	}

}
