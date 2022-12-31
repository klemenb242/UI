import java.util.Random;

import java.util.*;

class GeneticAlgorithm {
    public static int POPULATION_SIZE = 25;
    public static int DEFAULT_GENERATION_LIMIT = 1000;

    List<Warehouse> population;

    Warehouse solvedWarehouse;

    // If we have a solution, that is better than the best solution we have seen,
    // quit
    int generationLimit = DEFAULT_GENERATION_LIMIT;

    int generation = 0;

    char[][] initialState;
    char[][] finalState;

    public GeneticAlgorithm(char[][] initialState, char[][] finalState, int generationLimit) {
        this.initialState = initialState;
        this.finalState = finalState;
        this.generationLimit = generationLimit;
        this.population = new ArrayList<>();
        solvedWarehouse = null;
    }

    // initialize population of individuals with random values
    public void initializePopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Warehouse warehouse = new Warehouse(initialState, finalState);
            warehouse.makeRandomMove();
            population.add(warehouse);
        }
        generation = 1;
    }

    // evaluate the fitness of each individual in the population using the rating
    // system
    public void evaluateFitness() {
        // Calculate the fitness score for each individual in the population
        for (Warehouse warehouse : population) {
            if (warehouse.isSolved()) {
                solvedWarehouse = warehouse;
                break;
            }
        }

    }

    public void generateNextGeneration() {
        // sort population by fitness
        population.sort((a, b) -> Double.compare(-b.stateScore, -a.stateScore));
        // clone 30% of best performers and replace with worst
        int warehousesToClone = (int) (POPULATION_SIZE * 0.3);
        for (int i = 0; i < warehousesToClone; i++) {
            Warehouse parent = population.get(i);
            Warehouse child = parent.deepClone();
            population.set(population.size() - 1 - i, child);
        }

        for (Warehouse warehouse : population) {
            warehouse.makeRandomMove();
        }
        generation++;

    }

    public Warehouse run() {
        initializePopulation();
        while (generation < generationLimit) {
            evaluateFitness();
            if (solvedWarehouse != null)
                break;
            generateNextGeneration();
        }
        return solvedWarehouse;
    }
}

public class Genetsko {
    static String initialFile = "primer5_zacetna.txt";
    static String finalFile = "primer5_koncna.txt";

    public static void main(String[] args) throws Exception {
        char[][] initialState = Warehouse.readStateFromFile(initialFile);
        char[][] finalState = Warehouse.readStateFromFile(finalFile);

        ArrayList<Warehouse> solutions = new ArrayList<>();
        int generationLimit = GeneticAlgorithm.DEFAULT_GENERATION_LIMIT;

        for (int i = 0; i < 1000; i++) {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(initialState, finalState, generationLimit);
            Warehouse solution = geneticAlgorithm.run();
            if (solution != null && solution.getNumberOfMoves() < generationLimit) {
                generationLimit = solution.getNumberOfMoves();
                solutions.add(solution);
                System.out.println("Found solution in " + geneticAlgorithm.generation + " generations");
            }
        }

        // sort solutions by number of moves
        solutions.sort((a, b) -> Integer.compare(a.getNumberOfMoves(), b.getNumberOfMoves()));
        Warehouse best = solutions.get(0);
        System.out.println(best.isSolved());
        Warehouse temp = new Warehouse(initialState, finalState);
        System.out.println(temp);
        for (Warehouse.Move move : best.getMoves()) {
            System.out.println(move);
            temp.move(move.getFromCol(), move.getToCol());
            System.out.println(temp);
        }
        System.out.println("Best solution found in " + best.getNumberOfMoves() + " moves");
    }

}
