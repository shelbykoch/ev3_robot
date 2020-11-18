package ev3_robot;
import ev3_robot.Enums.Heading;

//import ev3_robot.Enums.Legend;

public class Mapping {

	public static Tile[][] Map;
	public final int row = 7;
	public final int col = 5;
	private Robot robot;
	
	private int rotation = 94;

	public Mapping(Robot robot) {
		// initialize Map
		Map = new Tile[row][col];

		// Set robot instance so we can control it within the context of the map
		this.robot = robot;

		// Set robot's location within the Map
		robot.Heading = robot.Heading.East;
		robot.Row = row - 1;
		robot.Col = 0;

		// Initialize all tiles in map
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				Map[r][c] = new Tile();
			}
		}
	}

	public void SetTile(int row, int col, Tile.Legend state) {
		Mapping.Map[row][col].State = state;
	}

	public Tile GetLegend(int row, int col) {
		return Mapping.Map[row][col];
	}

	public void Traverse() throws InterruptedException {
		for (int i = 0; i < row; i++) {
			TraverseRow();
			if(i != row - 1)
				TraverseColumn();
		}
	}

	private void TraverseRow() throws InterruptedException {
		for (int i = 0; i < col - 1; i++) {
			// Travel one tile
			robot.Pilot.travel(Tile.Size);
			// Update robot location
			if (robot.Heading == Heading.East)
				robot.Col++;
			else
				robot.Col--;
		}
		// Check for obstacle or ball
		// TimeUnit.SECONDS.sleep(1);
	}

	private void TraverseColumn() throws InterruptedException {
		switch (robot.Heading) {
		case East:
			robot.Pilot.rotate(rotation);
			robot.Heading = robot.Heading.North;
			// Check for obstacle or ball
			robot.Pilot.travel(Tile.Size);
			robot.Pilot.rotate(rotation);
			robot.Heading = robot.Heading.West;
			break;
		case West:
			robot.Pilot.rotate(-rotation);
			robot.Heading = robot.Heading.North;
			// Check for obstacle or ball
			robot.Pilot.travel(Tile.Size);
			robot.Pilot.rotate(-rotation);
			robot.Heading = robot.Heading.East;
			break;
		default:
			break;

		}
		robot.Row--;
		// TimeUnit.SECONDS.sleep(1);
	}

	// Takes the robots current column and returns the expected distance to the wall
	// based on the size of the tiles. This function assumes that the IR sensor is
	// mounted on the front of the robot and thus would be from the front most edge
	// of the currently occupied tile.
	private float DistanceToWall() {
		// If facing east,
		if (robot.Heading == Heading.East)
			return (col - robot.Col) * Tile.Size;
		else
			return (robot.Col - 0) * Tile.Size;
	}

	private void CheckForObstacle() {
		// Compare sensor reading to expected distance to wall
		// If it it close enough tag the tile as having an obstacle
		// Avoid the square, and continue the traverse loop
	}

}
