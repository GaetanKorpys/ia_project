package awele.bot.test;

import awele.bot.Bot;
import awele.bot.demo.minmax.MinMaxNode;
import awele.bot.oldNegamax.NegamaxBot;
import awele.core.Board;
import awele.core.InvalidBotException;

public class NegamaxNode {

    /** Numéro de joueur de l'IA */
    private static int player;

    /** Profondeur maximale */
    private static int maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;

    /** Test pour les heuristiques */
    static Bot.HEURISTICS testHeuristic;


    /** Algorithme récursif */
    public NegamaxNode(Board board, int depth, double alpha, double beta)
    {
        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double[Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = -Double.MAX_VALUE;

        /* On parcourt toutes les coups possibles */
        for(int i = 0; i < Board.NB_HOLES; i++)
        {
            /* Si le coup est jouable */
            if(board.getPlayerHoles()[i] != 0)
            {
                /* Sélection du coup à jouer */
                double [] decision = new double [Board.NB_HOLES];
                decision [i] = 1;

                try {

                    /* On copie la grille de jeu et on joue le coup sur la copie */
                    Board copy = board.playMoveSimulationBoard(board.getCurrentPlayer(), decision);

                    int score = copy.getScore(copy.getCurrentPlayer ());
                    //copy.playMoveSimulationScore (copy.getCurrentPlayer (), decision);
                    //copy = copy.playMoveSimulationBoard (copy.getCurrentPlayer (), decision);

                    /** Conditions d'arret */
                    /** Noeud terminal ou profondeur max atteinte */
                    if ( (depth >= NegamaxNode.maxDepth) || (score < 0) || (copy.getScore (Board.otherPlayer (copy.getCurrentPlayer ())) >= 25) || (copy.getNbSeeds () <= 6) ){
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
                    else
                    {
                        /* On construit le noeud suivant */
                        NegamaxNode child = new NegamaxNode (copy, depth + 1, -beta, -alpha);
                        /* On récupère l'évaluation du noeud fils */
                        this.decision [i] = -child.getEvaluation ();
                    }

                    /* L'évaluation courante du noeud est mise à jour */
                    this.evaluation = Double.max (this.decision [i], this.evaluation);

                    if (depth > 0)
                    {
                        alpha = Double.max(alpha, this.decision[i]);
                        if(alpha >= beta)
                            break;
                    }

                } catch (InvalidBotException e) {
                    this.decision [i] = 0;
                }

            }
        }

    }

    public static NegamaxNode iterativeDeepeningNegamax(Board board, double timeLimit, int maxDepth) {
        NegamaxNode bestNode = null;
        long startTime = System.currentTimeMillis();
        for (NegamaxNode.maxDepth = 0; NegamaxNode.maxDepth <= maxDepth  && System.currentTimeMillis() - startTime < timeLimit; NegamaxNode.maxDepth++ ) {
            bestNode = new NegamaxNode(board, 0, -Double.MAX_VALUE, Double.MAX_VALUE);
        }

        //bestNode = new MaxNode(board, 0, 0);
        return bestNode;
    }

    private int test(Board board) {
        return 0;
    }

    private int diffScore (Board board)
    {
        return board.getScore (Board.otherPlayer(board.getCurrentPlayer())) - board.getScore (board.getCurrentPlayer());
    }

    /** Ne fonctionne parfois pas en dessous de 100ms */
    private int best(Board board) {

        int total = 0;
        int[] seedsPlayer = board.getPlayerHoles(), seedsOpponent = board.getOpponentHoles();

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

        return (25 * (board.getScore (Board.otherPlayer(board.getCurrentPlayer())) - board.getScore(board.getCurrentPlayer()))) - total;

        //return (25 * (board.getScore (MinMaxNode.player) - board.getScore(Board.otherPlayer (MinMaxNode.player)))) - total;
    }

    protected static void initialize(Board board, int maxDepth, Bot.HEURISTICS testHeuristic)
    {
        NegamaxNode.maxDepth = maxDepth;
        NegamaxNode.player = board.getCurrentPlayer ();
        NegamaxNode.testHeuristic = testHeuristic;
    }

    protected static void initialize (Board board, Bot.HEURISTICS testHeuristic)
    {
        NegamaxNode.player = board.getCurrentPlayer ();
        NegamaxNode.testHeuristic = testHeuristic;
    }

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
