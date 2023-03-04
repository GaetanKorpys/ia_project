package awele.bot.mybot;

import awele.bot.Bot;
import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.HashMap;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class EndGameBot extends CompetitorBot
{
    /** Profondeur maximale */
    //Nombre Impair Autrement BUG
    private static final int MAX_DEPTH = 3;
    private  HashMap <Long, MinMaxNode> HashMap;
    
    
    /**
     * @throws InvalidBotException
     */
    public EndGameBot() throws InvalidBotException
    {
        this.setBotName ("ENDGAME V2");
        this.addAuthor ("Vivien KORPYS");
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
        HashMap = new HashMap <Long, MinMaxNode>();
    
    }

    /**
     * Sélection du coup selon l'algorithme MinMax
     */
    @Override
    public double [] getDecision (Board board)
    {
        MinMaxNode.initialize (board, EndGameBot.MAX_DEPTH,HashMap);
    
    
        double[] test=  new MaxNode(board).getDecision ();
 
        HashMap<Long, MinMaxNode> map = MaxNode.getNodes();
        //Boucle while+iterator
        //System.out.println("Boucle while");
        // Iterator iterator = map.entrySet().iterator();
        // while (iterator.hasNext()) {
        //   Map.Entry mapentry = (Map.Entry) iterator.next();
        //     System.out.println("clef: "+mapentry.getKey()
                    //             + " | valeur: " + mapentry.getValue());
        // }
        //System.out.println("Nombre de noeux: "+map.size());
    
        return  test;
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish ()
    {
    }
}
