import java.util.Random;

import java.util.Random;

class GeneticAlgorithm {
    char[][] initialState;
    char[][] finalState;

    public GeneticAlgorithm(char[][] initialState, char[][] finalState) {
        this.initialState = initialState;
        this.finalState = finalState;
    }

    // initialize population of individuals with random values
    public void initializePopulation() {
    }

    // evaluate the fitness of each individual in the population using the rating
    // system
    public void evaluateFitness() {
    }

    // select the fittest individuals from the population to be the parents for the
    // next generation
    public void selectParents() {
    }

    // generate the next generation of individuals by applying genetic operators to
    // the parents
    public void generateNextGeneration() {
    }

    // repeat the above steps for a predetermined number of generations or until a
    // satisfactory solution is found
    public void run() {
        initializePopulation();
        while (true) {
            evaluateFitness();
            selectParents();
            generateNextGeneration();
        }
    }
}

public class Genetsko {

    public static void main(String[] args) throws Exception {
        Warehouse warehouse = Warehouse.createWareHouse("test_zacetna.txt", "test_koncna.txt");
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            try {
                int x = random.nextInt(warehouse.numCols);
                int y = random.nextInt(warehouse.numCols);
                warehouse.move(x, y);
                System.out.println(warehouse);
                System.out.println(warehouse.stateScore());
                System.out.println("---------------");

            } catch (Exception e) {
                System.out.println("Fail");
            }
        }

    }

}
