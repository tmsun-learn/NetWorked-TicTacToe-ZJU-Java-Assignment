package com.suntc.tictactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server
{
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<String> UserNames = new ArrayList<String>();
	public static ArrayList<Socket> WaitingArray = new ArrayList<Socket>();
	public static ArrayList<ProduceConsume> ProConArray = new ArrayList<ProduceConsume>();
	final static int PORT = 444;
	
	
	//Command Codes
	public static final int CMD_LEN = 7;
	public static final String C_NAMEIS = "#NAMEIS";
	public static final String C_ENQUEUE = "#ENQUEU";
	public static final String C_MOVETO = "#MOVETO";
	public static final String S_UNKNOWN_CMD = "#UNKNOW";
	public static final String S_NAME_CONFIRED = "#NAMEOK";
	public static final String S_OPPONENT_ARRIVE = "#OPPARR";
	public static final String S_YOU_MOVEFIRST = "#MOVEFI";
	public static final String S_OPPONENT_MOVEFIRST = "#MOVESE";
	public static final String S_MOVEOK = "#MOVEOK";
	public static final String S_MOVENG = "#MOVENG";
	public static final String S_NOTYOURTURN = "#NYOURT";
	public static final String S_OPP_MOVEOK = "#OPMVOK";
	public static final String S_YOUWIN = "#YOUWIN";
	public static final String S_YOULOSE = "#YOULOS";
	public static final String S_GAMETIE = "#GAMTIE";
	public static final String S_OPPONENT_QUIT = "#OPPQUI";
	public static final String E_SOCKETDEAD = "#SOCKDD";
	
	public static void main(String[] args)
	{
		try
		{
			ServerSocket Server = new ServerSocket(PORT);
			System.out.println("Waiting for clients...");
			ServerHelper SH = new ServerHelper();
			Thread X = new Thread(SH);
			X.start();
			while (true)
			{
				Socket Sock = Server.accept();
				ConnectionArray.add(Sock);
				System.out.println("Connection from: " + Sock.getLocalAddress());
				ProduceConsume P = new ProduceConsume();
				ServerReturn SR = new ServerReturn(Sock,P);
				X = new Thread(SR);
				X.start();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
