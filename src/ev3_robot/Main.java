package ev3_robot;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
<<<<<<< HEAD

import lejos.hardware.Button;
import lejos.hardware.lcd.GraphicsLCD;

public class Main {
	public static void main(String[] args) throws Exception {
		Robot robot = Robot.getInstance();
		ColorThread colorThread = new ColorThread();
		colorThread.run();
	}
}

class ColorThread extends Thread {

	float[] sensor_val = new float[1];
	MoveThread moveThread;
	Robot robot;

	public void run() {
		try {
			robot = Robot.getInstance();
			moveThread = new MoveThread();
			moveThread.run();
			while(Button.ESCAPE.isUp()) {
				robot.ColorSensor.getRedMode().fetchSample(sensor_val, 0);
				moveThread.SetColor(sensor_val[0]);
			}

		} catch (Exception e) {
			System.out.println("Exception is caught on move thread");
		}

	}

=======
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
>>>>>>> 60742e077e2abddd73ef86a0630c5118fcc75aae
}

class MoveThread extends Thread {

	Robot robot = Robot.getInstance();
	float color;
	float offset;
	float target = 0.35f;

	public void run() {
		try {
			robot = Robot.getInstance();
			robot.Pilot.forward();
			while(Button.ESCAPE.isUp()) {
				//offset = color - target;
				if(color > target)
				{
					robot.rightTrack.setSpeed(360);
					robot.leftTrack.setSpeed(180);
				}
				else if (color < target)
				{
					robot.rightTrack.setSpeed(180);
					robot.leftTrack.setSpeed(360);
				}
				else
				{
					robot.rightTrack.setSpeed(360);
					robot.leftTrack.setSpeed(360);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception is caught on move thread");
		}
	}
	
	public void SetColor(float value) {
		color = value;
	}
}