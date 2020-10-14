package ev3_robot;

import java.util.concurrent.TimeUnit;

public class Main{
    public static void main(String[] args) throws Exception {
    	Robot robot = Robot.getInstance();
        robot.RightTrack.forward();
        robot.LeftTrack.forward();
        robot.Claw.backward();
        TimeUnit.SECONDS.sleep(4);
        robot.Claw.forward();
        TimeUnit.SECONDS.sleep(4);
        robot.Claw.stop();
        robot.LeftTrack.stop();
        robot.RightTrack.stop();
    }
}
