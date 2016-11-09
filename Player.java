//Player Class
/*This is the Threaded class that will keep track of the
"players", their associated states, and will do the checking
of chairs
 */

import java.util.ArrayList;

class Player extends Thread{
    
    ArrayList<Chair> chairArray;
    ArrayList<Player> playerArray;
    ArrayList<Player> roundWinners;
    
    int totalChairs,myState, myId;
    int[]chairsFilledCount;
    int myChair;
    volatile boolean[] gameInfo;
    
    
     /*
      @chairArray=arrayList of Chair objects shared among all threads 
      @playerArray=arrayList of players (static) shared among all threads
      @roundWinners=arrayList of players (static) which is emptied at every round,
         and every round in which a player finds a seat, they add themselves to it
      @myState=int, 0 if I don't have a chair to sit on, 1 if I do
      @totalChairs=int, totalNumber of chairs available to all players
      @myId=int, my thread number
      @chairsFilledCount= array of 1 int, everytime any player gets a chair, we increment that one val
      @gameInfo: boolean array, stores 3 important states:
         gameInfo[0]=round status: (True if round is OVER, false if round is still active)
	 gameInfo[1]=music status: (True if music is playing, false if music has stopped)
	 gameInfo[2]=game status:  (True if entire game is over, false if game is still in play)
     */
    
    public Player(int threadNum, int numChairs,ArrayList<Chair> chairs, int[]chairsCount, boolean[] gameStatus, ArrayList<Player> playersArray, ArrayList<Player> sittingPlayers)
    {
	//see above comment for description on each field
	this.roundWinners=sittingPlayers;
	this.myId=threadNum;
	this.totalChairs=numChairs;
	this.myState=0; 
	this.chairArray=chairs;
	this.chairsFilledCount=chairsCount;
	this.playerArray=playersArray;
	this.gameInfo=gameStatus;
    }

    public boolean allChairsFull()
    {
	//returns true if the amount of chairs that have been taken is
	//equal to the total amount of chairs in play
	return this.chairsFilledCount[0]==this.chairArray.size();
    }
    
    public void run()
    {
	//method executed asynchronously by all threads
	//To do:
	/*
	  Randomly generate a number between 0-totalChairs
	  Check the state of the chair at that index in chairArray (lock it down)
	  If free, take the chair, change its state, increment some global
	  chairsTakenCount, and keep repeating this process until a chair gets taken
	 */
	
	//keep looping until game is over:
	while(this.gameInfo[2]==false)
	    {

		//if music is currently playing OR I already found a chair just chill and do nothing
		//wait for a notification that music has stopped
		if(this.gameInfo[1]==true || this.myState==1)
		    {
			
			synchronized(this.gameInfo)
			    {
				try{
				    //System.out.println("Entering wait");
				    this.gameInfo.wait();}
				catch(InterruptedException e){
				    System.out.println("Interrupted waiting for thread ");}
				
			    }
		    }
		
		//music stopped? FIND A DANG CHAIR YO! CHOP CHOP	
		//randomly wait a little
		try
		    {
			this.sleep((long)((Math.random()*100)));
		    }
		
		catch(InterruptedException e)
		    {
			System.out.println("That shouldn't have happened");
		    }
		
		if(this.gameInfo[2])
		    return; //check if game finished while I was waiting
		//System.out.println("HERE 1");
		while(this.myState==0)
		    {
			//System.out.println("HERE 2");
			//while I don't have a chair
			if(this.allChairsFull())
			    {
				//all chairs taken, and Im still looking for a chair,
				//wait, that means I am dead
				//et tu brutus?
				System.out.printf("Round Loser: P%d\n",this.myID());
				this.playerArray.remove(this);
				//round is over, update gameInfo and alert main thread 
				this.gameInfo[0]=true;
				synchronized(this.gameInfo)
				    {
					this.gameInfo.notifyAll();
				    }
				return; //this means I am the last thread, I lost...what is life...
			    }
			
			//rand index:
			int randIndex=(int)(Math.random()*this.chairArray.size());
			Chair checkChair=this.chairArray.get(randIndex);
			
			//lock the specific chair & check its status
			synchronized(checkChair)
			    {
				boolean isFree=checkChair.getStatus();
				if(isFree)
				    {
					//if it's free take it!
					checkChair.changeStatus(false);
					checkChair.playerOccupying=this.myId;
					this.myState=1; //Houston, I have gotten a chair
					
					synchronized(this.roundWinners)
					    {
						this.roundWinners.add(this); //add myself to roundWinners
					    }
					synchronized(this.chairsFilledCount)
					    {
						this.chairsFilledCount[0]++; //increment chairsFilledCount
					    }
				    }
			    }
			
		    }
	    }
	
    }


    //BELOW: Self explanatory functions
    public int myID()
    {
	return this.myId;
    }
    
    public int seatFound()
    {
	return this.myState;
    }

    public void setSeatFound(int status)
    {
	this.myState=status;
    }
    
}
