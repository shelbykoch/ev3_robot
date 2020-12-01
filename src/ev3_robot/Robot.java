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

//Robot class handles everything related to the robot object
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

	// Constructor is set to private to instantiate a single robot
	// object. The robot instance can then be accessed through the
	// static getInstance() method. This ensures the single robot 
	// instance accessible anywhere in the program
	private Robot() {
		// Initialize motors
		rightTrack = new EV3LargeRegulatedMotor(MotorPort.A);
		leftTrack = new EV3LargeRegulatedMotor(MotorPort.B);
		claw = new EV3MediumRegulatedMotor(MotorPort.D);

		// Wheels and chassis -> used to construct move pilot class
		Wheel leftWheel = WheeledChassis.modelWheel(leftTrack, 3.15).offset(9.60);
		Wheel rightWheel = WheeledChassis.modelWheel(rightTrack, 3.15).offset(-9.60);
		WheeledChassis chassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel },
				WheeledChassis.TYPE_DIFFERENTIAL);

		// Move pilot maps rotations and movements to the robots specific wheel size and
		// track offset
		Pilot = new MovePilot(chassis);

		// Initialize sensors
		IRSensor = new EV3IRSensor(SensorPort.S1);
		ColorSensor = new EV3ColorSensor(SensorPort.S2);
		UltrasonicSensor = new NXTUltrasonicSensor(SensorPort.S3);
	}

	// ---------------ROBOT INSTANCE---------------------//

	// Returns instance of Robot object
	public static Robot getInstance() {
		return robot_instance;
	}

	// ----------------CLAW MOVEMENT----------------------//

	// Opens the claw in stages -- intended to settle ball when scoring
	// before releasing it entirely
	public void OpenClawSlow() throws InterruptedException {
		claw.setSpeed(claw.getMaxSpeed() / 4);
		claw.rotate(-240);
		TimeUnit.SECONDS.sleep(2);
		claw.rotate(-140);
	}

	// Opens claw from closed position
	public void OpenClaw() {
		claw.setSpeed(claw.getMaxSpeed() / 4);
		claw.rotate(-380);
	}

	// Closes claw from open position
	public void CloseClaw() {
		claw.setSpeed(claw.getMaxSpeed() / 4);
		claw.rotate(380);
	}

	// --------------------SENSORS---------------------------//

	// Returns sample value of color sensor in red mode
	// red mode returns the most reliable values for detecting the ball
	public float GetColorSensorReading() {
		float[] sample = new float[1];
		ColorSensor.getRedMode().fetchSample(sample, 0);
		return sample[0];

	}

	// Returns distance sample from EV3 IR sensor
	public float GetIRDistance() {
		float[] sample = new float[1];
		this.IRSensor.getDistanceMode().fetchSample(sample, 0);
		return sample[0];
	}

	// Returns distance sample from NXT ultrasonic sensor
	public float GetUltrasonicDistance() {
		float[] sample = new float[1];
		this.UltrasonicSensor.getDistanceMode().fetchSample(sample, 0);
		return sample[0];
	}

}
