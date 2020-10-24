package ev3_robot;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

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