package ev3_robot;

import java.util.concurrent.TimeUnit;

import ev3_robot.Enums.Heading;

public class RobotPilot {
	public int currentRow; // Probably need to move this out into a navigator class and use pilot for just motion
	public int currentCol;// Probably need to move this out into a navigator class and use pilot for just motion
	public Heading currentHeading;// Probably need to move this out into a navigator class and use pilot for just motion
	
	
	public RobotPilot(int startingRow, int startingCol, Heading startingHeading) {
		this.currentRow = startingRow; 
		this.currentCol = startingCol;
		this.currentHeading = startingHeading;
	}
	
	
	public void RowMoveForwardOneTile(Robot robot) throws InterruptedException {
		robot.Pilot.travel(Tile.Size);
		TimeUnit.SECONDS.sleep(1);
		
		if(this.currentHeading == Heading.East) {
			this.currentRow++;
		}else {
			this.currentRow--;
		}
	}
	
	
	//keeping logic simple. New heading should be East or west
	public void MoveColumn(Heading columnDirection, Heading newHeading) {
		if(newHeading == Heading.South || newHeading == Heading.North) {
			throw new Exception("No Current implentation for new heading of North and South");
		}
		
		// Rotate towards new column. 
		if (this.currentHeading == Heading.East) {
			if (columnDirection == Heading.North) {
				robot.Pilot.rotate(85);		
				this.currentHeading = Heading.North;
				this.currentCol++;
			}else {
				robot.Pilot.rotate(-85);
				this.currentHeading = Heading.South;
				this.currentCol--;
			}
									
		}else {
			if (columnDirection == Heading.North) {
				robot.Pilot.rotate(-85);		
				this.currentHeading = Heading.North;
				this.currentCol++;
			}else {
				robot.Pilot.rotate(85);
				this.currentHeading = Heading.South;				
			}
		}
		
		//move into column
		robot.Pilot.travel(tile_size);
		
		//update column
		if (columnDirection == Heading.North) {
			this.currentCol++;
		}else {
			this.currentCol--;
		}
		
		//Rotate to new heading
		If (this.currentHeading == Heading.North){
			if(newHeading == Heading.East) {
				robot.Pilot.rotate(-85); // really need to try this out... Not to confident that my numbers are correct
			}else {
				robot.Pilot.rotate(85);
			}
		}else {
			if(newHeading == Heading.East) {
				robot.Pilot.rotate(85); // really need to try this out... Not to confident that my numbers are correct
			}else {
				robot.Pilot.rotate(-85);
			}
		}
		
	}
	
	public void ReverseHeading() {
		switch(this.currentHeading) {
			case North:
				robot.Pilot.rotate(85);
				robot.Pilot.rotate(85);
				this.currentHeading = Heading.South;
				break;
			case South:
				robot.Pilot.rotate(85);
				robot.Pilot.rotate(85);
				this.currentHeading = Heading.North;
				break;
			case East:
				robot.Pilot.rotate(85);
				robot.Pilot.rotate(85);
				this.currentHeading = Heading.West;
				break;
			case West:
				robot.Pilot.rotate(85);
				robot.Pilot.rotate(85);
				this.currentHeading = Heading.East;
				break;
		}
			
	}
}
