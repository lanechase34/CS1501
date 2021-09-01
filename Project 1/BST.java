/*
    Binary Search Tree implementation by cdl52
*/
package cs1501_p1;

public class BST<T extends Comparable<T>> implements BST_Inter<T> {

    private BTNode<T> root;
	private int direction; //this is to help with delete method
	private String s;
	private String temp;

	public BST(){
		root = null;
	}

    public void put(T key)
    {
		// if(key == null){
		// 	throw new IllegalArgumentException("arguments to put == NULL");
		// }
		root = put(this.root, key);	
		return;
	}
    /**
	 * Private recursive helper method for put
	 * @param	Root method always starts at the root
	 * @param	key value to be added to BST
	 */
	private BTNode<T> put(BTNode<T> currNode, T key)
    {
		//case 1 we are at an empty node
		if(currNode == null){
			BTNode<T> newNode = new BTNode<T>(key);
			newNode.setLeft(null);
			newNode.setRight(null);
			return newNode;
		}

		//case 2 we must compare the values of the current node to the key in order to determine which way to traverse the tree
		int cmp = key.compareTo(currNode.getKey());
		
		//if cmp <0 we traverse left, and cmp>0 traverse right
		if(cmp < 0){
			currNode.setLeft(put(currNode.getLeft(), key));
		}
		else if(cmp > 0){
			currNode.setRight(put(currNode.getRight(), key));
		}
			return currNode;
		
	}

    public boolean contains(T key)
	{
		return contains(this.root, key);
	}
    /**
	 * Contains helper method which traverses tree until the key is found or we reach a null
	 * @param	root method always starts at root of tree
	 * @param	key Generic type value to look for in the BST
	 * @return true if key is in the tree, false otherwise
	 */
	private boolean contains(BTNode<T> currNode, T key)
	{
		if(currNode == null) return false;

		//if cmp<0 traverse left, if cmp>0 traverse right, else cmp=0 which signifies a hit
		int cmp = key.compareTo(currNode.getKey());
		if(cmp == 0) return true;
		if(cmp < 0)
		{
			return contains(currNode.getLeft(), key);
		}
		return contains(currNode.getRight(), key);
	}

    public void delete(T key)
	{
		deleteHelper(this.root, key);

        return;
    }
	/**
	 * Remove a key from the BST, if key is present
	 * 
	 * @param	key Generic type value to remove from the BST
	 * @return true if key is delete, false if key is not present
	 */
	private boolean deleteHelper(BTNode<T> root, T key)
	{
		//first step of delete is to get pointer to parent node of the node to be deleted
		//for this we call the helper method findParentNode
		BTNode<T> curr = findParentNode(this.root, key);

		//Direction keeps track of where the node to be delete is, 0 if the node is not there, 1 to the left and -1 to the right
		if(direction == 0) return false; //key is not present

		//case 1: root remove
		if(root.getLeft() == null && root.getRight() == null && root.getKey().equals(key)){
			root = null;
			return true;
		}

		//case 2: there are 2 or more children of the node to be deleted
		//case 2.1: to the left
		if(direction == -1 && countChildren(curr.getLeft()) == 2){
			BTNode<T> deadNode = curr.getLeft(); //this is the node to be deleted
			curr = curr.getLeft();
			curr = curr.getLeft();

			while(curr.getRight() != null)
			{
				curr = curr.getRight();
			}

			T data = curr.getKey();
			delete(data); //remove the leaf node we are replacing at the deadnode
			deadNode.setKey(data);
			return true;
		}
		//case 2.2: to the right
		if(direction == 1 && countChildren(curr.getRight()) == 2){
			BTNode<T> deadNode = curr.getRight();
			curr = curr.getRight();
			curr = curr.getLeft();

			while(curr.getRight() != null)
			{
				curr = curr.getRight();
			}

			T data = curr.getKey();
			delete(data);
			deadNode.setKey(data);
			return true;
		}

		//case 3: there is 1 child of the node to be deleted
		//case 3.1: the left node of the parent has only 1 child
		if(direction == -1 && countChildren(curr.getLeft()) == 1){
			BTNode<T> deadNode = curr.getLeft();
			if(deadNode.getLeft() != null){
				curr.setLeft(curr.getLeft().getLeft());
			}
			else{
				curr.setLeft(curr.getLeft().getRight());
			}
			return true;
		}
		//case 3.2: the right node of the parent has only 1 child
		if(direction == 1 && countChildren(curr.getRight()) == 1){
			BTNode<T> deadNode = curr.getRight();
			if(deadNode.getLeft() != null){
				curr.setRight(curr.getRight().getLeft());
			}
			else{
				curr.setRight(curr.getRight().getRight());
			}
			return true;
		}

		//case 4: leaf node remove
		if(direction == -1 && curr.getLeft().getKey().equals(key)){
			curr.setLeft(null);
			return true;
		}
		if(direction == 1 && curr.getRight().getKey().equals(key)){
			curr.setRight(null);
			return true;
		}
		return false;
	}	
    
	/**
	 * Helper method to get reference to the parent node of the node to be delted
	 * @param root root of BST
	 * @param key key to find in the child node
	 * @return parent node
	 */
	private BTNode<T> findParentNode(BTNode<T> root, T key)
	{
		BTNode<T> curr = root;
		
		//case 1: the node to be removed is the root
		if(root.getKey().equals(key)){
			return curr;
		}

		//case 2: we must traverse the tree to find the node to delete
		while(curr != null)
		{
			int cmp = key.compareTo(curr.getKey());

			//case 2.1: check the left for key
			if(curr.getLeft() != null && curr.getLeft().getKey().equals(key)){
				direction = -1;
				return curr;
			}

			//case 2.2: check the right for key
			if(curr.getRight() != null && curr.getRight().getKey().equals(key)){
				direction = 1;
				return curr;
			}

			//case 2.3: haven't found the key yet, so traverse 1 deeper in tree
			if(cmp > 0){
				curr = curr.getRight();
			}
			else{
				curr = curr.getLeft();
			}
		}

		//node not found to delete
		direction = 0;
		return null;
	}

	/**
	 * Helper method to calculate the number of children a node has
	 * Possible return values include 0, 1, and 2
	 * @param currChild some node where the # of children nodes needs to be calculated
	 * @return the number of children a node has
	 */
	private int countChildren(BTNode<T> currChild){
		int children = 0;
		if(currChild.getLeft() != null) children++;
		if(currChild.getRight() != null) children++;
		return children;
	}

    public int height()
	{
		return countHeight(this.root);
	}
    /**
	 * Helper method to count the maximum height of tree
	 * Also will count the maximum number of children of a specific node
	 * @param root
	 * 
	 * @return maximum height of tree
	 */
    private int countHeight(BTNode<T> root)
	{
		if(root == null) return 0;
		//Math.max calculates the maximum value between the two inputs and thus will result in teh longest traversal down the tree
		return 1 + Math.max(countHeight(root.getLeft()), countHeight(root.getRight()));
	}

	/**
	 * Determine if the BST is height-balanced
	 *
	 * <p>
	 * A height balanced binary tree is one where the left and right subtrees
	 * of all nodes differ in height by no more than 1.
	 *
	 * @return true if the BST is height-balanced, false if it is not
	 */
    public boolean isBalanced()
	{
        return isBalanced(this.root);
    }

	/**Helper method for isBalanced
	 * @param	takes root of tree to analyze to determine if balanced
	 * @return true if the BST is height-balanced, false if it is not
	 */
	private boolean isBalanced(BTNode<T> root)
	{
		int leftTree;
		int rightTree;
		if(root == null) return true;

		leftTree = countHeight(root.getLeft());
		rightTree = countHeight(root.getRight());

		if(Math.abs(leftTree-rightTree) <= 1 && isBalanced(root.getLeft()) && isBalanced(root.getRight())){
			return true;
		}
		return false;
	}


    public String inOrderTraversal()
	{
		s = "";
		inOrderTraversal(this.root);
		s = s.substring(0, s.length() - 1);
		return s;
	}
    /**
	 * In order traversal follows L -> read key -> R which is what the recursive method below does
	 * At each stopping point, the next key is appending onto string s
	 * @param root
	 * @param s
	 */
	private void inOrderTraversal(BTNode<T> root)
    {
		if(root != null){
			//first step is to traverse all the way left on the tree
			inOrderTraversal(root.getLeft());

			//add the node to our string
			s = s + root.getKey() + ":";

			//then procede to the right
			inOrderTraversal(root.getRight());
		}
		
	}

    public String serialize()
	{
		s = "";
		serialize(this.root, this.root);
		s = s.substring(0, s.length() - 1);
        return s;
    }

	/**
	 * Serialize follows a pre order traversal of the bst while following these rules
	 * R: root of tree, I: an interior node of the tree (ex not a root not a leaf), L: leaf node, X: stand-in for a null reference
	 * For each node, the left child is listed first then its right 
	 * @param root	root of bst
	 * @return s	returns serialized string of bst
	 */
	private void serialize(BTNode<T> root, BTNode<T> parentNode)
	{
		int directionS;
		//concactinating the string
		if(root != null)
		{
			String currKey = "";
			//if s is empty we must fill the root node
			if(s.isEmpty())
			{
				temp ="R";
				currKey = "" + root.getKey();
				s = s + temp + "(" + currKey + ")" + ",";
			}
			//node is an interior node if it has 1 or 2 children
			else if(countChildren(root) >= 1)
			{
				temp = "I";
				currKey = "" + root.getKey();
				s = s + temp + "(" + currKey + ")" + ",";
			}

			//node is a leaf if it has no children
			else if(countChildren(root) == 0)
			{
				//this is to determine if whether the X(NULL) comes before or after the node depending on if the child is to the left or right
				//If child is on the left it follows I(Parent Node), L(Child Node), X(NULL)
				//if child is ont he right it follows I(Parent Node), X(NULL), L(Child Node)
				if(parentNode.getLeft() != null){
					directionS = -1; //1 if to the right
				}
				else{
					directionS = 1;
				}

				if(countChildren(parentNode) == 1 && directionS == 1)
				{
					temp = "X";
					currKey = "NULL";
					s = s + temp + "(" + currKey + ")" + ",";
				}

				temp = "L";
				currKey = "" + root.getKey();
				s = s + temp + "(" + currKey + ")" + ",";

				if(countChildren(parentNode) == 1 && directionS == -1)
				{
					temp = "X";
					currKey = "NULL";
					s = s + temp + "(" + currKey + ")" + ",";
				}
			}
			//pre order follows do work, left, then finally right
			serialize(root.getLeft(), root);
			serialize(root.getRight(), root);
			
			
		}
	}
	/**
	 * Produce a deep copy of the BST that is reversed (i.e., left children
	 * hold keys greater than the current key, right children hold keys less
	 * than the current key).
	 *
	 * @return	Deep copy of the BST reversed
	 */
	public BST_Inter<T> reverse()
	{
		BST<T> revTree = new BST<T>();
		revTree.put(this.root.getKey());
		revTree = makeCopy(revTree, this.root);
		reverse(revTree.root);
		return revTree;
    }

	/**
	 * Helper method to deep copy the original tree
	 * @param revTree new tree to fill
	 * @param oldRoot old tree to copy values from
	 * @return full new tree that is exactly the same as the oldtree in value
	 */
	private BST<T> makeCopy(BST<T> revTree, BTNode<T> oldRoot)
	 {
	 	if(oldRoot != null){
	  		revTree.put(oldRoot.getKey());
	  		makeCopy(revTree, oldRoot.getLeft());
	  		makeCopy(revTree, oldRoot.getRight());
			
	  	} return revTree;
	 }

	/**
	 * Helper method to reverse the values inside a tree
	 * @param revTreeRoot takes root of tree to reverse
	 */
	private void reverse(BTNode<T> revTreeRoot)
	{
		if(revTreeRoot == null || countChildren(revTreeRoot) == 0) return;
		
		//creating new temp node for children flip
		BTNode<T> temp = new BTNode<T>(null);
		if(revTreeRoot.getRight() != null){
	 		temp.setKey(revTreeRoot.getRight().getKey());
			temp.setLeft(revTreeRoot.getRight().getLeft());
			temp.setRight(revTreeRoot.getRight().getRight());
		}else temp = null;
		//flip the children of the node we are currently at
	 	revTreeRoot.setRight(revTreeRoot.getLeft());
	 	revTreeRoot.setLeft(temp);
		
		//traverse further down the tree
		if(revTreeRoot.getLeft() != null){
			reverse(revTreeRoot.getLeft());
		}
		if(revTreeRoot.getRight() != null){
			reverse(revTreeRoot.getRight());
		}
		return;
	}

}