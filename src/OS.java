import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
import java.io.File;  
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException; 

public class OS {


public static ArrayList<String> readFile(String file)
{
    ArrayList<String> res = new ArrayList<String>();
    try {
    File myObj = new File(file);
    Scanner myReader = new Scanner(myObj);
    while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        if(data.length()>0)
        res.add(data);
    }
    myReader.close();
    } catch (FileNotFoundException e) {
    System.out.println("readFile error");
    e.printStackTrace();
    }
    return res;
}

public static void main(String[]args)
{
	ArrayList<Integer> process = new ArrayList<Integer>();
	ArrayList<Integer> clock = new ArrayList<Integer>();
	boolean isRoundRobin = false;
	PriorityQueue<Program> readyQueue = new PriorityQueue<Program>();
	
	File folder = new File("C:\\\\Users\\\\batoo\\\\eclipse-workspace\\\\OS\\\\src");
    File[] files = folder.listFiles();
    
    int process_id = 0;
    int program_counter = 0;
    int start_address = 0;
    // put in ready queue
    for(int i = 0; i < files.length; i++)
    {
      if(files[i].getPath().substring(files[i].getPath().length()-4, files[i].getPath().length()).equals(".txt"))
      {
      	ArrayList<String> output = readFile(files[i].getPath());
        Program p = new Program(process_id, program_counter, start_address, start_address + output.size() -1, output);
		process_id+=1;
		start_address+=output.size();
		readyQueue.add(p);
      }
    }
    
    PriorityQueue<Program> roundRobin = new PriorityQueue<>((obj1, obj2) -> {
        int result = Integer.compare(obj1.programCounter, obj2.programCounter);

        if (result == 0) {
            result = Integer.compare(obj1.processID, obj2.processID);
        }

        return result;
    });

    roundRobin.addAll(readyQueue);
    
    PriorityQueue<Program> PQCopy = new PriorityQueue<Program>(roundRobin);
    System.out.print("Ready Queue: ");
	while (!PQCopy.isEmpty()) {
	    Program obj = PQCopy.poll();
	    System.out.print(obj.processID+ " ");
     }
    if(isRoundRobin)
    {
    	int clockCycle = 0;
    	
    	Program currentProgram = roundRobin.peek();
		
		while(!roundRobin.isEmpty())
		{
			currentProgram = roundRobin.remove();
    		ArrayList<String> category = currentProgram.categorizeProgram();
    		int PC = currentProgram.programCounter;
    		
    		for(int quantum = 0; quantum < 2; quantum +=1)
    		{
    			
    			if(PC <currentProgram.instructions.size())
    			{String currCategory = category.get(PC);
    		   	switch(currCategory)
    		   	{
    		   	case "variable_assignments": currentProgram.assign(currentProgram.instructions.get(PC));break;
    		   	case "file_IO": currentProgram.file_io(currentProgram.instructions.get(PC));break;
    		   	case "print_commands": System.out.println("Output from process: "+currentProgram.print(currentProgram.instructions.get(PC)));break;
    		   	case "arithmetic_operations": currentProgram.operation(currentProgram.instructions.get(PC));break; 
    		   	default:break;
    		   	}
    		   	currentProgram.incrementPC();
    		   	PC = currentProgram.programCounter;
    		   	
    		   	clock.add(clockCycle);
    		   	process.add(currentProgram.processID);
    		   	System.out.println("Clock Cycle = "+clockCycle);
        		System.out.println("-----------------------------------------");
        		System.out.println("Process in CPU: "+currentProgram.processID);
        		PQCopy = new PriorityQueue<Program>(roundRobin);
        		System.out.print("Ready Queue: ");
        		while (!PQCopy.isEmpty()) {
        		    Program obj = PQCopy.poll();
        		    System.out.print(obj.processID+ " ");
                 }
        		System.out.println();
        		System.out.print("Memory: ");
        		System.out.println(currentProgram.memory);
        		System.out.println();
        		clockCycle+=1;
    			}
    		}
    		if(PC<currentProgram.instructions.size())
    		{
    			roundRobin.add(currentProgram);
    		}
		}
		
    }
    else
    {
    	PriorityQueue<Program> shortestJobFirst = new PriorityQueue<>((obj1, obj2) ->
        Integer.compare(obj1.instructions.size(), obj2.instructions.size()));
	
    	shortestJobFirst.addAll(readyQueue);
		
		
    	//print clock cycle, ready queue contents, process in CPU, memory, gantt chart
		int clockCycle = 0;
		int size = shortestJobFirst.size();
    	for( int i = 0; i < size; i++)
    	{
    		Program currentProgram = shortestJobFirst.remove();
    	
    		ArrayList<String> category = currentProgram.categorizeProgram();
    		System.out.println(category);
    		
    		
    		for (int PC=0; PC<currentProgram.instructions.size(); PC+=1)
    		{
    			String currCategory = category.get(PC);
    		   	switch(currCategory)
    		   	{
    		   	case "variable_assignments": currentProgram.assign(currentProgram.instructions.get(PC));break;
    		   	case "file_IO": currentProgram.file_io(currentProgram.instructions.get(PC));break;
    		   	case "print_commands": System.out.println("Output from process: "+currentProgram.print(currentProgram.instructions.get(PC)));break;
    		   	case "arithmetic_operations": currentProgram.operation(currentProgram.instructions.get(PC));break; 
    		   	default:break;
    		   	}
    		   	
    		   	process.add(currentProgram.processID);
    			clock.add(clockCycle);
    		   	System.out.println("Clock Cycle = "+clockCycle);
        		System.out.println("-----------------------------------------");
        		System.out.println("Process in CPU: "+currentProgram.processID);
        		PQCopy = new PriorityQueue<Program>(shortestJobFirst);
        		System.out.print("Ready Queue: ");
        		while (!PQCopy.isEmpty()) {
        		    Program obj = PQCopy.poll();
        		    System.out.print(obj.processID+ " ");
                 }
        		System.out.println();
        		System.out.print("Memory: ");
        		System.out.println(currentProgram.memory);
        		System.out.println();
        		clockCycle+=1;
    		}
    	}
    }
    
    //Print Gantt Chart
    
    System.out.println("----------------GANTT CHART----------------");
    
    int lastProcess = -1;
    for(int i = 0; i< process.size(); i++)
    {
    	if(lastProcess==-1)
    	{
    		System.out.println("Process: "+process.get(i));
    		System.out.print("Cycle: "+clock.get(i)+" to ");
    		lastProcess = process.get(i);
    	}
    	else {
    		if(process.get(i)!=lastProcess)
    		{
    			System.out.println(clock.get(i-1));
    			System.out.println("Process: "+process.get(i));
        		System.out.print("Cycle: "+clock.get(i)+" to ");
        		lastProcess = process.get(i);
    		}
    		
    		if(i==process.size()-1)
    		{
    			
        		System.out.print(clock.get(i));
    		}
    	 }
      }
   }
}
