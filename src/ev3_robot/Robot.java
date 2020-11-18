package ev3_robot;

import java.awt.dnd.DropTargetListener;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.*;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.hardware.lcd.*;
import ev3_robot.Enums.Heading;

public class Robot {
	// Global instance
	private static Robot robot_instance = new Robot();

	// Motors
	public final BaseRegulatedMotor rightTrack;
	public final BaseRegulatedMotor leftTrack;
	private final BaseRegulatedMotor claw;

	// Pilot
	public MovePilot Pilot;
	public WheeledChassis Chassis;

	// Sensors
	public final EV3IRSensor IRSensor;
	public final EV3ColorSensor ColorSensor;
	// public final EV3UltrasonicSensor UltrasonicSensor;

	// States
	private ClawState clawState;
	public Heading Heading;

	// State Enumerators
	private enum ClawState {
		OPENED, CLOSED
	}

	private Robot() {
		// Initialize motors
		rightTrack = new EV3LargeRegulatedMotor(MotorPort.A);
		leftTrack = new EV3LargeRegulatedMotor(MotorPort.B);
		claw = new EV3MediumRegulatedMotor(MotorPort.D);

		// Initialize move pilot
		Wheel leftWheel = WheeledChassis.modelWheel(leftTrack, 3.15).offset(9.01);
		Wheel rightWheel = WheeledChassis.modelWheel(rightTrack, 3.15).offset(-9.01);
		WheeledChassis chassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel },
				WheeledChassis.TYPE_DIFFERENTIAL);
		Pilot = new MovePilot(chassis);
		Chassis = chassis;

		// Initialize sensors
		IRSensor = new EV3IRSensor(SensorPort.S1);
		IRSensor.setCurrentMode(0);
		ColorSensor = new EV3ColorSensor(SensorPort.S2);
		// UltrasonicSensor = new EV3UltrasonicSensor(SensorPort.S3);

		// Set States
		SetClawState(ClawState.OPENED);

		// Set Heading
		Heading = Heading.East;
	}

	///// PUBLIC FUNCTIONS ///////////

	// Returns instance of Robot object to
	public static Robot getInstance() {
		return robot_instance;
	}

	public void OpenClaw() {
		if (clawState == ClawState.CLOSED) {
			claw.setSpeed(claw.getMaxSpeed());
			claw.rotate(1650);
			clawState = ClawState.OPENED;
		} else
			Sound.twoBeeps();

	}

	public void CloseClaw() {
		if (clawState == ClawState.OPENED) {
			claw.setSpeed(claw.getMaxSpeed());
			claw.rotate(-1650);
			clawState = ClawState.CLOSED;
		} else
			Sound.twoBeeps();

	}

	/////// PRIVATE FUNCTIONS //////////

	private void SetClawState(ClawState state) {
		clawState = state;
	}
}
