package src.pas.battleship.agents;


// SYSTEM IMPORTS
import java.util.*;

// JAVA PROJECT IMPORTS
import edu.bu.battleship.agents.Agent;
import edu.bu.battleship.game.Game.GameView;
import edu.bu.battleship.game.EnemyBoard;
import edu.bu.battleship.game.EnemyBoard.Outcome;
import edu.bu.battleship.utils.Coordinate;
import edu.bu.battleship.game.PlayerView;


public class ProbabilisticAgent
    extends Agent
{

    public ProbabilisticAgent(String name)
    {
        super(name);
        System.out.println("[INFO] ProbabilisticAgent.ProbabilisticAgent: constructed agent");
    }

    @Override
    public Coordinate makeMove(final GameView game)
    {
        Random rand = new Random();
        //System.out.println(game.getEnemyShipTypeToNumRemaining());

        //find size of board to use
        int max = 0;
        while(game.isInBounds(max+1, max+1)){
            max++;
        }


        Outcome[][] result = game.getEnemyBoardView();
       
        //pick random coordinate until not attacked yet
        int x = rand.nextInt(max);
        int y = rand.nextInt(max);
        while(!result[x][y].equals(Outcome.UNKNOWN)){
            x = rand.nextInt(max);
            y = rand.nextInt(max);
        }
        System.out.println("c:" +x+","+y+"   "+result[x][y]);
        
        Coordinate coor = new Coordinate(x,y);
        return coor;
    }

    @Override
    public void afterGameEnds(final GameView game) {}

}
