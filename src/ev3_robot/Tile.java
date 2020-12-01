package ev3_robot;

public class Tile {
	
	//Holds state of tile
	private Legend state;
	
	//Size of tile in centimeters
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
		Obstacle
	}
	
	//Set state of tile
	public void SetState(Legend state)
	{	
		this.state = state;
	}
	
	//Get state of tile
	public Legend GetState()
	{
		return this.state;
	}
}
