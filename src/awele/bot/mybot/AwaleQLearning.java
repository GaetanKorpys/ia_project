package awele.bot.mybot;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.bot.demo.first.FirstBot;
import awele.bot.demo.knn1.Knn1Bot;
import awele.bot.demo.knn2.Knn2Bot;
import awele.bot.demo.last.LastBot;
import awele.bot.random.RandomBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;
import awele.run.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Random;

public class AwaleQLearning extends DemoBot {

    // Définir les constantes pour la taille de la grille de jeu et le nombre d'actions possibles
    //private static final int NUM_STATES = 1024;
    private static final int NUM_ACTIONS = 6;

    // Définir les constantes pour le taux d'apprentissage, le facteur de récompense et le facteur d'exploration
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.9;
    private static final double EXPLORATION_RATE = 0.1;

    //Nombre de test pour l'apprentissage du bot
    private static final int NUM_TESTS = 1000;

    private static final int MAX_LEARNING_TIME = 1000 * 60 * 70 * 1; // 1 h
    private static final int MAX_DECISION_TIME = 200; // 100 ms
    private static final int MAX_MEMORY = 1024 * 1024 * 64; // 64 MiB
    private static final int MAX_TOTAL_MEMORY = 1024 * 1024 * 1024; // 1 GiB

    private boolean hasPlayed;

    private int lastAction;
    private int[] lastState;
    private Board lastBoard;


    // Initialiser la Q-Table
   // private double[][] qTable;

    // La table Q est maintenant une table de hachage
    private HashMap<Integer, double[]> qTable;

    // Générateur de nombres aléatoires
    private Random random;

    // Évaluation des coups selon la Q-Table
    private double [] decision;

    private static final String Q_TABLE_FILE_NAME = "qTable.json";

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

    public AwaleQLearning() throws InvalidBotException {
        this.setBotName ("QLearning");
        this.addAuthor ("Gaëtan Korpys");
        this.addAuthor ("Théo Rousseau");

        // Initialisation de la Q-Table à 0
        //qTable = new double[NUM_STATES][NUM_ACTIONS];
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
    public void initialize() {


    }

    @Override
    public void finish() {

    }


    // Fonction pour obtenir le hash d'un état de jeu
    private int getHash(int[] state) {
        return Arrays.hashCode(state);
    }

    /*
    // Initialiser la Q-Table avec des valeurs aléatoires
    private void initializeQTable() {
        for (int i = 0; i < NUM_STATES; i++) {
            double[] qValues = new double[NUM_ACTIONS];
            for (int j = 0; j < NUM_ACTIONS; j++) {
                qValues[j] = Math.random();
            }
            qTable.put(i, qValues);
        }
    }
    */
    // Obtenir les valeurs de Q pour un état donné
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
            /*
            // Mise à jour de la Q
            int state = getHash(lastState);
            int newState = getHash(allPits);
            double[] qValues = qTable[state];
            double[] newQValues = qTable[newState];
            double maxNewQValue = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < NUM_ACTIONS; i++) {
                if (newQValues[i] > maxNewQValue) {
                    maxNewQValue = newQValues[i];
                }
            }
            double updatedQValue = qValues[lastAction] + LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxNewQValue - qValues[lastAction]);
            qValues[lastAction] = updatedQValue;
            qTable[state] = qValues;

            // Mettre à jour la Q-Table en fonction de l'action, de l'état et de la récompense
            //updateQTable(board.getPlayerHoles(), lastAction, reward);
            */
        }

        hasPlayed = true;
        lastAction = bestHole;
        lastState = board.getPlayerHoles();
        lastBoard = (Board) board.clone();
        return this.getDecision(bestHole);
    }

    /*
    public double[] getActionValues(int[] board) {
        int state = getHash(board);
        if (!QTable.containsKey(state)) {
            QTable.put(state, new double[NUM_ACTIONS]);
        }
        return QTable.get(state);
    }

    public void updateQ(int[] board, int action, double value) {
        int state = encodeState(board);
        if (!qTable.containsKey(state)) {
            qTable.put(state, new double[NUM_ACTIONS]);
        }
        double[] actionValues = qTable.get(state);
        actionValues[action] += value;
    }
    */
    /*
    public int getHash(int[] board) {
        int hash = 17;
        int multiplier = 31;
        for (int i = 0; i < board.length; i++) {
            hash = hash * multiplier + board[i];
        }
        return hash;
    }
    */

    private double[] getDecision(int indexHolePlayed) {
        for(int i = 0; i<Board.NB_HOLES; i++)
            decision[i] = 0;
        decision[(int) indexHolePlayed] = 1;
        return decision;
    }


    @Override
    public void learn() {
        long startTime = System.currentTimeMillis();
        int episodeCount = 0; // nombre d'épisodes joués


        try {

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

            /*
            while (episodeCount < 10 && System.currentTimeMillis() - startTime < MAX_LEARNING_TIME) {
                episodeCount++;

                awele.play ();
                System.out.println("last: " + episodeCount);

            }
            */
        } catch (InvalidBotException e) {
            e.printStackTrace();
        }
    }

    /*
    // Fonction d'encodage de l'état du jeu en un entier unique
    private int encodeState(int[] board) {
        int state = 0;
        for (int i = 0; i < 6; i++) {
            state |= (board[i] & 0x03) << (i * 2);
        }
        for (int i = 6; i < 12; i++) {
            state |= (board[i] & 0x03) << ((i - 6) * 2 + 12);
        }
        return state;
    }
    */

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
    /*
    // Fonction pour choisir une action à partir d'un état donné
    private int chooseAction(int[] pits) {
        int state = getHash(pits);
        double[] qValues = qTable[state];

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

    */
    /*
    // Fonction pour choisir l'action suivante à partir de la Q-Table
    private int chooseAction(int gameState) {
        int action;
        Random rand = new Random();

        // Utiliser l'exploration avec une probabilité EXPLORATION_RATE
        if (rand.nextDouble() < EXPLORATION_RATE) {
            action = rand.nextInt(NUM_ACTIONS);
        }
        // Sinon, choisir l'action avec la plus grande valeur dans la Q-Table
        else {
            action = 0;
            for (int i = 1; i < NUM_ACTIONS; i++) {
                if (qTable[gameState][i] > qTable[gameState][action]) {
                    action = i;
                }
            }
        }
        return action;
    }
    */


    /*
    private int getAction(int[] gameState) {
        double maxQ = Double.NEGATIVE_INFINITY;
        int bestAction = -1;

        for (int action = 0; action < NUM_ACTIONS; action++) {
            double qValue = qTable[getStateIndex(gameState)][action];
            if (qValue > maxQ) {
                maxQ = qValue;
                bestAction = action;
            }
        }

        return bestAction;
    }*/

    /*
    // Fonction pour mettre à jour la Q-Table en fonction de l'action et de la récompense obtenues
    private void updateQTable(int[] prevState, int action, double reward) {

        // Mise à jour de la Q
        int state = encodeState(pits);
        int newState = encodeState(newPits);
        double[] qValues = qTable[state];
        double[] newQValues = qTable[newState];
        double maxNewQValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < NUM_ACTIONS; i++) {
            if (newQValues[i] > maxNewQValue) {
                maxNewQValue = newQValues[i];
            }
        }
        double updatedQValue = qValues[action] + LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxNewQValue - qValues[action]);
        qValues[action] = updatedQValue;
        qTable[state] = qValues;


        double maxQ = qTable[nextState][0];
        for (int i = 1; i < NUM_ACTIONS; i++) {
            if (qTable[nextState][i] > maxQ) {
                maxQ = qTable[nextState][i];
            }
        }
        qTable[prevState][action] += LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxQ - qTable[prevState][action]);
    }

    // Fonction principale pour exécuter l'apprentissage par renforcement
    public void train(int numEpisodes) {
        for (int i = 0; i < numEpisodes; i++) {
            // Initialiser l'état du jeu
            int[] gameState = new int[GRID_SIZE];

            // Jouer le jeu jusqu'à la fin de l'épisode
            while (!isEndOfEpisode(gameState)) {
                // Choisir l'action suivante
                int action = chooseAction(gameState);

                // Mettre à jour l'état du jeu en fonction de l'action choisie
                int[] nextState = getNextState(gameState, action);

                // Obtenir la récompense pour l'action choisie
                double reward = getReward(gameState, action);

                // Mettre à jour la Q-Table en fonction de l'action, de l'état et de la récompense
                updateQTable(gameState, action, nextState, reward);

                // Mettre à jour l'état du jeu
                gameState = nextState;
            }
        }
    }

    // Fonction pour déterminer si la partie est terminée
    private boolean isEndOfEpisode(int[] gameState) {
        // TODO:
        // Vérifier si toutes les cases de l'adversaire sont vides
        boolean opponentEmpty = true;
        for (int i = GRID_SIZE / 2; i < GRID_SIZE; i++) {
            if (gameState[i] != 0) {
                opponentEmpty = false;
                break;
            }
        }
        if (opponentEmpty) {
            return true;
        }

        // Si aucune des conditions ci-dessus n'est remplie, le jeu continue
        return false;
    }

    /*
    // Fonction pour obtenir l'état suivant en fonction de l'action choisie
    private int[] getNextState(int[] gameState, int action) {
        int[] nextState = gameState.clone();

        // Distribuer les graines autour de la grille dans le sens horaire
        int seeds = nextState[action];
        nextState[action] = 0;
        int currentPos = action;
        while (seeds > 0) {
            currentPos = (currentPos + 1) % GRID_SIZE;
            if (currentPos != action + GRID_SIZE / 2) {
                nextState[currentPos]++;
                seeds--;
            }
        }

        // Récolter les graines si possible
        int endPos = currentPos;
        boolean captured = false;
        while (nextState[currentPos] == 2 || nextState[currentPos] == 3) {
            if (currentPos < GRID_SIZE / 2) {
                nextState[GRID_SIZE / 2 - 1] += nextState[currentPos];
            } else {
                nextState[GRID_SIZE - 1] += nextState[currentPos];
            }
            nextState[currentPos] = 0;
            captured = true;
            currentPos = (currentPos - 1 + GRID_SIZE) % GRID_SIZE;
        }

        // Si l'adversaire a des graines dans la case en face, les capturer aussi
        if (captured && currentPos >= GRID_SIZE / 2 && currentPos < GRID_SIZE - 1 && nextState[currentPos] == 1) {
            int oppositePos = GRID_SIZE - currentPos - 2;
            nextState[GRID_SIZE / 2 - 1] += nextState[oppositePos] + 1;
            nextState[currentPos] = 0;
            nextState[oppositePos] = 0;
        }

        return nextState;
    }
     */



    /*
    // Fonction pour jouer une partie complète avec la stratégie apprise
    public void play() {
        // Initialiser l'état du jeu
        int[] gameState = new int[GRID_SIZE];

        // Jouer le jeu jusqu'à la fin de la partie
        while (!isEndOfEpisode(gameState)) {
            // Choisir l'action à effectuer en fonction de la Q-Table
            int action = chooseAction(gameState);

            // Mettre à jour la Q-Table
            updateQTable(gameState, action);

            // Obtenir la récompense pour l'action choisie
            double reward = getReward(gameState, action);

            // Obtenir l'état suivant en fonction de l'action choisie
            int[] nextState = getNextState(gameState, action);

            // Mettre à jour l'état du jeu
            gameState = nextState;
        }
    }*/


}

