import java.awt.*;
import java.util.*;
import java.util.Vector;
// Purpose:	This class controls the computer player.
public class AI {
	
	AI(PlayGrid g, LightCycle lc) {
		grid = g;
		me = lc;
	}
	
	
	private PlayGrid grid;
	private LightCycle me;
	private static LightCycle enemy;
	private static int paranoia = 50;
	private static int anger = 50;
	private static int neutral = 50;
	private static int lastMove = 0;
	
	public static void setAnger(int i) { anger = Math.abs(i); }
	public static void setParanoia(int i) { paranoia = Math.abs(i); }
	public static void setNeutral(int i) { neutral = Math.abs(i); }
	public static void setEnemy(LightCycle c) { enemy = c; }
	
	//temporary until real AI gets here
	public int nextDirection() {
		Vector<Integer> possibilities = new Vector<Integer>();
		Random generator = new Random();
		
		lastMove++;
		
		possibilities.add(0);possibilities.add(1);possibilities.add(2);possibilities.add(3);
		
		// Our first priority: make sure we don't crash on next move in current direction
		if(grid.collides(nextPoint(me.getDirection()))) {
				return makeChoice(possibilities);		
		}
		
		
		if(paranoid()) {
			System.out.println("I'm paranoid!");
			lastMove = 0;
			int r = generator.nextInt(2);
			switch(r) {
			case 0:
				//If we're up against a barrier, try to get off of it
				int turn1 = fixDirection( me.getDirection() - 1 );
				int turn2 = fixDirection( me.getDirection() + 1 );
				if(grid.collides(nextPoint(turn2)) || grid.collides(nextPoint(turn1))) {
					System.out.println("Turning off a wall!");
					return makeChoice(possibilities);
				}
			case 1:
				//run away!
				int newDir = -1;
				switch(enemy.getDirection()) {
				case 0:
					if(me.getX() > enemy.getX())
						newDir = 0;
					else
						newDir = 2;
					break;
				case 1:
					if(me.getY() > enemy.getY())
						newDir = 3;
					else
						newDir = 1;
					break;
				case 2:
					if(me.getX() > enemy.getX())
						newDir = 2;
					else
						newDir = 0;
					break;
				case 3:
					if(me.getY() > enemy.getY())
						newDir = 1;
					else
						newDir = 3;
					break;						
				}
				if(!grid.collides(nextPoint(newDir)) && pathRating(newDir) > 5)
					return newDir;
			}
				
		}		
		if(angry()) {
			System.out.println("I'm angry!");
			lastMove = 0;
			//go after the enemy! rar!
			int xdiff = enemy.getX() - me.getX();
			int ydiff = enemy.getY() - me.getY();
			int newDir = -1;
			
			//if we're closer in the Y, go Y
			if(Math.abs(xdiff) > Math.abs(ydiff)) {
				if(ydiff > 0)
					newDir = 3;
				if(ydiff < 0)
					newDir = 1;
				if(grid.collides(nextPoint(newDir)))
					newDir = -1;
			} else if(newDir == -1 && Math.abs(xdiff) > 0) {
				if(xdiff > 0)
					newDir = 0;
				if(xdiff < 0)
					newDir = 2;
				if(grid.collides(nextPoint(newDir))) 
					newDir = -1;
			}
			
			if(newDir != -1 && pathRating(newDir) > 5)
				return newDir;
			
		}
		
		return me.getDirection();
	}
	
	/**
	 * Get a proposed next point!
	 * @return Point
	 */
	public Point nextPoint(int d) {
		switch(d) {
			case 0: return new Point(me.getPoint().x + 1, me.getPoint().y);
			case 1: return new Point(me.getPoint().x, me.getPoint().y - 1);
			case 2: return new Point(me.getPoint().x - 1, me.getPoint().y);
			case 3: return new Point(me.getPoint().x, me.getPoint().y + 1);
			default: return new Point(0,0);
		}
	}
	
	/**
	 * fix malformed directions
	 */
	public int fixDirection(int d) {
		if(d > 3)
			d = 0;
		if(d < 0)
			d = 3;
		return d;
	}
	
	public boolean paranoid() {
		int sum = paranoia + anger + neutral;
		Random g = new Random();
		
		if(g.nextInt(sum) < paranoia) {
		//	System.out.println("I'm paranoid!");
			return true;
		}
		else
			return false;
	}
	
	public boolean angry() {
		int sum = paranoia + anger + neutral;
		Random g = new Random();
		
		if(g.nextInt(sum) < anger)
			return true;
		else
			return false;
	}
	
	public int makeChoice(Vector<Integer> dirs) {
		int max = 0;
		int element = 0;
		int i = 1;
		LightCycle c = new LightCycle(me.getGrid(), me.getX(), me.getY(), dirs.elementAt(0));
		c.setDirection(dirs.elementAt(0));
		max = pathRating(c);
		for(i = 1; i < dirs.size(); i++) {
			c = new LightCycle(me.getGrid(), me.getX(), me.getY(), dirs.elementAt(i));
			c.setDirection(dirs.elementAt(i));
			int temp = pathRating(c);
			if(temp > max) {
				max = temp;	
				element = i;
			}
		}
		System.out.println("I like going dir " + dirs.elementAt(element) + " rank " + max);
		return dirs.elementAt(element);
	}
	
	public int pathRating(LightCycle c) {
		int i = 1;
		c.move();
		while(!grid.collides(c.getPoint())) {
			System.out.println("Move");
			c.move();
			i++;
		}
		return i;
	}
	public int pathRating(int dir) {
		int i = 1;
		LightCycle c = new LightCycle(me.getGrid(), me.getX(), me.getY(), dir);
		c.move();
		while(!grid.collides(c.getPoint())) {
			System.out.println("Move");
			c.move();
			i++;
		}
		return i;
	}
}
