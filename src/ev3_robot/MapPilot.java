package ev3_robot;

import java.util.concurrent.TimeUnit;

import ev3_robot.Enums.Heading;
import lejos.hardware.Sound;
import ev3_robot.Tile.Legend;

//Map pilot is the core of the program. This is what has the robot traverse the game board
public class MapPilot {
	// Robot object
	private Robot robot;

	// Map context
	private Map map;

	// Map Pilot info -> information about the robot's location and condition
	private int row; // row index of robot
	private int col; // column index of robot
	private Heading heading; // heading of robot

	// Goal location information
	// Single point goal
	private int goalRow = 3;
	private int goalCol = 7;

	// Two point goal
	// int goalRow = 1;
	// int goalCol = 14;

	// Sets the state of the pilot
	// allows for more intuintive control of searching logic
	private PilotState state;

	private enum PilotState {
		Searching, Scoring, Finished
	}

	// Map pilot construct. Requires the context map the starting row, starting
	// column, and starting heading of the robot placed inside of the map
	public MapPilot(Map context, int startingRow, int startingCol, Heading startingHeading) {
		map = context;
		robot = Robot.getInstance();
		heading = startingHeading;
		state = PilotState.Searching;
	}

	// --------------------Map Traversal Methods------------------------------//

	// Loop through all map rows to search for ball
	// If the ball is found the functions lower in the stack
	// will return true, allowing the functions higher in the stack
	// to cease their operation
	public void Search() throws InterruptedException {
		for (int i = 0; i < map.GetRow(); i++) {
			if (state == PilotState.Searching)
				SearchRow();
			else
				return;
		}
	}

	// Once it is one a particular row, it will traverse across all tiles on that
	// particular row
	private void SearchRow() throws InterruptedException {

		// Direct the robot towards the left or rightmost columns depending on its
		// heading
		int targetCol = heading == Heading.East ? map.GetCol() - 1 : 0;

		// While there are columns left to traverse in the row, move forward, check for
		// an obstacle, swivel for ball, and repeat
		while (col != targetCol) {
			if (state == PilotState.Searching) {
				ForwardOne();
				CheckForObstacle();
				Swivel();
			} else
				break;

		}
		// If we are still searching then move to the next row and begin again
		if (state == PilotState.Searching)
			MoveToNextRow();
		// If we are scoring then take the ball to the hole!!
		else if (state == PilotState.Scoring) {
			NavigateToGoal();
			Score();
		}
	}

	// Moves robot north on row to begin new traversal movement
	private void MoveToNextRow() throws InterruptedException {
		if (row >= 2) // Do not move north if you are at the top of the map
		{
			Heading targetHeading;

			if (heading == Heading.East) {
				targetHeading = Heading.West;
				col -= 4;

			} else {
				targetHeading = Heading.East;
				col += 4;
			}

			FaceDirection(Heading.North);
			Forward(3);
			FaceDirection(targetHeading);
		}
	}

	// Since we had issues with our NXT sensor we had to rely on some simplistic
	// obstacle avoidance. We can expect the obstacle to be roughly the size of a
	// coffee cup and can make our tile adjustments based on that sizing factor
	private void AvoidObstacle() throws InterruptedException {
		// Mark tile as obstacle
		int obstacleRow = row;
		int obstacleCol = col;

		// The robot is not on the obstacle tile, it is next to it
		// we need to iterate one value to properly mark the obstacle tile
		switch (heading) {
		case East:
			obstacleCol++;
			break;
		case West:
			obstacleCol--;
			break;
		case North:
			obstacleRow--;
			break;
		case South:
			obstacleRow++;
			break;
		}
		// Set tile as obstacle
		map.SetTile(obstacleRow, obstacleCol, Legend.Obstacle);

		// OBSTACLE AVOIDANCE
		int stages = 3;
		Heading targetHeading = heading;
		Heading avoidanceHeading = Heading.North;
		Heading returnHeading = Heading.South;

		if (targetHeading == Heading.East || targetHeading == Heading.West) {
			avoidanceHeading = Heading.North;
			returnHeading = Heading.South;
		} else {
			avoidanceHeading = Heading.East;
			returnHeading = Heading.West;
		}

		// Begin avoidance -> box around the tile
		FaceDirection(avoidanceHeading);
		for (int i = 0; i < stages; i++) {
			ForwardOne();
		}
		FaceDirection(targetHeading);
		for (int i = 0; i < stages + 4; i++) {
			ForwardOne();
		}
		FaceDirection(returnHeading);
		for (int i = 0; i < stages; i++) {
			ForwardOne();
		}
		// Face the right way before the row traversal method continues
		FaceDirection(targetHeading);
	}

	// --------------------Simple Movement------------------------------//

	// Moves robot forward one square in the direction it is facing
	// Iterates the current row or col value accordingly
	public void ForwardOne() throws InterruptedException {
		// Move forward one tile
		robot.Pilot.travel(Tile.Size);
		// Update robot location
		switch (heading) {
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

	public void Forward(int tiles) throws InterruptedException {
		for (int i = 0; i < tiles; i++)
			ForwardOne();
	}

	// Rotates robot to face direction passed to method based
	public void FaceDirection(Heading desiredHeading) {
		// East = 0, North = 1, West = 2; South = 3;
		int target = (desiredHeading.ordinal() - this.heading.ordinal());

		// target with 3 or -3 will do a 270 degree rotation which is unnecessary
		// divide by -3 to spin 90 degrees in the opposite direction to end with the
		// the same target heading but less rotation.
		if (target % 3 == 0)
			target /= -3;

		// Rotate robot and update heading
		robot.Pilot.rotate(target * 90);
		heading = desiredHeading;
	}

	// -------------------------Swivel-------------------------------//

	private void Swivel() {
		// Once the robot enters a new tile this will rotate the robot left and right to
		// search for the ball increasing its field of view
		// We must counter the swivel movement before navigating to any other tiles and
		// due to the inaccuracies of the track we must slightly correct the rotations
		int degrees = 5;
		int stages = 3;
		int sign; // Instead of using two bespoke loops, just use a nested loop and set the sign
					// to +1 on the first iteration and -1 on the second iteration
		int counterRotation;

		for (int i = 0; i < 2; i++) {
			sign = i == 0 ? 1 : -1;
			for (int j = 0; j < stages; j++) {
				robot.Pilot.rotate(sign * degrees);
				counterRotation = -sign * (j + 1) * degrees;
				if (CheckForBall()) {
					CatchBall();
					robot.Pilot.rotate(counterRotation);
					state = PilotState.Scoring;
					return;
				} else
					robot.Pilot.rotate(counterRotation);
			}
		}
		robot.Pilot.rotate(1); //Offset the bias of the swivel. This helps keep the robot on a straight line
	}

	// -------------------Object and Ball Detection-----------------------//

	// Compare sensor reading to expected distance to wall
	// If it is close enough tag the tile as having an obstacle
	// Avoid the square, and continue to traverse the loop
	private void CheckForObstacle() throws InterruptedException {
		// First, let's get the column and row of the next tile we are about to traverse
		// so we can check if it is marked as an obstacle
		int nextRow = row;
		int nextCol = col;

		switch (heading) {
		case East:
			nextCol++;
			break;
		case West:
			nextCol--;
			break;
		case North:
			nextRow--;
			break;
		case South:
			nextRow++;
			break;
		}

		// If we have mapped the next tile as an obstacle or have detected an obstacle
		// then avoid it
		if (map.GetTile(nextRow, nextCol).GetState() == Legend.Obstacle) {
			AvoidObstacle();
			return;
		} else if (robot.GetIRDistance() < 7.0f && !NearWall()) {
			map.SetTile(nextRow, nextCol, Legend.Obstacle);
			AvoidObstacle();
			return;
		}
	}

	private boolean CheckForBall() {
		return robot.GetColorSensorReading() >= 0.07f;
	}

	// The sensors can incorrectly detect walls as a ball or an obstacle
	// This function will return whether the robot is near the wall of the map
	// This allows the logic flow to ignore potential false positives
	private boolean NearWall() {
		return (col <= 2 && col >= map.GetCol() - 2);
	}

	// ----------------Movements once ball is detected------------------//

	// Grab ball once directly under color sensor
	private boolean CatchBall() {
		robot.OpenClaw();
		// Reverse and rotate to center claw on the ball
		robot.Pilot.travel(-3.5);
		robot.Pilot.rotate(-25);
		robot.CloseClaw();
		// Undo moves to grab ball
		robot.Pilot.rotate(25);
		robot.Pilot.travel(3.5);
		return true;
	}

	private void NavigateToGoal() throws InterruptedException {
		// Count the rows to traverse
		int targetRow = goalRow - row;

		// Count the columns to traverse
		int targetCol = goalCol - col;

		// Our robots tile position is based on the front of the robot. If the robot is
		// facing west and scoring a one point goal then it needs to turn around. This
		// will adjust the robots map position without it moving forward which must be
		// reflected if we want to score correctly
		if (heading == Heading.West && col > goalCol)
			col += 4;

		// If the robot is not on the right col, we must traverse until it matches the
		// goal column
		if (targetCol != col) {

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
		}

		// With how the map is set, the goal will always be morth of the ball
		FaceDirection(Heading.North);

		// Traverse the necessary columns north to score the goal
		while (row != goalRow) {
			ForwardOne();
		}
	}

	// Places ball in the goal area and returns to the midfield
	private void Score() throws InterruptedException {
		// Release the ball
		robot.OpenClawSlow();
		// Back away from the ball
		robot.Pilot.travel(-30);
		// Close the claw
		robot.CloseClaw();
		// turn around
		robot.Pilot.rotate(180);
		// Move forward three tiles towards center of game board
		Forward(3);
		// Celebrate with a couple beeps
		Sound.twoBeeps();
	}
}
