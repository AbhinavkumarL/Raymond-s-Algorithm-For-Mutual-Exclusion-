import java.io.*;
import java.util.*;
import java.lang.Iterable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import java.util.Random;
import java.util.concurrent.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.*;    
import java.text.*;
// import com.sun.nio.sctp.*;

/**
 * Multi Threaded Server class handles threads created by the server for the
 * incoming sockets.
 */

public class MultiThreadedServer implements Runnable {

    private SctpChannel channel;
    // private Token serverToken;
    private boolean isStopped = false;
    private Thread runningThread = null;
    public static String host;
    public static int port;
    public static Node node ;
    // // public static int numOfMsgsRcvd;
    // public static int startBroadCast =0;
    // public static int boradCastCount =0;
    // public static int convergeCastCount =0;
    // public static boolean broadCastCompleted= true;


    public MultiThreadedServer(SctpChannel channel, Node node){
        this.channel = channel;
        this.node = node;
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.runningThread.interrupt();
            this.channel.close();
        } catch (IOException e) {
            throw new RuntimeException("MultiThreadedServer: stop: Error closing server", e);
        }
    }

    @Override
    public void run() {
        // synchronized(this){
        //     this.runningThread = Thread.currentThread();
        // }
        try{
            processChannel(this.channel);
        }catch( Exception e){
            System.out.println(e);
        }
       
    }
    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException,Exception {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return o.readObject();
            }
        }
    }

    private void processChannel (SctpChannel channel) throws IOException, ClassNotFoundException, Exception {
        
        try {
            ByteBuffer bf = ByteBuffer.allocate(640000);
            MessageInfo messageInfo = channel.receive(bf, null, null); 
            bf.flip();
            byte[] bytes = new byte[bf.limit()];
            bf.get(bytes, 0, bf.limit());
            bf.clear();
            Token t = (Token) deserialize(bytes);
             parseMessage(t);
        } catch (IOException e) {
            System.out.println("MultiThreadedServer: run: IOException: " + e);
            System.exit(1);
        }
    }

    public void parseMessage(Token t) throws Exception, IOException{
        System.out.println();
        System.out.println("line 108:"+t);
        if (t.type.equals("request")){
           
            // node.addToRequestQ(t.senderHost+","+t.senderPort);
            // System.out.println("request Added to Q:="+node.requestQ.size());

            if ( !(node.host.equals(t.senderHost) && node.port == t.senderPort)){
                node.addToRequestQ(t.senderHost+","+t.senderPort);
                System.out.println("request Added to Q:="+node.requestQ.size());
            }

            if(node.initialToken != null){

                if (!Application.flag){
                    forwardResponse(node.initialToken);
                }
            }else{
                forwardRequest();
            }

        }else if(t.type.equals("response")){
            node.setInitialToken(t);
            node.setTreeNeighHost(node.host);
            node.setTreeNeighPort(node.port);
            node.printRequestQ();

            if (node.requestQ.size()>0 && node.getTopInRequestQ().split(",")[0].equals(node.host) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])== node.port){
                System.out.println("Response received ,removing Top:="+node.getTopInRequestQ());
                node.removeTopInRequestQ();
                
                if(!Application.criticalSection(node)){
                    node.csExecutions++;

                    node.totalTime += (System.currentTimeMillis() - node.reqTimes.removeFirst());
                    if(node.requestQ.size()>0){
                        forwardResponse(node.initialToken);
                    }
                }
            }else{
                System.out.println("Response received ,removing Top:="+node.getTopInRequestQ());
                forwardResponse(t);
                if (node.requestQ.size()>0){
                    forwardRequest();
                }
            }
        }

        if (node.csExecutions == node.totalRequest && node.requestQ.size()==0){
            System.out.println("################Line 144:Termination detected#############");
            Application.printMCTC(node, node.increment, ((int)node.totalTime)/node.totalRequest);
                // System.out.println("Line 133:Message Complexity:="+ node.increment);
                // System.out.println("Line 143:Time Complexity:="+ ((int)node.totalTime)/node.totalRequest);
        }


    }

    // public void parseMessage(Token t) throws Exception, IOException{
    //     System.out.println();
    //     System.out.println("line 108:"+t);
        
        
    //     if (t.type.equals("request")){ 

    //         if (node.initialToken !=null && node.requestQ.size() >0 && node.getTopInRequestQ().split(",")[0].equals(node.host) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])==node.port){
    //             System.out.println("my own request received and i am in top entering CS");
    //             node.removeTopInRequestQ();
    //             System.out.println("removed my request in top, Q.size:="+node.requestQ.size()+": top:="+node.requestQ.peek());
                
    //             Application.csEnter(node);
    //             if (!Application.csLeave(node)){

    //                 System.out.println(" line 110:queue size:"+node.requestQ.size() +": top:="+node.requestQ.peek());
    //                 if (!node.requestQ.isEmpty()){
    //                     // System.out.println(" line 112:Q not Empty forwarding Response:"+ node.getTopInRequestQ());
    //                     Token initT = new Token("response", node.host, node.port,node.initialToken.password);
    //                     forwardResponse(initT);
    //                     // node.initialToken = null;
    //                 }
    //             }   
    //         }else if (node.initialToken!= null && !Application.flag ){
    //             System.out.println("line 121:received request from "+ t.senderPort);
    //             node.addToRequestQ(t.senderHost+","+t.senderPort);
    //             System.out.println("line 122:queue size:"+node.requestQ.size());
    //             System.out.println(" line 132:not my own request , not in CS, send Password");
    //             // if (node.getTopInRequestQ().split(",")[0].equals(t.senderHost) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])==t.senderPort){
    //             //     System.out.println("respons with password");
    //             if (!node.requestQ.isEmpty() ){
    //                 System.out.println("Q not Empty:"+ node.getTopInRequestQ());
    //                 Token initT = new Token("response", node.host, node.port,node.initialToken.password);
    //                 forwardResponse(initT);
    //                 // node.initialToken = null;
    //             }
    //         }else if (node.initialToken!= null && Application.flag ){
    //             System.out.println("line 121:received request from "+ t.senderPort);
    //             node.addToRequestQ(t.senderHost+","+t.senderPort);
    //             System.out.println("line 122:queue size:"+node.requestQ.size());
    //                 // }
    //         }else if (node.initialToken == null){
    //             System.out.println("line 121:received request from "+ t.senderPort);
    //             node.addToRequestQ(t.senderHost+","+t.senderPort);
    //             System.out.println("line 122:queue size:"+node.requestQ.size());
    //             forwardRequest();
    //         // }else{
    //         //     // when we are not in top but have token
    //         //     System.out.println("line 121:received request from "+ t.senderPort);
    //         //     node.addToRequestQ(t.senderHost+","+t.senderPort);
    //         //     System.out.println("line 122:queue size:"+node.requestQ.size());
    //         //     //System.out.println(node.initialToken.toString());
    //         //     if (node.initialToken!= null && !Application.flag ){
    //         //         System.out.println(" line 132:not my own request , not in CS, send Password");
    //         //         // if (node.getTopInRequestQ().split(",")[0].equals(t.senderHost) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])==t.senderPort){
    //         //         //     System.out.println("respons with password");
    //         //         if (!node.requestQ.isEmpty() ){
    //         //             System.out.println("Q not Empty:"+ node.getTopInRequestQ());
    //         //             Token initT = new Token("response", node.host, node.port,node.initialToken.password);
    //         //             forwardResponse(initT);
    //         //             node.initialToken = null;
    //         //         }
    //         //         // }
    //         //     }else if (node.initialToken == null){
    //         //         forwardRequest();
    //         //     }
    //         }
    //     }else if (t.type.equals("response")){
    //         System.out.println("line 141:=processing response message"+": top:="+node.requestQ.peek()+": queue size:="+node.requestQ.size());
    //         node.setInitialToken(t);
    //         if ( node.requestQ.size() !=0 && node.getTopInRequestQ().split(",")[0].equals(node.host) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])==node.port){
    //             node.setTreeNeighHost(node.host);
    //             node.setTreeNeighPort(node.port);
    //             System.out.println("removing top in Queue:"+node.requestQ.peek());

    //             if (!t.senderHost.equals(node.host) && t.senderPort != node.port){
    //                 node.removeTopInRequestQ();
    //             }

    //             Application.csEnter(node);
    //             if ( !Application.csLeave(node)){
    //                 if (!node.requestQ.isEmpty()){

    //                     System.out.println("Q not Empty:"+ node.getTopInRequestQ());
    //                     Token initT = new Token("response", node.host, node.port,node.initialToken.password);
    //                     forwardResponse(initT);
    //                     // node.initialToken = null;
    //                 }
    //             }    
    //         }else if (node.requestQ.size()>0 && !node.getTopInRequestQ().split(",")[0].equals(node.host) && Integer.parseInt(node.getTopInRequestQ().split(",")[1])!=node.port){
    //             System.out.println("line 159:=forwarding response message:"+node.requestQ.peek());
    //             forwardResponse(t);
    //         }
    //     }
    // }

    public void forwardRequest(){
        node.increment++;
        Token ft = new Token("request",node.host, node.port);
        Client c = new Client(node.getTreeNeighHost(), node.getTreeNeighPort(), ft);
        Thread fthread = new Thread(c);
        fthread.start();
    }

    public void forwardResponse(Token t){
        //node.removeTopInRequestQ();
        node.increment++;
        System.out.println(" line 175:forwarding Response:"+ node.getTopInRequestQ());
        if (node.requestQ.size()>0){
            String fresHost = node.getTopInRequestQ().split(",")[0];
            int fresPort = Integer.parseInt(node.getTopInRequestQ().split(",")[1]);

            node.setTreeNeighHost(fresHost);
            node.setTreeNeighPort(fresPort);

            // node.removeTopInRequestQ();
            // System.out.println("line 184: Tree Neighbors swapped"+node.treeNeighPort);

             // if ( fresHost.equals(node.host) && fresPort == node.port){
             //     node.addToRequestQ(node.host+","+node.port);
             // }  

                if (!(fresHost.equals(node.host) && fresPort == node.port)){
                    node.removeTopInRequestQ();
                    System.out.println("line 184: Tree Neighbors swapped"+node.treeNeighPort);

                }
            Token ft = new Token("response",node.host, node.port, t.getPassword());
            Client c = new Client(fresHost,fresPort, ft);
            //System.out.println("line 195:")
            Thread fthread = new Thread(c);
            fthread.start();
            node.initialToken = null;

            // if (node.requestQ.size() >0){
            //     forwardRequest();
            // }
        }
    }
}