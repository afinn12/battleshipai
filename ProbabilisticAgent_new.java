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

import java.util.Map;
import java.util.HashMap;


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
        //find size of board to use
        int max = game.getGameConstants().getNumCols();
        
        //initialize probabilities for each cell
        double[][] probabilities = new double[max][max];
        
        //iterate over each cell
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                //if cell was attacked, prob = 0
                if (!game.isInBounds(x, y) || !game.getEnemyBoardView()[x][y].equals(Outcome.UNKNOWN)) {
                    probabilities[x][y] = 0.0;
                } else {
                    //calculate prob for each ship type
                    for (Constants.Ship.ShipType shipType : Constants.Ship.ShipType.values()) {
                        int shipSize = Constants.Ship.getShipSize(shipType);
                        double prob = calculateProbability(game, x, y, shipSize);
                        //update prob for cell
                        probabilities[x][y] += prob;
                    }
                }
            }
        }
        
        //find coord with highest prob
        Coordinate highestProbabilityCoord = findHighestProbabilityCoordinate(probabilities);

        return highestProbabilityCoord;
    }

    // Helper method to calc the prob for a specific ship size at a given coordinate
    private double calculateProbability(GameView game, int x, int y, int shipSize) {
        //count number of empty squares around the given coordinate
        int emptyCount = 0;
        for (int i = x - shipSize + 1; i <= x; i++) {
            if (i >= 0 && i < game.getGameConstants().getNumCols() && game.isInBounds(i, y) && game.getEnemyBoardView()[i][y] == Outcome.UNKNOWN) {
                emptyCount++;
            }
        }
        for (int j = y - shipSize + 1; j <= y; j++) {
            if (j >= 0 && j < game.getGameConstants().getNumCols() && game.isInBounds(x, j) && game.getEnemyBoardView()[x][j] == Outcome.UNKNOWN) {
                emptyCount++;
            }
        }
        
        //calc prob based on number of empty squares
        double prob = (double) emptyCount / (2 * shipSize);
        return prob;
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
