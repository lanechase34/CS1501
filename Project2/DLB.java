/* 
    Implementation of DLB for 1501 project 2
    Chase Lane cdl52
*/
package cs1501_p2;
import java.util.ArrayList;

public class DLB implements Dict {

    private DLBNode root;
    private int currChar;
    private final char termination = '^';
    private int count;
    private String search = "";

    public DLB(){
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
        key = key + termination; //add terminate char at the end of our string
        currChar = 0;
        count +=1; //incrementing count to use for count() method
        root = add(this.root, key, currChar);
        return;
    }

    private DLBNode add(DLBNode root, String key, int currChar){
        //if we finished adding the word return
        if(currChar == key.length()) return null;

        //first check if we are at a null pointer
        if(root == null){
            DLBNode newNode = new DLBNode(key.charAt(currChar));
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

    private boolean contains(DLBNode root, String key, int currChar){

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

    private boolean containsPrefix(DLBNode root, String prefix, int currChar){
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
        //currChar in the prefix was not found so return false
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
    private int searchByChar(DLBNode root, String search){
        boolean containsSearch = contains(search);
        boolean containsPrefix = containsPrefix(search);

        //neither a valid word or prefix
        if(containsSearch == false && containsPrefix == false){
            return -1;
        }
        //not a valid word but valid prefix
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
        ArrayList<String> suggest = new ArrayList<String>();
        currChar = 0;
        DLBNode preFinder = preFinder(this.root, search, currChar);
        //if(preFinder == null) return null; 
        String suggestString = search;       
        suggest(suggest, preFinder.getDown(), suggestString);
        return suggest;
    }
    //helper method to find the end of the prefix we are searching for
    private DLBNode preFinder(DLBNode root, String search, int currChar){
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
    private void suggest(ArrayList<String> suggest, DLBNode preRoot, String suggestString){
        //if the suggest arraylist contains 5 words, we return
        if(suggest != null && suggest.size() == 5) return;
        //if the word we are concatenating currently reaches a termination node, we add it
        if(preRoot.getLet() == termination){
            suggest.add(suggestString);
            
        }
        //otherwise we keep concatenating the string
        suggestString = suggestString + preRoot.getLet();

        //recurse down first, then to the right
        if(preRoot.getDown() != null){
            suggest(suggest, preRoot.getDown(), suggestString);
        }

        if(preRoot.getRight() != null){
            //since we are traversing right, so we dont want the previous addition to the string to be added
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

    private void traverse(ArrayList<String> traverse, DLBNode root, String word){
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
            //since we are traversing right, so we dont want the previous addition to the string to be added
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


    // public ArrayList<String> testprint(){
    //     ArrayList<String> testprint = new ArrayList<String>();
    //     String word = "";
    //     testprint(testprint, this.root, word);
    //     return testprint;
    // }

    // private void testprint(ArrayList<String> testprint, DLBNode root, String word){
    //     //if we are at the termination node we add the current word to the dictionary
    //     if(root.getLet() == termination){
    //         word = word + root.getLet();
    //         testprint.add(word);
    //     }
    //     //we keep concatenating the string
    //     word = word + root.getLet();

    //     //recurse down first, then to the right
    //     if(root.getDown() != null){
    //         word = word + "D";
    //         testprint(testprint, root.getDown(), word);
    //     }

    //     if(root.getRight() != null){
    //         //since we are traversing right, so we dont want the previous addition to the string to be added
    //         word = word.substring(0, word.length()-2);
    //         word = word + "R";
    //         testprint(testprint, root.getRight(), word);
    //     }
    //     return;
    // }
}
