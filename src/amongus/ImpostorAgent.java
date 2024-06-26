
package amongus;

import amongus.actions.ActivateSabotage;
import amongus.actions.Kill;
import amongus.actions.MoveEast;
import amongus.actions.MoveNorth;
import amongus.actions.MoveSouth;
import amongus.actions.MoveWest;
import amongus.models.AgentRoomState;
import amongus.utils.Pair;
import frsf.cidisi.faia.agent.Action;
import frsf.cidisi.faia.agent.Perception;
import frsf.cidisi.faia.agent.search.Problem;
import frsf.cidisi.faia.agent.search.SearchAction;
import frsf.cidisi.faia.agent.search.SearchBasedAgent;
import frsf.cidisi.faia.solver.search.AStarSearch;
import frsf.cidisi.faia.solver.search.BreathFirstSearch;
import frsf.cidisi.faia.solver.search.DepthFirstSearch;
import frsf.cidisi.faia.solver.search.GreedySearch;
import frsf.cidisi.faia.solver.search.IEstimatedCostFunction;
import frsf.cidisi.faia.solver.search.IStepCostFunction;
import frsf.cidisi.faia.solver.search.Search;
import frsf.cidisi.faia.solver.search.UniformCostSearch;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ImpostorAgent extends SearchBasedAgent 
{
    private final ImpostorAgentState myState;
    private final GameGoal myGoal;
    private final Vector<SearchAction> myActions = new Vector<>();
    
    public ImpostorAgent(HashMap<String,AgentRoomState> gameRooms, HashMap<String, Pair<String,Long>> gameCrew, 
            List<String> sabotages, GameGoal goal)
    {
        //Mi estado
        this.myState = new ImpostorAgentState(gameRooms,gameCrew,sabotages);
        this.setAgentState(myState);
        
        //Mis acciones
        myActions.add(new Kill());
        myActions.add(new ActivateSabotage());
        myActions.add(new MoveNorth());
        myActions.add(new MoveEast());
        myActions.add(new MoveSouth());
        myActions.add(new MoveWest());

        //Mi objectivo
        this.myGoal = goal;
        
        //Mi problema
        Problem problem = new Problem(this.myGoal,this.myState,this.myActions);
        
        this.setProblem(problem);
    }
    
    @Override
    public Action selectAction()
    {
        // Create the search strategy
        //DepthFirstSearch strategy = new DepthFirstSearch();
        //BreathFirstSearch strategy = new BreathFirstSearch();
        
        /*
        //Uniform Cost:
        IStepCostFunction costFunction = new AgentStateCost();
        UniformCostSearch strategy = new UniformCostSearch(costFunction);
        
        System.out.println("Resolviendo con costo uniforme");
        */
        
        IEstimatedCostFunction heuristic = new AgentHeuristic();
        GreedySearch strategy = new GreedySearch(heuristic);
        
        /*
        IStepCostFunction cost = new AgentStateCost();
        IEstimatedCostFunction heuristic = new AgentHeuristic();
        AStarSearch strategy = new AStarSearch(cost, heuristic);
        */
         

        Search searchSolver = new Search(strategy);

        /* Generate an XML file with the search tree. It can also be generated
         * in other formats like PDF with PDF_TREE */
        //searchSolver.setVisibleTree(Search.EFAIA_TREE);

        // Set the Search searchSolver.
        this.setSolver(searchSolver);

        // Ask the solver for the best action
        Action selectedAction = null;
        try 
        {
            selectedAction = this.getSolver().solve(new Object[]{this.getProblem()});
        } 
        catch (Exception ex) 
        {
            System.out.println("ERROR: No se pudo seleccionar siguiente acción debido a: " + ex);
            ex.printStackTrace();
        }

        // Return the selected action
        return selectedAction;
    }

    @Override
    public void see(Perception p) 
    {
        this.myState.updateState(p);
    }    

}
