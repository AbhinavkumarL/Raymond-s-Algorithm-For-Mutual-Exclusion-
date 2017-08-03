import java.io.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Service{
	List<String> list = new ArrayList<String>();
	int numNodes;
	int expMean ;
	int csDuration;
	int numRequests;
	String startNode;
	int startPort;
	Node node;
	Token t;

/**
	 * Method to load the configuration file for building neighbor nodes.
	 * 
	 * @Exception IOException
	 */
	public void loadFile() throws IOException{
		
		// for (String Line : Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/CS6378/Project2/config.txt"))){
		for (String Line : Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/config.txt"))){
			if (Line.startsWith("#params")){
				this.numNodes = Integer.parseInt(Line.split(" ")[1]);
				this.expMean = Integer.parseInt(Line.split(" ")[2]);
				this.csDuration = Integer.parseInt(Line.split(" ")[3]);
				this.numRequests = Integer.parseInt(Line.split(" ")[4]);
			}else {
				list.add(Line);
			}
		}
		System.out.println(list.get(0));
		startNode = list.get(0).split(" ")[1];
		startPort = Integer.parseInt(list.get(0).split(" ")[2]);
	}
	/**
	 * Method to build neighbors associated to a particular node.
	 * 
	 * @return null
	 */
	public void  buildNodes(String host, int port, int neighbor){
		node= new Node(host, port);
		String s = list.get(neighbor);
		node.setTreeNeighHost(s.split(" ")[1]);
		node.setTreeNeighPort(Integer.parseInt(s.split(" ")[2]));
		node.setCsDuration(csDuration);
		node.setNumRequest(numRequests);
		node.setTotalRequest(numRequests);

		System.out.println("Line 54:"+startNode +":"+ startPort);
		if( (node.getHost().equals(startNode)) && (node.getPort()==startPort)){
			// node.setPassword("1234enter");
			Token t = new Token("Initial", node.host, node.port, "1234Enter");
			System.out.println("Token Created at :"+ node.port);
			node.setInitialToken(t);
		}

		Server sctpServer = new Server(node);
		Thread t =new Thread(sctpServer);
		try {

			t.start();
			t.sleep(15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("build Nodes Completed:");
		node.printTreeneighbors();
		while(node.getNumRequest()>0 ){
			sendRequestMsg();
			node.reqTimes.add(System.currentTimeMillis());
			node.decNumRequest();
			System.out.println("line 75:request number:="+node.getNumRequest());
		}
	}
    public double getRandom(int p) { 
    	Random r = new Random();
    	return -(Math.log(1-(r.nextDouble()))*p); 
    } 




    public void sendRequestMsg(){
    	
    	String queueEntry = node.host+","+node.port;
    	//System.out.println("line 83:"+ node.getTreeNeighHost()+":"+node.getTreeNeighPort());

    	Client c = new Client(node.getTreeNeighHost(), node.getTreeNeighPort(), new Token("request",node.host, node.port));
    	Thread t =new Thread(c);
		try {
			
			node.addToRequestQ(queueEntry);
			System.out.println("line 88:"+ node.requestQ.size());

			t.sleep((int)getRandom(expMean));
			t.start();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public static void main(String [] args)  throws IOException{
		if (args.length>0){
			Service sv = new Service();
			sv.loadFile();
			sv.buildNodes(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
		}

	}
}