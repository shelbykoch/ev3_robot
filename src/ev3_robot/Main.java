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
		
		LogThread log = new LogThread();
		
		// Initiate map with rows, columns, and tile size
		Map map = new Map(23, 18, 6.35f);
		// This class acts as both navigator and pilot
		MapPilot pilot = new MapPilot(map);
		log.run();
		pilot.Traverse();
	}
}
class LogThread extends Thread {
	public void run() {
		try {
			// Get robot instance
			Robot robot = Robot.getInstance();

			// File and buffer writer to store sensor values in a text log
			FileWriter fw = new FileWriter("ultrasoniclog.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);

			// Run manual loop to get multiple sensor readings
			for (int i = 0; i < 30; i++) {
				// Fetch sensor sample and write to text log
				out.println(String.valueOf(robot.GetUltrasonicDistance()));
				Thread.sleep(1000);
			}
			// Close file stream
			out.close();
		} catch (Exception e) {
			System.out.println("Exception is caught on travel thread");
		}
	}
}