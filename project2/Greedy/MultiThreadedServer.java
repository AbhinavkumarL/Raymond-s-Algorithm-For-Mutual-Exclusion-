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

            // node.sent = false;
            while(node.contains()){
                Application.criticalSection(node);
                node.csExecutions++;
                node.totalTime += (System.currentTimeMillis() - node.reqTimes.removeFirst());
                node.remove();
            }

            if (!Application.flag && node.requestQ.size()>0){
                forwardResponse(node.initialToken);

                if (node.requestQ.size()>0){
                    forwardRequest();
                }
            }
            
        }

            // System.out.println("Line 130 :"+ node.csExecutions+":"+node.total+":"+ node.requestQ.size());
         if (node.csExecutions == node.totalRequest && node.requestQ.size()==0){
            System.out.println("################Line 144:Termination detected#############");
            int mc = node.increment;
            int tc =  ((int)node.totalTime)/node.totalRequest;
            Application.printMCTC(node, mc,tc);
                System.out.println("Line 133:Message Complexity:="+ node.increment);
                System.out.println("Line 143:Time Complexity:="+ ((int)node.totalTime)/node.totalRequest);
        }
    }

    public void forwardRequest(){
        node.increment++;
        Token ft = new Token("request",node.host, node.port);
        Client c = new Client(node.getTreeNeighHost(), node.getTreeNeighPort(), ft);
        Thread fthread = new Thread(c);
        node.sent = true;
        // node.msgComp++;
        fthread.start();
    }

    public void forwardResponse(Token t){
        node.increment++;
        //node.removeTopInRequestQ();
        System.out.println(" line 175:forwarding Response:"+ node.getTopInRequestQ());
        if (node.requestQ.size()>0){
            String fresHost = node.getTopInRequestQ().split(",")[0];
            int fresPort = Integer.parseInt(node.getTopInRequestQ().split(",")[1]);

            node.setTreeNeighHost(fresHost);
            node.setTreeNeighPort(fresPort);

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
        }
    }
}