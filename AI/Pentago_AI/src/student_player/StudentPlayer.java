package student_player;

import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import java.util.*;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260803654");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        final long startTime = System.nanoTime();
        long duration = 0;
        int myID = boardState.getTurnPlayer();
        ArrayList<PentagoMove> legalMoves = new ArrayList<PentagoMove>(boardState.getAllLegalMoves()); //array of all legal moves at this board state
        ArrayList<PentagoMove> goodMoves = new ArrayList<PentagoMove>(removeBadMoves(boardState, legalMoves, myID)); //array of all moves worth considering at this board state
        if (goodMoves.size() == 0) { 
        	return boardState.getRandomMove(); //when there were no good moves, the program would stop working
        }
        int[] probs = new int[goodMoves.size()]; //win rates for each move
        int best = 0; //position of best win rate in probs array
        while(true) { 
        	for (int i=0; i<goodMoves.size(); i++) { //for all moves, we run simulations and update the associated win rate
        		duration = System.nanoTime() - startTime; //compute time elapsed since beginning of turn
        		if (duration>1990000000) { //stop if we get too close to time limit (2 seconds)
        			return goodMoves.get(best); //if too close to time limit, return best move so far
        		}
        		PentagoBoardState tempState = (PentagoBoardState) boardState.clone();
        		PentagoMove possMove = goodMoves.get(i);
        		tempState.processMove(possMove); //process the i'th move in the arraylist of good moves
        		probs = updateProb(doSimulation(tempState), probs, i, myID).clone(); //after running simulation that begins after processed move, update win rate of move based on result of game
        		if (probs[i]>probs[best]) { //update the position of the best move if we find new best
        			best = i;
        		}
        	}
        }
    }
    
    public ArrayList<PentagoMove> removeBadMoves(PentagoBoardState boardState, ArrayList<PentagoMove> legalMoves, int myID) { //takes all the moves not worth considering out of the array of moves being considered
    	for (int i=0; i<legalMoves.size(); i++) { //for all moves in arraylist of legal moves, process each one
    		PentagoBoardState tempState = (PentagoBoardState) boardState.clone();
    		int count=0;
    		tempState.processMove(legalMoves.get(i));
    		ArrayList<PentagoMove> nextMoves = new ArrayList<PentagoMove>(tempState.getAllLegalMoves());
    		for (int j=0; j<nextMoves.size(); j++) { //for all moves in arraylist of legal moves after the above move, check if we lose
    			PentagoBoardState tempState2 = (PentagoBoardState) tempState.clone();
    			tempState2.processMove(nextMoves.get(j));
    			if (tempState2.getWinner() == 1 - myID) {
    				count++;
    			}
    		}
    		if (count>0) { //count>0 if we any of the moves in the initial arraylist of legal moves led to an immediate loss on the following turn
    			if (tempState.getWinner() != myID) {
    			legalMoves.remove(i);
    			i--;
    			}
    		}
    	}
    	return legalMoves;
    }
    
    public int[] updateProb(int winner, int[] probs, int moveNum, int myID) { //updates the win rate for a given move, with a given board state
    	if (winner != 0 && winner != 1) { //don't update if we have a tie
    		return probs;
    	}
    	probs[moveNum] += (winner == myID ? 1 : -1); //add 1 to win rate if we won, -1 if we lost
    	return probs;
    }
    
    public int doSimulation(PentagoBoardState boardState) { //performs a single simulation from a given board state
    	PentagoBoardState tempState = (PentagoBoardState) boardState.clone();
    	while(!tempState.gameOver()) { //while the simulated game is not done, process a random move
    		PentagoMove tempMove = (PentagoMove) getRandomMove(tempState);
    		tempState.processMove(tempMove);
    	}
    	return tempState.getWinner(); //return the winner of the game
    }
    
    public Move getRandomMove(PentagoBoardState boardState) { //truly random version of getRandomMove
        ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
        Random rand = new Random();
        return moves.get(rand.nextInt(moves.size()));
    }
    
}