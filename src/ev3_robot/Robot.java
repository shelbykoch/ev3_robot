package ev3_robot;

import java.util.concurrent.TimeUnit;
import lejos.hardware.Sound;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.*;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
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
	public final NXTUltrasonicSensor UltrasonicSensor;

	private Robot() {
		// Initialize motors
		rightTrack = new EV3LargeRegulatedMotor(MotorPort.A);
		leftTrack = new EV3LargeRegulatedMotor(MotorPort.B);
		claw = new EV3MediumRegulatedMotor(MotorPort.D);

		// Initialize move pilot
		Wheel leftWheel = WheeledChassis.modelWheel(leftTrack, 3.15).offset(9.60);
		Wheel rightWheel = WheeledChassis.modelWheel(rightTrack, 3.15).offset(-9.60);
		WheeledChassis chassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel },
				WheeledChassis.TYPE_DIFFERENTIAL);
		Pilot = new MovePilot(chassis);
		Chassis = chassis;

		// Initialize sensors
		IRSensor = new EV3IRSensor(SensorPort.S1);
		IRSensor.setCurrentMode(0);
		ColorSensor = new EV3ColorSensor(SensorPort.S2);
		UltrasonicSensor = new NXTUltrasonicSensor(SensorPort.S3);
		UltrasonicSensor.getDistanceMode();
	}

	///// PUBLIC FUNCTIONS ///////////

	// Returns instance of Robot object to
	public static Robot getInstance() {
		return robot_instance;
	}

	public boolean Swivel() throws InterruptedException {
		// Swivels robot to detect ball
		// Swivel to the left and then swivel to the right
		int degrees = 5;
		int stages = 3;
		// Undo will counter the rotations. So for every positive rotation increment,
		// undo is decremented one
		// conversely, for every negative rotation undo is incremented one
		int undo = 0;

		for (int i = 0; i < stages; i++) {
			Pilot.rotate(degrees);
			undo--;
			if (CheckForBall(undo, degrees))
				return true;
		}

		// Reset robot to neutral position before swiveling in other direction
		UndoSwivel(undo, degrees);
		undo = 0;

		// Swivel robot in other direction to search for ball
		for (int i = 0; i < stages; i++) {
			Pilot.rotate(-degrees);
			undo++;
			if (CheckForBall(undo, degrees))
				return true;
		}
		UndoSwivel(undo, degrees);
		//Pilot.rotate(-1);
		return false;
	}

	public boolean CheckForBall(int undo, int degrees) throws InterruptedException {
		if (GetColorSensorReading() >= 0.10f) {
			CatchBall();
			UndoSwivel(undo, degrees);
			return true;
		}
		return false;
	}

	private void UndoSwivel(int rotationCount, int rotationDegrees) {
		Pilot.rotate(rotationCount * rotationDegrees);
	}

	// Grab ball once directly under color sensor
	public boolean CatchBall() throws InterruptedException {
		OpenClaw();
		// Reverse and rotate to center claw on the ball
		Pilot.travel(-7);
		Pilot.rotate(-25);
		CloseClaw();
		// Undo moves to grab ball
		Pilot.rotate(25);
		Pilot.travel(7);
		return true;
	}

	public void Score() throws InterruptedException {
		// Release the ball
		OpenClawSlow();
		Pilot.travel(-30);
		CloseClaw();
		Pilot.rotate(180);
		Pilot.travel(30);
		Sound.twoBeeps();
	}

	public float GetIRDistance() {
		float[] sample = new float[1];
		this.IRSensor.fetchSample(sample, 0);
		return sample[0];
	}

	public float GetUltrasonicDistance() {
		float[] sample = new float[1];
		this.UltrasonicSensor.fetchSample(sample, 0);
		return sample[0];
	}

	/////// PRIVATE FUNCTIONS //////////

	private void OpenClawSlow() throws InterruptedException {
		claw.setSpeed(claw.getMaxSpeed() / 4);
		claw.rotate(-240);
		TimeUnit.SECONDS.sleep(2);
		claw.rotate(-140);
	}

	private void OpenClaw() {
		claw.setSpeed(claw.getMaxSpeed() / 4);
		claw.rotate(-380);
	}

	private void CloseClaw() {
			claw.setSpeed(claw.getMaxSpeed() / 4);
			claw.rotate(380);
			
	}

	private float GetColorSensorReading() {
		float[] sample = new float[1];
		ColorSensor.getRedMode().fetchSample(sample, 0);
		return sample[0];

	}

}
