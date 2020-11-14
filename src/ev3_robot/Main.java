package ev3_robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import lejos.hardware.Battery;
import lejos.hardware.Button;

public class Main {
	public static void main(String[] args) throws Exception {
		Robot robot = Robot.getInstance();
		final float tile_size = 20.32f;
		Boolean[][] board = new Boolean[6][7];
		
		float[]  sensor_val = new float[1];
		robot.IRSensor.getDistanceMode().fetchSample(sensor_val, 0);
		float distance = sensor_val[0];
		
		for(int i = 0; i < 6; i ++)
		{
			TraverseRow(robot);
			TraverseColumn(robot);
		}
			
		WriteBatteryLog(robot);
	}
	
	public static void TraverseRow(Robot robot) throws InterruptedException
	{
		final float tile_size = 17.0f;
		for(int i = 0; i < 5; i++)
		{
			robot.Pilot.travel(tile_size);
			TimeUnit.SECONDS.sleep(1);
		}
		if(robot.row_pos == 7)
			robot.row_pos = 0;
		else
			robot.row_pos = 7;
	}
	
	public static void TraverseColumn(Robot robot) throws InterruptedException
	{
		final float tile_size = 17.0f;
		if(robot.row_pos == 7)
		{
			robot.Pilot.rotate(85);
			robot.Pilot.travel(tile_size);
			robot.Pilot.rotate(85);
		}
		if(robot.row_pos == 0)
		{
			robot.Pilot.rotate(-85);
			robot.Pilot.travel(tile_size);
			robot.Pilot.rotate(-85);
		}
		TimeUnit.SECONDS.sleep(1);
	}

	public static void WriteBatteryLog(Robot robot) {
		//Robot travels one foot
		robot.Pilot.travel(30.48f);
		try {
		FileWriter fw = new FileWriter("batterylog.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out = new PrintWriter(bw);
		out.println("Voltage (milli volts): " + String.valueOf(Battery.getVoltageMilliVolt() + " Battery Current: " + Battery.getBatteryCurrent() + " Motor Current: " + Battery.getMotorCurrent()));
		out.close();
		} catch (Exception ex)
		{
			
		}
	}
	
	public static void LineFollower()
	{
		/*
		float[] sensor_val = new float[1];
		float color = 0.35f;
		float target = 0.35f;
		float offset = 0.0f;
		int fullspeed = 200;
		float multiplier = 3f;
		
		

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
		*/
	}

}
