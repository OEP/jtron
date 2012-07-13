import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

// Purpose:	This class handles the graphics and collisions.
public class PlayGrid extends JComponent {
	PlayGrid() {
		colorGrid = new Color[64][64];
		XSIZE = 64;
		YSIZE = 64;
		TILESIZE = 4;
		wipe();
	}
	PlayGrid(int x, int y) {
		colorGrid = new Color[y][x];
		XSIZE = x;
		YSIZE = y;
		TILESIZE = 4;
		wipe();
	}
	PlayGrid(int x, int y, int s) {
		colorGrid = new Color[y][x];
		XSIZE = x;
		YSIZE = y;
		TILESIZE = s;
		wipe();
	}
	
	private static Color [][] colorGrid;
	private static Color bgColor = new Color(0,0,0);
	private static boolean manualRedraw = false;
	private static int XSIZE;
	private static int YSIZE;
	private static int TILESIZE;
		
	public static void main(String[] args) {
		final Point p = new Point(35,25);
		
		JFrame frame = new JFrame("PlayGrid class test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel content = (JPanel) frame.getContentPane();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		content.setLayout(gb);
		
		//The PlayGrid code
		final PlayGrid basic = new PlayGrid(50,50,5);
		basic.setPreferredSize(new Dimension(basic.getPixelWidth(),basic.getPixelHeight()));
		basic.setColor(p.x, p.y, Color.red);
		c.gridy = 0;
		c.gridx = 0;
		c.weighty = 1;
		content.add(basic,c);
		
		//Button navigation
		JPanel under = new JPanel();
		under.setLayout(new GridLayout(2,1));
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(3,3));
		final JButton up = new JButton("Up");
		final JButton down = new JButton("Down");
		final JButton left = new JButton("Left");
		final JButton right = new JButton("Right");
		buttons.add(new JPanel());buttons.add(up);buttons.add(new JPanel());buttons.add(left);buttons.add(new JPanel());buttons.add(right);buttons.add(new JPanel());buttons.add(down);buttons.add(new JPanel());
		buttons.setPreferredSize(new Dimension(basic.getPixelWidth() + 5, 50));
		
		JPanel textContainer = new JPanel(new FlowLayout());
		final JLabel msgBox = new JLabel("No collisions!");
		textContainer.add(msgBox);
		
		under.add(buttons);
		under.add(textContainer);
		c.gridy = 1;
		c.weighty = 0;
		content.add(under,c);
	
		
		
		//Button actions
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == up)
					p.y--;
				if(e.getSource() == down)
					p.y++;
				if(e.getSource() == left)
					p.x--;
				if(e.getSource() == right)
					p.x++;
				
				if(basic.collides(p.x, p.y)) //do we hit something here other than background?
					msgBox.setText("You collided!");
				else msgBox.setText("No collisions!");
				String bleh = new String(percentLeft() + "%");
				msgBox.setText(bleh);
				basic.setColor(p.x, p.y, Color.red);
				basic.draw();
			}
		};
		up.addActionListener(action);
		down.addActionListener(action);
		left.addActionListener(action);
		right.addActionListener(action);
		
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public int getGridWidth() { return XSIZE; }
	public int getGridHeight() { return YSIZE; }
	public int getPixelWidth() { return (TILESIZE * XSIZE); }
	public int getPixelHeight() { return (TILESIZE * YSIZE); }
	
	public void setGridSize(int x, int y) {
		colorGrid = new Color[y][x];
		XSIZE = x;
		YSIZE = y;
		TILESIZE = 4;
		wipe();
	}
	public void setTileSize(int i) { TILESIZE = i; }
	
	public void draw() {
		repaint();
	}
	
	public void setColor(int x, int y, Color c) {
		if(x < 0 || x >= XSIZE || y < 0 || y >= YSIZE) {
			System.out.println("PlayGrid: Tried to paint out of range");
			return;
		}
		
		colorGrid[y][x] = c;
		repaint();
	}
	
	public boolean collides(int x, int y) {
		if(x < 0 || x >= XSIZE || y < 0 || y >= YSIZE)
			return true;
		
		if(colorGrid[y][x] != bgColor)
			return true;
		else return false;
	}
	
	public boolean collides(LightCycle p) {
		return collides(p.getX(),p.getY());
	}
	
	public boolean collides(Point p) {
		return collides(p.x,p.y);
	}
	
	public void paintComponent(Graphics g) {
		//probably do the actual 'update' to the graphics array here
		
		for(int i = 0; i < YSIZE; i++)
			for(int j = 0; j < YSIZE; j++) {
				g.setColor(colorGrid[i][j]);
				g.fillRect((j*TILESIZE), (i*TILESIZE), TILESIZE, TILESIZE); //paint a block
			}
		
	}
	
	//wipe it with bgColor
	public void wipe() {
		for(int i = 0; i < YSIZE; i++)
			for(int j = 0; j < XSIZE; j++)
				colorGrid[i][j] = bgColor;
	}
	//wipe with a given color
	public void wipe(Color c) {
		for(int i = 0; i < YSIZE; i++)
			for(int j = 0; j < XSIZE; j++)
				colorGrid[i][j] = c;
	}
	
	public static int percentLeft() {
		int total = 0;
		int filled = 0;
		for(int i = 0; i < YSIZE; i++)
			for(int j = 0; j < XSIZE; j++) {
				total++;
				if(colorGrid[i][j] == bgColor) {
					filled++;
				}
			}
		
		double percent = (((double)filled) / total) * 100;
		System.out.println(filled + " filled " + total + " total");
		System.out.println(percent + "%");
		return (int)percent;
		
	}
	
}
