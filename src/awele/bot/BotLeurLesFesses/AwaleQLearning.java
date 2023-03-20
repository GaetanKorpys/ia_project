package awele.bot.BotLeurLesFesses;

import awele.core.Board;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


/** Classe à ne pas utiliser pour le tournoi !
 *  Il s'agit de notre idée initiale pour le projet qui consiste à utiliser l'apprentissage par renforcement.
 *  Nous tenions à partager le début du code même si le bot n'a finalement pas été implémenté */
public class AwaleQLearning { //extends CompetitorBot

    // Définir les constantes pour la taille de la grille de jeu et le nombre d'actions possibles
    private static final int NUM_ACTIONS = 6;
    private static final int MAX_LEARNING_TIME = 1000 * 60 * 70 * 1; // 1 h

    // Définir les constantes pour le taux d'apprentissage, le facteur de récompense et le facteur d'exploration
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.9;
    private static final double EXPLORATION_RATE = 0.1;

    private boolean hasPlayed;

    private int lastAction;
    private int[] lastState;
    private Board lastBoard;

    // La table Q est maintenant une table de hachage
    private HashMap<Integer, double[]> qTable;

    // Générateur de nombres aléatoires
    private Random random;

    // Évaluation des coups selon la Q-Table
    private double [] decision;

    private static final String Q_TABLE_FILE_NAME = "qTable.json";

    /** Code à commenter en dessous si la classe ne doit pas extends DemoBot ou CompetitiorBot pour ne pas perturber le tournoi ou les tests
     *
     * -------- DEBUT ICI -------- */
    /*
    public AwaleQLearning() throws InvalidBotException {
        this.setBotName ("QLearning");
        this.addAuthor ("Gaëtan Korpys");
        this.addAuthor ("Théo Rousseau");

        // Initialisation de la Q-Table
        qTable = new HashMap<Integer, double[]>();
        lastState = new int[12];

        // Initialisation du générateur de nombres aléatoires
        random = new Random();

        decision = new double [Board.NB_HOLES];

        hasPlayed = false;

        try {
            loadQTableFromJson();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void learn() {
        long startTime = System.currentTimeMillis();
        int episodeCount = 0; // nombre d'épisodes joués


        try {

            //Les advsersaires pour l'apprentissage
            LastBot lastBot = new LastBot();
            lastBot.learn ();

            Knn1Bot knn1Bot  = new Knn1Bot();
            knn1Bot.learn ();

            Knn2Bot knn2Bot = new Knn2Bot();
            knn2Bot.learn ();

            AwaleQLearning qLearning = new AwaleQLearning();

            Awele awele = new Awele (qLearning, lastBot);
            awele.play ();

            qLearning.printQtable();

            qLearning.saveQTableToJson(qTable);


            while (System.currentTimeMillis() - startTime < MAX_LEARNING_TIME) {
                episodeCount++;

                awele.play ();
                System.out.println("last: " + episodeCount);

            }

        } catch (InvalidBotException e) {
            e.printStackTrace();
        }
    }



    @Override
    public double[] getDecision(Board board) {

        int[] allPits = new int[12];
        for (int i = 0; i<board.NB_HOLES; i++)
        {
            allPits[i] = board.getPlayerHoles()[i];
            allPits[i+6] = board.getOpponentHoles()[i];
        }

        //Renvoi la meilleur valeur contenue dans la Q-Table
        int bestHole = chooseAction(allPits);

        if(hasPlayed)
        {

            // Obtenir la récompense pour l'action choisie
            double reward = getReward(board, lastBoard);

            updateQTable(lastState, lastAction, allPits, reward);

            //Mise à jour de la Q-Table


        }

        hasPlayed = true;
        lastAction = bestHole;
        lastState = board.getPlayerHoles();
        lastBoard = (Board) board.clone();
        return this.getDecision(bestHole);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void finish() {
    }
    */
    /** -------- FIN ICI -------- */

    private double[] getDecision(int indexHolePlayed) {
        for(int i = 0; i<Board.NB_HOLES; i++)
            decision[i] = 0;
        decision[(int) indexHolePlayed] = 1;
        return decision;
    }

    // Sauvegarde la Q-Table dans un fichier JSON
    public static void saveQTableToJson(HashMap<Integer, double[]> qTable) {
        try {
            // Crée un objet Gson pour formater la Q-Table en JSON
            Gson gson = new GsonBuilder().create();

            // Crée un flux de sortie pour écrire dans le fichier JSON
            FileWriter writer = new FileWriter(Q_TABLE_FILE_NAME);

            // Convertit la Q-Table en JSON et l'écrit dans le fichier
            gson.toJson(qTable, writer);

            // Ferme le flux de sortie
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialise la Q-Table à partir d'un fichier JSON
    public static HashMap<Integer, double[]> loadQTableFromJson() {
        HashMap<Integer, double[]> qTable = new HashMap<>();
        try {
            // Crée un objet Gson pour lire le fichier JSON
            Gson gson = new GsonBuilder().create();

            // Crée un flux d'entrée pour lire le fichier JSON
            FileReader reader = new FileReader(Q_TABLE_FILE_NAME);

            // Obtient le type de la Q-Table (HashMap<Integer, double[]>) à partir du type de la classe
            Type qTableType = new TypeToken<HashMap<Integer, double[]>>() {}.getType();

            // Convertit le JSON en Q-Table
            qTable = gson.fromJson(reader, qTableType);

            // Ferme le flux d'entrée
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier de la Q-Table n'existe pas. Une nouvelle Q-Table a été créée.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return qTable;
    }


    /** Fonction pour obtenir le hash d'un état de jeu */
    private int getHash(int[] state) {
        return Arrays.hashCode(state);
    }

    /** Obtenir les valeurs de Q pour un état donné */
    private double[] getQValues(int[] state) {
        int hash = getHash(state);
        if (!qTable.containsKey(hash)) {
            double[] qValues = new double[NUM_ACTIONS];
            qTable.put(hash, qValues);
        }
        return qTable.get(hash);
    }

    // Mettre à jour la Q-Table en fonction de l'action choisie et de la récompense obtenue
    private void updateQTable(int[] lastState, int lastAction, int[] allPits, double reward) {
        double[] qValues = getQValues(lastState);
        double[] newQValues = getQValues(allPits);
        double maxNewQValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < NUM_ACTIONS; i++) {
            if (newQValues[i] > maxNewQValue) {
                maxNewQValue = newQValues[i];
            }
        }
        double updatedQValue = qValues[lastAction] + LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxNewQValue - qValues[lastAction]);
        qValues[lastAction] = updatedQValue;
        qTable.put(getHash(lastState), qValues);
    }

    public void printQtable()
    {
        System.out.println(qTable);
    }


    // Fonction pour déterminer si un coup est valide
    private boolean isMoveValid(int[] pits, int action) {
        int stones = pits[action];
        if (stones == 0) {
            return false;
        }
        return true;
    }


    // Fonction pour obtenir la récompense pour l'action choisie
    private double getReward(Board board, Board lastBoard) {
        int indexPlayer = board.getCurrentPlayer();
        double reward = board.getScore(indexPlayer) - lastBoard.getScore(indexPlayer);
        return reward;
    }

    // Fonction pour choisir une action à partir d'un état donné
    private int chooseAction(int[] pits) {
        int state = getHash(pits);
        //double[] qValues = qTable.getOrDefault(state, new double[NUM_ACTIONS]);
        double[] qValues = getQValues(pits);
        // Exploration (choix aléatoire d'une action)
        if (random.nextDouble() < EXPLORATION_RATE) {
            int action;
            do {
                action = random.nextInt(NUM_ACTIONS);
            } while (!isMoveValid(pits, action));
            return action;
        }

        // Exploitation (choix de la meilleure action)
        else {
            int bestAction = -1;
            double maxQValue = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < NUM_ACTIONS; i++) {
                if (isMoveValid(pits, i)) {
                    if (qValues[i] > maxQValue) {
                        maxQValue = qValues[i];
                        bestAction = i;
                    }
                }
            }
            return bestAction;
        }
    }

}

