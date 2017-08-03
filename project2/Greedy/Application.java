import java.util.*;
import java.io.*;
import java.lang.*;    
import java.text.*;
import java.util.concurrent.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public  class Application{

	public static boolean flag = false;

	// public synchronized static  void csEnter(Node n){
	// 		String host = n.host;
	// 		int port = n.port;
	// 		flag = true;
	// 		Date today;
	// 		String result;
	// 		SimpleDateFormat formatter;
	// 		ExecutorService executor = Executors.newSingleThreadExecutor();
	// 		Future future = executor.submit(new Runnable(){

	// 		    public void run(){
	// 		        	FileWriter file = new FileWriter((Paths.get(System.getProperty("user.dir") + "/output.txt")).toString(),true);
	// 					BufferedWriter bufferedWriter =new BufferedWriter(file);

	// 					formatter = new SimpleDateFormat("H:mm:ss:SSS");
	// 					today = new Date();
	// 					result = formatter.format(today);
	// 					bufferedWriter.write("In CS:");
	// 					bufferedWriter.write(host);
	// 					bufferedWriter.write(":");
	// 					bufferedWriter.write(port);
	// 					bufferedWriter.write(":");
	// 					bufferedWriter.write(result);
	// 					bufferedWriter.newLine();
	// 					bufferedWriter.close();
	// 		        // return "OK";
	// 		    }
	// 		});
	// 		try {
	// 		    System.out.println(future.get(n.getCsDuration(), TimeUnit.MILLISECONDS)); //timeout is in 2 seconds
	// 		} catch (TimeoutException e) {
	// 		    System.err.println("Timeout");
	// 		}
	// 		executor.shutdownNow();
	// 		flag = false;
	// 	// csLeave();
	// }

	public  static void csEnter(Node n) throws Exception , IOException{
		System.out.println("executing CS:"+n.port);
		flag = true;
		String host = n.host;
		int port = n.port;
		Date today;
		String result;
		SimpleDateFormat formatter;
		FileWriter file = null;
		BufferedWriter bufferedWriter = null;
		try{
			file = new FileWriter(Paths.get(System.getProperty("user.dir") + "/output.txt").toString(),true);
			bufferedWriter =new BufferedWriter(file);
			
			formatter = new SimpleDateFormat("H:mm:ss:SSS");
			today = new Date();
			result = formatter.format(today);
			// System.out.println("Locale: " + currentLocale.toString());
			bufferedWriter.newLine();
			bufferedWriter.write("In CS:");
			bufferedWriter.write(host);
			bufferedWriter.write(":");
			bufferedWriter.write(Integer.toString(port));
			bufferedWriter.write(":");
			bufferedWriter.write(result);
			bufferedWriter.newLine();
			bufferedWriter.close();
			file.close();
		}catch (IOException e){
			System.out.println(e);
		}finally{
			try{
				if (bufferedWriter != null){
					bufferedWriter.close();
				}
				if (file != null){
					file.close();
				}
			}catch (IOException e){
				System.out.println(e);
			}
			
		}


		try{
		Thread.sleep(n.csDuration);
		}
		catch(Exception e){
			System.out.println(e);
		}
		//flag = false;

	}
	public  static  void csLeave(Node n) throws IOException{
		String host = n.host;
		int port = n.port;
		Date today;
		String result;
		SimpleDateFormat formatter;
		FileWriter file = null;
		BufferedWriter bufferedWriter = null;
		try{
			file = new FileWriter(Paths.get(System.getProperty("user.dir") + "/output.txt").toString(),true);
			bufferedWriter =new BufferedWriter(file);
			
			formatter = new SimpleDateFormat("H:mm:ss:SSS");
			today = new Date();
			result = formatter.format(today);
			// System.out.println("Locale: " + currentLocale.toString());
			
			bufferedWriter.write("Out CS:");
			bufferedWriter.write(host);
			bufferedWriter.write(":");
			bufferedWriter.write(Integer.toString(port));
			bufferedWriter.write(":");
			bufferedWriter.write(result);
			bufferedWriter.newLine();
		}catch (IOException e){
			System.out.println(e);
		}finally{
			try{
				if (bufferedWriter != null){
					bufferedWriter.close();
				}
				if (file != null){
					file.close();
				}
			}catch (IOException e){
				System.out.println(e);
			}
			
		}
		flag = false;
	}

	public synchronized static boolean criticalSection(Node n) throws Exception , IOException{
		csEnter(n);
		csLeave(n);

		System.out.println("left CS:"+ n.port);
		return flag;
	}

	public static void printMCTC(Node n, int mc, int tc){
		int m = mc;
		int t = tc;
		String host = n.host;
		int port = n.port;
		FileWriter file = null;
		BufferedWriter bufferedWriter = null;
		try{
            file = new FileWriter(Paths.get(System.getProperty("user.dir") + "/MCTC.txt").toString(),true);
            bufferedWriter =new BufferedWriter(file);
            // System.out.println("Locale: " + currentLocale.toString());
            
            bufferedWriter.write("results:");
            bufferedWriter.write(host);
            bufferedWriter.write(":");
            bufferedWriter.write(Integer.toString(port));
            bufferedWriter.write(" : Message Complexity:=");
            bufferedWriter.write(Integer.toString(m));
            bufferedWriter.write(" : Time Complexity:=");
            bufferedWriter.write(Integer.toString(t));
            bufferedWriter.newLine();
            
        }catch (IOException e){
        	System.out.println(e);
        }finally{
			try{
				if (bufferedWriter != null){
					bufferedWriter.close();
				}
				if (file != null){
					file.close();
				}
			}catch (IOException e){
				System.out.println(e);
			}
			
		}
	}
}