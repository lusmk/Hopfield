import java.util.Arrays;

public class Autoassociator {
	private int weights[][];
	private int trainingCapacity;
	
	public Autoassociator(CourseArray courses) {
		// creates a new Hopfield network with the same number of neurons 
		// as the number of courses in the input CourseArray
		weights = new int[courses.length()][courses.length()];
		trainingCapacity = (int) (0.139 * courses.length());
		initialState();
	}


	public int getTrainingCapacity() {
		return trainingCapacity;
	}
	
	public void training(int pattern[]) {
		for (int i = 0; i < weights.length; i ++){
			for (int j = 0; j < weights[i].length; j++){
				if (j != i) weights[i][j] += pattern[i] * pattern[j];
			}
		}
		trainingCapacity --;
	}
	
	public int unitUpdate(int neurons[]) {
		// implements a single update step and
		// returns the index of the randomly selected and updated neuron
		int index = (int) Math.floor((Math.random()*(neurons.length-1))) + 1; //select random index from neurons array
		unitUpdate(neurons, index);
		return index;
	}
	
	public void unitUpdate(int neurons[], int index) {
		// TO DO
		// implements the update step of a single neuron specified by index
		neurons[index] = nextState(neurons, index);
	}
	
	public void chainUpdate(int neurons[], int steps) {
		// implements the specified number od update steps
		for (int i = 0; i < steps; i ++)
			unitUpdate(neurons);
	}
	
	public void fullUpdate(int neurons[]) {
		// updates the input until the final state achieved
		int[] previous = neurons.clone();
		int[] current = null;
		while(true){
			unitUpdate(neurons);
			current = neurons.clone();
			if(Arrays.equals(current, previous)) return;
			previous = current.clone();
		}
	}


	private void initialState(){
		for (int i = 0; i < weights.length; i ++)
			for (int j = 0; j < weights[i].length; j ++)
				weights[i][j] = 0;
	}

	private int nextState(int[] neurons, int k){
		int arg = 0;
		for (int j = 0; j < neurons.length; j++){
			arg += weights[k][j] * neurons[j];
		}

		if (arg > 0) return 1;
		else return - 1;
	}

}
