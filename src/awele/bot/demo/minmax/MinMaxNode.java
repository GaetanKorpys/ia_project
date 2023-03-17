package awele.bot.demo.minmax;

import awele.bot.Bot;
import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Noeud d'un arbre MinMax
 */
public abstract class MinMaxNode
{
    /** Numéro de joueur de l'IA */
    private static int player;

    /** Profondeur maximale */
    private static int maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;

    /** Test pour les heuristiques */
    static CompetitorBot.HEURISTICS testHeuristic;

    /**
     * Constructeur... 
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     */
    public MinMaxNode (Board board, int depth, double alpha, double beta, double limitTime, double startTime)
    {
        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double [Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = this.worst ();
        /* On parcourt toutes les coups possibles */
        for (int i = 0; i < Board.NB_HOLES; i++)
            /* Si le coup est jouable */
            if (board.getPlayerHoles () [i] != 0)
            {
                /* Sélection du coup à jouer */
                double [] decision = new double [Board.NB_HOLES];
                decision [i] = 1;
                /* On copie la grille de jeu et on joue le coup sur la copie */
                Board copy = (Board) board.clone ();
                try
                {
                    int score = copy.playMoveSimulationScore (copy.getCurrentPlayer (), decision);
                    copy = copy.playMoveSimulationBoard (copy.getCurrentPlayer (), decision);
                    /* Si la nouvelle situation de jeu est un coup qui met fin à la partie,
                       on évalue la situation actuelle */   
                    if ((score < 0) || (copy.getScore (Board.otherPlayer (copy.getCurrentPlayer ())) >= 25) || (copy.getNbSeeds () <= 6) ){
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
                    else
                    {
                        /* Si la profondeur maximale n'est pas atteinte */
                        if (depth < MinMaxNode.maxDepth)
                        {
                            /* On construit le noeud suivant */
                            MinMaxNode child = this.getNextNode (copy, depth + 1, alpha, beta, limitTime - (System.currentTimeMillis() - startTime), startTime);
                            /* On récupère l'évaluation du noeud fils */
                            this.decision [i] = child.getEvaluation ();
                        }
                        /* Sinon (si la profondeur maximale est atteinte), on évalue la situation actuelle */
                        else
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
                    /* L'évaluation courante du noeud est mise à jour, selon le type de noeud (MinNode ou MaxNode) */
                    this.evaluation = this.minmax (this.decision [i], this.evaluation);

                    if(this.alphabeta(this.evaluation, alpha, beta))
                        break;

                    /* Coupe alpha-beta */ 
                    if (depth > 0)
                    {
                        alpha = this.alpha (this.evaluation, alpha);
                        beta = this.beta (this.evaluation, beta);
                    }                        
                }
                catch (InvalidBotException e)
                {
                    this.decision [i] = 0;
                }
            }
    }



    public static MinMaxNode iterativeDeepeningNegamax(Board board, double timeLimit, int maxDepth) {
        MinMaxNode bestNode = null;
        long startTime = System.currentTimeMillis();
        for ( MinMaxNode.maxDepth = 0; MinMaxNode.maxDepth <= maxDepth  /*&& System.currentTimeMillis() - startTime < timeLimit*/; MinMaxNode.maxDepth++ ) {
            bestNode = new MaxNode(board,0, 0);
        }

        //bestNode = new MaxNode(board, 0, 0);
        return bestNode;
    }

    private int test(Board board) {
        return 0;
    }

    private int diffScore (Board board)
    {
        return board.getScore (MinMaxNode.player) - board.getScore (Board.otherPlayer (MinMaxNode.player));
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
        return (25 * (board.getScore (MinMaxNode.player) - board.getScore(Board.otherPlayer (MinMaxNode.player)))) - total;
    }


    /** Pire score pour un joueur */
    protected abstract double worst ();

    /**
     * Initialisation
     */
    protected static void initialize(Board board, int maxDepth, Bot.HEURISTICS testHeuristic)
    {
        MinMaxNode.maxDepth = maxDepth;
        MinMaxNode.player = board.getCurrentPlayer ();
        MinMaxNode.testHeuristic = testHeuristic;
    }

    protected static void initialize (Board board, Bot.HEURISTICS testHeuristic)
    {
        MinMaxNode.player = board.getCurrentPlayer ();
        MinMaxNode.testHeuristic = testHeuristic;
    }


    /**
     * Mise à jour de alpha
     * @param evaluation L'évaluation courante du noeud
     * @param alpha L'ancienne valeur d'alpha
     * @return
     */
    protected abstract double alpha (double evaluation, double alpha);

    /**
     * Mise à jour de beta
     * @param evaluation L'évaluation courante du noeud
     * @param beta L'ancienne valeur de beta
     * @return
     */
    protected abstract double beta (double evaluation, double beta);

    /**
     * Retourne le min ou la max entre deux valeurs, selon le type de noeud (MinNode ou MaxNode)
     * @param eval1 Un double
     * @param eval2 Un autre double
     * @return Le min ou la max entre deux valeurs, selon le type de noeud
     */
    protected abstract double minmax (double eval1, double eval2);

    /**
     * Indique s'il faut faire une coupe alpha-beta, selon le type de noeud (MinNode ou MaxNode)
     * @param eval L'évaluation courante du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un booléen qui indique s'il faut faire une coupe alpha-beta
     */
    protected abstract boolean alphabeta (double eval, double alpha, double beta);

    /**
     * Retourne un noeud (MinNode ou MaxNode) du niveau suivant
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un noeud (MinNode ou MaxNode) du niveau suivant
     */
    protected abstract MinMaxNode getNextNode (Board board, int depth, double alpha, double beta, double limitTime, double startTime);

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
