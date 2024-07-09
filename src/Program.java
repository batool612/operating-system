import java.util.*;
import java.io.File;  
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException; 

public class Program implements Comparable {
    HashMap<String, String> memory; //variable,value
    int processID;
    int programCounter;
    int startAddress;
    int endAddress;
    ArrayList<String> instructions;

    public Program(int processID, int programCounter,int startAddress, int endAddress, ArrayList<String> instructions)
    {
       this.processID = processID;
       this.programCounter = programCounter;
       this.startAddress = startAddress;
       this.endAddress = endAddress;
       this.instructions = instructions;
       this.memory = new HashMap<String, String>(); //variable,value
    }
    // readfile 
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

    // Writefile 
    public void writeFile(String file, String value)
    {
        try {
            File myObj = new File(file);
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
              System.out.println("File already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }

          try {
            FileWriter myWriter = new FileWriter(memory.get(file));
            myWriter.write(memory.get(value));
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }
    /// Category 
    public ArrayList<String> categorizeProgram()
    {
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.instructions.size(); i++)
        {
            String category = "";
            String instruction = this.instructions.get(i);
            String word = instruction.split(" ")[0];

            switch(word)
            {
                case "assign": category = "variable_assignments"; break;
                case "writeFile": category = "file_IO"; break;
                case "readFile": category = "file_IO"; break;
                case "print": category = "print_commands"; break;
                case "default": category = "unknown_command"; break;
            }
            if(category.equals("variable_assignments") && instruction.split(" ").length >= 3 ){
              if (instruction.split(" ")[2].equals("add") || instruction.split(" ")[2].equals("multiply") || 
               instruction.split(" ")[2].equals("divide") || instruction.split(" ")[2].equals("subtract"))
               category = "arithmetic_operations";
   
               if (instruction.split(" ")[2].equals("readFile"))
               category = "file_IO";
            }
            res.add(category);
        }
        return res;
    }
     //// Assign 
      /// case of user input and the other case 
    public void assign(String instruction)
    { 
        String value = "";
        if(instruction.split(" ")[2].equals("input"))
        {
            Scanner myObj = new Scanner(System.in);  
            System.out.println("Enter value for variable " + instruction.split(" ")[1]);
            value = myObj.nextLine();
        }
        else
        {
           value = instruction.split(" ")[2];
        }
        
        memory.put(instruction.split(" ")[1],value);
    }
     //// Operation 
    public void operation(String instruction)
    {
        String value = "";
        String operator = instruction.split(" ")[2];
        String operand_1 = instruction.split(" ")[3];
        String value_1 = memory.get(operand_1);
        String operand_2 = instruction.split(" ")[4];
        String value_2 = memory.get(operand_2);
        String res = instruction.split(" ")[1];
        
        switch(operator) 
        {
            case "add": value = String.valueOf(Integer.parseInt(value_1) + Integer.parseInt(value_2)); break;
            case "multiply":value = String.valueOf(Integer.parseInt(value_1) * Integer.parseInt(value_2));break;
            case "divide": value = String.valueOf((double)Integer.parseInt(value_1) / Integer.parseInt(value_2));break;
            case "subtract": value = String.valueOf(Integer.parseInt(value_1) - Integer.parseInt(value_2));break;
            default:break;
        }
        assign("assign " + res + " " + value);
    }

    //assign b readFile a
    //writeFile a b => meaning we write value in b into file name which is in a
    public void file_io(String instruction)
    {
        if(instruction.split(" ")[2].equals("readFile"))
        {
            String res = instruction.split(" ")[1];
            String fileName = memory.get(instruction.split(" ")[3]);
            ArrayList<String> FileData = readFile(fileName);
            String value = String.join(",",FileData);
            assign("assign " + res + " " + value);
        }
        else
        {
          writeFile(instruction.split(" ")[1], instruction.split(" ")[2]);
        }
    }
    
    public String print(String instruction)
    {
        String variable = instruction.split(" ")[1];
        return memory.get(variable);
    }
   
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void incrementPC()
	{
		this.programCounter+=1;
	}
	public static void main(String[]args)
    {
      
       
    }
}
