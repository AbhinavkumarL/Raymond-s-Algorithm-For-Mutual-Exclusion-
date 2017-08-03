import java.io.*;
import java.util.*;
import java.lang.Iterable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import java.util.Random;


public class Server implements Runnable {
	public static String host;
	public static int port;
    public static Node node ;

  
	public Server (Node n){
		this.host = n.host;
		this.port = n.port;
		this.node = n;
        // numOfMsgsRcvd = 0;
	}
	public Server(String host, int port){
		this.host=host;
		this.port=port;
	}
	private boolean isStopped = false;
	
	private boolean isStopped(){
		return this.isStopped;
	}
	
	/**
	 * Implementation of the run method for Runnable class.
	 * Creating sctpServerSocket on all nodes connected.
	 */
	@Override
	
	public void run(){
		try {
			runAsServer(this.host, this.port);
		}catch (Exception e){
			System.out.println(e);
		}
		
	}
	/**
	 * Method to create an SCTPserverSocket for each machine connected from shell script.
	 * @Exception IOException
	 * @return null
	 */
	private void runAsServer(String host, int port) throws Exception, IOException, ClassNotFoundException {
        
		// System.out.println("Creating server on :"+host+"::"+port);
        if ((host != null) && (port != 0)) {
            try {
                InetSocketAddress serverSocket = new InetSocketAddress(host, port);
                SctpServerChannel sctpServerChannel = SctpServerChannel.open().bind(serverSocket);
                while (!isStopped()) {
                	 MultiThreadedServer mts = new MultiThreadedServer(sctpServerChannel.accept(), node);
                     new Thread(mts).start();
                	//processChannel(sctpServerChannel.accept());
                }
            } catch (IOException e) {
                throw new RuntimeException("Node: runAsServer: Cannot open port on " + this.port, e);
            }
        } else {
            System.out.println("Node: runAsServer: Error: Hostname and port are not set.");
            System.out.println("Node: runAsServer: Exiting program...");
            System.exit(1);
        }
    }

    // private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    //     try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
    //         try(ObjectInputStream o = new ObjectInputStream(b)){
    //             return o.readObject();
    //         }
    //     }
    // }

    // private void processChannel (SctpChannel channel) throws Exception, IOException, ClassNotFoundException {
        
    //     try {
    //         ByteBuffer bf = ByteBuffer.allocate(640000);
    //         MessageInfo messageInfo = channel.receive(bf, null, null); 
    //         bf.flip();
    //         byte[] bytes = new byte[bf.limit()];
    //         bf.get(bytes, 0, bf.limit());
    //         bf.clear();
    //         Token t = (Token) deserialize(bytes);
    //         // System.out.println("109:Message received from: " + t);
    //         // System.out.println("\n");
    //         // if (t.getType().equals("buildTree")|| t.getType().equals("confirm")||t.getType().equals("InTree")){
    //         //     parseMessage(t);
    //         // }else {
    //         //     parseBroadCastMessage(t);
    //         // }
    //          parseMessage(t);
    //     } catch (IOException e) {
    //         System.out.println("MultiThreadedServer: run: IOException: " + e);
    //         System.exit(1);
    //     }
    // }

    // public void parseMessage(Token t) throws Exception, IOException{
    // 	System.out.println("line 108:"+t);
        
    //     if (t.type.equals("request")){ // when received request messages
    //         //System.out.println("line 111:"+node.getTopInRequestQ()+":"+t.senderHost+":"+t.senderPort);
    //         // when we have token and top oin list 
    //         if (node.initialToken !=null && node.getTopInRequestQ().split(",")[0].equals(t.senderHost) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])==t.senderPort){
    //             System.out.println("my own request , in CS");
    //             node.removeTopInRequestQ();
    //             System.out.println("top in Queue:"+node.requestQ.peek());
    //             Application.csEnter(node);
    //             if (Application.csLeave(node)){
    //                 if (!node.requestQ.isEmpty()){
    //                     System.out.println("Q not Empty:"+ node.getTopInRequestQ());
    //                     Token initT = new Token("response", node.host, node.port,node.initialToken.password);

    //                     forwardResponse(initT);
    //                     node.initialToken = null;
    //                 }
    //                 System.out.println("left CS, password still with me.");
    //             }   
    //             //Token frestoken = new Token("response", node.)
    //         }else{
    //             // when we are not in top but have token
    //             node.addToRequestQ(t.senderHost+","+t.senderPort);
    //             System.out.println("top in Queue:"+node.requestQ.peek());
    //             System.out.println(node.initialToken.toString());
    //             if (node.initialToken!= null){
    //                 System.out.println(" line 132:not my own request ");
    //                 // if (node.getTopInRequestQ().split(",")[0].equals(t.senderHost) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])==t.senderPort){
    //                 //     System.out.println("respons with password");
    //                 if (!node.requestQ.isEmpty()){
    //                     System.out.println("Q not Empty:"+ node.getTopInRequestQ());
    //                     Token initT = new Token("response", node.host, node.port,node.initialToken.password);
    //                     forwardResponse(initT);
    //                     node.initialToken = null;
    //                 }
    //                 // }
    //             }else{
    //                 forwardRequest(t);
    //             }
    //         }
    //     }else if (t.type.equals("response")){
    //         System.out.println("processing response message");
    //         node.setInitialToken(t);
    //         if (node.getTopInRequestQ().split(",")[0].equals(node.host) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])==node.port){
    //             node.setTreeNeighHost(node.host);
    //             node.setTreeNeighPort(node.port);
    //             System.out.println("removing top in Queue:"+node.requestQ.peek());

    //             // node.removeTopInRequestQ();
    //             Application.csEnter(node);
    //             if (Application.csLeave(node)){
    //                 if (!node.requestQ.isEmpty()){
    //                     System.out.println("Q not Empty:"+ node.getTopInRequestQ());
    //                     Token initT = new Token("response", node.host, node.port,node.initialToken.password);
    //                     forwardResponse(initT);
    //                     node.initialToken = null;
    //                 }
    //             }    
    //         }else{
    //             System.out.println("forwarding response message");
    //             forwardResponse(t);
    //         }
    //     }
    // }

    // public void forwardRequest(Token t){

    //     Token ft = new Token("request",node.host, node.port);
    //     Client c = new Client(node.getTreeNeighHost(), node.getTreeNeighPort(), ft);
    //     Thread fthread = new Thread(c);
    //     fthread.start();
    // }

    // public void forwardResponse(Token t){
    //     node.removeTopInRequestQ();
    //     System.out.println("line 183:"+node.requestQ.peek());
    //     if (node.requestQ.size()>0){
    //         String fresHost = node.getTopInRequestQ().split(",")[0];
    //         int fresPort = Integer.parseInt(node.getTopInRequestQ().split(",")[1]);

    //         node.setTreeNeighHost(fresHost);
    //         node.setTreeNeighPort(fresPort);
    //         node.removeTopInRequestQ();
    //         System.out.println("line 191:"+node.requestQ.peek());

    //         Token ft = new Token("response",node.host, node.port, t.getPassword());
    //         Client c = new Client(fresHost,fresPort, ft);
    //         //System.out.println("line 195:")
    //         Thread fthread = new Thread(c);
    //         fthread.start();
    //     }
    // }

}