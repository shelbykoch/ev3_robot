package ev3_robot;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import lejos.hardware.lcd.GraphicsLCD;

public class Main{
    public static void main(String[] args) throws Exception {
    	//Instantiate and run move thread
    	//Robot movement and sensor readings are on different threads so they can run simultaneously
    	MoveThread moveThread = new MoveThread();
    	moveThread.run();
    }
}

class MoveThread extends Thread { 
	
	public void run() { 
		try { 
			//Get robot instance
			Robot robot = Robot.getInstance();
			//Set travel to length of game field
			robot.Pilot.travel(100); 
			//Instantiate and run logging thread
			LogThread logThread = new LogThread();
			logThread.run();  
			} 
		catch (Exception e) { 
				System.out.println("Exception is caught on travel thread"); 
		} 
	} 
}

class LogThread extends Thread { 
	public void run() { 
		try { 
			//Get robot instance
			Robot robot = Robot.getInstance();
			//Create float array to store values from sensor
			float[] sample = new float[1];
	    	
			//File and buffer writer to store sensor values in a text log
	    	FileWriter fw = new FileWriter("log3.txt", true);
	        BufferedWriter bw = new BufferedWriter(fw);
	        PrintWriter out = new PrintWriter(bw);
	    	
	        //Run manual loop to get multiple sensor readings
	    	for(int i = 0; i < 100; i++)
	    	{
	    		//Fetch sensor sample and write to text log
	    		robot.IRSensor.fetchSample(sample, 0);
	    		out.println(String.valueOf(sample[0]));
	    	}    		
	    	//Close file stream
	    	out.close();
			} catch (Exception e) 
		{ 
				System.out.println("Exception is caught on travel thread"); 
		} 
	} 
}
