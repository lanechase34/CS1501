/*
    Implementation of indirection using a dlb
    Chase Lane
*/

package cs1501_p3;
//import java.util.ArrayList;

class DLBNode{
    private boolean valid;
    private DLBNode right;
    private DLBNode down;
    private char let;
    //location keeps track of the specific VIN location in the PQ
    //possible values are 0 to count-1
    private int location;

    //each termination node will hold the car object to make finding the locations
    //of specific make and models in the respective PQ much faster
    private Car car;

    public DLBNode(char let){
        this.let = let;
        this.valid = true;
        this.down = null;
        this.right = null;
        this.location = -1;
        this.car = null;
    }
    public char getLet(){
        return this.let;
    }
    public boolean getValid(){
        return this.valid;
    }
    public DLBNode getDown(){
        return this.down;
    }
    public DLBNode getRight(){
        return this.right;
    }
    public int getLocation(){
        return this.location;
    }
    public Car getCar(){
        return this.car;
    }
    public void setValid(boolean v){
        valid = v;
    }
    public void setDown(DLBNode d){
        down = d;
    }
    public void setRight(DLBNode r){
        right = r;
    }
    public void setLocation(int l){
        location = l;
    }
    public void setCar(Car c){
        car = c;
    }
}

public class DLB {

    private DLBNode root;
    private int currChar;
    private final char termination = '^';
    private int index = 0;

    public DLB(){
        root = null;
    }

    public void add(String VIN, Car c){

        if(VIN == null) throw new IllegalArgumentException("VIN to add to DLB is null");
        if(c == null) throw new IllegalArgumentException("Car to add to DLB is null");
        //basic steps to make methods of a DLB easier to implement
        VIN = VIN + termination;
        currChar = 0;
        DLBNode parentNode = null;

        root = add(this.root, VIN, currChar, parentNode, c);
        return;
    }

    private DLBNode add(DLBNode root, String VIN, int currChar, DLBNode parentNode, Car car){
        //if we finished adding the VIN, we must update the parent node (termination node), to contain information pertaining to this VIN
        if(currChar == VIN.length()){
                //this is to make sure if we remove the car then add it back in, it will be valid again
                parentNode.setValid(true);
                //now we must assign the car object the termination node (parent node)
                parentNode.setCar(car);
            return null;
        } 

        //first check if we are at a null pointer
        if(root == null){
            DLBNode newNode = new DLBNode(VIN.charAt(currChar));
            /*at this point, the rest of the string needs to be added on its own branch then appended back on to
            the previous linked list */
            newNode.setDown(add(newNode.getDown(), VIN, currChar + 1, newNode, car));
            return newNode;
            
        }

        //we are not at a null pointer
        else{
            //test if we are at the currChar in the key to be added
            if(root.getLet() == VIN.charAt(currChar)){
                root.setDown(add(root.getDown(), VIN, currChar + 1, root, car));
            }
            //if we are not we must keep going right
            else{
                root.setRight(add(root.getRight(), VIN, currChar, root, car));
                return root;
            }
        }
        return root; 
    }

    public boolean contains(String VIN){
        if(VIN == null) throw new IllegalArgumentException("VIN to dlb contains() is null");
        VIN = VIN + termination;
        currChar = 0;
        DLBNode parentNode = null;
        return contains(this.root, VIN, currChar, parentNode);
    }

    private boolean contains(DLBNode root, String VIN, int currChar, DLBNode parentNode){
        //first we must check if we have traversed the entire key in the dlb and if we did, we know the key is in the dlb
        if(currChar == VIN.length() && parentNode.getValid() == true) return true;

        //if anytime we reach a null, we know the key is not in the dlb
        if(root == null) return false;
        //loop through the current linked list until the currChar is found
        while(root != null){
            //if we find the currChar in the list, we traverse downwards and repeat the same process
            if(root.getLet() == VIN.charAt(currChar)){
                return contains(root.getDown(), VIN, currChar + 1, root);
            }
            root = root.getRight();
        }
        //if currChar is not found, we have reach a null and return false
        return false;
    }

    /**
     * Removes a specified VIN from DLB
     * Accomplishes this by setting the termination node(of the VIN), valid to false
     * @param VIN specified VIN that we want removed
     */
    public void remove(String VIN){
        if(VIN == null) throw new IllegalArgumentException("VIN to dlb remove() is null");
        VIN = VIN + termination;
        currChar = 0;
        DLBNode parentNode = null;
        remove(this.root, VIN, currChar, parentNode);
    }

    private void remove(DLBNode root, String VIN, int currChar, DLBNode parentNode){
        if(currChar == VIN.length()){
            parentNode.setValid(false);
            return;
        }
        if(root == null) return;
        while(root != null){
            if(root.getLet() == VIN.charAt(currChar)){
                remove(root.getDown(), VIN, currChar + 1, root);
            }
            root = root.getRight();
        }
        return;
    }

    /**
     * Updates the location value of the VIN to match where it is in the PQ
     * @param VIN VIN to update the location value of
     * @param PQLocation What to update the location value to
     */
    public void update(String VIN, int PQLocation){
        if(VIN == null) throw new IllegalArgumentException("VIN to dlb remove() is null");
        VIN = VIN + termination;
        currChar = 0;
        DLBNode parentNode = null;
        update(this.root, VIN, currChar, parentNode, PQLocation);
    }

    private void update(DLBNode root, String VIN, int currChar, DLBNode parentNode, int PQLocation){
        if(currChar == VIN.length()){
            parentNode.setLocation(PQLocation);
            return;
        }
        if(root == null) return;
        while(root != null){
            if(root.getLet() == VIN.charAt(currChar)){
                update(root.getDown(), VIN, currChar + 1, root, PQLocation);
            }
            root = root.getRight();
        }
        return;
    }

    public int getLocation(String VIN){
        if(VIN == null) throw new IllegalArgumentException("VIN to dlb get location() is null");
        VIN = VIN + termination;
        currChar = 0;
        DLBNode parentNode = null;
        return getLocation(this.root, VIN, currChar, parentNode);
    }

    private int getLocation(DLBNode root, String VIN, int currChar, DLBNode parentNode){
        if(currChar == VIN.length() && parentNode.getValid() == true){
            return parentNode.getLocation();
        }
        if(root == null) return -1;
        while(root != null){
            if(root.getLet() == VIN.charAt(currChar)){
                return getLocation(root.getDown(), VIN, currChar + 1, root);
            }
            root = root.getRight();
        }
        return -1;
    }

    public Car[] traverse(String make, String model, int count){
        //if every car in the PQ is of the same make and model it would have max size count
        Car makeModelArr[] = new Car[count];
        this.index = 0;
        traverse(this.root, makeModelArr, make, model);
        return makeModelArr;
    }

    /**
     * Traverse will traverse the DLB looking for cars with the specific make and model then return them in an array
     * @param root root of DLB
     * @param makeModelArr array to be populated with cars of make and model
     * @param make make of the car
     * @param model model of the car
     * @param i index of the array we are currently at (always starts at 0)
     */
    private void traverse(DLBNode root, Car makeModelArr[], String make, String model){
        if(root.getLet() == termination){
            if(root.getCar().getMake().equals(make) && root.getCar().getModel().equals(model)){
            //add the car to the makeModelArr
            makeModelArr[index] = root.getCar();
            this.index += 1;
            }
        }
        //recurse down first, then to the right
        if(root.getDown() != null){;
            traverse(root.getDown(), makeModelArr, make, model);
        }

        if(root.getRight() != null){
            traverse(root.getRight(), makeModelArr, make, model);
        }
        return;
    }

    //test methods below
    
    // public ArrayList<String> testprint(){
    //     ArrayList<String> testprint = new ArrayList<String>();
    //     String word = "";
    //     testprint(testprint, this.root, word);
    //     return testprint;
    // }   

    // private void testprint(ArrayList<String> testprint, DLBNode root, String word){
    //     //if we are at the termination node we add the current word to the dictionary
    //     if(root.getLet() == termination){
    //         String temp = "";
            
    //         temp = word + " " + root.getLocation() + " " + "(" + root.getValid() + ")";
    //         if(root.getCar() == null){
    //             temp = temp + "NULL";
    //         }
    //         else{
    //             temp = temp + " " + root.getCar().getMake() + ":" + root.getCar().getModel();
    //         }
            
    //         testprint.add(temp);
    //     }
    //     //we keep concatenating the string
    //     word = word + root.getLet();

    //     //recurse down first, then to the right
    //     if(root.getDown() != null){;
    //         testprint(testprint, root.getDown(), word);
    //     }

    //     if(root.getRight() != null){
    //         //since we are traversing right, so we dont want the previous addition to the string to be added
    //         word = word.substring(0, word.length()-1);
    //         testprint(testprint, root.getRight(), word);
    //     }
    //     return;
    // }
}
