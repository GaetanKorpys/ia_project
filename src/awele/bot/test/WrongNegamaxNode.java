package awele.bot.test;

import awele.bot.Bot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché Noeud d'un arbre MinMax
 */
public class WrongNegamaxNode {
	/** Numéro de joueur de l'IA */
	private static int player;

	/**
	 * Profondeur maximale
	 */
	private static int maxDepth;
	
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
	
	public WrongNegamaxNode(Board board, double depth, int myTour, int opponentTour, double a, double b) {
		
		
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
							|| (copy.getNbSeeds() <= 6) || !(depth < WrongNegamaxNode.maxDepth))
					{
						switch (testHeuristic){
							case DIFF_SCORE:
								this.decision [i] = this.diffScore (copy);
								break;
							case BEST:
								this.decision [i] = this.best (copy);
								break;
							case TEST:
								this.decision [i] = this.test (copy);
								break;
						}
					}
						/* Sinon, on explore les coups suivants */
					else {
						
						
						
						/* Si le noeud n'a pas encore été calculé, on le construit */
						/* On construit le noeud suivant */
						WrongNegamaxNode child = new WrongNegamaxNode(copy, depth + 1, opponentTour, myTour, -b, -a);
						
						/* On récupère l'évaluation du noeud fils */
						this.decision[i] = -child.getEvaluation();
						
						/*
						 * Sinon (si la profondeur maximale est atteinte), on évalue la situation
						 * actuelle
						 */
						
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
	

	protected static void initialize(Board board, int maxDepth, Bot.HEURISTICS testHeuristic)
	{
		WrongNegamaxNode.maxDepth = maxDepth;
		WrongNegamaxNode.player = board.getCurrentPlayer ();
		WrongNegamaxNode.testHeuristic = testHeuristic;
	}

	protected static void initialize (Board board, Bot.HEURISTICS testHeuristic)
	{
		WrongNegamaxNode.player = board.getCurrentPlayer ();
		WrongNegamaxNode.testHeuristic = testHeuristic;
	}
	public static WrongNegamaxNode iterativeDeepeningNegamax(Board board, int myTour, int oppenentTour, double timeLimit, int maxDepth) {

		WrongNegamaxNode bestNode = null;
		long startTime = System.currentTimeMillis();

		for (WrongNegamaxNode.maxDepth = 0; WrongNegamaxNode.maxDepth <= maxDepth  && System.currentTimeMillis() - startTime < timeLimit; WrongNegamaxNode.maxDepth++ ) {
			bestNode = new WrongNegamaxNode(board, 0, myTour, oppenentTour, -Double.MAX_VALUE, Double.MAX_VALUE);
		}

		return bestNode;
	}
	
	private int best(Board board) {
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
		return (25 * (board.getScore(board.getCurrentPlayer()) - board.getScore(Board.otherPlayer(board.getCurrentPlayer())))) - total;
	}

	private int test(Board board) {
		return 0;
	}

	private int diffScore (Board board)
	{
		return board.getScore (WrongNegamaxNode.player) - board.getScore (Board.otherPlayer (WrongNegamaxNode.player));
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
