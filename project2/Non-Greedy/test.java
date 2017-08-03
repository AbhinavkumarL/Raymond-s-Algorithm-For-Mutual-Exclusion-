
import java.io.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test{
	public static void main(String[] args){

    	Random r = new Random();
    	System.out.println(-(Math.log(1-(r.nextDouble()))*20));
	}
}