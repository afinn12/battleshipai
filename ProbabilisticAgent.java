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
                    //calculate prob for each ship size
                    for (int i=2; i<6; i++) {
                        double prob = calculateProbability(game, x, y, i);
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


        //calculate the maximum number of consecutive hits, from x, y
        // int xmaxhit=0;
        // int xhit=0;
        // int ymaxhit = 0;
        // int yhit = 0;

        // for(int i=1; i<shipSize; i++){
        //     if(game.isInBounds(x+i, y)){
        //         if(game.getEnemyBoardView()[x+i][y] == Outcome.HIT){
                        // //only increment xhit if first square or fit xhit is >0 (has only run into hit)
        //             if(xhit>0){
        //                 xhit++;
        //             }else if (i ==1){
        //                 xhit++;
        //             }
        //         }else{
        //             //System.out.print("status   :" + game.getEnemyBoardView()[i][y]);
                        // //store max hit and reset hit to 0
        //             if(xmaxhit < xhit){
        //                 xmaxhit = xhit;
        //             }   
        //             xhit = 0;        
        //         }
        //     }
        //     if(game.isInBounds(x-i, y)){ 
                    // //use next to only keep growing if the next square on the other side is also a hit, only counts consecutive incluidng x, y
        //         boolean next = false;
        //         if(game.getEnemyBoardView()[x-i][y] == Outcome.HIT){
        //             if(i==1){
        //                 next = true;
        //                 xhit++;
        //             }else if (next){
        //                 xhit++;
        //             }
        //         }else{
        //             //System.out.print("status   :" + game.getEnemyBoardView()[i][y]);
        //             if(xmaxhit < xhit){
        //                 xmaxhit = xhit;
        //             }   
        //             xhit = 0;
        //         }
        //     }
        //     if(game.isInBounds(x, y+i)){
        //         if(game.getEnemyBoardView()[x][y+i] == Outcome.HIT){
        //             if(yhit>0){
        //                 yhit++;
        //             }else if (i ==1){
        //                 yhit++;
        //             }
        //         }else{
        //             //System.out.print("status   :" + game.getEnemyBoardView()[i][y]);
        //             if(ymaxhit < yhit){
        //                 ymaxhit = yhit;
        //             }   
        //             yhit = 0;
                    
        //         }
        //     }
        //     if(game.isInBounds(x, y-i)){
        //         if(game.getEnemyBoardView()[x][y-i] == Outcome.HIT){
        //             if(yhit>0){
        //                 yhit++;
        //             }else if (i ==1){
        //                 yhit++;
        //             }
        //         }else{
        //             //System.out.print("status   :" + game.getEnemyBoardView()[i][y]);
        //             if(ymaxhit < yhit){
        //                 ymaxhit = yhit;
        //             }   
        //             yhit = 0;
                    
        //         }
        //     }
        // }
        // if(xmaxhit>0 || ymaxhit>0){
        //     return 0.9+(0.01*xmaxhit)+(0.01*ymaxhit);
        // }


        int maxxconsec = 0;
        int xconsec=0;
        
        for (int i = x - shipSize+1; i <x+shipSize-1; i++) {
            System.out.print("x:"+x+"   y:"+y+"    x2:"+i+"   size:"+shipSize);
            if (game.isInBounds(i, y)) {
                if(game.getEnemyBoardView()[i][y] == Outcome.UNKNOWN){
                    
                    xconsec++;
                    System.out.print("    checking: "+i+", "+y);
                }else if (game.getEnemyBoardView()[i][y] == Outcome.MISS || game.getEnemyBoardView()[i][y] == Outcome.SUNK){
                    //System.out.print("status   :" + game.getEnemyBoardView()[i][y]);
                    if(maxxconsec < xconsec){
                        maxxconsec = xconsec;
                    }
                    xconsec = 0;
                }else if (game.getEnemyBoardView()[i][y] == Outcome.HIT){
                    //System.out.print("status   :" + game.getEnemyBoardView()[i][y]);
                    if(i+1==x || i-1==x){
                        //System.out.println("HIT AT:" + i + ", "+y);
                        return 1f;
                    }
                }
            }
            if(maxxconsec < xconsec){
                maxxconsec = xconsec;
            }
            System.out.println("\n maxx: "+maxxconsec);
        }
        System.out.println("\n");
        int maxyconsec = 0;
        int yconsec=0;


        for (int j = y - shipSize+1; j <y+shipSize-1;j++) {
            System.out.print("x:"+x+"   y:"+y+"    y2:"+j+"   size:"+shipSize);
            if (game.isInBounds(x, j)) {
                if(game.getEnemyBoardView()[x][j] == Outcome.UNKNOWN){
                    yconsec++;
                    System.out.print("    checking: "+x+", "+j);
                }else if (game.getEnemyBoardView()[x][j] == Outcome.MISS || game.getEnemyBoardView()[x][j] == Outcome.SUNK){
                    if(maxyconsec < yconsec){
                        maxyconsec = yconsec;
                    }
                    yconsec = 0;
                }else if(game.getEnemyBoardView()[x][j] == Outcome.HIT){
                    if(j+1==y || j-1==y){
                        System.out.println("HIT AT:" + x + ", "+j);
                        return 1f;
                    }
                }
            }
            if(maxyconsec < yconsec){
                maxyconsec = yconsec;
            }
            System.out.println("\n maxy: "+maxyconsec);
        }
        
        
        //calc prob based on number of empty squares
        double prob = (double) (xconsec+yconsec) / (2 *2* shipSize);
        System.out.println("prob:"+prob);
        System.out.println("\n\n");
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