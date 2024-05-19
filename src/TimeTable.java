import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {

	private JPanel screen = new JPanel(), tools = new JPanel();
	private JButton tool[];
	private JTextField field[];
	private CourseArray courses;
	private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};

	private boolean started = false;

	private Autoassociator autoAssociator = null;

	private static int updatePer;
	private static int updateNum;
	
	public TimeTable() {
		super("Dynamic Time Table");
		setSize(500, 800);
		setLayout(new FlowLayout());
		
		screen.setPreferredSize(new Dimension(400, 800));
		add(screen);
		
		setTools();
		add(tools);

		updateNum = 1;
		updatePer = 1;
		setVisible(true);
	}
	
	public void setTools() {
		String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:", "Update Period:", "Update Number:"};
		field = new JTextField[capField.length];
		
		String capButton[] = {"Load", "Start", "Step", "Print", "Exit", "Continue", "Train"};
		tool = new JButton[capButton.length];
		
		tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));
		
		for (int i = 0; i < field.length; i++) {
			tools.add(new JLabel(capField[i]));
			field[i] = new JTextField(5);
			tools.add(field[i]);
		}
		
		for (int i = 0; i < tool.length; i++) {
			tool[i] = new JButton(capButton[i]);
			tool[i].addActionListener(this);
			tools.add(tool[i]);
		}
		
		field[0].setText("17");
		field[1].setText("381");
		field[2].setText("lse-f-91.stu");
		field[3].setText("1");
		field[5].setText("0");
		field[6].setText("0");
	}
	
	public void draw() {
		Graphics g = screen.getGraphics();
		int width = Integer.parseInt(field[0].getText()) * 10;
		for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
			g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
			g.drawLine(0, courseIndex, width, courseIndex);
			g.setColor(CRScolor[CRScolor.length - 1]);
			g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
		}
	}
	
	private int getButtonIndex(JButton source) {
		int result = 0;
		while (source != tool[result]) result++;
		return result;
	}
	
	public void actionPerformed(ActionEvent click) {
		int min, step, clashes;
		
		switch (getButtonIndex((JButton) click.getSource())) {
		case 0:
			int slots = Integer.parseInt(field[0].getText());
			courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
			courses.readClashes(field[2].getText());
			if (autoAssociator == null) {
				autoAssociator = new Autoassociator(courses);
				System.out.println("Training capacity: " +  autoAssociator.getTrainingCapacity());
			}
			draw();
			break;
		case 1:
			started = false;
			min = Integer.MAX_VALUE;
			updatePer = Integer.parseInt(field[5].getText());
			updateNum = Integer.parseInt(field[6].getText());
			step = 0;
			for (int i = 1; i < courses.length(); i++) courses.setSlot(i, 0);
			
			for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				clashes = courses.clashesLeft();
				if (clashes < min) {
					min = clashes;
					step = iteration;
				}

				for(int k = 0; k < updateNum; k++) runUnitUpdate();
			}
			System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
			setVisible(true);
			started = true;
			break;
		case 2:
			courses.iterate(Integer.parseInt(field[4].getText()));
			draw();
			break;
		case 3:
			System.out.println("Exam\tSlot\tClashes");
			for (int i = 1; i < courses.length(); i++)
				System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
			break;
		case 4:
			System.exit(0);
			break;
		case 5:
			if (started) {
				min = courses.clashesLeft();
				step = 0;
				for (int currentIter = 1; currentIter <= Integer.parseInt(field[3].getText()); currentIter++) {
					courses.iterate(Integer.parseInt(field[4].getText()));
					draw();
					clashes = courses.clashesLeft();
					if (clashes < min) {
						min = clashes;
						step = currentIter;
					}
				}
				System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
				setVisible(true);
			}
			break;
		case 6:
			min = Integer.MAX_VALUE;
			step = 0;
			int all = Integer.parseInt(field[3].getText());
			for(int i = 1; i<courses.length(); i++) courses.setSlot(i, 0);
			for (int iter  = 1; iter <= all; iter++){
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				clashes = courses.clashesLeft();
				if (clashes < min)
				{
					min = clashes;
					step = iter;
				}
			}
			setVisible(true);
			train(Integer.parseInt(field[0].getText()));
			System.out.println("Training capacity: " +  autoAssociator.getTrainingCapacity());
		}
	}

	public static void main(String[] args) {
		new TimeTable();
	}

	private void runUnitUpdate()
	{
		int[] clashedTimeSlot = null;
		for (int i = 1; i < courses.length(); i++) {
			if (CourseHasClashes(i)) {
				clashedTimeSlot = courses.getTimeSlots(courses.slot(i));
				clashedTimeSlot[0] = courses.slot(i);
			}
		}
		if (clashedTimeSlot != null) {
			int numOfSlots = Integer.parseInt(field[0].getText());
			int timeSlotIndex = clashedTimeSlot[0];
			int updatedNeuronIndex = autoAssociator.unitUpdate(clashedTimeSlot);
			if (clashedTimeSlot[updatedNeuronIndex] == 1 && courses.slot(updatedNeuronIndex) != timeSlotIndex) {
				courses.setSlot(updatedNeuronIndex, timeSlotIndex);
			}
			if (clashedTimeSlot[updatedNeuronIndex] == -1 && courses.slot(updatedNeuronIndex) == timeSlotIndex) {
				int newTimeSlotIndex = (int) (Math.floor(Math.random() * numOfSlots));
				courses.setSlot(updatedNeuronIndex, newTimeSlotIndex);
			}
			draw();
		}
		else {
			System.out.println("Minimum achieved");
		}
	}


	private void train(int slots){
		if (autoAssociator.getTrainingCapacity() == 0) return;
		int[] timeSlots = initializeTimeslots(slots);

		for (int i = 1; i < courses.length(); i++){
			if(CourseHasClashes(i)) timeSlots[courses.slot(i)] = -1;
		}

		for (int i = 0 ; i< timeSlots.length;i++) {
			if (autoAssociator.getTrainingCapacity()==0) return;
			else if (TimeslotNoClash(timeSlots[i])) autoAssociator.training(courses.getTimeSlots(timeSlots[i]));
		}
	}


	private int[] initializeTimeslots(int slots){
		int[] timeslots = new int[slots];
		for (int i = 0; i < slots; i++)
			timeslots[i] = i;
		return timeslots;
	}

	private boolean CourseHasClashes(int i){
		return courses.status(i) != 0;
	}

	private boolean TimeslotNoClash(int timeslot){
		return timeslot != -1;
	}
}
