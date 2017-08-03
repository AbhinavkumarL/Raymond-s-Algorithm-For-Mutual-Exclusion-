import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

public class Client implements Runnable {
	String host;
	int port;
	private Token clientToken;
	
	public Client(String host, int port, Token t){
		this.host=host;
		this.port=port;
		this.clientToken =t;
	}
	private static byte[] serialize(Token clientToken) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(clientToken);
            }
            return b.toByteArray();
        }
    }
	
	public void connectNeighbors(){
		
		try {
	        InetSocketAddress socketAddress = new InetSocketAddress(this.host, this.port); 
	        SctpChannel sctpChannel = SctpChannel.open(); 
	        sctpChannel.connect(socketAddress, 1 ,1);
	        ByteBuffer bb = ByteBuffer.allocate(640000); 
	        MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
	        byte[] b= new byte[bb.limit()];
	        bb.put(serialize(this.clientToken)); 
	        bb.flip();
	        try {
	        	// System.out.println("sending message:");
	        	sctpChannel.send(bb, messageInfo); 
	        } catch (Exception e) { 
	            e.printStackTrace(); 
	        } 
	        // System.out.println("Closing channel");
	        sctpChannel.close();
	        Thread.currentThread().interrupt();
	    } catch (IOException e) {
	        System.out.println(e);
	    }
	}
	
	@Override
    public void run() {
        connectNeighbors();
    }
}
