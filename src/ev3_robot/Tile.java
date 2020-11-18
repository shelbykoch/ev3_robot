package ev3_robot;

public class Tile {
	
	//Holds state of tile
	public Legend State;
	
	//Size of tile -> used to traverse map 
	public static float Size = 17.0f;
		
	//Constructor
	public Tile()
	{
		this.State = Legend.Unknown;
	}
	
	//Enum for tile states
	public enum Legend {
		Unknown,
		Empty,
		Obstacle,
		Line,
		Ball
	}
}
