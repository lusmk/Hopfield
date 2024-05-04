import java.util.*;

public class Course {
		
	public Vector<Course> clashesWith;
	public int mySlot, force;
	
	public Course() {
		clashesWith = new Vector<Course>();
		mySlot = 0;
	}
	
	public Course(int newSlot) {
		clashesWith = new Vector<Course>();
		mySlot = newSlot;
	}
	
	public void addClash(Course thatClash) {
		clashesWith.add(thatClash);
	}
	
	public int clashSize() {
		int result = 0;
		for (int i = 0; i < clashesWith.size(); i++) 
			if (mySlot == clashesWith.elementAt(i).mySlot) 
				result++;
		return result;
	}
	
	public int unitClashForce() {
		for (int i = 0; i < clashesWith.size(); i++)
			if (mySlot == clashesWith.elementAt(i).mySlot) 
				return 1;
		
		return 0;
	}
	
	public void setForce() {
		force = unitClashForce();
	}
	
	public void shift(int limit) {
		mySlot += force;
		if (mySlot < 0) {
			mySlot = limit - 1;
		}
		else if (mySlot >= limit) { 
			mySlot = 0;
		}
	}
}
