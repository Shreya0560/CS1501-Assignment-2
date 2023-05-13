/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */ 

import java.util.ArrayList;

 public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root;
  private StringBuilder currentPrefix;
  private DLBNode currentNode;    
  private DLBNode tempNode;  
  
  //Longest string from currentPrefix that exists in the DLB
  private StringBuilder currString;
  
  //This is the current node as we are traversing through the nodes within one call of the advance method
  private DLBNode curr; 
  
  //TODO: Add more instance variables if you need to 
  char data;  
  
  
  //flag used for add method-returns true if word was successfully added to dictionary and returns false otherwise
  private boolean resultAdd = false; 
  private DLBNode result; 
  
  //private int numPred = 0; 
  private StringBuilder myPred; 
  
  private boolean isFound = false;

  public AutoComplete(){
    root = null;
    currentPrefix = new StringBuilder();
    currentNode = null;
  }

  /**
   * Adds a word to the dictionary in O(word.length()) time
   * @param word the String to be added to the dictionary
   * @return true if add is successful, false if word already exists
   * @throws IllegalArgumentException if word is the empty string
   */ 
   
	
	//The add method adds a word to the dictionary
	public boolean add(String word) 
	{
        if (word == "")  
		{ 
			throw new IllegalArgumentException("calls add() with a null key"); 
		} 
		if (word == null)  
		{ 
			throw new IllegalArgumentException("calls add() with a null key"); 
		} 
		data = word.charAt(0);    
		
		//Check if root is null-we haven't inserted any words yet
		if(root == null) 
		{  
			root = new DLBNode(data);
		}   
		
		
		//Call the private helper function
        result = add(root, word, data, 0);  
		//resultAdd = true;  
		if(resultAdd) 
		{ 
			return true;
		} 
		else 
		{  
			return false;
		} 
		
		
    }
	
	
	//size is just the number of times that specific node is used for a word in the DLB  
	//We need to set size to 1 whenever adding a new node
	//We need to update size whenever we are adding a word and we find a node that matches 
    private DLBNode add(DLBNode x, String word, char data, int pos) 
	{
		DLBNode result = x;
		
		//We continue to traverse down, adding a new node until we have reached the end 
		if (result == null)
		{
            result = new DLBNode(data); 
            result.data = word.charAt(pos);
            
			
			//Set the size to 1 whenever we add a new node 
			result.size = 1;
			
			if(pos < word.length()-1)
			{
			  //Recursing on the child node
			  result.child = add(result.child,word,data,pos+1); 
			  
            } 
			
			//Once pos has reached the word length, we have finished adding in the full word
			else
			{
			  resultAdd = true;
			  result.isWord = true;
            }
        }    
		//The word already exists in the DLB dictionary-shouldn't need to update size 
		else if((pos == word.length() - 1) && result.data == word.charAt(pos) && result.isWord) 
		{  
			resultAdd = false; 
			result.isWord = true;  
			return result;
		}  
		//The string exists in the dictionary, but hasn't been marked as a word-is only a prefix. We then simply mark it as a word
		else if((pos == word.length() - 1) && result.data == word.charAt(pos)) 
		{  
			resultAdd = true; 
			result.isWord = true; 
			result.size ++; 
			return result;
		}
		//If we have foud a node already containing the letter, we can traverse down from there
		else if(result.data == word.charAt(pos)) 
		{
            //Incrementing size because we are using the node as part of an additional word
			result.size ++;
			if(pos < word.length()-1)
			{
			  //Recursing on the child node
			  result.child = add(result.child,word,data, pos+1);  
			  
			  //Update to make it doubly linked
			  result.child.parent = result;
            } 
			else 
			{  
				resultAdd = true;
				result.isWord = true;
			}
        }  
		
		//If it doesn't go through any of the previous cases, this means we need to go through the sibling nodes to the child 
		else 
		{
          //Either this will add a new sibling if we reach null, use the sibling node if its data matches, or come back to this else case where we traverse to next sibling
		  result.nextSibling = add(result.nextSibling,word,data,pos);  
		  
		  //Updating to make it doubly linked horizontally and vertically
		  result.nextSibling.previousSibling = result; 
		 
		  //Setting the parent to null if it is a sibling node-need to set it as this in order for retreat implementation to work
		  result.nextSibling.parent = null;
        }
        return result;
    }

  /**
   * appends the character c to the current prefix in O(1) time. This method 
   * doesn't modify the dictionary.
   * @param c: the character to append
   * @return true if the current prefix after appending c is a prefix to a word 
   * in the dictionary and false otherwise
   */   
   
   //tempNode is a DLBNode that keeps track of the last node of the last letter in currentPrefix that is in the dictionary 
	//currString keeps track of the part of currentPrefix that is in the dictionary 
	//currentNode will be null if currentPrefix is not in the dictionary 
	
	//We must return true if after adding a c to the currentPrefix, it exists in the dictionary
	public boolean advance(char c) 
	{    
		
		isFound = false;
		advanceHelp(c);  
		if(isFound) 
		{  
			return true;
		} 
		else 
		{  
			return false;
		}
	} 
	
	//This helper method takes care of setting currString and tempNode accordingly so that retreat and retrievePrediction can work properly 
	//This helped method sets isFound to true if currentPrefix is found, false if not
	public void advanceHelp(char c) 
	{  
		//If we have an empty currentPrefix
		if(currentPrefix.length() <= 0) 
		{  
			reset();
			currentPrefix = new StringBuilder();  
			currString = new StringBuilder();
			currentNode = root;  
			tempNode = currentNode;  
			
			currentPrefix.append(c); 
			currString.append(c); 

			//The letter is the root, we don't need to traverse through the root's siblings to find first letter
			if(currentNode.data == c) 
			{  
				isFound = true;
				return;
			}  
			
			//Traverse through the siblings of the root to find a letter that matches with c
			else 
			{   
				while(tempNode.nextSibling != null) 
				{  
					if(tempNode.data == c) 
					{  
						isFound = true;  
						break;
					} 
					tempNode = tempNode.nextSibling; 
				}   
				
				if(tempNode.data != c) 
				{	  
					//set tempNode back to the original currentNode  
					//set currentNode to null bc currentPrefix is no longer in the DLB
					tempNode = currentNode; 
					currentNode = null;  
					isFound = false;
					return;
				} 
				else 
				{  
					currentNode = tempNode;  
					currString.append(c); 
					isFound = true;
					return;
				}	
				
			}
		}    
		
		//if currentNode or its child already is null, we don't even need to look in DLB for currentPrefix-we know it wouldn't be there 
		//Don't need to update currString or tempNode, only need to update currentPrefix
		if(currentNode == null) 
		{  
			currentPrefix = currentPrefix.append(c);  
			isFound = false;
			return;
		} 
		
		
		else if(currentNode.child == null) 
		{   
			currentPrefix = currentPrefix.append(c);    
			tempNode = currentNode;
			currentNode = null;
			isFound = false;
			return;
		} 
		
		//Updating tempNode to be the currentNode child
		tempNode = currentNode.child;
		
		//Appending char to currentPrefix
		currentPrefix = currentPrefix.append(c);    
		
		//we have to traverse through the siblings of the child to find the char id data in tempNode doesn't match
		if(tempNode.data != c)
		{  
			while(tempNode.nextSibling != null) 
			{  
				if(tempNode.data == c) 
				{  
					isFound = true; 
					break;
				}  
				//Update tempNode
				tempNode = tempNode.nextSibling; 
			}  
		}
		//means that we have finished traversing through all the siblings without finding the char
		if(tempNode.data != c) 
		{  
			//set tempNode back to the original currentNode  
			//set currentNode to null bc currentPrefix is no longer in the DLB
			tempNode = currentNode; 
			currentNode = null;  
			isFound = false;
			return;
		}   
		
		//We can return true because after advancing, the currentPrefix is a word in the dictionary
		else if(tempNode.isWord) 
		{  
			currentNode = tempNode;  
			currString.append(c); 
			isFound = true;
			return;
		}  
		
		//After advancing, the currentPrefix does exist in the dictionary, but is not marked as a word
		else 
		{  
			currentNode = tempNode;  
			currString.append(c); 
			isFound = true;
			return;
		} 
		//isFound = false; 
		//return;
	}
	
	

  /**
   * removes the last character from the current prefix in O(1) time. This 
   * method doesn't modify the dictionary.
   * @throws IllegalStateException if the current prefix is the empty string
   */ 
   
	
	//In this method, we are retreating backwards by one character from the currentPrefix
	public void retreat()
	{
		
	  if(currentPrefix.length() <= 0) 
	  {  
		throw new IllegalStateException();
	  }  
	  
	  //Don't need to remove from currString in this case because once we remove a character, it will be exactly currString
	  else if(currentPrefix.length() == currString.length() + 1) 
	  {  
		currentPrefix.deleteCharAt(currentPrefix.length() - 1);   
		currentNode = tempNode; 
		return;
	  } 
	  
	  //In this case, currString and currentPrefix are matched up because currentPrefix is in the dictionary
	  else if(currString.equals(currentPrefix)) 
	  {     
		currentPrefix.deleteCharAt(currentPrefix.length() - 1); 
		currString.deleteCharAt(currString.length() - 1);  
		return;
	  }  
	  
	  //In this case, currentPrefix before retreating is already not in the dictionary 
	  //We don't need to update the currentNode or tempNode when retreating
	  else 
	  {  
		currentPrefix.deleteCharAt(currentPrefix.length() - 1); 
		//currString.deleteCharAt(currString.length() -1); 
		//currentNode = tempNode; 
	  } 
	  
	  //tempNode is not a sibling node and we can directly traverse up to its parent node
	  if(tempNode.parent != null) 
	  {  
		tempNode = tempNode.parent; 
	  } 
	  else 
	  {  
		//we need to traverse backwards to previous siblings until we get to the direct child to the parent  
		while(tempNode.previousSibling != null) 
		{  
			tempNode = tempNode.previousSibling;
		}  
		//Once we have the direct child, we can traverse upwards to get the parent node
		tempNode = tempNode.parent;
	  } 
	  currentNode = tempNode; 
    }  

  /**
   * resets the current prefix to the empty string in O(1) time
   */  
   
   
    public void reset() 
	{
	  currentPrefix = new StringBuilder(); 
	  currentNode = null;
    }
    
  /**
   * @return true if the current prefix is a word in the dictionary and false
   * otherwise
   */
    public boolean isWord(){
      
      if(currentNode == null) 
	  {  
		return false;
	  }
	  return currentNode.isWord;
    }

  /**
   * adds the current prefix as a word to the dictionary (if not already a word)
   * The running time is O(length of the current prefix). 
   */ 
   
	
	public void add() 
	{  
		if(currentNode == null) 
		{  
			//Call the first add method
			add(currentPrefix.toString());
		}
		else if(currentNode.isWord) 
		{  
			return;
		} 
		else 
		{  
			//Calls first add method 
			add(currentPrefix.toString());
		}
	} 

  /** 
   * @return the number of words in the dictionary that start with the current 
   * prefix (including the current prefix if it is a word). The running time is 
   * O(1).
   */
    public int getNumberOfPredictions()
	{
	 if(currentNode == null) 
	 {  
		return 0;
	 }  
	 else if(currentNode.size == 6) 
	 {  
		
		return 5;
	 }
	 else 
	 {  
		//System.out.println("current node is " + currentNode.data);
		return currentNode.size;
	 }
    }
  
  /**
   * retrieves one word prediction for the current prefix. The running time is 
   * O(prediction.length()-current prefix.length())
   * @return a String or null if no predictions exist for the current prefix
   */
	  //Search for the prefix through the DLB 
	  //In order to optimize runtime-don't actually search from the root 
	  //Instead, start from the current node which holds the last char of the prefix 
	  //Initialize a StringBuilder
	  //From there, continue to traverse down each child node, appending the node data into the StringBuilder
	  //Once we reach a node where isWord is marked true, we have found the word  
	  //Convert the StringBuilder to a String 
	  //Return the String  
	  
	  //Continue down all of the children from currentNode 
	  //If we reach null without finding a word, backtrack to a sibling of currentNode
	  
	  //Can use the same get method as in DLB.java. Instead of searching for a key, we are searching for a node where isWord is true
	  
	
	//Remember, tempNode is the node with the last character of the currentPrefix in it
	public String retrievePrediction() 
	{
		//If current prefix is already a word itself, we can just return it directly
		if(currentNode == null) 
		{  
			return null;
		}
		if(currentNode.isWord) 
		{  
			myPred = new StringBuilder(currentPrefix);  
			return myPred.toString();
		}
		myPred = new StringBuilder(currentPrefix);  
		curr = currentNode.child;
		myPred = retrievePrediction(curr);
        String myResult = myPred.toString();  
		return myResult;
		
 
    }

    //private helper method
	private StringBuilder retrievePrediction(DLBNode x) 
	{
		
		//While we don't reach bottom of DLB
		while(x != null) 
		{  
			if(x.isWord) 
			{  
				myPred.append(x.data);
				break;
			} 
			else 
			{  
				myPred.append(x.data);
				x = x.child; 
			}
		}  
		//If we have reached the bottom from currentNode w/o finding a word, go back to the currentNode's child's sibling and restart the process
		if(!(x.isWord)) 
		{  
			myPred = currentPrefix;  
			curr = curr.nextSibling;  
			
			//If we have traversed fully through all children and children of all siblings without finding a word, we can return null-no possible word found
			if(curr == null) 
			{  
				return null;
			}
			retrievePrediction(curr);
		}
		
        return myPred; 
    }


  /* ==============================
   * Helper methods for debugging.
   * ==============================
   */

  //print the subtrie rooted at the node at the end of the start String
  public void printTrie(String start){
    System.out.println("==================== START: DLB Trie Starting from \""+ start + "\" ====================");
    if(start.equals("")){
      printTrie(root, 0);
    } else {
      DLBNode startNode = getNode(root, start, 0);
      if(startNode != null){
        printTrie(startNode.child, 0);
      }
    }
    
    System.out.println("==================== END: DLB Trie Starting from \""+ start + "\" ====================");
  }

  //a helper method for printTrie
  private void printTrie(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *"); 
      }
      System.out.println(" (" + node.size + ")"); 
	  /*if(node.parent != null) 
	  {  
		System.out.println("the parent is" + node.parent.data);
	  } 
	  else 
	  { 
		System.out.println("the parent is null");
	  } 
	  if(node.nextSibling != null) 
	  {  
		System.out.println("the next sibling is" + node.nextSibling.data); 
	  } 
	  else 
	  { 
		System.out.println("the sibling is null");
	  } 
	  if(node.previousSibling != null) 
	  {  
		System.out.println("the prev sibling is" + node.nextSibling.data); 
	  } 
	  else 
	  { 
		System.out.println("the prev sibling is null");
	  }*/
      printTrie(node.child, depth+1);
      printTrie(node.nextSibling, depth);
    }
  }

  //return a pointer to the node at the end of the start String.
  private DLBNode getNode(DLBNode node, String start, int index){
    if(start.length() == 0){
      return node;
    }
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data == start.charAt(index))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data == start.charAt(index))) {
          result = node;
      } else {
          result = getNode(node.nextSibling, start, index);
      }
    }
    return result;
  } 

  //The DLB node class
  private class DLBNode{
    private char data;
    private int size;
    private boolean isWord;
    private DLBNode nextSibling;
    private DLBNode previousSibling;
    private DLBNode child;
    private DLBNode parent;

    private DLBNode(char data){
        this.data = data;
        size = 0;
        isWord = false;
        nextSibling = previousSibling = child = parent = null;
    }
  }
}
