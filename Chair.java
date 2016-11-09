//Chair class

class Chair{
    int playerOccupying;
    boolean myStatus;

    public Chair(boolean status)
    {
	this.myStatus=status;
    }

    public boolean getStatus()
    {
	return this.myStatus;
    }

    public void changeStatus(boolean status)
    {
	this.myStatus=status;
    }



}
