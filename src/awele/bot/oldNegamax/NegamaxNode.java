package awele.bot.oldNegamax;

import awele.bot.Bot;
import awele.bot.demo.minmax.MaxNode;
import awele.bot.demo.minmax.MinMaxNode;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché Noeud d'un arbre MinMax
 */
public class NegamaxNode {
	/**
	 * Profondeur maximale
	 */
	private static int maxDepth;

	private final double depth;
	
	/**
	 * L'évaluation du noeud
	 */
	private double evaluation;

	/**
	 * Évaluation des coups selon MinMax
	 */
	private final double[] decision;

	/** Test pour les heuristiques */
	static Bot.HEURISTICS testHeuristic;
	
	/**
	 * Constructeur...
	 *
	 * @param board L'état de la grille de jeu
	 * @param depth La profondeur du noeud
	 */
	
	public NegamaxNode(Board board, double depth, int myTour, int opponentTour, double a, double b) {
		this.depth = depth;
		/* On crée index de notre situation */
		
		/* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
		this.decision = new double[Board.NB_HOLES];
		/* Initialisation de l'évaluation courante */
		this.evaluation = -Double.MAX_VALUE;
		Board copy;
		double[] decisionTemp = new double[Board.NB_HOLES];
		
		for (int i = 0; i < Board.NB_HOLES; i++) {
			/* Si le coup est jouable */
			if (board.getPlayerHoles()[i] != 0) {
				/* Sélection du coup à jouer */
				decisionTemp[i] = (i + 1);
				/* On copie la grille de jeu et on joue le coup sur la copie */
				// Board copy = (Board) board.clone();
				try {
					//int score_tmp = copy.playMoveSimulationScore(copy.getCurrentPlayer(), decision);
					copy = board.playMoveSimulationBoard(myTour, decisionTemp);
					
					
					if ((copy.getScore(myTour) < 0) || (copy.getScore(opponentTour) >= 25)
							|| (copy.getNbSeeds() <= 6) || !(depth < NegamaxNode.maxDepth)){
						switch (testHeuristic){
							case DIFF_SCORE:
								this.decision[i] = diffScore(copy, myTour, opponentTour);
								break;
							case BEST:
								this.decision[i] = best(copy, myTour, opponentTour);
								break;
							case TEST:
								this.decision[i] = test(copy, myTour, opponentTour);
								break;
						}
					}

						/* Sinon, on explore les coups suivants */
					else {
						
						
						
						/* Si le noeud n'a pas encore été calculé, on le construit */
						/* On construit le noeud suivant */
						NegamaxNode child = new NegamaxNode(copy, depth + 1, opponentTour, myTour, -b, -a);
						
						/* On récupère l'évaluation du noeud fils */
						this.decision[i] = -child.getEvaluation();

						
					}
					/*
					 * L'évaluation courante du noeud est mise à jour, selon le type de noeud
					 * (MinNode ou MaxNode)
					 */
					if (this.decision[i] > this.evaluation) {
						this.evaluation = this.decision[i];
					}
					
					if (depth > 0) {
						a = Double.max(a, this.decision[i]);
						if (a >= b) {
							break;
						}
					}
					
				} catch (InvalidBotException e) {
					this.decision[i] = 0;
				}
			}
		}
	}

	public static NegamaxNode iterativeDeepeningNegamax(Board board, double timeLimit, int maxDepth) {
		NegamaxNode bestNode = null;
		long startTime = System.currentTimeMillis();
		for ( NegamaxNode.maxDepth = 0; NegamaxNode.maxDepth <= maxDepth  && System.currentTimeMillis() - startTime < timeLimit; NegamaxNode.maxDepth++ ) {
			bestNode = new NegamaxNode(board, 0, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), -Double.MAX_VALUE, Double.MAX_VALUE);
		}

		//bestNode = new MaxNode(board, 0, 0);
		return bestNode;
	}
	
	/**
	 * Initialisation
	 */
	protected static void initialize(int maxDepth, Bot.HEURISTICS testHeuristic) {
		NegamaxNode.maxDepth = maxDepth;
		NegamaxNode.testHeuristic = testHeuristic;
	}

	protected static void initialize (Bot.HEURISTICS testHeuristic)
	{
		NegamaxNode.testHeuristic = testHeuristic;
	}

	private int test (Board board, int myTour, int opponentTour)
	{
		return 0;
	}

	private int diffScore (Board board, int myTour, int opponentTour)
	{
		return board.getScore (myTour) - board.getScore (opponentTour);
	}
	
	private int best(Board board, int myTour, int opponentTour) {
		int total = 0;
		int[] seedsPlayer = board.getPlayerHoles(), seedsOpponent = board.getOpponentHoles();
		
		for (int i = 0; i < 6; i++) {
			int seedP = seedsPlayer[i];
			int seedO = seedsOpponent[i];
			if (seedP > 12)
				total += 28;
			else if (seedP == 0)
				total -= 54;
			else if (seedP < 3)
				total -= 36;
			
			if (seedO > 12)
				total -= 28;
			else if (seedO == 0)
				total += 54;
			else if (seedO < 3)
				total += 36;
		}





		//if(myTour != board.getCurrentPlayer())
			//System.out.println("AAAAAA");

		/** Fonctionne , gange contre tous les bots*/
		return (25 * (board.getScore(myTour) - board.getScore(opponentTour))) - total;

		/** Ne fonctionne pas , perd contre certains bots -> incohérence */
		//return (25 * (board.getScore(board.getCurrentPlayer()) - board.getScore(Board.otherPlayer(board.getCurrentPlayer())))) - total;
	}
	
	
	/**
	 * L'évaluation du noeud
	 *
	 * @return L'évaluation du noeud
	 */
	double getEvaluation() {
		return this.evaluation;
	}
	
	
	/**
	 * L'évaluation de chaque coup possible pour le noeud
	 *
	 * @return
	 */
	double[] getDecision() {
		return this.decision;
	}
}
