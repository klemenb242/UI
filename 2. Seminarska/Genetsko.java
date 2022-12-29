import java.util.Random;

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
