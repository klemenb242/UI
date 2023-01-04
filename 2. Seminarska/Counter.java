public class Counter {
    int numExploredNodes = 0;
    int maxDepth = 0;
    int maxMemory = 0;
    double timeInSeconds = 0;
    // timeInSeconds
    private long startTime = System.currentTimeMillis();
    private long endTime = System.currentTimeMillis();

    public void startTiming() {
        startTime = System.currentTimeMillis();
    }

    public void stopTiming() {
        endTime = System.currentTimeMillis();
        timeInSeconds = (endTime - startTime) / 1000.0;
    }

    public boolean checkMaxDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
        return maxDepth == depth;
    }

    public boolean checkMaxMemory(int memory) {
        maxMemory = Math.max(maxMemory, memory);
        return maxMemory == memory;
    }

    public int incrementExploredNodes() {
        return addToExploredNodes(1);
    }

    public int addToExploredNodes(int num) {
        numExploredNodes += num;
        return numExploredNodes;
    }

    public void reset() {
        numExploredNodes = 0;
        maxDepth = 0;
        maxMemory = 0;
        timeInSeconds = 0;
    }

    public String toString() {
        // every property in a new line
        return "Explored nodes: " + numExploredNodes + "\nMax depth: " + maxDepth + "\nMax memory: " + maxMemory
                + "\nTime in seconds: " + timeInSeconds;
    }

}
