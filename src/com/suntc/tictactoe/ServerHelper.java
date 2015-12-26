package com.suntc.tictactoe;


public class ServerHelper implements Runnable
{
	public static synchronized void CheckWaiting()
	{
		if (Server.WaitingArray.size() >= 2)
		{
			System.out.println("ServerHelper: Match players " + Server.WaitingArray.get(0).getPort() + "~" + Server.WaitingArray.get(1).getPort());
			//public PlayerHelper(Game gm, Socket s, Lock l, Condition c, boolean b)
			Game game = new Game();
			PlayerHelper player1 = new PlayerHelper(game, Server.WaitingArray.get(0), Server.ProConArray.get(0), Server.UserNames.get(1));//,Server.LockArray.get(0),Server.CondArray.get(0),Server.InGameBoolArray.get(0));
			PlayerHelper player2 = new PlayerHelper(game, Server.WaitingArray.get(1), Server.ProConArray.get(1), Server.UserNames.get(0));//,Server.LockArray.get(1),Server.CondArray.get(1),Server.InGameBoolArray.get(1));
			player1.SetOpponent(player2);
			player2.SetOpponent(player1);
			game.SetPlayer(player1, player2);
			Thread X = new Thread(player1);
			Thread Y = new Thread(player2);
			X.start();
			Y.start();
			Server.WaitingArray.remove(0);
			Server.ProConArray.remove(0);
			Server.UserNames.remove(0);
			Server.WaitingArray.remove(0);
			Server.ProConArray.remove(0);
			Server.UserNames.remove(0);
		}
	}
	
	public void run()
	{
		while (true)
		{
			CheckWaiting();
		}
	}
}
