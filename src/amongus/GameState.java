
package amongus;

import amongus.models.CrewMember;
import amongus.models.CrewMemberState;
import amongus.models.Room;
import amongus.models.RoomState;
import amongus.models.Sabotage;
import amongus.utils.Utils;
import frsf.cidisi.faia.state.EnvironmentState;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

//El GameState contiene el estado completo del juego
public class GameState extends EnvironmentState 
{
    private static Game environment;
    
    //Atributos del mundo
    private static HashMap<String,Room> map;
    private static HashMap<String,Sabotage> sabotages; 
    private static HashMap<String, CrewMember> crews = new HashMap<>();
    private final List<RoomState> roomStates = new ArrayList<>();  
    private final List<CrewMemberState> crewStates = new ArrayList<>();   
    private Long gameTime;
    
    //Atributos del agente
    private Room agentRoom;
    private Long agentEnergy;
    //private boolean agentSensorAvail; 
    private Long agentSensorLastTime; //Cuando estuvo activo por última vez
    
    //Se activa cuando el juego debe darle información extrasensorial al agente (en la sgte percepción)
    private boolean omniscientAgent;

    public GameState(Game environment) 
    {
        //Setear información estática
        GameState.environment = environment;
        GameState.map = GameState.environment.map;
        GameState.sabotages = GameState.environment.sabotages;
        
        //Setear estado inicial del juego
        this.initState();
    }
    
    
    @Override
    public void initState() 
    {
        //Tiempo inicial
        this.gameTime = 0l;
        
        //Energía del agente, cantidad de tripulantes, etc...
        Long agentEnergy = Utils.randomBetween.apply(environment.MAX_ENERGY,environment.MIN_ENERGY);
        Long totalCrew = Utils.randomBetween.apply(environment.MAX_CREW,environment.MIN_CREW);
        
        boolean agentExtraSensor = Math.round(Math.random()) == 0;   //Sensor activado inicialmente?
        
        List<String> roomNames = new ArrayList<>();    
        map.forEach((key,val) -> 
        {
            roomNames.add(key);               
            roomStates.add(new RoomState(val)); //Crear el initial state de cada ambiente
        });
        
        //Estado inicial del agente
        int agentInitialIndex = Utils.randomBetween.apply(map.size() - 1,0).intValue();
        String agentRoomName = roomNames.get(agentInitialIndex);
        
        this.agentRoom = map.get(agentRoomName);
        this.agentRoom.getState().setAgentPresent(true);
        this.agentEnergy = agentEnergy;
        this.omniscientAgent = agentExtraSensor;
        this.agentSensorLastTime = 0l;
        
        //Distribuir tripulantes en el mapa
        for(int i = 0; i < totalCrew; i++)
        {
            
            int crewInitialIndex = Utils.randomBetween.apply(map.size() - 1,0).intValue();
            String crewRoomName = roomNames.get(crewInitialIndex);            
            Room crewRoom = map.get(crewRoomName);  
            
            CrewMember crew = new CrewMember(i);
            CrewMemberState crewState = new CrewMemberState(crew,crewRoom,this.gameTime);
            crewRoom.getState().addMember(crew);
            crewStates.add(crewState);
            crews.put(crew.getName(),crew);
        }  
    } 
    
    /*-- 
        Setters que permiten modificar el estado del juego. Usado por acciones del agente y el WorldAction
        Nota: SOLO GameState se encarga de modificar el estado del juego. 
    */
    
    public void setAgentRoom(String newAgentRoom) 
    {
        RoomState currentRoomState = getAgentRoom().getState();
        currentRoomState.setAgentPresent(false);
        
        this.agentRoom = map.get(newAgentRoom);
        this.agentRoom.getState().setAgentPresent(true);
       
    }

    public void setAgentEnergy(Long agentEnergy) 
    {
        this.agentEnergy = agentEnergy;
    }

    public void addCrewKilled(String name)
    {
        CrewMember crew = GameState.crews.get(name);
        crew.getState().setIsAlive(false);
        crew.getState().getCurrentRoom().getState().deleteMember(crew);
    }
    
    public void setCrewRoom(String crewName, String roomName, Long gameTime)
    {
        CrewMember crew = GameState.crews.get(crewName);
        Room newRoom = GameState.map.get(roomName);
        crew.getState().getCurrentRoom().getState().deleteMember(crew);
        crew.getState().setCurrentRoom(newRoom);
        newRoom.getState().addMember(crew);
        crew.getState().setLastMoveTime(gameTime);
    }
    
    public void removeSabotage(String name) //Cuando se completa un sabotaje
    {
        GameState.sabotages.get(name).getRoom().getState().setIsSabotable(false);
    }

    /* Unused 
    public void setAgentSensorAvail(boolean agentSensorAvail) 
    {    
        this.agentSensorAvail = agentSensorAvail;
    }
    */

    public void setGameTime(Long gameTime) 
    {
        this.gameTime = gameTime;
    }

    public void setAgentSensorLastTime(Long agentSensorLastTime) 
    {
        this.agentSensorLastTime = agentSensorLastTime;
    }
    
    public void setOmniscientAgent(boolean omniscientAgent) 
    {
        this.omniscientAgent = omniscientAgent;
    }
    
     
    // -- Getters
    public HashMap<String, Room> getMap() 
    {
        return map;
    }

    public List<RoomState> getRoomStates() 
    {
        return roomStates;
    }

    public HashMap<String,CrewMember> getCrews() 
    {
        return crews;
    }

    public Game getEnvironment() 
    {
        return environment;
    }

    public List<CrewMemberState> getCrewStates() 
    {
        return crewStates;
    }

    public Room getAgentRoom() 
    {
        return agentRoom;
    }

    public Long getAgentEnergy() 
    {
        return agentEnergy;
    }
    
    public Long getAgentSensorLastTime() 
    {
        return agentSensorLastTime;
    }

    /*
    public boolean isAgentSensorAvail() 
    {
        return agentSensorAvail;
    }
    */

    public Long getGameTime() 
    {
        return gameTime;
    } 
    
    public boolean isOmniscientAgent() 
    {
        return omniscientAgent;
    }
        
    public HashMap<String,Sabotage> getSabotages()
    {
        return GameState.sabotages;
    }
    
    @Override
    public String toString() 
    {
        StringBuilder text = new StringBuilder("--Mundo | Tiempo: ").append(this.gameTime).append("--\n");
        text.append("¿Dónde está cada tripulante?: \n");
        this.crewStates.forEach(state -> 
        {
            text
                    .append("Me llamo: ")
                    .append(state.getCrew().getName())
                    .append(" y estoy en: ")
                    .append(state.getCurrentRoom().getName());
                    if(!state.isAlive()) text.append(" <-- MUERTO");
                    text.append("\n");
        });
        
        return text.toString();
        
    }

    /*
        Usando por el Game para clonar el estado
        Nota: las referencias de las entidades a sus estados no son guardadas en las copias. (Para que no apunten a un estado copiado)
    */
    
    public GameState(List<RoomState> roomStates, List<CrewMemberState> crewStates, Long gameTime, 
            Room agentRoom, Long agentEnergy, Long agentSensorLastTime, Boolean omniscientAgent, Boolean copyFlag )
    {
        this.roomStates.addAll(roomStates);
        this.crewStates.addAll(crewStates);
        this.gameTime = gameTime;
        this.agentRoom = agentRoom;
        this.agentEnergy = agentEnergy;
        this.agentSensorLastTime = agentSensorLastTime;
        this.omniscientAgent = omniscientAgent;
    }
    
    @Override
    public GameState clone()
    {
        List<RoomState> roomStatesClone = new ArrayList<>();
        List<CrewMemberState> crewStatesClone = new ArrayList<>();
        
        this.roomStates.stream().forEach(it -> roomStatesClone.add(it.clone()));
        
        this.crewStates.stream().forEach(it -> crewStatesClone.add(it.clone()));
        
        GameState newState = new GameState(roomStatesClone, crewStatesClone,
                this.gameTime,this.agentRoom,this.agentEnergy,this.agentSensorLastTime,this.omniscientAgent,true);
        
        return newState;
    }
    
    
    
    
    
    
    
    
    

}
