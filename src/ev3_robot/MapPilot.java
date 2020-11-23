package ev3_robot;

import java.util.concurrent.TimeUnit;

import ev3_robot.Enums.Heading;
import ev3_robot.Tile.Legend;

public class MapPilot {
	// Robot object
	private Robot robot;

	// Map context
	private Map map;

	// Current row index of robot
	private int row;

	// Current column index of robot
	private int col;

	// Current heading of robot
	private Heading Heading;

	// Value for 90 degree rotation (may be adjusted to account for under or over
	// rotation
	private int rotationMultiplier = 90;

	public MapPilot(Map context) {
		map = context;
		robot = Robot.getInstance();

		// This configuration assumes the robot is placed in the bottom left corner
		// (with 0,0 being the top left), facing the right
		row = map.GetRow() - 1;
		col = 0;
		Heading = Heading.East;
	}

	// ------------Navigator Methods-------------------//

	// Uses a loop to iterate all rows of the map
	public void Traverse() throws InterruptedException {
		for (int i = 0; i < map.GetRow(); i++) {
			TraverseRow();
			NorthOneRow();
		}
	}

	// Once it is one a particular row, it will traverse across all tiles on that
	// particular row
	private void TraverseRow() throws InterruptedException {
		int targetCol;
		if (Heading == Heading.East)
			targetCol = map.GetCol() - 1;
		else
			targetCol = 0;

		while (col != targetCol) {
			ForwardOne();
			CheckForObstacle();
		}
	}

	// Moves robot north on row to begin new traversal movement
	private void NorthOneRow() throws InterruptedException {
		if (row != 0) // Do not move north if you are at the top of the map
		{
			Heading targetHeading;

			if (Heading == Heading.East)
				targetHeading = Heading.West;
			else
				targetHeading = Heading.East;

			FaceDirection(Heading.North);
			ForwardOne();
			FaceDirection(targetHeading);
		}
	}

	private void AvoidObstacle() throws InterruptedException {

		//OBSTACLE AVOIDANCE
		//Turn LEFT and then go around the object UNLESS we are on the last row, then we should turn RIGHT and go around
		//Our traversal goes: Turn left -> Forward -> Turn Right -> Forward until Ultrasonic sensor doesnt see the object
		// -> Turn right -> Forward -> Turn left and we should be facing the original direction
		float[] sample = new float[1];
		
		Heading targetHeading;

		if (Heading == Heading.East)
			targetHeading = Heading.West;
		else
			targetHeading = Heading.East;
		
		//Begin avoidance
		FaceDirection(Heading.North);
		//If on the last row we go south to avoid instead
			
		ForwardOne();
		FaceDirection(targetHeading);
		
		//While the UltrasonicSensor senses the object still, move forward
		//Pause added because UltrasonicSensor needs time to reset
		robot.UltrasonicSensor.fetchSample(sample, 0);
		while(sample[0] < 60){
			ForwardOne();
			TimeUnit.SECONDS.sleep(1);
			robot.UltrasonicSensor.fetchSample(sample, 0);
		}
		
		FaceDirection(Heading.South);
		ForwardOne();
		FaceDirection(targetHeading);	
	}

	// ------------------Atomic Actions--------------------------//
	// --------------------Movement------------------------------//

	// Moves robot forward one square in the direction it is facing
	// Iterates the current row or col value accordingly
	public void ForwardOne() throws InterruptedException {
		// Move forward one tile
		robot.Pilot.travel(Tile.Size);
		// Update robot location
		switch (Heading) {
		case East:
			col++;
			break;
		case West:
			col--;
			break;
		case North:
			row--;
			break;
		case South:
			row++;
			break;
		}
	}

	// Rotates robot to face direction passed to method based
	public void FaceDirection(Heading heading) {
		// East = 0, North = 1, West = 2; South = 3;
		int target = (heading.ordinal() - this.Heading.ordinal());
		int rotation = target * rotationMultiplier;
		robot.Pilot.rotate(rotation);
		Heading = heading;
	}

	// ----------------------Sensors--------------------------//

	// Takes the robots current column and returns the expected distance to the wall
	// based on the size of the tiles. This function assumes that the IR sensor is
	// mounted on the front of the robot and thus would be from the front most edge
	// of the currently occupied tile.
	private float DistanceToWall() {
		// If facing east,
		if (Heading == Heading.East)
			return (map.GetCol() - col) * Tile.Size;
		else
			return (map.GetCol() - 0) * Tile.Size;
	}

	// Compare sensor reading to expected distance to wall
	// If it is close enough tag the tile as having an obstacle
	// Avoid the square, and continue to traverse the loop
	private void CheckForObstacle() throws InterruptedException {
		
		float[] sample = new float[1];
		// Create float array to store values from sensor
		robot.IRSensor.fetchSample(sample, 0);
		//Check if IR SENSOR has found an obstacle
		if(sample[0] < 8 && col != 0 && col != map.GetCol()-1)
		{
			int tileOffset = 0;
			if(Heading == Heading.East)
				tileOffset++;
			else
				tileOffset--;
			lejos.hardware.Sound.beep();
			//Mark the tile we just checked out as an obstacle
			map.SetTile(row, (col+tileOffset), Legend.Obstacle);
			TimeUnit.SECONDS.sleep(3);
			AvoidObstacle();
		}
	}
}
