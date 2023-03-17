package awele.bot.demo.minmax;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class MinMaxIDBot extends CompetitorBot
{
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 8;

    /** Temps d'exécutuion limite */
    private static final int TIME_LIMIT = 100;

    /** Heuristique choisie */
    private static final HEURISTICS HEURISTIC = HEURISTICS.DIFF_SCORE;
	
    /**
     * @throws InvalidBotException
     */
    public MinMaxIDBot() throws InvalidBotException
    {
        this.setBotName ("MinMaxID & " + HEURISTIC);
        this.addAuthor ("Alexandre Blansché");
    }

    /**
     * Rien à faire
     */
    @Override
    public void initialize ()
    {
    }

    /**
     * Pas d'apprentissage
     */
    @Override
    public void learn ()
    {
    }

    /**
     * Sélection du coup selon l'algorithme MinMax
     */
    @Override
    public double [] getDecision (Board board)
    {
        MinMaxNode.initialize(board, HEURISTIC);
        MinMaxNode minMaxNode = MinMaxNode.iterativeDeepeningNegamax(board, MinMaxIDBot.TIME_LIMIT, MinMaxIDBot.MAX_DEPTH);
        return minMaxNode.getDecision();
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish ()
    {
    }
}
