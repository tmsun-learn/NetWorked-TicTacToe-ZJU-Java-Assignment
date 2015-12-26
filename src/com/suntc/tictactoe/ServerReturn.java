package com.suntc.tictactoe;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerReturn implements Runnable
{
	Socket Sock;
	private Scanner in;
	private PrintWriter out;
	String Message;
	String PlayerName;
	private ProduceConsume ProCon;
	
	public ServerReturn(Socket X, ProduceConsume p)
	{
		this.Sock = X;
		this.ProCon = p;
	}
	
	public synchronized void Enqueue()
	{
		Server.WaitingArray.add(this.Sock);
		Server.ProConArray.add(this.ProCon);
		Server.UserNames.add(PlayerName);
		System.out.println("Queue length: " + Server.WaitingArray.size());

	}
	
	public void CheckConnection()
	{
		if (!Sock.isConnected())
		{
			for (int i = 0; i < Server.ConnectionArray.size(); ++i)
			{
				if (Server.ConnectionArray.get(i) == Sock)
				{
					System.out.println("Client " + Sock + "disconnected from server");
					Server.ConnectionArray.remove(i);
				}
			}
		}
	}
	
	public void run()
	{
		try
		{
			in = new Scanner(Sock.getInputStream());
			out = new PrintWriter(Sock.getOutputStream());
			try
			{
				while (true)
				{
					/*
					System.out.println("ServerReturn: InGame = " + InGame);
					while (InGame)
					{
						try
						{
							NotPlay.await();
						}
						catch (InterruptedException e)
						{ }
					}
					*/
					CheckConnection();
					if(!in.hasNext())
	                {
	                    return;
	                }
					Message = in.nextLine();
					if (Message != null)
					{
						System.out.println(Message);
						String CmdCode = Message.substring(0, Server.CMD_LEN);
						String Param = Message.substring(Server.CMD_LEN);
						if (CmdCode.equals(Server.C_ENQUEUE))
						{
							System.out.println("Socket " + Sock.getPort() + " enqueue");
							Enqueue();
						}
						else if (CmdCode.contains(Server.C_NAMEIS))
						{
							this.PlayerName = Param;
							System.out.println("Player " + Sock.getPort() + " has a name: " + this.PlayerName);
							out.println(Server.S_NAME_CONFIRED);
							out.flush();
						}
						else // send to PlaterHelper
						{
							ProCon.Put(Message);
						}
					}
			}	
				
			}
			finally
			{
				
			}
		}
		catch (IOException e)
		{
			System.err.println("IO Exception with Socket " + Sock);
		}
		catch (NoSuchElementException e)
		{
			System.err.println("Cannot scan input, may be client disconnected");
		}
		
		finally
		{
			System.out.println("Client " + Sock + "disconnected from server");
			for (int i = 0; i < Server.WaitingArray.size(); ++i)
			{
				if (Server.WaitingArray.get(i) == Sock)
				{
					System.out.println("remove from waiting queue");
					Server.WaitingArray.remove(i);
					Server.ProConArray.remove(i);
					Server.UserNames.remove(i);
				}
			}
			System.out.println("Inform PlayerHelper");
			ProCon.Put(Server.E_SOCKETDEAD);
			try
			{
				Sock.close();
			}
			catch (Exception e)
			{ }
		}
		
	}
	
}
