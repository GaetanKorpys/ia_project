package awele.bot.test;

import awele.bot.Bot;
import awele.core.Board;
import awele.core.InvalidBotException;

public class NegamaxNode {

    /** Numéro de joueur de l'IA */
    private static int player;

    /** Profondeur maximale */
    private static int maxDepth;

    /** Profondeur */
    public int depth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;

    /** Test pour les heuristiques */
    static Bot.HEURISTICS testHeuristic;

    /** Tableau d'index de décision trié dans l'ordre décroissant */
    private static double [] sortDecision;


    /** Algorithme Negamax récursif */
    public NegamaxNode(Board board, int depth, double alpha, double beta, int myBotTurn)
    {
        this.depth = depth;
        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double[Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = -Double.MAX_VALUE;


        /* On parcourt toutes les coups possibles */
        for(int i = 0; i < Board.NB_HOLES; i++)
        {
            /* Si le coup est jouable */
            if(board.getPlayerHoles()[(int) sortDecision[i]] != 0)
            {

                /* Sélection du coup à jouer */
                double [] decision = new double [Board.NB_HOLES];
                decision [(int) sortDecision[i]] = 1;

                try {

                    /* On copie la grille de jeu et on joue le coup sur la copie */
                    Board played = board.playMoveSimulationBoard(myBotTurn, decision);


                    /** Conditions d'arret */
                    /** Noeud terminal ou profondeur max atteinte */
                    if ( (depth >= NegamaxNode.maxDepth) || (played.getScore(myBotTurn) < 0) || (Board.otherPlayer( played.getScore (myBotTurn)) >= 25) || (played.getNbSeeds () <= 6) ){

                        switch (testHeuristic){
                            case BEST:
                                this.decision [i] = this.best (played, myBotTurn);
                                break;
                            case TEST:
                                this.decision [i] = this.test (played, myBotTurn);
                                break;
                        }

                    }
                    else
                    {
                        /* On construit le noeud suivant */
                        NegamaxNode child = new NegamaxNode (played, depth + 1, -beta, -alpha, Board.otherPlayer(myBotTurn));

                        /* On récupère l'évaluation du noeud fils */
                        /** On ajoute la négation pour l'algorithme Negamax */
                        this.decision [i] = -child.getEvaluation ();

                    }

                    /* L'évaluation courante du noeud est mise à jour */
                    this.evaluation = Double.max (this.decision [i], this.evaluation);

                    /* Coupe alpha beta */
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

    private int countConsecutiveHolesWithOneOrTwoSeeds(int[] holes) {
        int count = 0;
        int consecutive = 0;
        for (int i = 0; i < holes.length; i++) {
            if (holes[i] <= 2) {
                consecutive++;
                if (consecutive > count) {
                    count = consecutive;
                }
            } else {
                consecutive = 0;
            }
        }
        return count;
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

    public static double[] sortIndexAscending(double[] arr) {
        int n = arr.length;
        double[] index = new double[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
        }
        for (int i = 0; i < n-1; i++) {
            int minIdx = i;
            for (int j = i+1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            double tmp = arr[i];
            arr[i] = arr[minIdx];
            arr[minIdx] = tmp;
            double tmpIdx = index[i];
            index[i] = index[minIdx];
            index[minIdx] = tmpIdx;
        }
        return index;
    }

    public static double[] sort(double[] arr, int myTurnBot, Board board) {
        if(myTurnBot == board.getCurrentPlayer())
            sortDecision = sortIndexDescending(arr);
        else
            sortDecision = sortIndexAscending(arr);
        return sortDecision;
    }



    public static NegamaxNode iterativeDeepeningNegamax(Board board, double timeLimit, int maxDepth) {

        NegamaxNode bestNode = null;
        long startTime = System.currentTimeMillis();
        for (NegamaxNode.maxDepth = 0; /*NegamaxNode.maxDepth <= maxDepth  && */System.currentTimeMillis() - startTime < timeLimit; NegamaxNode.maxDepth++ ) {
            bestNode = new NegamaxNode(board, 0, -Double.MAX_VALUE, Double.MAX_VALUE, board.getCurrentPlayer());

        }
        return bestNode;
    }

    private int test(Board board, int myTurnBot) {

        int[] seedsPlayer = board.getPlayerHoles();
        int[] seedsOpponent = board.getOpponentHoles();

        int res;
        int reward = 0;

        /** Tour de BotLeurLesFesses */
        /** board.getCurrentPlayer() = bot adverse */
        if(NegamaxNode.player == myTurnBot){
            for (int i = 0; i < 6; i++) {
                int seedP = seedsPlayer[i];
                int seedO = seedsOpponent[i];
                if (seedP >= 12)
                    reward -= 30;
                else if (seedP == 0)
                    reward += 50;
                else if (seedP < 3)
                    reward += 40;

                if (seedO >= 12)
                    reward += 30;
                else if (seedO == 0)
                    reward -= 50;
                else if (seedO < 3)
                    reward -= 40;

            }
            //reward += 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getPlayerHoles());
            //reward -= 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getOpponentHoles());
            res =  ( (30* ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player)))) + reward);
        }

        /** Tour de l'adversaire */
        /** board.getCurrentPlayer() = BotLeurLesFesses  */
        else{

            for (int i = 0; i < 6; i++) {
                int seedP = seedsPlayer[i];
                int seedO = seedsOpponent[i];
                if (seedP >= 12)
                    reward += 30;
                else if (seedP == 0)
                    reward -= 50;
                else if (seedP < 3)
                    reward -= 40;

                if (seedO >= 12)
                    reward -= 30;
                else if (seedO == 0)
                    reward += 50;
                else if (seedO < 3)
                    reward += 40;

            }
            //reward -= 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getPlayerHoles());
            //reward += 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getOpponentHoles());
            res =  - 1 *( (30* ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player)))) + reward);
        }
        return  res;
    }

    private int best(Board board, int myTurnBot) {

        int[] seedsPlayer = board.getPlayerHoles();
        int[] seedsOpponent = board.getOpponentHoles();

        int res;
        int reward = 0;

        /** Tour de BotLeurLesFesses */
        /** board.getCurrentPlayer() = bot adverse */
        if(NegamaxNode.player == myTurnBot){
            for (int i = 0; i < 6; i++) {
                int seedP = seedsPlayer[i];
                int seedO = seedsOpponent[i];
                if (seedP >= 12)
                    reward -= 30;
                else if (seedP == 0)
                    reward += 50;
                else if (seedP < 3)
                    reward += 40;

                if (seedO >= 12)
                    reward += 30;
                else if (seedO == 0)
                    reward -= 50;
                else if (seedO < 3)
                    reward -= 40;

            }
            //reward += 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getPlayerHoles());
            //reward -= 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getOpponentHoles());
            res =  ( ( ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player)))) + reward);
        }

        /** Tour de l'adversaire */
        /** board.getCurrentPlayer() = BotLeurLesFesses  */
        else{

            for (int i = 0; i < 6; i++) {
                int seedP = seedsPlayer[i];
                int seedO = seedsOpponent[i];
                if (seedP >= 12)
                    reward += 30;
                else if (seedP == 0)
                    reward -= 50;
                else if (seedP < 3)
                    reward -= 40;

                if (seedO >= 12)
                    reward -= 30;
                else if (seedO == 0)
                    reward += 50;
                else if (seedO < 3)
                    reward += 40;

            }
            //reward -= 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getPlayerHoles());
            //reward += 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getOpponentHoles());
            res =  - 1 *( ( ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player)))) + reward);
        }
        return  res;
    }

    protected static void initialize(Board board, int maxDepth, Bot.HEURISTICS testHeuristic)
    {
        NegamaxNode.maxDepth = maxDepth;
        NegamaxNode.player = board.getCurrentPlayer ();
        NegamaxNode.testHeuristic = testHeuristic;
        /** On instancie le tableau avec une recherche de gauche à droite au départ */
        sortDecision = new double[Board.NB_HOLES];
        for(int i = 0; i < Board.NB_HOLES; i++)
            sortDecision[i] = i;
    }

    protected static void initialize (Board board, Bot.HEURISTICS testHeuristic)
    {
        NegamaxNode.player = board.getCurrentPlayer ();
        NegamaxNode.testHeuristic = testHeuristic;
        /** On instancie le tableau avec une recherche de gauche à droite au départ */
        sortDecision = new double[Board.NB_HOLES];
        for(int i = 0; i < Board.NB_HOLES; i++)
            sortDecision[i] = i;
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
