package com.suntc.tictactoe;

import java.util.Random;

public class Game
{
	public BoardOwner Board[];
	private PlayerHelper PHelper1, PHelper2;
	private PlayerHelper CurrentPlayer = null;
	private int NumOfMove;
	private Random Rand = new Random();
	public static final int VALIDMOVE = 1;
	public static final int INVALIDMOVE = 2;
	public static final int NOTYOURTURN = 3;
	
	/*
	 *    board
	 *   0  1  2
	 *   3  4  5
	 *   6  7  8
	 *   
	 * */
	/*
	public void Game()
	{
		Board = new BoardOwner[9];
		for (BoardOwner bo : Board)
		{
			bo = new BoardOwner();
		}
	}
	*/
	public void SetPlayer(PlayerHelper helper1, PlayerHelper helper2)
	{
		System.out.println("Game: SetPlayers");
		PHelper1 = helper1;
		PHelper2 = helper2;
		if (Rand.nextInt(2) == 0)
		{
			this.CurrentPlayer = PHelper1;
			/*ugly..*/
			PHelper1.MoveFirst = true;
			PHelper2.MoveFirst = false;
		}
		else
		{
			this.CurrentPlayer = PHelper2;
			PHelper1.MoveFirst = false;
			PHelper2.MoveFirst = true;
		}
		Board = new BoardOwner[9];
		for (int i = 0; i < 9; ++i)
		{
			Board[i] = new BoardOwner();
		}
	}
	
	public synchronized int ValidateMove(PlayerHelper player, int location)
	{
		if (player == CurrentPlayer)
		{
			System.out.println("Game: location = " + location);
			if (Board[location].Owner == null)
			{
				Board[location].Owner = player;
				Board[location].MoveOrder = NumOfMove++;
				CurrentPlayer = player.GetOpponent();
				CurrentPlayer.RecordOpponentMove(location);
				System.out.println("Game: return VALIDMOVE");
				return VALIDMOVE;
			}
			else
			{
				System.out.println("Game: return INVALIDMOVE");
				return INVALIDMOVE;
			}
		}
		else
		{
			System.out.println("Game: return NOTYOURTURN");
			return NOTYOURTURN;
		}
	}
	
	public boolean HasWinner()
	{
		return ((Board[0].Owner != null) && (Board[0].Owner == Board[1].Owner) && (Board[1].Owner == Board[2].Owner) ||
				(Board[3].Owner != null) && (Board[3].Owner == Board[4].Owner) && (Board[4].Owner == Board[5].Owner) ||
				(Board[6].Owner != null) && (Board[6].Owner == Board[7].Owner) && (Board[7].Owner == Board[8].Owner) ||
				(Board[0].Owner != null) && (Board[0].Owner == Board[3].Owner) && (Board[3].Owner == Board[6].Owner) ||
				(Board[1].Owner != null) && (Board[1].Owner == Board[4].Owner) && (Board[4].Owner == Board[7].Owner) ||
				(Board[2].Owner != null) && (Board[2].Owner == Board[5].Owner) && (Board[5].Owner == Board[8].Owner) ||
				(Board[0].Owner != null) && (Board[0].Owner == Board[4].Owner) && (Board[4].Owner == Board[8].Owner) ||
				(Board[2].Owner != null) && (Board[2].Owner == Board[4].Owner) && (Board[4].Owner == Board[6].Owner));
	}
	
	public boolean Tie()
	{
		for (BoardOwner b : Board)
		{
			if (b.Owner == null)
				return false;
		}
		if (!HasWinner())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void HasQuit(PlayerHelper player)
	{
		if (player == PHelper1)
			PHelper2.OpponentQuit();
		else if (player == PHelper2)
			PHelper1.OpponentQuit();
			
	}
	
	public class BoardOwner
	{
		public PlayerHelper Owner = null;
		public int MoveOrder = -1;
	}
}
