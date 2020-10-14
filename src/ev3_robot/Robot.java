package ev3_robot;

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.*;
import lejos.hardware.port.SensorPort;

public class Robot {
	
	private static Robot robot_instance = new Robot();
	
	//Motors
	public final BaseRegulatedMotor RightTrack; 
	public final BaseRegulatedMotor LeftTrack; 
	public final BaseRegulatedMotor Claw;
	
	//Sensors
	public final EV3IRSensor IR;
	
	
	
	private Robot()
	{
		RightTrack = new EV3LargeRegulatedMotor(MotorPort.A);
		LeftTrack = new EV3LargeRegulatedMotor(MotorPort.B);
		Claw = new EV3MediumRegulatedMotor(MotorPort.D);
		IR = new EV3IRSensor(SensorPort.S1);
		
	}
	
	public static Robot getInstance()
	{
		return robot_instance;
	}
}
