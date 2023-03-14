package awele.bot.depht;

import awele.bot.mybot.MinMaxNode;
import awele.core.Board;
import awele.core.InvalidBotException;

public class DynamicDepthNode {
    /** Numéro de joueur de l'IA */
    private static int player;

    /** Profondeur maximale */
    private static int maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;

    private static final int MAX_DEPTH = 8;
    private static final long MAX_TIME_MS = 100;

    /**
     * Constructeur...
     * @param board L'état de la grille de jeu
     */

    public DynamicDepthNode(Board board, int myTour, int opponentTour, double a, double b, long startTime) {
        double depth = 1; // Profondeur initiale

        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double[Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = -Double.MAX_VALUE;

        Board copy;
        double[] decisionTemp = new double[Board.NB_HOLES];
        long elapsedTime;

        do {
            for (int i = 0; i < Board.NB_HOLES; i++) {
                if (board.getPlayerHoles()[i] != 0) {
                    decisionTemp[i] = i + 1;
                    try {
                        copy = board.playMoveSimulationBoard(myTour, decisionTemp);
                        double score;

                        if ((copy.getScore(myTour) < 0) || (copy.getScore(opponentTour) >= 25) || (copy.getNbSeeds() <= 6)) {
                            score = diffScore(copy);
                        } else {
                            DynamicDepthNode child = new DynamicDepthNode(copy, opponentTour, myTour, -b, -a, startTime);
                            score = -child.getEvaluation();
                        }

                        if (score > evaluation) {
                            evaluation = score;
                        }

                        a = Math.max(a, score);
                        if (a >= b) {
                            break;
                        }
                    } catch (InvalidBotException e) {
                        decision[i] = 0;
                    }
                }
            }

            depth++; // Augmente la profondeur pour la prochaine itération

            elapsedTime = System.currentTimeMillis() - startTime; // Calcule le temps écoulé
            System.out.println(elapsedTime);
        } while (elapsedTime < MAX_TIME_MS); // Continue tant qu'on a pas atteint la profondeur max ou le temps max

        this.decision = decisionTemp;
    }

    private int diffScore (Board board){

        int score;
        int seeds;

        score = 25 * (board.getScore (awele.bot.depht.DynamicDepthNode.player) - board.getScore (Board.otherPlayer (DynamicDepthNode.player)));
        int total = 0;
        for (int i = 0; i < 6; i++) {
            seeds = board.getPlayerHoles()[i];

            if (seeds > 12)
                total += 28;
            else if (seeds == 0)
                total -= 54;
            else if (seeds < 3)
                total -= 36;
        }

        for (int i = 0; i < 6; i++) {
            seeds = board.getOpponentHoles()[i];

            if (seeds > 12)
                total -= 28;
            else if (seeds == 0)
                total += 54;
            else if (seeds < 3)
                total += 36;
        }


        return score+total; }

    /**
     * L'évaluation du noeud
     * @return L'évaluation du noeud
     */
    double getEvaluation ()
    {
        return this.evaluation;
    }

    /**
     * L'évaluation de chaque coup possible pour le noeud
     * @return
     */
    double [] getDecision ()
    {
        return this.decision;
    }
}
