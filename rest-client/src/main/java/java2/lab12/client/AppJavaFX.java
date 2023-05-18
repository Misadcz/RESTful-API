package java2.lab12.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppJavaFX
{
	public static void main(String[] args) throws IOException {
		System.out.println("Choose language:");
		System.out.println("1 - Cestina, 2 - English, 3 - Polish");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		String s = bufferRead.readLine();

		String choice = "en";
		if(s.contains("1"))
		{choice = "cs";}

		if( s.contains("2"))
		{choice = "en";}

		if( s.contains("3"))
		{choice = "pl";}

		System.out.println(choice);

		TableViewSample.main(choice,args);
	}
}



