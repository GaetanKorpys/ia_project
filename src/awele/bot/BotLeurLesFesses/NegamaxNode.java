package awele.bot.BotLeurLesFesses;

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
    //static Bot.HEURISTICS testHeuristic;

    /** Tableau d'index de décision pour trier les coups afin d'optimiser l'élagae */
    private static double [] sortDecision;

    /** Utilisée pour la fonction d'évaluation */
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
                        /*
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
                        */
                        this.decision[i] = this.heuristic(played, myBotTurn);
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

    /** L'objectif est d'utiliser au mieux le temps d'exéction */
    public static NegamaxNode iterativeDeepeningNegamax(Board board, double timeLimit, int maxDepth) {

        NegamaxNode bestNode = null;
        long startTime = System.currentTimeMillis();
        for (NegamaxNode.maxDepth = 0; /*NegamaxNode.maxDepth <= maxDepth  &&*/ System.currentTimeMillis() - startTime < timeLimit; NegamaxNode.maxDepth++ ) {
            bestNode = new NegamaxNode(board, 0, -Double.MAX_VALUE, Double.MAX_VALUE, board.getCurrentPlayer());

        }
        return bestNode;
    }

    /** Fonction d'évaluation qui prend plusieurs paramètres en compte dans le jeu :
     *      - Case vide : moins de flexibilité dans le jeu et prise consécutive possible
     *      - Case graines < 3 : prise consécutive possible
     *      - Case graines >= 12 : Stratégie du Kroo
     *      - Case graines < 3 consécutives : emplifie le phénomène de prise consécutive
     *
     *      return : La somme de la reward  et ( de la différence du score multipliée par un coefficient pour accentuer ce critère )
     */
    private int heuristic(Board board, int myTurnBot) {

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
            reward += 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getPlayerHoles());
            reward -= 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getOpponentHoles());
            res =  reward + (30 * ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player))));
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
            reward -= 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getPlayerHoles());
            reward += 5 * countConsecutiveHolesWithOneOrTwoSeeds(board.getOpponentHoles());

            /** Multiplication du résultat par - 1 car c'est le tour de l'adversaire */
            res =  reward + (30 * ((board.getScore (NegamaxNode.player)) - board.getScore(Board.otherPlayer(NegamaxNode.player))));
            res = res * -1;
        }
        return  res;
    }

    /** Focntions d'initialisation pour tester dynamiquement l'iterative deepening ou une autre heuristique */
    /*
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
    */

    protected static void initialize(Board board)
    {
        NegamaxNode.player = board.getCurrentPlayer ();

        /** Initialisation du tableau pour un parcours des coups classique : de gauche à droite
         *  Utile uniquement pour l'implémentation du Move Ordering */
        sortDecision = new double[Board.NB_HOLES];
        for (int i = 0; i < Board.NB_HOLES; i++)
            sortDecision[i] = i;
    }

    /**
     * L'évaluation du noeud
     * @return
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
