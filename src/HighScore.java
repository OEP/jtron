import java.io.*;
import java.util.*;

public class HighScore {
	private static File scoreFile = new File("JTronScore.dat");
	
	HighScore(){
		try{
			if(scoreFile.length() == 0){ 
				scoreFile.delete();
				System.out.println("Flushed high scores.");
			}
			System.out.println("Had length " + scoreFile.length());
			if(!scoreFile.exists()) scoreFile.createNewFile();
		} catch(Exception e){ System.out.println("Highscore: Failed to create dat file"); }
	}
	
	public static int getHighScore(){
		int buffer = 0;
		try{
			Scanner s = new Scanner(scoreFile);
			
			buffer = s.nextInt();
			
		} catch(Exception e){ System.out.println("HighScore: Failed to get highscore"); System.out.println(e);}
		return buffer;
	}
	
	public static void setHighScore(int newScore){
		try{
			scoreFile.delete();
			scoreFile.createNewFile();
			FileOutputStream writer = new FileOutputStream(scoreFile);
			PrintStream writeOut = new PrintStream(writer);
			writeOut.println(newScore);
			return;
		} catch(Exception e){}
	}
	
	public static boolean handleScore(int currentScore){
		int highScore = getHighScore();
		System.out.println("High score was " + getHighScore());
		System.out.println("Your score was " + currentScore);
		if(currentScore > highScore){
			setHighScore(currentScore);
			return true;
		} else {
			return false;
		}
	}
}
