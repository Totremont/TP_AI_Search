

package amongus.models;


public class CrewMemberState {
    
    private final CrewMember crew;
    private Boolean isAlive;
    private Room currentRoom;
    //Cuándo se movió por última vez
    private Long lastMoveTime;

    public CrewMemberState(CrewMember crew, Room room, Long createTime) {
        this.crew = crew;
        this.crew.setState(this);
        this.currentRoom = room;
        this.isAlive = true;
        this.lastMoveTime = createTime;
    }
    
    public void setCurrentRoom(Room currentRoom) 
    {
        this.currentRoom = currentRoom;
    }

    public void setIsAlive(Boolean isAlive) 
    {
        this.isAlive = isAlive;
    }
    
    public void setLastMoveTime(Long lastMove) {
        this.lastMoveTime = lastMove;
    }

    public CrewMember getCrew() {
        return crew;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Boolean isAlive() {
        return isAlive;
    }

    public Long getLastMoveTime() {
        return lastMoveTime;
    }
    
    //Constructor especial para clonar
    public CrewMemberState(CrewMember crew, Room room, Long lastMoveTime, Boolean isAlive, Boolean copyFlag) 
    {
        this.crew = crew;
        //this.crew.setState(this);
        this.currentRoom = room;
        this.isAlive = isAlive;
        this.lastMoveTime = lastMoveTime;
    } 

    @Override
    public CrewMemberState clone()
    {
        CrewMemberState newState = new CrewMemberState(this.crew,this.currentRoom,this.lastMoveTime, this.isAlive,true);
        return newState;
    }
    
    
    
    
    
    
    
    
    
    

}
