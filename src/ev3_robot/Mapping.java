package ev3_robot;
import ev3_robot.Enums.Legend;

public class Mapping {
	
	private static Mapping mapping_instance = null; 
	
	static Legend [][] map;
	public final int row = 5;
	public final int col = 7;
	public final float tile_size = 20.32f;
	
	private Mapping() {
		map = new Legend [col][row];
		
		// Initialize all spaces as unknown
		for(int c = 0; c < col; c++) {
			for(int r = 0; r < row; r++) {
				map[c][r] = Legend.Unknown;
			}
		}		
	}	
	
	public static Mapping GetInstance() {
		if(mapping_instance == null) {
			mapping_instance = new Mapping();
		}		
		
		return mapping_instance;		
	}
	
	public void SetLegend(int row, int col, Legend legend) {
		this.map[row][col] = legend;
	}
	
	public Legend GetLegend(int row, int col) {
		return this.map[row][col];
	}
	

}


