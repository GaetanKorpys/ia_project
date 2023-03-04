package awele.bot.mybot;

import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.HashMap;

/**
 * @author Alexandre Blansché
 * Noeud d'un arbre MinMax
 */
public abstract class MinMaxNode
{
    /** Numéro de joueur de l'IA */
    private static int player;
    
    boolean iaTurn;
    

    
    public static HashMap<Long, MinMaxNode> getNodes() {
        return nodes;
    }
    
    
    /** Table de hachage pour stocker les noeuds et éviter des calculs */
    private static HashMap<Long, MinMaxNode> nodes;
    
    /** Profondeur maximale */
    private static int maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;
    
    /** L'indice du noeud pour la table de hachage */
    private long index;
    
 
    
    /**
     * Constructeur... 
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     */
    
    /**
     * Returns an unique hash identifier for the current board. The
     * produced hash code is a 44 bit number which uniquely identifies
     * a position and turn.
     *
     * @return  The hash code value for this object
     */
    public long hash(Board board) {
    long hash=9;
    hash =hash*10;
    
        int total = 0;
        for (int i = 0; i < 6; i++) {
            int Player = board.getPlayerHoles()[i];
            hash = hash + Player;
            hash =hash*10;
            int Opponent = board.getOpponentHoles()[i];
            hash = hash + Opponent;
            hash =hash*10;
            
        }
        if(iaTurn){
        hash = hash + board.getScore (MinMaxNode.player);
        hash =hash*10;
        hash = hash + board.getScore (Board.otherPlayer (MinMaxNode.player));}
        else {
            hash = hash + board.getScore (Board.otherPlayer (MinMaxNode.player));
            hash =hash*10;
            hash = hash + board.getScore (MinMaxNode.player);
    
        }
        return hash;
    }
    
    

    
    public MinMaxNode (Board board, int depth, double alpha, double beta)
    {
    
        this.index = hash(board);//134234
        //if (depth==0)
            //MinMaxNode.nodes.put (this.index, this);
    
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
                    if ((score < 0) ||
                            (copy.getScore (Board.otherPlayer (copy.getCurrentPlayer ())) >= 25) ||
                            (copy.getNbSeeds () <= 6))
                        this.decision [i] = this.diffScore (copy);
                    /* Sinon, on explore les coups suivants */
                    else
                    {
                        /* Si la profondeur maximale n'est pas atteinte */
                        if (depth < MinMaxNode.maxDepth)
                        {
                            /* On récupère l'indice du nouvel état du plateau de jeu */
                            long index = hash (copy);
                            /* Et on recherche le noeud correspondant dans la liste des noeuds déjà calculés */
                            MinMaxNode child = MinMaxNode.getNode (index);
                            /* Si le noeud n'a pas encore été calculé, on le construit */
    
                            if (child == null){
                                 /* On construit le noeud suivant */
                                child = this.getNextNode (copy, depth + 1, alpha, beta);
    
                            }
                            /* On récupère l'évaluation du noeud fils */
                            this.decision [i] = child.getEvaluation ();
                        }
                        /* Sinon (si la profondeur maximale est atteinte), on évalue la situation actuelle */
                        else
                            this.decision [i] = this.diffScore (copy);
                    }
                    /* L'évaluation courante du noeud est mise à jour, selon le type de noeud (MinNode ou MaxNode) */
                    
                    this.evaluation = this.minmax (this.decision [i], this.evaluation);
    
    
                    /* Si l'évaluation actuelle est égale à l'optimalité pour le type de noeud, inutile de continuer */
                    //if ((depth > 0) && (this.evaluation == this.getBestEvaluation ()))
                      //      break;
                    
                    
                    /* Coupe alpha-beta */ 
                    if (depth > 0)
                    {
                        if (this.alphabeta (this.evaluation, alpha, beta))
                           break;
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
    
    public long getIndex(Board board)
    {
        return this.index;
    }
    
    
    /**
     * Récupération d'un noeud déjà calculé
     * @param index L'indice du noeud
     * @return Le noeud qui a l'indice indiqué ou null s'il n'a pas encore été calculé
     */
    public static MinMaxNode getNode (long index)
    {
        return MinMaxNode.nodes.get (index);
    }
    
    /**
     * @return Le nombre de noeuds calculées
     */
    public static int getNbNodes ()
    {
        return MinMaxNode.nodes.size ();
    }
    
    
    
    /** Pire score pour un joueur */
    protected abstract double worst ();

    /**
     * Initialisation
     */
    protected static void initialize (Board board, int maxDepth,HashMap <Long, MinMaxNode> HashMap )
    {
        MinMaxNode.nodes = HashMap;
        MinMaxNode.maxDepth = maxDepth;
        MinMaxNode.player = board.getCurrentPlayer ();
    }

    private int diffScore (Board board){
    
        int score;
        int seeds;
    
        score = 25 * (board.getScore (MinMaxNode.player) - board.getScore (Board.otherPlayer (MinMaxNode.player)));
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
    protected abstract MinMaxNode getNextNode (Board board, int depth, double alpha, double beta);

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
