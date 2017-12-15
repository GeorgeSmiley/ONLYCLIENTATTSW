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
	public void addNode(Node n) throws IOException {
		restclient.connect();
		restclient.doPost(Request.REQUEST_ADD,gson.toJson(n));
		restclient.disconnect();
		
	}

	public void remNode(String name) throws IOException {
		restclient.connect();
		restclient.doPost(Request.REQUEST_REMOVE, name);
		restclient.disconnect();
	}

	public Node getAllNodes() throws IOException {
		restclient.connect();
		Node main=gson.fromJson(restclient.doPost(Request.REQUEST_ALL,null), Node.class);
		restclient.disconnect();
		return main;
	}

	public Node getShortestPath(Node from, Node to) throws IOException {
		restclient.connect();
		Node initial=gson.fromJson(restclient.doPost(Request.REQUEST_PATH, gson.toJson(from)+"->"+gson.toJson(to)),Node.class);
		restclient.disconnect();
		return initial;
	}

}
