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
    	Robot robot = Robot.getInstance();
    	/*robot.OpenClaw();
    	robot.CloseClaw();
    	robot.CloseClaw();
    	robot.OpenClaw();*/
    	
    	//robot.Pilot.travel(30.48);
    	//robot.Pilot.rotate(-360);
    	//robot.Pilot.backward();
    	MoveThread moveThread = new MoveThread();
    	moveThread.run();
	
    }
}

class MoveThread extends Thread { 
	
	public void run() { 
		try { 
			Robot robot = Robot.getInstance();
			robot.Pilot.travel(100); 
	    	DisplayThread displayThread = new DisplayThread();
	    	displayThread.run();  
			} 
		catch (Exception e) { 
				System.out.println("Exception is caught on travel thread"); 
		} 
	} 
}

class DisplayThread extends Thread { 
	public void run() { 
		try { 
			Robot robot = Robot.getInstance();
	    	float[] sample = new float[1];
	    	
	    	FileWriter fw = new FileWriter("log3.txt", true);
	        BufferedWriter bw = new BufferedWriter(fw);
	        PrintWriter out = new PrintWriter(bw);
	    	
	    	for(int i = 0; i < 100; i++)
	    	{
	    		robot.IRSensor.fetchSample(sample, 0);
	    		out.println(String.valueOf(sample[0]));
	    	}    		
	    	out.close();
			} catch (Exception e) 
		{ 
				System.out.println("Exception is caught on travel thread"); 
		} 
	} 
}
