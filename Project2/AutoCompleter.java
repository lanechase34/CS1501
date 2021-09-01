/*
    Implementation of AutoCompleter
    Chase Lane cdl52
*/
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class AutoCompleter implements AutoComplete_Inter{

    public DLB DLB = new DLB();
    public UserHistory UH = new UserHistory();
    //first constructor accepts dictionary file and user history file
    public AutoCompleter(String dictFile, String UHFile){
        //populate the dictionary by using a scanner
        try( Scanner s = new Scanner(new File(dictFile))){
            while(s.hasNext()) {
                DLB.add(s.nextLine());
            }
        }
        catch (IOException e) {
            System.out.println("Dictionary file not found");
            e.printStackTrace();
        }

        //populate the userhistory
        try( Scanner s = new Scanner(new File(UHFile))){
            while(s.hasNext()) {
                UH.add(s.nextLine());
            }
        }
        catch (IOException e) {
            System.out.println("UserHistory file not found");
            e.printStackTrace();
        }
    }

    //second constructor accepts only a dictionary file
    public AutoCompleter(String dictFile){
        //populate the dictionary by using a scanner
        try( Scanner s = new Scanner(new File(dictFile))){
            while(s.hasNext()) {
                DLB.add(s.nextLine());
            }
        }
        catch (IOException e) {
            System.out.println("Dictionary file not found");
            e.printStackTrace();
        }
        
    }

    /**
	 * Produce up to 5 suggestions based on the current word the user has
	 * entered These suggestions should be pulled first from the user history
	 * dictionary then from the initial dictionary
	 *
	 * @param 	next char the user just entered
	 *
	 * @return	ArrayList<String> List of up to 5 words prefixed by cur
	 */	
	public ArrayList<String> nextChar(char next){
        //append search strings in both structures
        DLB.searchByChar(next);
        UH.searchByChar(next);

        //first retrieve suggestions from userhistory
        ArrayList<String> nextStringsUH = new ArrayList<String>();
        nextStringsUH = UH.suggest();

        //if the nextStrings arraylist already contains 5 words after user history we can just return it
        if(nextStringsUH != null && nextStringsUH.size() == 5){
            return nextStringsUH;
        }

        //second retrieve the suggestions from the DLB
        ArrayList<String> nextStringsDict = new ArrayList<String>();
        nextStringsDict = DLB.suggest();

        //if user history suggestions is empty, we will just return the dictionary suggestions
        if(nextStringsUH.size() == 0){
            return nextStringsDict;
        }
        int nextStringsDict_SIZE = nextStringsDict.size();
        //append the UH suggestions with suggestions from DLB until there are 5
        for(int i = 0; i < 5; i++){
            /* Once the UH suggestions totals 5 we break */
            if(nextStringsUH.size() == 5) break;
            /* We are checking here if there arent 5 total words to be added to the UH suggest from Dict suggest */
            if(i < nextStringsDict_SIZE){
                //if the userhistory suggestions already contains the current word, skip it
                if(!nextStringsUH.contains(nextStringsDict.get(i))){
                    nextStringsUH.add(nextStringsDict.get(i));
                }
            }
        }
        return nextStringsUH;
    }

	/**
	 * Process the user having selected the current word
	 *
	 * @param 	cur String representing the text the user has entered so far
	 */
	public void finishWord(String cur){
        UH.add(cur);
        UH.resetByChar();
        DLB.resetByChar();
    }

	/**
	 * Save the state of the user history to a file
	 *
	 * @param	fname String filename to write history state to
	 */
	public void saveUserHistory(String fname){
        //first create the new file
        try{
            File UHOutputFile = new File(fname);
            UHOutputFile.createNewFile();

             //creating the file writer
            try{
                FileWriter writer = new FileWriter(UHOutputFile);

                ArrayList<String> UHOutputArray = new ArrayList<String>();
                //calling write User history output file which populates an arraylist
                UHOutputArray = UH.writeUH();
                
                //writing this populated arraylist to the file
                for(int i = 0; i < UHOutputArray.size(); i++){
                    String s  = UHOutputArray.get(i) + "\n";
                    writer.write(s);
                }
                writer.close();
            }catch (IOException e) {
                System.out.println("Error creating User History File Writer");
                e.printStackTrace();
            }       
        }catch (IOException e) {
            System.out.println("Error creating new UserHistory output file");
            e.printStackTrace();
        }

        
    }   
}
