package ev3_robot;

import java.util.concurrent.TimeUnit;

import ev3_robot.Enums.Heading;
import lejos.hardware.Sound;
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

	// location of goal to score one point
	private int goalRow = 3;
	private int goalCol = 6;

	// location of goal to score two points
	// int goalRow = 1;
	// int goalCol = 14;

	public MapPilot(Map context) {
		map = context;
		robot = Robot.getInstance();

		// This configuration assumes the robot is placed in the bottom left corner
		// (with 0,0 being the top left), facing the right
		row = 18;
		col = 3;
		Heading = Heading.East;
	}

	// ------------Navigator Methods-------------------//

	// Uses a loop to iterate all rows of the map
	public void Traverse() throws InterruptedException {
		for (int i = 0; i < map.GetRow(); i++) {
			if (TraverseRow())
				return;
			NorthOneRow();
		}
	}

	// Once it is one a particular row, it will traverse across all tiles on that
	// particular row
	private boolean TraverseRow() throws InterruptedException {
		int targetCol;
		if (Heading == Heading.East)
			targetCol = map.GetCol() - 1;
		else
			targetCol = 0;

		while (col != targetCol) {
			ForwardOne();
			CheckForObstacle();
			if (robot.Swivel()) {
				NavigateToGoal();
				robot.Score();
				return true;
			}

		}
		return false;
	}

	// Moves robot north on row to begin new traversal movement
	private void NorthOneRow() throws InterruptedException {
		if (row != 0) // Do not move north if you are at the top of the map
		{
			Heading targetHeading;

			if (Heading == Heading.East) {
				targetHeading = Heading.West;
				col -= 4;

			} else {
				targetHeading = Heading.East;
				col += 4;
			}

			FaceDirection(Heading.North);
			ForwardOne();
			FaceDirection(targetHeading);
		}

	}

	public void NavigateToGoal() throws InterruptedException {
		// Count the rows to traverse
		int targetRow = goalRow - row;

		// Count the columns to traverse
		int targetCol = goalCol - col;

		// If goal is to the right of the robot's location, face east
		if (targetCol > 0) {
			FaceDirection(Heading.East);
			// else, face west
		} else {
			FaceDirection(Heading.West);
		}

		// Traverse the difference between the robot's current column and the goal
		// column
		while (col != goalCol) {
			ForwardOne();
			CheckForObstacle();
		}

		// Face north
		FaceDirection(Heading.North);

		// Traverse the necessary columns north to score the goal
		while (row != goalRow) {
			ForwardOne();
		}
	}

	private void AvoidObstacle() throws InterruptedException {
		// OBSTACLE AVOIDANCE
		int stages = 4;
		Heading targetHeading = Heading;
		Heading avoidanceHeading = Heading.North;
		Heading returnHeading = Heading.South;

		// Begin avoidance
		FaceDirection(avoidanceHeading);
		for (int i = 0; i < stages; i++) {
			ForwardOne();
		}
		FaceDirection(targetHeading);
		for (int i = 0; i < stages + 3; i++) {
			ForwardOne();
		}
		FaceDirection(returnHeading);
		for (int i = 0; i < stages; i++) {
			ForwardOne();
		}
		// Face the right way before the row traversal method continues
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

		// target with 3 or -3 will do a 270 degree rotation which is unnecessary
		// divide by -3 to spin 90 degrees in the opposite direction to end with the
		// the same target heading but less rotation.
		if (target == 3 || target == -3)
			target /= -3;

		int rotation = target * rotationMultiplier;
		robot.Pilot.rotate(rotation);
		Heading = heading;
	}

	// ----------------------Sensors--------------------------//

	// Compare sensor reading to expected distance to wall
	// If it is close enough tag the tile as having an obstacle
	// Avoid the square, and continue to traverse the loop
	private void CheckForObstacle() throws InterruptedException {
		// Check if IR SENSOR has found an obstacle
		if (robot.GetIRDistance() < 7.0f && col > 2 && col < map.GetCol() - 2) {
			int tileOffset = 0;
			if (Heading == Heading.East)
				tileOffset++;
			else
				tileOffset--;
			Sound.beep();
			// Mark the tile we just checked out as an obstacle
			map.SetTile(row, (col + tileOffset), Legend.Obstacle);
			TimeUnit.SECONDS.sleep(3);
			AvoidObstacle();
		}
	}
}
