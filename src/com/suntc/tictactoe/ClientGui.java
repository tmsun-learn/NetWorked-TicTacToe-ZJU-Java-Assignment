package com.suntc.tictactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientGui {

	final static String SERVER_IP = "127.0.0.1";
	final static int SERVER_PORT = 444;
	private static Socket Sock;
	private static PrintWriter out;
	private static BufferedReader in;
	
	private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
	
	static void Connect() throws IOException
	{
		Sock = new Socket(SERVER_IP,SERVER_PORT);
		out = new PrintWriter(Sock.getOutputStream());
		in = new BufferedReader(new InputStreamReader(Sock.getInputStream()));
	}
	public static void main(String[] args) throws Exception
	{
		try
		{
			Connect();
		}
		catch (UnknownHostException e)
		{
			System.err.println("Host not found...");
		}
		catch (IOException e)
		{
			System.err.println("IO Exception occured...");
		}
		while (true)
		{
			String input = stdin.readLine();
			System.out.println( "input = " + input );
			out.println(input);
			out.flush();
		}
		
			
	}
}
