import java.util.*;
import java.io.*;

public class Token implements Serializable {

	String password;
	String type;
	String senderHost;
	int senderPort;
	int time;

	Token( String t, String h, int p){
		this.type = t;
		this.senderHost = h;
		this.senderPort = p;
	}

	Token( String t, String h , int p, String s){
		this.type = t;
		this.senderHost = h;
		this.senderPort = p;
		this.password = s;
	}

	public String toString(){
		return " 19: Message from "+senderHost +","+senderPort+","+type+","+password;
	}

	public String getHost(){
		return this.senderHost;
	}
	public void setHost(String h){
		this.senderHost = h;
	}

	public int  getPort(){
		return this.senderPort;
	}
	public void setPort(int p){
		this.senderPort = p;
	}

	public String getPassword(){
		return this.password;
	}
	public void setPassword(String pw){
		this.password=pw;
	}
}