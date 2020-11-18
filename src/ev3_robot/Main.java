package ev3_robot;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import lejos.hardware.Battery;
import lejos.hardware.Button;

public class Main {
	public static void main(String[] args) throws Exception {
		Robot robot = Robot.getInstance();
		Mapping map = new Mapping(robot);
		map.Traverse();
	}
	
}



