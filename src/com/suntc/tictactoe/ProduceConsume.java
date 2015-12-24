package com.suntc.tictactoe;


public class ProduceConsume {
	private String Message = new String();
	private boolean Available = false;
	
	public synchronized String Get()
	{
		while (Available == false)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{}	
		}
		Available = false;
		notifyAll();
		return Message;
	}
	
	public synchronized void Put(String value)
	{
		while (Available == true)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{}
		}
		Message = value;
		Available = true;
		notifyAll();
	}
}
