package src.pas.battleship.agents;


// SYSTEM IMPORTS
import java.util.*;

// JAVA PROJECT IMPORTS
import edu.bu.battleship.agents.Agent;
import edu.bu.battleship.game.Game.GameView;
import edu.bu.battleship.game.EnemyBoard;
import edu.bu.battleship.game.EnemyBoard.Outcome;
import edu.bu.battleship.utils.Coordinate;
//import edu.bu.battleship.game.PlayerView;
import edu.bu.battleship.game.Constants;
import edu.bu.battleship.game.Constants.Ship;


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
        int max = game.getGameConstants().getNumCols();

        //initialize probabilities for each cell
        double[][] probabilities = new double[max][max];

        //calculate priors for each cell
        double priorProb = 1.0 / (max * max);
        
        //finds ship types and associated num remaining
        System.out.println(game.getGameConstants().getShipTypeToPopulation()+"\n\n");
        java.util.Map<Ship.ShipType, Integer> enemyShipTypeToNumRemaining = game.getEnemyShipTypeToNumRemaining();

        //iterate over each cell
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                //if cell was attacked, prob = 0
                if (!game.isInBounds(x, y) || !game.getEnemyBoardView()[x][y].equals(Outcome.UNKNOWN)) {
                    probabilities[x][y] = 0.0;
                    continue;
                }

                //calc posterior prob for each cell w Bayesian inference
                double postProb = priorProb;

                //update post prob based on remaining ships
                for (Ship.ShipType shipType : enemyShipTypeToNumRemaining.keySet()) {
                    int numRemaining = enemyShipTypeToNumRemaining.get(shipType);
                    int shipLength = Ship.getShipLength(shipType);

                    //calc prob of ship placement in this cell
                    double probabilityOfShip = (double) numRemaining / (max * max);
                    //calc prob based on placement probability
                    postProb *= probabilityOfShip;

                    //update post prob based on orientation
                    //horizontal placement
                    if (game.isInBounds(x + shipLength - 1, y)) {
                        for (int i = 0; i < shipLength; i++) {
                            postProb *= probabilityOfShip;
                        }
                    } //vertical placement
                    else if (game.isInBounds(x, y + shipLength - 1)) {
                        for (int i = 0; i < shipLength; i++) {
                            postProb *= probabilityOfShip;
                        }
                    }
                }
                probabilities[x][y] = postProb;
            }
        }
        //find the coord with highest prob
        Coordinate highestProbabilityCoord = findHighestProbabilityCoordinate(probabilities);

        return highestProbabilityCoord;      
    }

    // Helper method to find the coordinate with the highest probability
    private Coordinate findHighestProbabilityCoordinate(double[][] probabilities) {
        double maxProbability = Double.MIN_VALUE;
        Coordinate maxProbabilityCoord = new Coordinate(0, 0);

        // Iterate over each cell and update maxProbability and maxProbabilityCoord if needed
        for (int x = 0; x < probabilities.length; x++) {
            for (int y = 0; y < probabilities[x].length; y++) {
                if (probabilities[x][y] > maxProbability) {
                    maxProbability = probabilities[x][y];
                    maxProbabilityCoord = new Coordinate(x, y);
                }
            }
        }

        return maxProbabilityCoord;
    }

    @Override
    public void afterGameEnds(final GameView game) {}

}
