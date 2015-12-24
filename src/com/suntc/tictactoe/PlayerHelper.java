package com.suntc.tictactoe;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class PlayerHelper implements Runnable
{
	private Socket Sock;
	private PlayerHelper Opponent;
	private Game Game;
	private boolean GameOver = false;
	private String Message, CmdCode, Param;
	private int GameRetCode;
	
	private Scanner in;
	private PrintWriter out;
	
	private ProduceConsume ProCon;
	//private Lock Alock;
	//private Condition NotPlay;
	//private Boolean InGame;
	
	public PlayerHelper(Game gm, Socket s, ProduceConsume p)//, Lock l, Condition c, Boolean b)
	{
		this.Game = gm;
		this.Sock = s;
		this.ProCon = p;
		//this.Alock = l;
		//this.NotPlay = c;
		//this.InGame = b;
		
		try
		{
			this.in = new Scanner(Sock.getInputStream());
			this.out = new PrintWriter(Sock.getOutputStream());	
		}
		catch (IOException e)
		{
			
		}
		catch (NoSuchElementException e)
		{
			
		}
		
	}

	public void SetOpponent(PlayerHelper ph)
	{
		this.Opponent = ph;
	}
	
	public PlayerHelper GetOpponent()
	{
		return this.Opponent;
	}
	
	public void RecordOpponentMove(int location)
	{
		out.println(Server.S_OPP_MOVEOK + location);
		if (Game.HasWinner())
		{
			out.println(Server.S_YOULOSE);
		}
		else if (Game.Tie())
		{
			out.println(Server.S_GAMETIE);
		}
	}
	
	public void run()
	{
		
		while (!GameOver)
		{
			Message = ProCon.Get();
			System.out.println("PlayerHelper: " + Message);
			if (Message != null)
			{
				CmdCode = Message.substring(0, Server.CMD_LEN);
				Param = Message.substring(Server.CMD_LEN);
			}
			else
			{
				continue;
			}
			if (CmdCode.equals(Server.C_MOVETO))
			{
				GameRetCode = Game.ValidateMove(this, Integer.parseInt(Param));
				if (GameRetCode == Game.VALIDMOVE)
				{
					out.println(Server.S_MOVEOK + Param);
					if (Game.HasWinner())
					{
						out.println(Server.S_YOUWIN);
					}
					else if (Game.Tie())
					{
						out.println(Server.S_GAMETIE);
					}
						
				}
				else if (GameRetCode == Game.INVALIDMOVE)
				{
					
				}
				else if (GameRetCode == Game.NOTYOURTURN)
				{
					
				}
			}
			else if(CmdCode.equals(""))
			{
				
			}
		}//while
		
	}
}
