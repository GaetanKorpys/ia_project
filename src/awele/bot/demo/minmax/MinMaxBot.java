package awele.bot.demo.minmax;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.Date;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class MinMaxBot extends CompetitorBot
{
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 9;

    /** Heuristique choisie */
    private static final HEURISTICS HEURISTIC = HEURISTICS.DIFF_SCORE;


    /**
     * @throws InvalidBotException
     */
    public MinMaxBot() throws InvalidBotException
    {
        this.setBotName ("MinMax & " + HEURISTIC + " & " + MAX_DEPTH);
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
        MinMaxNode.initialize (board, MinMaxBot.MAX_DEPTH, HEURISTIC);
        MinMaxNode minMaxNode = new MaxNode(board,0,0);
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
