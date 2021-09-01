/*
    Implementation of UserHistory
    Chase Lane cdl52
*/
import java.util.*; 

//same exact node as DLB except we will keep track of how many times a word has been selected by a user
class UHNode {
    private char let;
    //frequency will keep track of how many times a word has been used by user
    //0 for every node except the termination node
    private int frequency;
    private UHNode right;
    private UHNode down;
    public UHNode(char let) {
        this.let = let;
        this.frequency = 1;
        this.right = null;
        this.down = null;
    }
    public char getLet() {
        return let;
    }
    public UHNode getRight() {
        return right;
    }
    public UHNode getDown() {
        return down;
    }
    public void setRight(UHNode r) {
        right = r;
    }
    public void setDown(UHNode d) {
        down = d;
    }
    public void setFreq(int freq){
        frequency = freq;
    }
    public int getFreq() {
        return frequency;
    }
}

//Word Class will be used to suggest the most frequent words to the user
//contains the word and how many times the word has been selected, so it is easily able to sort
class Word {
    private String word;
    private int frequency;

    public Word(String word, int freq){
        this.word = word;
        this.frequency = freq;
    }

    public String getWord(){
        return word;
    }
    public int getFrequency(){
        return frequency;
    }
}

//comparator for the word class that will be used to find the top 5 most frequent words
class WordComparator implements Comparator<Word> {
    public int compare(Word word1, Word word2){
        //if the words have the same frequency we must determine the alphabetical order
        if(word1.getFrequency() == word2.getFrequency()){
            int comparison = word1.getWord().compareTo(word2.getWord()); //negative if before, 0 if same, positive if after alphabetically
            if(comparison < 0){
                return -1;
            }
            if(comparison > 0){
                return 1;
            }
            else{
                return 0;
            }
        }
        else if(word1.getFrequency() < word2.getFrequency()){
            return 1;
        }
        else{
            return -1;
        }

    }
}

public class UserHistory implements Dict{
    
    private UHNode root;
    private int currChar;
    private final char termination = '^';
    private int count;
    private String search = "";

    public UserHistory(){
        root = null;
        count = 0;
    }

    /**
	 * Add a new word to the dictionary
	 *
	 * @param 	key New word to be added to the dictionary
	 */	
	public void add(String key){
        if(key == null) throw new IllegalArgumentException("key to put() is null");

        //if the word is already part of user history
        if(contains(key) == true){
            UHNode keyNode = preFinder(this.root, key, currChar).getDown();
            //now we must find the termination node for this word and incrememnt the frequency
            while(keyNode != null){
                if(keyNode.getLet() == termination){
                    keyNode.setFreq(keyNode.getFreq() + 1);
                }
                keyNode = keyNode.getRight();
            }
            
        }
        //word is not yet part of user history
        else{
            key = key + termination; //add terminate char at the end of our string
            currChar = 0;
            count +=1; //incrementing count to use for count() method
            root = add(this.root, key, currChar);
        }
        return;
    }

    private UHNode add(UHNode root, String key, int currChar){
        //if we finished adding the word return
        if(currChar == key.length()) return null;

        //first check if we are at a null pointer
        if(root == null){
            UHNode newNode = new UHNode(key.charAt(currChar));
            /*at this point, the rest of the string needs to be added on its own branch then appended back on to
            the previous linked list */
            newNode.setDown(add(newNode.getDown(), key, currChar + 1));
            return newNode;
        }
        //we are not at a null pointer
        else{
            //test if we are at the currChar in the key to be added
            if(root.getLet() == key.charAt(currChar)){
                root.setDown(add(root.getDown(), key, currChar + 1));
            }
            //if we are not we must keep going right
            else{
                root.setRight(add(root.getRight(), key, currChar));
                return root;
            }
        }
        return root;
    }


	/** 
	 * Check if the dictionary contains a word
	 *
	 * @param	key	Word to search the dictionary for
	 *
	 * @return	true if key is in the dictionary, false otherwise
	 */
	public boolean contains(String key){
        if(key == null) throw new IllegalArgumentException("key to contains() is null");
        currChar = 0;
        key = key + termination; //add terminate char at the end of our string
        return contains(this.root, key, currChar);
    }

    private boolean contains(UHNode root, String key, int currChar){

        //first we must check if we have traversed the entire key in the dlb and if we did, we know the key is in the dlb
        if(currChar == key.length()) return true;

        //if anytime we reach a null, we know the key is not in the dlb
        if(root == null) return false;

        //loop through the current linked list until the currChar is found
        while(root != null){
            //if we find the currChar in the list, we traverse downwards and repeat the same process
            if(root.getLet() == key.charAt(currChar)){
                return contains(root.getDown(), key, currChar + 1);
            }
            root = root.getRight();
        }
        //if currChar is not found, we have reach a null and return false
        return false;

    }
	/**
	 * Check if a String is a valid prefix to a word in the dictionary
	 *
	 * @param	pre	Prefix to search the dictionary for
	 *
	 * @return	true if prefix is valid, false otherwise
	 */
	public boolean containsPrefix(String pre){
        if(pre == null) throw new IllegalArgumentException("pre to containsPrefix() is null");
        currChar = 0;
        return containsPrefix(this.root, pre, currChar);
    }

    private boolean containsPrefix(UHNode root, String prefix, int currChar){
        //follow the same steps as contain except for when we find the key(prefix) in the dlb
        //we must check if there is another node attached to the key which confirms that this is a prefix
        if(currChar == prefix.length()){
            if(root.getLet() != termination || root.getRight() != null){
                return true;
            }
            else {
                return false;
            }
        }
        if(root == null) return false;
        while(root != null){
            if(root.getLet() == prefix.charAt(currChar)){
                return containsPrefix(root.getDown(), prefix, currChar + 1);
            }
            root = root.getRight();
        }
        return false;
    }

	/**
	 * Search for a word one character at a time
	 *
	 * @param	next Next character to search for
	 *
	 * @return	int value indicating result for current by-character search:
	 *				-1: not a valid word or prefix
	 *				 0: valid prefix, but not a valid word
	 *				 1: valid word, but not a valid prefix to any other words
	 *				 2: both valid word and a valid prefix to other words
	 */
	public int searchByChar(char next){
        search = search + next;
        return searchByChar(this.root, search);
    }

    /**
    *   Helper method which searches for the concatenated version of the string with previous char inputs
    *   @param    root DLB root
    *   @param    search String full of chars to search by
    */
    private int searchByChar(UHNode root, String search){
        boolean containsSearch = contains(search);
        boolean containsPrefix = containsPrefix(search);

        //neither a valid word or prefix
        if(containsSearch == false && containsPrefix == false){
            return -1;
        }
        //not a valid word and valid prefix
        else if(containsSearch == false && containsPrefix == true){
            return 0;
        }
        //valid word but not a valid prefix
        else if(containsSearch == true && containsPrefix == false){
            return 1;
        }
        //valid word and valid prefix
        else{
            return 2;
        }
    }

	/**
	 * Reset the state of the current by-character search
	 */
	public void resetByChar(){
        search = "";
        return;
    }

	/**
	 * Suggest up to 5 words from the dictionary based on the current
	 * by-character search
	 * 
	 * @return	ArrayList<String> List of up to 5 words that are prefixed by
	 *			the current by-character search
	 */
	public ArrayList<String> suggest(){
        //create new arraylist of 'words' whioh contains string word, int freq
        ArrayList<Word> suggest = new ArrayList<Word>();

        currChar = 0;
        //like the DLB start right at the end of the current searchBychar
        UHNode preFinder = preFinder(this.root, search, currChar);

        //if the search doesnt exist in the UH return null
        if(preFinder == null){
            ArrayList<String> emptySuggest = new ArrayList<String>();
            return emptySuggest;
        }
        
        String suggestString = search;
        //now go through all possible words and list the frequencies of the words   
        suggest(suggest, preFinder.getDown(), suggestString);

        //sorting the 'word' arraylist using the new defined comparator
        //this will list the highest frequences at the start of the array
        Collections.sort(suggest, new WordComparator());

        //creating string arraylist for the highest words
        ArrayList<String> highestsuggest = new ArrayList<String>();
        for(int i = 0; i < suggest.size(); i++){
            if(i == 5) break;
            highestsuggest.add(suggest.get(i).getWord());
        }
        return highestsuggest;
    }
    //helper method to find the end of the prefix we are searching for
    private UHNode preFinder(UHNode root, String search, int currChar){
       while(root != null){
            if(root.getLet() == search.charAt(currChar) && currChar == search.length()-1) return root;

            else if(root.getLet() == search.charAt(currChar)){
                root = root.getDown();
                currChar += 1;
            }
            else {
                root = root.getRight();
            }
        }
        return null;
    }
    /**
     * Helper method for suggest which appends to the arraylist suggest
     * @param suggest ArrayList<String> which will contain the 5 words suggested
     * @param preRoot DLBNode that starts right below the end of the current search char 
     * @param search the current searchbychar string
     */
    private void suggest(ArrayList<Word> suggest, UHNode preRoot, String suggestString){
        if(preRoot == null) return;

        if(preRoot.getLet() == termination){
            suggest.add(new Word(suggestString, preRoot.getFreq()));
        }
        suggestString = suggestString + preRoot.getLet();

        //recurse down first, then to the right
        if(preRoot.getDown() != null){
            suggest(suggest, preRoot.getDown(), suggestString);
        }

        if(preRoot.getRight() != null){
            //since we are traversing right, we dont want the previous addition to the string to be added
            suggestString = suggestString.substring(0, suggestString.length()-1); 
            suggest(suggest, preRoot.getRight(), suggestString);
        }
        return;
    }

	/**
	 * List all of the words currently stored in the dictionary
	 * @return	ArrayList<String> List of all valid words in the dictionary
	 */
	public ArrayList<String> traverse(){
        ArrayList<String> traverse = new ArrayList<String>();
        String word = "";
        traverse(traverse, this.root, word);
        return traverse;
    }

    private void traverse(ArrayList<String> traverse, UHNode root, String word){
        //if we are at the termination node we add the current word to the dictionary
        if(root.getLet() == termination){
            traverse.add(word);
        }
        //we keep concatenating the string
        word = word + root.getLet();

        //recurse down first, then to the right
        if(root.getDown() != null){
            traverse(traverse, root.getDown(), word);
        }

        if(root.getRight() != null){
            //since we are traversing right, we dont want the previous addition to the string to be added
            word = word.substring(0, word.length()-1); 
            traverse(traverse, root.getRight(), word);
        }
        return;
    }
	/**
	 * Count the number of words in the dictionary
	 *
	 * @return	int, the number of (distinct) words in the dictionary
	 */
	public int count(){
        //count variable gets appended each time a word gets added
        //count is initialized to 0 when the DLB is created so an empty DLB will have count 0
        return count;
    }


    /**
     * Write the output of UH
     * @return ArrayList full of every word in UH including repetitions of words with freq > 1
     */
    public ArrayList<String> writeUH(){
        ArrayList<String> writeUH = new ArrayList<String>();
        String word = "";
        writeUH(writeUH, this.root, word);
        return writeUH;
    }

    private void writeUH(ArrayList<String> writeUH, UHNode root, String word){

        //if we are at the termination node we add the current word to the dictionary
        if(root.getLet() == termination){
            //for each freq, add another word
            int i = root.getFreq();
            while(i != 0){
                writeUH.add(word);
                i--;
            }
        }
        //we keep concatenating the string
        word = word + root.getLet();

        //recurse down first, then to the right
        if(root.getDown() != null){
            writeUH(writeUH, root.getDown(), word);
        }

        if(root.getRight() != null){
            //since we are traversing right, we dont want the previous addition to the string to be added
            word = word.substring(0, word.length()-1); 
            writeUH(writeUH, root.getRight(), word);
        }
        return;
    }
}
 

