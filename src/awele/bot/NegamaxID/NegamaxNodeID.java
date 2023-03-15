package awele.bot.NegamaxID;

import awele.bot.demo.minmax.MaxNode;
import awele.bot.demo.minmax.MinMaxNode;
import awele.bot.negatest.NegamaxNode;
import awele.core.Board;
import awele.core.InvalidBotException;

import javax.sound.midi.Soundbank;

/**
 * @author Alexandre Blansché Noeud d'un arbre MinMax
 */
public class NegamaxNodeID {
	/**
	 * Profondeur maximale
	 */
	private static int maxDepth;

	private static final long MAX_TIME_MS = 100;

	/** Numéro de joueur de l'IA */
	private static int player;
	
	/**
	 * L'évaluation du noeud
	 */
	private double evaluation;
	
	
	/**
	 * Évaluation des coups selon MinMax
	 */
	private final double[] decision;
	
	
	/**
	 * Constructeur...
	 *
	 * @param board L'état de la grille de jeu
	 */

	public static NegamaxNodeID iterativeDeepeningNegamax(Board board, int myTour, int oppenentTour,  double timeLimit, int maxDepth) {
		NegamaxNodeID bestNode = null;
		long startTime = System.currentTimeMillis();
		//long elapsedTime = System.currentTimeMillis() - startTime;
		for ( NegamaxNodeID.maxDepth = 0; NegamaxNodeID.maxDepth <= maxDepth  && System.currentTimeMillis() - startTime < timeLimit; NegamaxNodeID.maxDepth++ ) {
			bestNode = new NegamaxNodeID(board, 0, myTour, oppenentTour, -Double.MAX_VALUE, Double.MAX_VALUE);
		}

		//bestNode = new MaxNode(board, 0, 0);
		return bestNode;
	}



	public NegamaxNodeID(Board board, double depth, int myTour, int opponentTour, double a, double b) {
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
							|| (copy.getNbSeeds() <= 6) || !(depth < NegamaxNodeID.maxDepth))
						this.decision[i] = scoreEntireBoardById(copy, myTour, opponentTour);
						/* Sinon, on explore les coups suivants */
					else {



						/* Si le noeud n'a pas encore été calculé, on le construit */
						/* On construit le noeud suivant */
						NegamaxNodeID child = new NegamaxNodeID(copy, depth + 1, opponentTour, myTour, -b, -a);
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
	
	
	/**
	 * Initialisation
	 */
	protected static void initialize(Board board, int maxDepth) {
		//NegamaxNodeID.maxDepth = maxDepth;
		NegamaxNodeID.player = board.getCurrentPlayer ();
	}

	private int diffScore (Board board)
	{
		return board.getScore (NegamaxNodeID.player) - board.getScore (Board.otherPlayer (NegamaxNodeID.player));
	}
	
	private int scoreEntireBoardById(Board board, int myTour, int opponentTour) {
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
		return (25 * (board.getScore(myTour) - board.getScore(opponentTour))) - total;
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
