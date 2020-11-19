package ev3_robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ev3_robot.Tile.Legend;
import ev3_robot.Enums.Heading;

//import ev3_robot.Enums.Legend;

public class Map {

	// Number of rows
	private int row;
	// Number of columns
	private int col;

	// Pilot to maneuver around the map
	private MapPilot pilot;
	// 2D array containing all tiles on the map
	private Tile[][] map;

	public Map(int row, int col, float tileSize) {
		// Set map parameters
		this.row = row;
		this.col = col;

		// initialize Map
		map = new Tile[row][col];

		// Initialize all tiles in map
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				map[r][c] = new Tile(tileSize);
			}
		}
	}

	public void SetTile(int row, int col, Tile.Legend state) {
		this.map[row][col].SetState(state);
	}

	public Tile GetTile(int row, int col) {
		return this.map[row][col];
	}

	public int GetRow() {
		return this.row;
	}

	public int GetCol() {
		return this.col;
	}

	public void PrintMap() throws IOException {
		FileWriter fw = new FileWriter("map.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out = new PrintWriter(bw);
		
		String line = " ";
		
		try {
			for (int r = 0; r < row; r++) {
				for (int c = 0; c < col; c++) {
					if (map[r][c].GetState() != Legend.Unknown) {
						line += "X "; 
					} else {
						line += "0 ";
					}
				}
				out.println(line);
				line = " ";
			}			
		} catch (Exception e) {
			System.out.println("Exception is caught on travel thread");
		}finally {
			out.close();
		}

	}
}
