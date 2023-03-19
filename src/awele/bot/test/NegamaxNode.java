package awele.bot.test;

import awele.bot.Bot;
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

    /** Tableau d'index de décision trié dans l'ordre décroissant */
    private static double [] sortDescendingDecision;


    /** Algorithme récursif */
    public NegamaxNode(Board board, int depth, double alpha, double beta, int myBotTurn)
    {
        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double[Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = -Double.MAX_VALUE;


        /* On parcourt toutes les coups possibles */
        for(int i = 0; i < Board.NB_HOLES; i++)
        {
            /* Si le coup est jouable */
            if(board.getPlayerHoles()[(int) sortDescendingDecision[i]] != 0) //(int) sortDescendingDecision[i]
            {

                /* Sélection du coup à jouer */
                double [] decision = new double [Board.NB_HOLES];
                decision [(int) sortDescendingDecision[i]] = 1;//(int) sortDescendingDecision[i]

                try {

                    /* On copie la grille de jeu et on joue le coup sur la copie */
                    Board played = board.playMoveSimulationBoard(myBotTurn, decision);


                    /** Conditions d'arret */
                    /** Noeud terminal ou profondeur max atteinte */
                    if ( (depth >= NegamaxNode.maxDepth) || (played.getScore(myBotTurn) < 0) || (played.getScore (myBotTurn) >= 25) || (played.getNbSeeds () <= 6) ){
                        switch (testHeuristic){
                            case DIFF_SCORE:
                                this.decision [i] = this.diffScore (played);
                                break;
                            case BEST:
                                this.decision [i] = this.best (played, myBotTurn);
                                break;
                            case TEST:
                                this.decision [i] = this.test (played);
                                break;
                        }
                    }
                    else
                    {
                        /* On construit le noeud suivant */
                        NegamaxNode child = new NegamaxNode (played, depth + 1, -beta, -alpha, Board.otherPlayer(myBotTurn));
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

    public static double[] sortIndexDescending(double[] arr) {
        int n = arr.length;
        double[] index = new double[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
        }
        for (int i = 0; i < n-1; i++) {
            int maxIdx = i;
            for (int j = i+1; j < n; j++) {
                if (arr[j] > arr[maxIdx]) {
                    maxIdx = j;
                }
            }
            double tmp = arr[i];
            arr[i] = arr[maxIdx];
            arr[maxIdx] = tmp;
            double tmpIdx = index[i];
            index[i] = index[maxIdx];
            index[maxIdx] = tmpIdx;
        }
        return index;
    }



    public static NegamaxNode iterativeDeepeningNegamax(Board board, double timeLimit, int maxDepth) {

        NegamaxNode bestNode = null;
        long startTime = System.currentTimeMillis();
        for (NegamaxNode.maxDepth = 0; NegamaxNode.maxDepth <= maxDepth  && System.currentTimeMillis() - startTime < timeLimit; NegamaxNode.maxDepth++ ) {
            bestNode = new NegamaxNode(board, 0, -Double.MAX_VALUE, Double.MAX_VALUE, board.getCurrentPlayer());


            //for(int i = 0; i < Board.NB_HOLES; i++)
              //  System.out.println(bestNode.getDecision()[i]);

            /** Move ordering */
            //sortDescendingDecision = sortIndexDescending(bestNode.getDecision());
            //for(int i = 0; i < Board.NB_HOLES; i++)
              //  System.out.println(sortDescendingDecision[i]);

        }

        return bestNode;
    }

    private int test(Board board) {
        return 0;
    }

    private int diffScore (Board board)
    {
        return board.getScore (NegamaxNode.player) - board.getScore (Board.otherPlayer(NegamaxNode.player));
    }

    private int best(Board board, int myTurnBot) {

        int[] seedsPlayer = board.getPlayerHoles(), seedsOpponent = board.getOpponentHoles();

        int res;
        int total2 = 0;

        if(NegamaxNode.player == myTurnBot){
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
        return  res;
    }

    protected static void initialize(Board board, int maxDepth, Bot.HEURISTICS testHeuristic)
    {
        NegamaxNode.maxDepth = maxDepth;
        NegamaxNode.player = board.getCurrentPlayer ();
        NegamaxNode.testHeuristic = testHeuristic;
        /** On instancie le tableau avec une recherche de gauche à droite au départ */
        sortDescendingDecision = new double[Board.NB_HOLES];
        for(int i = 0; i < Board.NB_HOLES; i++)
            sortDescendingDecision[i] = i;
    }

    protected static void initialize (Board board, Bot.HEURISTICS testHeuristic)
    {
        NegamaxNode.player = board.getCurrentPlayer ();
        NegamaxNode.testHeuristic = testHeuristic;
        /** On instancie le tableau avec une recherche de gauche à droite au départ */
        sortDescendingDecision = new double[Board.NB_HOLES];
        for(int i = 0; i < Board.NB_HOLES; i++)
            sortDescendingDecision[i] = i;
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
