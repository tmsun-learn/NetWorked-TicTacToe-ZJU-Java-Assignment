package com.suntc.tictactoe;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PlayerHelper implements Runnable
{
	private Socket Sock;
	private PlayerHelper Opponent;
	private Game Game;
	private String OpponentName;
	private volatile boolean GameOver = false;
	public boolean MoveFirst;
	private String Message, CmdCode, Param;
	private int GameRetCode;
	
	private Scanner in;
	private PrintWriter out;
	
	private ProduceConsume ProCon;

	
	public PlayerHelper(Game gm, Socket s, ProduceConsume p, String name)//, Lock l, Condition c, Boolean b)
	{
		this.Game = gm;
		this.Sock = s;
		this.ProCon = p;
		this.OpponentName = name;
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
	
	public void OpponentQuit()
	{
		System.out.println("PlayerHelper: send opponent quit");
		out.println(Server.S_OPPONENT_QUIT);
		out.flush();
		GameOver = true;
	}
	
	public PlayerHelper GetOpponent()
	{
		return this.Opponent;
	}
	
	public void RecordOpponentMove(int location)
	{
		System.out.println("PlayerHelper: RecordOpponentMove " + location);
		out.println(Server.S_OPP_MOVEOK + location);
		out.flush();
		if (Game.HasWinner())
		{
			out.println(Server.S_YOULOSE);
			out.flush();
			GameOver = true;
		}
		else if (Game.Tie())
		{
			out.println(Server.S_GAMETIE);
			out.flush();
			GameOver = true;
		}
	}
	
	public void run()
	{
		out.println(Server.S_OPPONENT_ARRIVE + OpponentName);
		out.flush();
		if (MoveFirst)
		{
			out.println(Server.S_YOU_MOVEFIRST);
			out.flush();
		}
		else
		{
			out.println(Server.S_OPPONENT_MOVEFIRST);
			out.flush();
		}
		while (!GameOver)
		{
			Message = ProCon.Get();
			if (GameOver)
			{
				System.out.println("PlayerHelper: End run(), opponent is " + OpponentName);
				return;
			}
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
			if (CmdCode.equals(Server.E_SOCKETDEAD))
			{
				Game.HasQuit(this);
			}
			else if (CmdCode.equals(Server.C_MOVETO))
			{
				GameRetCode = Game.ValidateMove(this, Integer.parseInt(Param));
				if (GameRetCode == Game.VALIDMOVE)
				{
					out.println(Server.S_MOVEOK + Param);
					out.flush();
					if (Game.HasWinner())
					{
						System.out.println("PlayerHelper: HasWinner");
						out.println(Server.S_YOUWIN);
						out.flush();
						GameOver = true;
					}
					else if (Game.Tie())
					{
						System.out.println("PlayerHelper: Tie");
						out.println(Server.S_GAMETIE);
						out.flush();
						GameOver = true;
					}
						
				}
				else if (GameRetCode == Game.INVALIDMOVE)
				{
					out.println(Server.S_MOVENG);
					out.flush();
				}
				else if (GameRetCode == Game.NOTYOURTURN)
				{
					out.println(Server.S_NOTYOURTURN);
					out.flush();
				}
			}
			else
			{
				out.println(Server.S_UNKNOWN_CMD);
				out.flush();
			}
		}//while
		System.out.println("PlayerHelper: End run(), opponent is " + OpponentName);
	}
}
