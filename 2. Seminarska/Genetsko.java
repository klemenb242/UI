import java.util.Random;
import java.util.*;

class GeneticAlgorithm {
    private static int POPULATION_SIZE = 10;

    List<Warehouse> population;

    char[][] initialState;
    char[][] finalState;

    public GeneticAlgorithm(char[][] initialState, char[][] finalState) {
        this.initialState = initialState;
        this.finalState = finalState;
        this.population = new ArrayList<>();
    }

    // initialize population of individuals with random values
    public void initializePopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Warehouse warehouse = new Warehouse(initialState, finalState);
            Warehouse.Move move = warehouse.makeRandomMove();
            System.out.println(warehouse);
            System.out.println(warehouse.stateScore());
            population.add(warehouse);
        }
    }

    // evaluate the fitness of each individual in the population using the rating
    // system
    public void evaluateFitness() {
        // Calculate the fitness score for each individual in the population
        for (Warehouse warehouse : population) {
            warehouse.fitness = warehouse.stateScore();
        }
    }

    public Warehouse[] selectParents() {
        // Calculate the total fitness score of all individuals in the population
        int totalFitness = 0;
        for (Warehouse warehouse : population) {
            totalFitness += warehouse.fitness;
        }

        // Select the first parent using the roulette wheel selection method
        int randomNumber = new Random().nextInt(totalFitness);
        int runningTotal = 0;
        Warehouse parent1 = null;
        for (Warehouse warehouse : population) {
            runningTotal += warehouse.fitness;
            if (runningTotal > randomNumber) {
                parent1 = warehouse;
                break;
            }
        }

        // Select the second parent using the roulette wheel selection method
        randomNumber = new Random().nextInt(totalFitness);
        runningTotal = 0;
        Warehouse parent2 = null;
        for (Warehouse warehouse : population) {
            runningTotal += warehouse.fitness;
            if (runningTotal > randomNumber) {
                parent2 = warehouse;
                break;
            }
        }
        return new Warehouse[] { parent1, parent2 };
    }

    public void generateNextGeneration(Warehouse[] parents) {
    }

    // repeat the above steps for a predetermined number of generations or until a
    // satisfactory solution is found
    public void run() {
        initializePopulation();
        while (true) {
            evaluateFitness();
            Warehouse[] parents = selectParents();
            generateNextGeneration(parents);
        }
    }
}

public class Genetsko {
    static String initialFile = "primer1_zacetna.txt";
    static String finalFile = "primer1_koncna.txt";

    public static void main(String[] args) throws Exception {
        char[][] initialState = Warehouse.readStateFromFile(initialFile);
        char[][] finalState = Warehouse.readStateFromFile(finalFile);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(initialState, finalState);
        geneticAlgorithm.run();
    }

}
