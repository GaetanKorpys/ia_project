package awele.bot.demo.A;

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

	/** Numéro de joueur de l'IA */
	private static int player;
	
	private final double depth;
	
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
							|| (copy.getNbSeeds() <= 6) || !(depth < NegamaxNode.maxDepth))
						this.decision[i] = scoreEntireBoardById(copy, myTour, opponentTour);
						/* Sinon, on explore les coups suivants */
					else {
						
						
						
						/* Si le noeud n'a pas encore été calculé, on le construit */
						/* On construit le noeud suivant */
						NegamaxNode child = new NegamaxNode(copy, depth + 1, opponentTour, myTour, -b, -a);
						
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
	
	
	/**
	 * Initialisation
	 */
	protected static void initialize(int maxDepth, int player) {
		NegamaxNode.maxDepth = maxDepth;
		NegamaxNode.player = player;
	}
	
	
	private int scoreEntireBoardById(Board board, int myTour, int opponentTour) {
		int total = 0;
		int[] seedsPlayer = board.getPlayerHoles(), seedsOpponent = board.getOpponentHoles();

		int res, res2;
		int total2 = 0;
		int[] seedsPlayer2 = board.getPlayerHoles(), seedsOpponent2 = board.getOpponentHoles();





		if(NegamaxNode.player == myTour){
			for (int i = 0; i < 6; i++) {
				int seedP = seedsPlayer[i];
				int seedO = seedsOpponent[i];
				if (seedP >= 12)
					total2 -= 28;
				else if (seedP == 0)
					total2 += 54;
				else if (seedP < 3)
					total2 += 36;

				if (seedO >= 12)
					total2 += 28;
				else if (seedO == 0)
					total2 -= 54;
				else if (seedO < 3)
					total2 -= 36;
			}

			res =  ( (25 * ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player)))) + total2);
		}
		else{

			for (int i = 0; i < 6; i++) {
				int seedP = seedsPlayer[i];
				int seedO = seedsOpponent[i];
				if (seedP >= 12)
					total2 += 28;
				else if (seedP == 0)
					total2 -= 54;
				else if (seedP < 3)
					total2 -= 36;

				if (seedO >= 12)
					total2 -= 28;
				else if (seedO == 0)
					total2 += 54;
				else if (seedO < 3)
					total2 += 36;
			}
			res =  - 1 *( (25 * ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player)))) + total2);
			//res =  ( (25 * ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player)))) + total);
			//res = (25 * ((board.getScore (Board.otherPlayer(NegamaxNode.player))) - board.getScore(NegamaxNode.player))) - total;

		}

		System.out.println("total2 " + total2);
		System.out.println("res2 " + res);





		for (int i = 0; i < 6; i++) {
			int seedP = seedsPlayer[i];
			int seedO = seedsOpponent[i];
			if (seedP >= 12)
				total += 28;
			else if (seedP == 0)
				total -= 54;
			else if (seedP < 3)
				total -= 36;
			
			if (seedO >= 12)
				total -= 28;
			else if (seedO == 0)
				total += 54;
			else if (seedO < 3)
				total += 36;
		}
		int res4 = (25 * ((board.getScore (myTour)) - board.getScore(opponentTour))) - total;

		System.out.println("total " + total);
		System.out.println("res " + res4);
		/*

		int res = (25 * (board.getScore (Board.otherPlayer(board.getCurrentPlayer())) - board.getScore(board.getCurrentPlayer()))) - total;
		int res2 = (25 * (board.getScore (NegamaxNode.player) - board.getScore(Board.otherPlayer (NegamaxNode.player)))) - total;
		int res3 = (25 * (board.getScore (Board.otherPlayer (NegamaxNode.player))) - board.getScore((NegamaxNode.player))) - total;

		System.out.println(res4);
		System.out.println(res);
		System.out.println(res2);
		System.out.println(res3+"\n");
		*/
		return  res4;
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
