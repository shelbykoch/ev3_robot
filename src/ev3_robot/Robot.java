package ev3_robot;

import lejos.hardware.Sound;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.*;
import lejos.hardware.port.SensorPort;

public class Robot {
	
	//Global instance
	private static Robot robot_instance = new Robot();
	
	//Motors
	private final BaseRegulatedMotor rightTrack; 
	private final BaseRegulatedMotor leftTrack; 
	private final BaseRegulatedMotor claw;
	
	//Sensors
	public final EV3IRSensor IRSensor;
	//public final EV3ColorSensor ColorSensor;
	//public final EV3UltrasonicSensor UltrasonicSensor;
	
	//States
	private ClawState clawState;
	
	
	//State Enumerators
	private enum ClawState {
		OPENED,
		CLOSED
	}

	
	private Robot()
	{
		//Initialize motors
		rightTrack = new EV3LargeRegulatedMotor(MotorPort.A);
		leftTrack = new EV3LargeRegulatedMotor(MotorPort.B);
		claw = new EV3MediumRegulatedMotor(MotorPort.D);
		
		//Initialize sensors
		IRSensor = new EV3IRSensor(SensorPort.S1);
		//UltrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
		//ColorSensor = new EV3ColorSensor(SensorPort.S3);
		
		//Set States 
		SetClawState(ClawState.OPENED);
		
		
	}
	
	///// PUBLIC FUNCTIONS ///////////
	
	//Returns instance of Robot object to 
	public static Robot getInstance()
	{
		return robot_instance;
	}
	
	//Methods
	public void Forward() {
		//Add code to move robot forward, include parameters for speed, duration, etc...
	}
	
	public void Backward() {
		//Add code to move robot backward, include parameters for speed, duration, etc...
	}
	
	public void RotateClockwise() {
		
	}
	
	public void OpenClaw()
	{
		if(clawState == ClawState.CLOSED)
		{
			claw.setSpeed(claw.getMaxSpeed());
			claw.rotate(1650);
			clawState = ClawState.OPENED;
		}
		else
			Sound.twoBeeps();
			
	}
	
	public void CloseClaw()
	{
		if(clawState == ClawState.OPENED)
		{
			claw.setSpeed(claw.getMaxSpeed());
			claw.rotate(-1650);
			clawState = ClawState.CLOSED;
		}
		else
			Sound.twoBeeps();
			
	}
	
	/////// PRIVATE FUNCTIONS //////////

	private void SetClawState(ClawState state)
	{
		clawState = state;
	}
}
