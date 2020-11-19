package ev3_robot;

public class Tile {
	
	//Holds state of tile
	private Legend state;
	
	//Size of tile -> used to traverse map 
	public static float Size;
		
	//Constructor
	public Tile(float size)
	{
		Tile.Size = size;
		this.state = Legend.Unknown;
	}
	
	//Enum for tile states
	public enum Legend {
		Unknown,
		Empty,
		Obstacle,
		Line,
		Ball
	}
	
	public void SetState(Legend state)
	{	
		this.state = state;
	}
	
	public Legend GetState()
	{
		return this.state;
	}
}
