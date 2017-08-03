
import java.util.*;
import java.io.*;

public class Node{
	String host;
	int port;

	String treeNeighHost;
	int treeNeighPort;

	String tokenHost;
	int tokenPort;
	Token initialToken = null;
	int csDuration;
	int numRequest;
	int totalRequest;

	int increment=0;
	int csExecutions =0;

	long totalTime =0;
	LinkedList<Long> reqTimes = new LinkedList<>();

	Queue<String> requestQ = new LinkedList<>();

	Node(String h, int p ){
		this.host = h; 
		this.port = p;
	}

	public void setCsDuration(int t){
		this.csDuration=t;
	}
	public int getCsDuration(){
		return this.csDuration;
	}

	public void setTotalRequest(int t){
		this.totalRequest=t;
	}

	public void setNumRequest(int t){
		this.numRequest=t;
	}
	public int getNumRequest(){
		return this.numRequest;
	}
	public void decNumRequest(){
		this.numRequest--;
	}

	public void printTreeneighbors(){
		System.out.println("Line 23:"+treeNeighHost+":"+treeNeighPort);
	}
	public Token getInitialToken(){
		return this.initialToken;
	}
	public void setInitialToken( Token t){
		this.initialToken=t;
	}

	public String getHost(){
		return this.host;
	}
	public int getPort(){
		return this.port;
	}

	// gettters and setters for Tree neighbors
	public String getTreeNeighHost(){
		return this.treeNeighHost;
	}
	public void setTreeNeighHost(String s){
		this.treeNeighHost = s;
	}
	public int getTreeNeighPort(){
		return this.treeNeighPort;
	}
	public void setTreeNeighPort(int p){
		this.treeNeighPort = p;
	}

	//getters and sets for Token position
	public String getTokenHost(){
		return this.tokenHost;
	}
	public void setTokenHost(String s){
		this.tokenHost = s;
	}
	public int getTokenPort(){
		return this.tokenPort;
	}
	public void setTokenPort(int p){
		this.tokenPort= p;
	}
	/*
	 * Add requests to the Queue
	 */
	public Queue<String> getRequestQ(){
		return this.requestQ;
	}
	public void addToRequestQ( String req){
		this.requestQ.add(req);
	}
	public String getTopInRequestQ(){
		return this.requestQ.peek();
	}
	public void removeTopInRequestQ(){
		this.requestQ.remove();
	}
	public void printRequestQ(){
		for (String s : requestQ){
			System.out.print(s+",");
		}
	}
}