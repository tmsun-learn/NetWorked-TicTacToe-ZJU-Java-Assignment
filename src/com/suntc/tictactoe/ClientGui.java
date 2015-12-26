package com.suntc.tictactoe;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;



public class ClientGui {

	final static String SERVER_IP = "127.0.0.1";
	final static int SERVER_PORT = 444;
	private static Socket Sock;
	private static PrintWriter out;
	private static Scanner in;
	private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
	private boolean GameOver;
	private boolean IsWin;
	private boolean IsLose;
	private boolean IsTie;
	private String PlayerName;
	private String OpponentName;
	private boolean MoveFirst;
	private int Spaces[];//-1: not occupied, 0:opponent, 1:this
	
	private JFrame mainFrame;
	private JPanel panelButton;
	private Painter painter;
	private JPanel panelCont;
	private JDialog dlgWaiting;
	private JLabel lblWaiting;
	private JButton btnStart;
	private JButton btnQuit;
	private BufferedImage board;
	private BufferedImage cross;
	private BufferedImage circle;
	private BufferedImage background;
	private BufferedImage lose;
	private BufferedImage win;
	private BufferedImage tie;
	private ImageIcon loading;
	private CardLayout cl;
	private JLabel lblMyName;
	private JLabel lblOppName;
	private JLabel lblVersus;
	private JButton btnBack;
	private BoxLayout bxButton;
	private Font fontName;
	private Font fontVersus;
	
	private final int WIDTH = 720;
	private final int HEIGHT = 530;
	private final int BUTTON_WIDTH = 160;
	private final int BUTTON_HEIGHT = 50;
	private final int ICON_RANGE = 520;
	private final int ICON_COMP_X = 2;
	private final int ICON_COMP_Y = 7;
	private final int DIALOG_WIDTH = 300;
	private final int DIALOG_HEIGHT = 200;
	private final int GRID_LEN = 135;
	private final int GRID_GAP = 35;
	private final int GRID_START_X = 10;
	private final int GRID_END_X = 15;
	private final int GRID_START_Y = 15;
	private final int GRID_END_Y = 10;
	private final int BOARD_LEN = 500;
	private final int NAME_X = 530;
	private final int VERSUS_OFFSET = 20;
	private final int NAME_Y = 60;
	private final int NAME_GAP = 40;
	private final int NAME_WIDTH = 250;
	private final int NAME_HEIGHT = 50;
	private final int BACK_WIDTH = 120;
	private final int BACK_HEIGHT = 50;
	private final int BACK_X = 540;
	private final int BACK_Y = 400;
	
	
	static void Connect() throws IOException
	{
		Sock = new Socket(SERVER_IP,SERVER_PORT);
		out = new PrintWriter(Sock.getOutputStream());
		in = new Scanner(Sock.getInputStream());
	}
	
	static void Send(String s)
	{
		out.println(s);
		out.flush();
		System.out.println("ClientGui: Send " + s);
	}
	
	public void Prepare()
	{
		Spaces = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1};
	}
	
	public void Design()
	{
		mainFrame = new JFrame("TicTacToe");
		panelCont = new JPanel();
		cl = new CardLayout();
		panelCont.setLayout(cl);
		painter = new Painter();
		panelButton = new JPanel();
		dlgWaiting = new JDialog(mainFrame,"Waiting for opponent");
		lblWaiting = new JLabel("",loading,JLabel.CENTER);
		btnStart = new JButton("Play");
		btnQuit = new JButton("Quit");
		lblMyName = new JLabel("myname");
		lblOppName = new JLabel("oppname");
		lblVersus = new JLabel("Versus");
		btnBack = new JButton("Back");
		fontName = new Font("Monospaced",Font.BOLD,32);
		fontVersus = new Font("Serif",Font.BOLD,25);
		//bxButton = new BoxLayout(panelButton,BoxLayout.Y_AXIS);
		//panelButton.setLayout(bxButton);
		
		mainFrame.setSize(WIDTH,HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		dlgWaiting.setVisible(false);
		dlgWaiting.setResizable(false);
		dlgWaiting.setSize(DIALOG_WIDTH,DIALOG_HEIGHT);
		dlgWaiting.getContentPane().add(lblWaiting);
		dlgWaiting.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		btnStart.setBounds((WIDTH - BUTTON_WIDTH)/2, 70, BUTTON_WIDTH, BUTTON_HEIGHT);
		btnQuit.setBounds((WIDTH - BUTTON_WIDTH)/2, 170, BUTTON_WIDTH, BUTTON_HEIGHT);
		btnStart.addActionListener(new PlayAction());
		panelButton.setLayout(null);
		panelButton.add(btnStart);
		panelButton.add(btnQuit);
		panelCont.add(painter,"2");
		panelCont.add(panelButton,"1");
		cl.show(panelCont, "1");
		mainFrame.add(panelCont);
		lblMyName.setFont(fontName);
		lblOppName.setFont(fontName);
		lblVersus.setFont(fontVersus);
		lblMyName.setBounds(BOARD_LEN + NAME_X, NAME_Y, NAME_WIDTH, NAME_HEIGHT);
		lblOppName.setBounds(BOARD_LEN + NAME_X, NAME_Y + NAME_GAP + NAME_GAP, NAME_WIDTH, NAME_HEIGHT);
		lblVersus.setBounds(BOARD_LEN + NAME_X + VERSUS_OFFSET, NAME_Y + NAME_GAP, NAME_WIDTH, NAME_HEIGHT);
		btnBack.setBounds(BACK_X,BACK_Y,BACK_WIDTH,BACK_HEIGHT);
		btnBack.setVisible(false);
		btnBack.addActionListener(new BackAction());
		painter.setLayout(null);
		//painter.add(lblMyName);
		//painter.add(lblOppName);
		//painter.add(lblVersus);
		painter.add(btnBack);
	}
	
	public void Play() throws IOException
	{
		String ServerResponse;
		String CmdCode;
		String Param;
		while (true)
		{
			//String input = stdin.readLine();
			//System.out.println( "input = " + input );
			//out.println(input);
			//out.flush();
			/*
			if (GameOver)
			{//do cleaning
				for (int i = 0; i < 9; ++i)
					Spaces[i] = -1;
				GameOver = false;
			}*/
			ServerResponse = in.nextLine();
			System.out.println("ClientGui: " + ServerResponse);
			if (ServerResponse != null)
			{
				CmdCode = ServerResponse.substring(0, Server.CMD_LEN);
				Param = ServerResponse.substring(Server.CMD_LEN);	
			}
			else
			{
				continue;
			}
			if (CmdCode.equals(""))
			{
				
			}
			else if (CmdCode.equals(Server.S_YOU_MOVEFIRST))
			{
				MoveFirst = true;
				//set label
			}
			else if (CmdCode.equals(Server.S_OPPONENT_MOVEFIRST))
			{
				MoveFirst = false;
				//set label
			}
			else if (CmdCode.equals(Server.S_OPPONENT_ARRIVE))
			{
				OpponentName = Param;
				dlgWaiting.setVisible(false);
				mainFrame.setTitle(PlayerName + " V.S. " + OpponentName);
				lblMyName.setText(PlayerName);
				lblOppName.setText(OpponentName);
				painter.repaint();
				cl.show(panelCont, "2");
			}
			else if (CmdCode.equals(Server.S_MOVEOK))
			{
				Spaces[Integer.parseInt(Param)] = 1;
				painter.repaint();
			}
			else if (CmdCode.equals(Server.S_OPP_MOVEOK))
			{
				Spaces[Integer.parseInt(Param)] = 0;
				painter.repaint();
			}
			else if (CmdCode.equals(Server.S_YOUWIN))
			{
				GameOver = true;
				IsWin = true;
				painter.repaint();
			}
			else if (CmdCode.equals(Server.S_YOULOSE))
			{
				GameOver = true;
				IsLose = true;
				painter.repaint();
			}
			else if (CmdCode.equals(Server.S_GAMETIE))
			{
				GameOver = true;
				IsTie = true;
				painter.repaint();
			}
			
		}
	}
	
	public void LoadImages()
	{
		try
		{
			board = ImageIO.read(getClass().getResourceAsStream("/board.gif"));
			circle = ImageIO.read(getClass().getResourceAsStream("/circle.png"));
			cross = ImageIO.read(getClass().getResourceAsStream("/cross.png"));
			loading = new ImageIcon("res/loading.gif");
			background = ImageIO.read(getClass().getResourceAsStream("/background.png"));
			win = ImageIO.read(getClass().getResourceAsStream("/win.png"));
			lose = ImageIO.read(getClass().getResourceAsStream("/lose.png"));
			tie = ImageIO.read(getClass().getResourceAsStream("/tie.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception
	{
		
		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		{
			if ("Nimbus".equals(info.getName())) 
            {
            	UIManager.setLookAndFeel(info.getClassName());
            	break;
            }
        }
		try
		{
			Connect();
			ClientGui cg = new ClientGui();	
			cg.Prepare();
			cg.LoadImages();
			cg.Design();
			cg.Play();
		}
		catch (UnknownHostException e)
		{
			System.err.println("Host not found...");
		}
		catch (IOException e)
		{
			System.err.println("IO Exception occured...");
		}
		/*
		while (true)
		{
			String input = stdin.readLine();
			System.out.println( "input = " + input );
			out.println(input);
			out.flush();
		}
		*/
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		
		/*
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				
				//ClientGui cg = new ClientGui();	
				//cg.Play();
				//cg.LoadImages();
				//cg.Design();
			}
		});
		*/
	}

	private class Painter extends JPanel implements MouseListener
	{
		
		public Painter()
		{
			this.addMouseListener(this);
	        //this.setOpaque(true);
	        //this.setBackground(Color.WHITE);
		}
		
		public int GetGridNum(int x, int y)
		{
			if (x > GRID_START_X && x < GRID_START_X + GRID_LEN)//column 1
			{
				if (y > GRID_START_Y && y < GRID_START_Y + GRID_LEN)
					return 0;
				else if (y > GRID_START_Y + GRID_LEN + GRID_GAP && y < GRID_START_Y + GRID_LEN + GRID_LEN + GRID_GAP)
					return 3;
				else if (y > GRID_START_Y + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP && y < GRID_START_Y + GRID_LEN + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP)
					return 6;
			}
			else if (x > GRID_START_X + GRID_LEN + GRID_GAP && x < GRID_START_X + GRID_LEN + GRID_LEN + GRID_GAP)//column 1)
			{
				if (y > GRID_START_Y && y < GRID_START_Y + GRID_LEN)
					return 1;
				else if (y > GRID_START_Y + GRID_LEN + GRID_GAP && y < GRID_START_Y + GRID_LEN + GRID_LEN + GRID_GAP)
					return 4;
				else if (y > GRID_START_Y + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP && y < GRID_START_Y + GRID_LEN + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP)
					return 7;
			}
			else if (x > GRID_START_X + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP && x < GRID_START_X + GRID_LEN + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP)
			{
				if (y > GRID_START_Y && y < GRID_START_Y + GRID_LEN)
					return 2;
				else if (y > GRID_START_Y + GRID_LEN + GRID_GAP && y < GRID_START_Y + GRID_LEN + GRID_LEN + GRID_GAP)
					return 5;
				else if (y > GRID_START_Y + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP && y < GRID_START_Y + GRID_LEN + GRID_LEN + GRID_LEN + GRID_GAP + GRID_GAP)
					return 8;
			}
	
			return -1;
		}
		
		public void Render(Graphics g)
		{
			g.drawImage(board, 0, 0, null);
			
			if (MoveFirst)
			{//this:O, opponent:X
				for (int i = 0; i < 9; ++i)
				{
					if (Spaces[i] == 1)//this
						drawCircle(g,i);
					else if (Spaces[i] == 0)
						drawCross(g,i);
				}
			}
			else
			{//this:X, opponent:O
				for (int i = 0; i < 9; ++i)
				{
					if (Spaces[i] == 1)//this
						drawCross(g,i);
					else if (Spaces[i] == 0)
						drawCircle(g,i);
				}
			}
			
			g.setFont(fontName);
			g.drawString(PlayerName, NAME_X, NAME_Y);
			g.drawString(OpponentName, NAME_X, NAME_Y + NAME_GAP +NAME_GAP);
			g.setFont(fontVersus);
			g.drawString("Versus", NAME_X + VERSUS_OFFSET, NAME_Y + NAME_GAP);
			
			if (IsWin || IsLose || IsTie)
			{
				g.drawImage(background,0,0,null);
				btnBack.setVisible(true);
				if (IsWin)
					g.drawImage(win,0,0,null);
				if (IsLose)
					g.drawImage(lose,0,0,null);
				if (IsTie)
					g.drawImage(tie,0,0,null);
			}
		}
		
		public void drawCross(Graphics g, int n)
		{
			
			int offset = ICON_RANGE / 3;
			g.drawImage(cross, n%3 * offset + ICON_COMP_X, n/3 * offset + ICON_COMP_Y, null);
		}
		
		public void drawCircle(Graphics g, int n)
		{
			
			int offset = ICON_RANGE / 3;
			g.drawImage(circle, n%3 * offset + ICON_COMP_X, n/3 * offset + ICON_COMP_Y, null);
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Render(g);
		}
		
		@Override
		public void mouseClicked(MouseEvent e)
		{
			int x = e.getX(); 
			int y = e.getY();
			int gridNum;
			System.out.println(x);
			System.out.println(y);
			gridNum = GetGridNum(x,y);
			if (gridNum != -1)
			{
				Send(Server.C_MOVETO + gridNum);
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent e)
		{}
		
		@Override
		public void mouseExited(MouseEvent e)
		{}
		
		@Override
		public void mousePressed(MouseEvent e)
		{}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{}
	}
	
	private class PlayAction implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			//cl.show(panelCont, "2");
			
			if (PlayerName == null)
			{
				PlayerName = JOptionPane.showInputDialog(null,"Please Input Your Name:","");
				Send(Server.C_NAMEIS + PlayerName);
			}
			dlgWaiting.setVisible(true);
			Send(Server.C_ENQUEUE);
			
		}
	}
	
	private class BackAction implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			GameOver = false;
			IsWin = false;
			IsLose = false;
			IsTie = false;
			for (int i = 0; i < 9; ++i)
				Spaces[i] = -1;
			cl.show(panelCont, "1");
		}
	}
	
}
