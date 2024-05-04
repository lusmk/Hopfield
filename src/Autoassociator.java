public class Autoassociator {
	private int weights[][];
	private int trainingCapacity;
	
	public Autoassociator(CourseArray courses) {
		// TO DO
		// creates a new Hopfield network with the same number of neurons 
		// as the number of courses in the input CourseArray
	}
	
	public int getTrainingCapacity() {
		// TO DO
		
		return 0;
	}
	
	public void training(int pattern[]) {
		// TO DO
	}
	
	public int unitUpdate(int neurons[]) {
		// TO DO
		// implements a single update step and
		// returns the index of the randomly selected and updated neuron
		
		return 0;
	}
	
	public void unitUpdate(int neurons[], int index) {
		// TO DO
		// implements the update step of a single neuron specified by index
	}
	
	public void chainUpdate(int neurons[], int steps) {
		// TO DO
		// implements the specified number od update steps
	}
	
	public void fullUpdate(int neurons[]) {
		// TO DO
		// updates the input until the final state achieved
	}
}
