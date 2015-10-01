package cis232.lab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

	private static final String STUDENTS_FILE = "students.csv";
	private Random random = new Random();
	private ArrayList<Student> unpickedStudents;
	private ArrayList<Student> allStudents;
	private ArrayList<Student> pickedStudents = new ArrayList<>();
	private Scanner keyboard = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		Main main = new Main();
		main.run();

		System.out.println("LEADERBOARD");
		main.printLeaderboard();
		System.out.println("Hope everyone enjoys their bonus points!");
		
	}
	
	public Main() throws IOException{
		loadStudentsFromFile();
	}
	
	public void run() throws IOException{
		if(unpickedStudents.isEmpty()){
			System.out.println("You are all alone. Go home.");
			return;
		}
		
		while(askToPickStudent()){
			Student student = pickRandomStudent();
			System.out.println(student);
			System.out.printf("Did %s get it right? (y/n)%n", student);
			if(keyboard.nextLine().equalsIgnoreCase("y")){
				student.addPoint();
				System.out.printf("Great Job +1 point. %s has %d points.%n",
						student, student.getPoints());
				saveStudentsToFile();
			}else{
				System.out.printf("Better luck next time! %s has %d points.%n",
						student, student.getPoints());
			}
		}
	}

	private Student pickRandomStudent() {
		if(unpickedStudents.isEmpty()){
			ArrayList<Student> temp = unpickedStudents;
			unpickedStudents = pickedStudents;
			pickedStudents = temp;
		}
		
		Student student = unpickedStudents.remove(random.nextInt(unpickedStudents.size()));
		pickedStudents.add(student);
		return student;
	}

	private boolean askToPickStudent() {
		System.out.println("Pick a student? (y/n)");
		String pickInput = keyboard.nextLine();
		return pickInput.equalsIgnoreCase("y");
	}

	private void loadStudentsFromFile() throws FileNotFoundException {
		File originalFile = new File(STUDENTS_FILE);
			try{
				Scanner input = new Scanner(originalFile);
				unpickedStudents = new ArrayList<>();
				allStudents = new ArrayList<>();
				int index = 1;
				
				while(input.hasNextLine()){
					String data = input.nextLine();
					StringTokenizer tokens = new StringTokenizer(data, ",");
					Student student = new Student("", 0);
					
					try{
						student = new Student(tokens.nextToken(), Integer.parseInt(tokens.nextToken()));
					}catch(NumberFormatException exc){
					
						System.out.printf("Row %d is not valid. Contents: %s.\nStudent"
								+ " has a point value that is not an integer.", index, data);
						System.exit(0);
					}
					
					if(tokens.hasMoreTokens()){
						student.setPresent(tokens.nextToken());
					}
					if(student.isPresent()){
						unpickedStudents.add(student);
					}
					allStudents.add(student);
					index++;
				}
				input.close();
			}catch(FileNotFoundException x){
				System.out.printf("%s not found. Exiting Program", originalFile.getPath());
				System.exit(0);
			}
		}

	
		
	private void saveStudentsToFile() throws IOException{
		PrintWriter output = new PrintWriter(STUDENTS_FILE);
		for(Student s : allStudents){
			output.println(s.toCsvString());
		}
		output.close();
	}

	private void printLeaderboard(){
		Collections.sort(allStudents);
		
		for(int i = 0; i < allStudents.size(); i++){
			Student student = allStudents.get(i);
			System.out.printf("%d. %s (%d points)%n", i+1, student, student.getPoints());
		}
	}
}
