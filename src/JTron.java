import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;



import java.io.*;

import javax.imageio.ImageIO;

// Purpose:	This class runs the overall execution.
public class JTron {
	private static KeyEvent lastKey;
	
	public static void main(String [] args){
		buildOptions();
		buildUI();
		//scoreBoard.setHighScore(0);
		//scoreBoard.setHighScore(0);

		while(true) {
			if(theRedButton ) {
				score = 0;
				wins = 0;
				streak = true;
				optionsDone = false;
				theRedButton = false;
			}
			if(optionsDone && streak) {

				setupGame();
				centerFrame(frame);
				Options.setVisible(false);
				frame.setVisible(true);
				gameLoop();
				showWinner();
			} else if (optionsDone && !streak) {
				//high score thing
				//HighScore is called to work here
				if(scoreBoard.handleScore(score)) {
					JOptionPane.showMessageDialog(frame, "Good job! You set the high score!");
				}
				int selection = JOptionPane.showConfirmDialog(
					    frame,
					    "Would you like to play again?",
					    "Game Over",
					    JOptionPane.YES_NO_OPTION);
				
				if(selection == JOptionPane.NO_OPTION)
					System.exit(0);

				score = 0;
				streak = true;
			} else {
				frame.setVisible(false);
				Options.setVisible(true);
			}
		}
	}
	
	
	/**
	 * Pauses and shows the winner
	 */
	private static void showWinner() {
		int winner = 0;
		for(LightCycle p : players) {
			winner++;
			if(p.alive())
				break;
		}
		if(winner != 1) {
			wins = 0;
			streak = false;
			msg.setText("Wow! You lose!");
			lastKey = null;
		} else {
			int points = gameBoard.percentLeft();
			score = score + points;
			wins++;
			msg.setText("Good job! Press enter to continue...");
		}
		scoreLine.setText("Wins: " + wins + " Score: " + score + " High Score: " + scoreBoard.getHighScore());
		while(lastKey != null && lastKey.getKeyCode() != 10 && !theRedButton) { }

	}
	

	/**
	 * Sets conditions back to normal
	 */

	private static void setupGame() {
		Random gen = new Random();
		gameBoard.setGridSize(BOARD_WIDTH, BOARD_HEIGHT);
		gameBoard.setTileSize(TILE_SIZE);
		gameBoard.setPreferredSize(new Dimension(gameBoard.getPixelWidth(), gameBoard.getPixelHeight()));
		gameBoard.repaint();
		frame.pack();
		streak = true;
		lastKey = null;
		gameBoard.wipe();
		players.clear();
		scoreLine.setText("Wins: " + wins + " Score: " + score + " High Score: " + scoreBoard.getHighScore());

		/*gameBoard.setGridSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		gameBoard.setPreferredSize(new Dimension(gameBoard.getPixelWidth(), gameBoard.getPixelHeight()));*/
		//User interface is done. Let's make our contenders!
		int midY = BOARD_HEIGHT / 2;
		int midX = BOARD_WIDTH / 2;
		int adjustment = BOARD_WIDTH / 4;
		LightCycle p1 = new LightCycle(gameBoard, midX - adjustment,midY,0,P1_COLOR);
		LightCycle p2 = new LightCycle(gameBoard, midX + adjustment,midY,gen.nextInt(4),P2_COLOR);
		p1.setHuman(true);
		p2.setHuman(false);
		p2.getAI().setAnger(AI_ANGER);
		p2.getAI().setParanoia(AI_PARANOIA);
		p2.getAI().setNeutral(AI_NEUTRAL);
		p2.getAI().setEnemy(p1);
		
		players.add(p1); players.add(p2);
		//gameBoard.repaint();
		
		msg.setText("Press any key to start.");
	}
	
	
	/**
	 * The meat of the actual game
	 */
	private static void gameLoop() {
		gameDone = false;
		
		for(LightCycle p : players) {
			gameBoard.setColor(p.getX(), p.getY(), p.getTeam());
		}
		
		while(lastKey == null && !theRedButton) {} 
		
		msg.setText("Match in progress...");
		
		int currentPlayer = 0;
		int numPlayers = 0;
		while (!gameDone && !theRedButton) {
			
			for(LightCycle p : players) {
				currentPlayer++;
				if(p.isHuman()) {
					p.setDirection(lastKey);
				}
				p.move();
				
				if(!gameBoard.collides(p))
					gameBoard.setColor(p.getX(), p.getY(), p.getTeam());
				else
					p.die();
				
				if(p.alive())
					numPlayers++;
			}
			
			if(numPlayers <= 1)
				gameDone = true;
			
			try { Thread.sleep(interval); }
			catch(Exception e) { }
			
			currentPlayer = 0;
			numPlayers = 0;
		}
	}
	
	private static void centerFrame(JFrame f) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width / 2) - (f.getWidth() / 2);
		int y = (screenSize.height / 2) - (f.getHeight() / 2);
		f.setLocation(x,y);
	}
	
	/**
	 * Build the options frame
	 */
	private static void buildOptions() {		Options.setTitle("JTron Options");
		Options.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel content = (JPanel)Options.getContentPane();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		content.setLayout(gb);
		
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 0;
		BackgroundImagePanel logo = new BackgroundImagePanel();
		logo.setPreferredSize(new Dimension(200,75));
		content.add(logo,c);
		
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		JPanel optPanel = new JPanel();
		optPanel.setLayout(new GridLayout(5,2));
		
	
		optPanel.add(new JLabel("Grid size:"),c);
		final int [] dimArray = new int[] {30,40,50,60,70};
		final JComboBox dimensions = new JComboBox(new String[] {"30x30","40x40","50x50","60x60","70x70"});
		dimensions.setSelectedIndex(2);
		optPanel.add(dimensions);
		
		final Color[] colorArray = new Color[] {Color.red, Color.blue, Color.yellow, Color.green};
		final JComboBox P1Color = new JComboBox(new String[] {"Red","Blue","Yellow","Green"});
		final JComboBox P2Color = new JComboBox(new String[] {"Red","Blue","Yellow","Green"});
		P2Color.setSelectedIndex(1);
		optPanel.add(new JLabel("P1 color:"));
		optPanel.add(P1Color);
		optPanel.add(new JLabel("P2 color:"));
		optPanel.add(P2Color);
		
	
		final int[] diffArray = new int[] {1, 2, 3, 4};
		final JComboBox difficulty = new JComboBox(new String[] {"Kiddie", "Easy", "Medium", "Hard"});
		difficulty.setSelectedIndex(2);
		optPanel.add(new JLabel("AI Difficulty:"));
		optPanel.add(difficulty);
	
		
		final int[] speedArray = new int[] { 90, 70, 50};
		final JComboBox speed = new JComboBox(new String[] {"Slow", "Medium", "Fast"});
		speed.setSelectedIndex(1);
		optPanel.add(new JLabel("Speed:"));
		optPanel.add(speed);
		content.add(optPanel);
		
		
		
		JButton buttonGo = new JButton("Play!");
		buttonGo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				optionsDone = true;
				
				BOARD_WIDTH = dimArray[ dimensions.getSelectedIndex() ];
				BOARD_HEIGHT = dimArray[ dimensions.getSelectedIndex() ];
				int i;
				for(i = 0; ; i++) { 
					if((i * BOARD_WIDTH) >= MIN_SIZE) {
						TILE_SIZE = i;
						break;
					}
				}
				if(P2Color.getSelectedIndex() != P1Color.getSelectedIndex()) {
					P1_COLOR = colorArray[ P1Color.getSelectedIndex()  ];
					P2_COLOR = colorArray[ P2Color.getSelectedIndex()  ];
				}
				int diff =  diffArray[ difficulty.getSelectedIndex()];
				if(diff != 4) {
					AI_ANGER = diff;
					AI_PARANOIA = 5 - diff;
					AI_NEUTRAL = 1;
				} else {
					AI_ANGER = 1;
					AI_PARANOIA = 0;
					AI_NEUTRAL = 0;
				}
				interval = speedArray[ speed.getSelectedIndex() ];
			
			}
		});
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 6;
		
		content.add(buttonGo,c);
		//buttonGo.setPreferredSize(new Dimension(200,100));
		Options.pack();
		centerFrame(Options);
	}

		
	/**
	 * Build the main window for game playing
	 */
	private static void buildUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JMenuBar menuBar = new JMenuBar();
		final JMenu menuGame = new JMenu("Game");
			final JMenuItem menuGameNew = new JMenuItem("New");
			final JMenuItem menuGameQuit = new JMenuItem("Quit");
			
		final JMenu menuHelp = new JMenu("Help");
			final JMenuItem menuHelpAbout = new JMenuItem("About");
				
		frame.setJMenuBar(menuBar);
		menuBar.add(menuGame);
		menuBar.add(menuHelp);
		menuGame.add(menuGameNew); menuGame.add(menuGameQuit);
		menuHelp.add(menuHelpAbout);
		
		ActionListener menuAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == menuGameNew) {
					optionsDone = false;
					streak = true;
					theRedButton = true;
				}
				if(e.getSource() == menuGameQuit)
					System.exit(0);
				if(e.getSource() == menuHelpAbout) {
					JOptionPane.showMessageDialog(
							frame,
							"Written by Paul Kilgo and Drew Taylor.\nUniversity of Alabama\nCS-325 Spring 2008",
							"About JTron",
							JOptionPane.INFORMATION_MESSAGE
					);
					
				}
				System.out.println("Pkcedmenu!");
			}
		};
		
		
		menuGameNew.addActionListener(menuAction);
		menuGameQuit.addActionListener(menuAction);
		menuHelpAbout.addActionListener(menuAction);
				
		JPanel content = (JPanel) frame.getContentPane();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		content.setLayout(gb);
		
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setTitle("JTron");
		
		scoreLine = new JLabel("Wins: " + wins + " Score: " + score + " High Score: " + scoreBoard.getHighScore());
		c.gridx = 0;
		c.gridy = 0;
		content.add(scoreLine);
		
		gameBoard = new PlayGrid(BOARD_WIDTH,BOARD_HEIGHT);
		gameBoard.setPreferredSize(new Dimension(gameBoard.getPixelWidth(), gameBoard.getPixelHeight()));
		c.gridx = 0;
		c.gridy = 1;
		content.add(gameBoard, c);
		c.gridy = 2;
		msg = new JLabel("Press any key to start.");
		content.add(msg,c);
		
		frame.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent a){
				System.out.println("PRESSED " + a);
				lastKey = a;
			}
		});
		
		frame.pack();
		centerFrame(frame);
		
	}
	private static PlayGrid gameBoard;
	private static JFrame frame = new JFrame();
	private static JFrame Options = new JFrame();
	private static JLabel msg;
	private static JLabel scoreLine;
	private static ArrayList<LightCycle> players = new ArrayList<LightCycle>();
	private static int interval = 50;
	private static boolean gameDone = true;
	private static boolean theRedButton = false;
	public static final int DEFAULT_WIDTH = 400;
	public static final int DEFAULT_HEIGHT = 200;
	public static int BOARD_WIDTH = 75;
	public static int BOARD_HEIGHT = 75;
	public static int TILE_SIZE = 4;
	public static int MIN_SIZE = 400;
	public static int AI_PARANOIA = 5;
	public static int AI_ANGER = 2;
	public static int AI_NEUTRAL = 3;
	public static Color P1_COLOR = Color.red;
	public static Color P2_COLOR = Color.blue;
	public static int score = 0;
	public static int wins = 0;
	public static boolean optionsDone = false;
	public static boolean streak = true;
	public static HighScore scoreBoard = new HighScore();

}

class BackgroundImagePanel extends JPanel
{
  public Image backgroundImage;
  public BackgroundImagePanel() {
      try
      {
         this.backgroundImage = ImageIO.read(new File("JTron.png"));
      }
      catch(Exception e)
      {
         throw new RuntimeException(e);
      }
  }
  
  protected void paintComponent(Graphics g)
  {
     g.drawImage(backgroundImage,0,0,null);
  }
} 

