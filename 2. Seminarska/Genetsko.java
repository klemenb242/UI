import java.util.Random;

import java.util.*;

class GeneticAlgorithm {
    private static int POPULATION_SIZE = 10;

    List<Warehouse> population;

    Warehouse solvedWarehouse;

    int generation = 0;

    char[][] initialState;
    char[][] finalState;

    public GeneticAlgorithm(char[][] initialState, char[][] finalState) {
        this.initialState = initialState;
        this.finalState = finalState;
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
        double maxFitness = 0;
        // Calculate the fitness score for each individual in the population
        for (Warehouse warehouse : population) {
            warehouse.fitness = warehouse.stateScore();
            if (warehouse.fitness > maxFitness) {
                maxFitness = warehouse.fitness;
                if (warehouse.isSolved()) {
                    solvedWarehouse = warehouse;
                    break;
                }
            }
        }
        System.out.println("Max fitness of " + generation + ": " + maxFitness);
    }

    public Warehouse selectParent() {
        // Calculate the total fitness score of all individuals in the population
        double totalFitness = 0;
        for (Warehouse warehouse : population) {
            totalFitness += warehouse.fitness;
        }
        if (totalFitness == 0) {
            return population.get(new Random().nextInt(POPULATION_SIZE));
        }
        // Select the first parent using the roulette wheel selection method
        double randomNumber = new Random().nextDouble() * totalFitness;
        double runningTotal = 0;
        Warehouse parent = null;
        for (Warehouse warehouse : population) {
            runningTotal += warehouse.fitness;
            if (runningTotal > randomNumber) {
                parent = warehouse;
                break;
            }
        }
        return parent;

    }

    public void generateNextGeneration() {
        // sort population by fitness
        population.sort((a, b) -> Double.compare(b.fitness, a.fitness));
        Warehouse parent1 = selectParent();
        Warehouse parent2 = selectParent();
        Warehouse child1 = parent1.deepClone();
        Warehouse child2 = parent2.deepClone();
        // replace child1 and child2 with the worst individuals in the population
        population.set(population.size() - 1, child1);
        population.set(population.size() - 2, child2);

        for (Warehouse warehouse : population) {
            warehouse.makeRandomMove();
        }
        generation++;

    }

    // repeat the above steps for a predetermined number of generations or until a
    // satisfactory solution is found
    public Warehouse run() {
        initializePopulation();
        while (solvedWarehouse == null) {
            evaluateFitness();
            if (solvedWarehouse != null)
                break;
            generateNextGeneration();
        }
        return solvedWarehouse;
    }
}

public class Genetsko {
    static String initialFile = "primer2_zacetna.txt";
    static String finalFile = "primer2_koncna.txt";

    public static void main(String[] args) throws Exception {
        char[][] initialState = Warehouse.readStateFromFile(initialFile);
        char[][] finalState = Warehouse.readStateFromFile(finalFile);

        ArrayList<Warehouse> solutions = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(initialState, finalState);
            solutions.add(geneticAlgorithm.run());
            System.out.println("Found solution in " + geneticAlgorithm.generation + " generations");
        }
        // sort solutions by number of moves
        solutions.sort((a, b) -> Integer.compare(a.getNumberOfMoves(), b.getNumberOfMoves()));
        System.out.println("Best solution found in " + solutions.get(0).getNumberOfMoves() + " moves");
    }

}
