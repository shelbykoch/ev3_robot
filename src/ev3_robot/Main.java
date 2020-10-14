package ev3_robot;

import java.util.concurrent.TimeUnit;

public class Main{
    public static void main(String[] args) throws Exception {
    	Robot robot = Robot.getInstance();
    	robot.OpenClaw();
    	robot.CloseClaw();
    	robot.CloseClaw();
    	robot.OpenClaw();
    }
}
