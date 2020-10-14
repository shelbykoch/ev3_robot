package ev3_robot;

import java.util.concurrent.TimeUnit;

public class Main{
    public static void main(String[] args) throws Exception {
    	Robot robot = Robot.getInstance();
    	/*robot.OpenClaw();
    	robot.CloseClaw();
    	robot.CloseClaw();
    	robot.OpenClaw();*/
    	
    	//robot.Pilot.travel(30.48);
    	robot.Pilot.rotate(-360);
    	//robot.Pilot.backward();
    }
    
    private void FollowWall(Robot robot) {
    	int followDistance = 20;
    	int sample[] = new int[1];
    	while (true)
    	{
    		//if(robot.IRSensor.fetchSample(sample, 0))[0] < followDistance)
    	}
    }
}
