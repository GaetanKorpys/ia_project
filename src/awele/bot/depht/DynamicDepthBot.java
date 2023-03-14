package awele.bot.depht;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.bot.negatest.NegamaxNode;
import awele.core.Board;
import awele.core.InvalidBotException;

public class DynamicDepthBot extends DemoBot {

    public DynamicDepthBot() throws InvalidBotException
    {
        this.setBotName ("DynamicDepthBot");
        this.addAuthor ("Gaetan Korpys");
    }


    @Override
    public void initialize() {

    }

    @Override
    public void finish() {

    }

    @Override
    public double[] getDecision(Board board) {

        return new DynamicDepthNode(board, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), -9999, 9999, System.currentTimeMillis()).getDecision();
    }

    @Override
    public void learn() {

    }
}
