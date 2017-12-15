package tap.shortest_path_client;

import java.io.IOException;

import com.google.gson.Gson;




/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	Gson gson=new Gson();
        Client cl=new Client();
        cl.setRestServiceClient(new RestServiceClient("http://localhost:8080","http://localhost:8080/path"));
        System.out.println((cl.getShortestPath("node1","node2")));

       
        
    }
}
