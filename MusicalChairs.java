import java.util.ArrayList;

public class MusicalChairs{
    //Main Class
    //Responsible for:
    //Rounds, Checking if all chairs are full
    //Printing (winner, losers of each round)
    //getting rid of a chair each round, and KILLING a player

    public static ArrayList<Player> sitters=new ArrayList<Player>();
    public static ArrayList<Player> playersArray=new ArrayList<Player>();	
    public static ArrayList<Chair> chairs=new ArrayList<Chair>();
    public static int[] chairsFilled={0};
    public static boolean[] roundMusicAndGameStatus={false,true,false};
    
    /**
      @sitters: arrayList Players, players that found a seat in the last round
      @playersArray: arrayList of active players
      @chairs: arrayList of active chairs
      @chairsFilled: array, value of chairs that have been sat on
      @roundMusicAndGameStatus: boolean array, stores 3 important states:
         roundMusicAndGameStatus[0]=round status: (True if round is OVER, false if round is still active)
	 roundMusicAndGameStatus[1]=music status: (True if music is playing, false if music has stopped)
	 roundMusicAndGameStatus[2]=game status:  (True if entire game is over, false if game is still in play)

     */

    
    public static ArrayList<Player> populateArrayList(ArrayList<Player> list, int numOfPlayers, int numOfChairs)
    {
	//populates an ArrayList of players given the number of Players
	/*
	  @list: ArrayList<Player>, arraylist to populate
	  @numOfPlayers: int, number of players to be populated
	  @numOfChairs: int, number of chairs, required for Player constructor
	 */
	
	Player newPlayer=new Player(1,numOfChairs,chairs,chairsFilled, roundMusicAndGameStatus,playersArray,sitters);
	list.add(newPlayer);
	for(int i=1;i<numOfPlayers;i++)
	    {
		//notice we are starting at 1, chair 0 has already been filled and added to list
		list.add(new Player(i+1,numOfChairs,chairs,chairsFilled, roundMusicAndGameStatus,playersArray,sitters));
		
	    }

	return list;
    }

    public static void emptyAllSeats(ArrayList<Chair> chairsList)
    {
	//Changes the status of all chairs in chairList to true (i.e free to sit on)
	for(int i=0;i<chairsList.size();i++)
	    {
		chairsList.get(i).changeStatus(true);
	    }
    }

    public static void emptyAllPlayers(ArrayList<Player> playersList)
    {
	//Changes the status of all players in playersList to 0 (i.e. I need a chair)
	for(int i=0;i<playersList.size();i++)
	    {
		playersList.get(i).setSeatFound(0);
	    }
    }

    
    public static String printSitters(ArrayList<Player> sittersArray)
    {
	//creates a string of players that took a seat in the previous round (given in sittersArray)
	String toReturn="Round Winner(s): ";
	int i=0;

	for(i=0;i<sittersArray.size()-1;i++)
	    {
		toReturn+="P"+sittersArray.get(i).myID()+", ";
	    }
	toReturn+="P"+sittersArray.get(i).myID();
	return toReturn;
    }
    
    public static void main(String[]args)
    {
	int numberOfPlayers;
	
	if(args.length<1)
	    {
		//System.out.println("No number clarified, running with default amount: 10.\n");
		//We need to run default of 10
		numberOfPlayers = 6;
	    
	    }
	else
	    {
		numberOfPlayers = Integer.parseInt(args[0]);
	    }
	
	int numberOfChairs=numberOfPlayers-1;
	int totalRounds=numberOfChairs;
	int winningId=-1;
	/*
	  @numberOfChairs: number of chairs in round
	  @totalRounds: total number of chairs, will always be equal to the number
	    of chairs to begin the game
	  @winningId: the final winning id
	 */
	
	//playersArray contains all players
	//chairs holds all chairs
	for(int i=0;i<numberOfChairs;i++)
	    {
		//create new chairs and append them to the array list
		Chair newChair=new Chair(true);
		chairs.add(newChair);
	    }
	//create & start players
	playersArray=populateArrayList(playersArray,numberOfPlayers,numberOfChairs);
		
	for(int i=0;i<numberOfPlayers;i++)
	    {
		//starting all threads
		playersArray.get(i).start();
		
	    }
	
	for(int round=0;round<totalRounds;round++)
	    {
		System.out.format("Round %d Begins!\nPlayers Left: %d,  Chairs: %d\n",round+1,numberOfPlayers,numberOfChairs);
		roundMusicAndGameStatus[1]=false;//stop music, FIND CHAIRS FOOLS!
		roundMusicAndGameStatus[0]=false;//round is active!
		
		synchronized(roundMusicAndGameStatus)
		    {
			
			    roundMusicAndGameStatus.notifyAll();//tell everyone music has stopped!
		    }

		//while the round is active-do nothing
		while(roundMusicAndGameStatus[0]==false)
		    {
			//just wait for round to finish
			synchronized(roundMusicAndGameStatus)
			    {
				try{
				    //System.out.println("Main Thread entering wait");
				    roundMusicAndGameStatus.wait();}
				catch(InterruptedException e){
				    System.out.println("Interrupted waiting for thread ");}
				}
		    }
	    
		//round is over!
		//plaay the musssiiicc:
		roundMusicAndGameStatus[1]=true;
		
		//check if game is over:
		if(playersArray.size()==1)
		    {
			roundMusicAndGameStatus[2]=true; //update 
			winningId=playersArray.get(0).myID();
		    }

		for(int i=0;i<numberOfChairs;i++)
		    {
			System.out.printf("Chair[%d]: Player %d\n",i,chairs.get(i).playerOccupying);
		    }
		
		//print siiters, set all chairs to active, change status of all players
		//String roundWinnersToPrint=printSitters(sitters);              
		//System.out.println(roundWinnersToPrint);
		
		chairs.remove(0);                     //we have one less chair
		emptyAllSeats(chairs);                //make all chairs free
		numberOfChairs--;                     //one less chair
		chairsFilled[0]=0;                    //reset number of chairs filled
		numberOfPlayers--;                    //one less players
		emptyAllPlayers(playersArray);        //make all players "free"
		
		sitters.removeAll(sitters);           //clear round winners

		System.out.println("\n");
		
	    }
	//game is over pring final winner:
	System.out.printf("***Winner: Thread P%d***\n",winningId);
	System.exit(0);
    }
    
}
