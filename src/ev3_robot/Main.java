package ev3_robot;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import ev3_robot.Enums.Heading;
import lejos.hardware.Battery;
import lejos.hardware.Button;

public class Main {
	public static void main(String[] args) throws Exception {
		//Initiate map with rows, columns, and tile size
		Map map = new Map(23,18,6.35f);
		//This class acts as both navigator and pilot
		MapPilot pilot = new MapPilot(map);
		pilot.Traverse();
	}
	
}



