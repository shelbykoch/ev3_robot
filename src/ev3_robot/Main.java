package ev3_robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import lejos.hardware.Battery;
import lejos.hardware.Button;

public class Main {
	public static void main(String[] args) throws Exception {
		WriteBatteryLog();
		Robot robot = Robot.getInstance();
		float[] sensor_val = new float[1];
		float color = 0.35f;
		float target = 0.35f;
		float offset = 0.0f;
		int fullspeed = 270;
		float multiplier = 1.7f;
		
		

		robot.Pilot.forward();
		while (Button.ESCAPE.isUp()) {
			robot.ColorSensor.getRedMode().fetchSample(sensor_val, 0);
			color = sensor_val[0];
			offset = color - target;
			if (offset > 0) {
				robot.rightTrack.setSpeed(fullspeed);
				robot.leftTrack.setSpeed(fullspeed * (1 - offset*multiplier));
			} else if (offset < 0) {
				robot.rightTrack.setSpeed(fullspeed * (1 + offset*multiplier));
				robot.leftTrack.setSpeed(fullspeed);
			} else {
				robot.rightTrack.setSpeed(360);
				robot.leftTrack.setSpeed(360);
			}
		}
	}

	public static void WriteBatteryLog() {
		try {
		FileWriter fw = new FileWriter("batterylog.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out = new PrintWriter(bw);
		out.println(String.valueOf(Battery.getVoltage()));
		out.close();
		} catch (Exception ex)
		{
			
		}
	}

}
