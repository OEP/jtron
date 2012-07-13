import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Purpose:	This class controls the human player based on Input.
//
// Need a position and a direction.  Need to know if he
//	is computer or human.  Needs to handle the changing of
//	his direction.
public class LightCycle {
	LightCycle(PlayGrid g) {
		position = new Point(0,0);
		grid = g;
		cpu = new AI(g,this);
	}
	
	LightCycle(PlayGrid g, int x, int y) {
		grid = g;
		position = new Point(x,y);
		cpu = new AI(g,this);
	}
	
	LightCycle(PlayGrid g, int x, int y, int d) {
		grid = g;
		position = new Point(x,y);
		direction = d;
		cpu = new AI(g,this);
	}
	
	LightCycle(PlayGrid g, int x, int y, int d, Color c) {
		grid = g;
		position = new Point(x,y);
		direction = d;
		team = c;
		cpu = new AI(g,this);
	}
	
	LightCycle(LightCycle c) {
		grid = c.getGrid();
		position = c.getPoint();
		direction = c.getDirection();
		team = c.getTeam();
		cpu = new AI(grid,this);
	}
	
	
	private int direction = 0;
	private boolean human = true;
	private boolean alive = true;
	private Point position;
	private Color team = Color.red;
	private PlayGrid grid; 
	private AI cpu;
	
	public void die() { alive = false; }
	public void setHuman(boolean f) { human = f; }
	public void setTeam(Color c) { team = c; }
	public void setDirection(int i) {
		//we don't want the direction ever to be opposite what it currently is
		//if it is, ignore it
		int forbidden = direction - 2;
		if(forbidden < 0)
			forbidden = 4 + forbidden;
		else if (forbidden > 3)
			forbidden = forbidden % 4;
		if(i != forbidden)
			direction = i;		
	}
	
	public void setDirection(KeyEvent e) {
		switch(e.getKeyCode()) {
			case 37: setDirection(2); break;
			case 38: setDirection(1); break;
			case 39: setDirection(0); break;
			case 40: setDirection(3); break;
		}
	}
	
	public int getDirection() { return direction; }
	public boolean isHuman() { return human; } 
	public Color getTeam() { return team; }
	public int getX() { return position.x; }
	public int getY() { return position.y; }
	public PlayGrid getGrid() { return grid; }
	public Point getPoint() { return position; }
	public boolean alive() { return alive; }
	public AI getAI() { return cpu; }
	
	public void move() {
		if(!human) direction = cpu.nextDirection();
		
		if(alive) {
			switch(direction) {
				case 0: position.x++; break;
				case 1: position.y--; break;
				case 2: position.x--; break;
				case 3: position.y++; break;
			}
		}
	}
	

}
