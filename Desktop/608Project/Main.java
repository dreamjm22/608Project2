import storageManager.Disk;
import storageManager.MainMemory;
import storageManager.SchemaManager;
import parser.ParseTree;
import parser.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


// null problem
// select string "A" or A
// delete from where


public class Main {
    private static final String EXIT = "Exit";

    public static void main(String[] args) {
        Disk disk = new Disk();
        MainMemory mainMemory = new MainMemory();
        SchemaManager schemaManager = new SchemaManager(mainMemory,disk);
        ParseTree parseTree = new ParseTree();
        ParserOperator parserOperator = new ParserOperator(disk, mainMemory, schemaManager);
        boolean continueFlag = true;
        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================Welcome to TinySQL Program=====================");
        System.out.println("=====================(This is CSCE 608 Project 2)=====================");
        System.out.println("Type in 'Exit' to quit anytime");
        System.out.println("Please choose the following options:");
        System.out.println("A. Run SQL command from file");
        System.out.println("B. Run SQL command from your input (multiple lines acceptable)");

        while (continueFlag) {
            String userInput = scanner.nextLine();
            // String userInput = "A";
            switch (userInput) {
                case "A":
                    System.out.println("=====================Running SQL command from file=====================");
                    System.out.println("Please input file name:");
                    String fileName = scanner.nextLine();
                    // String fileName = "1.txt";
                    while(!fileName.equals(EXIT)) {
                        File file = new File(fileName);
                        List<String> SQLCommandList = fileReader(file);
                        if (SQLCommandList == null) {
                            System.out.println("Please input the correct file name:");
                            break;
                        }
                        for (String SQLCommand : SQLCommandList) {
                            System.out.println();
                            System.out.println(SQLCommand);
                            Node parentNode = parseTree.parse(SQLCommand);
                            parserOperator.parserOperatorProcessor(parentNode);
                        }
                        System.out.println("=====================Finished=====================");
                        System.out.println("Please input file name:");
                        fileName = scanner.nextLine();
                    }
                    if (fileName.equals(EXIT)) {
                        continueFlag = false;
                    }
                    break;
                case "B":
                    System.out.println("=====================Running SQL command from command lines=====================");
                    System.out.println("Please input SQL command:");
                    String SQLCommand = scanner.nextLine();
                    while(!SQLCommand.equals(EXIT)) {
                        Node parentNode = parseTree.parse(SQLCommand);
                        parserOperator.parserOperatorProcessor(parentNode);
                        System.out.println("=====================Finished=====================");
                        System.out.println("Please input SQL command:");
                        SQLCommand = scanner.nextLine();
                    }
                    if (SQLCommand.equals(EXIT)) {
                        System.exit(0);
                    }
                    break;
                case EXIT:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown user choice. Please choose again:");
            }
        }
    }

    private static List<String> fileReader(File file) {
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            List<String> fileLinesList=new ArrayList<String>();
            String fileLine = bufferedReader.readLine();
            while(fileLine != null) {
                fileLinesList.add(fileLine);
                fileLine = bufferedReader.readLine();
            }
            bufferedReader.close();
            return fileLinesList;
        } catch (IOException exception) {
            System.out.println("Encountered exception during open File");
            System.out.println(exception.getMessage());
            return null;
        }
    }
}
