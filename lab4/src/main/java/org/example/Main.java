package org.example;

public class Main {
    private static final int INF = Integer.MAX_VALUE;
    private int[][] costMatrix;
    private boolean[] visited;
    private int n;
    private int minCost;

    public Main(int[][] costMatrix) {
        this.costMatrix = costMatrix;
        this.n = costMatrix.length;
        this.visited = new boolean[n];
        this.minCost = INF;
    }

    public static void main(String[] args) {
        int[][] costMatrix = {
                {INF, 6, 4, 8, 12, INF},
                {3, INF, 1, 4, 3, 10},
                {INF, 11, INF, 3, 5, 3},
                {6, 2, 5, INF, 10, 4},
                {6, 7, 5, 2, INF, 6},
                {7, 10, 3, 1, 9, INF}
        };

        Main tsp = new Main(costMatrix);
        int minCost = tsp.findMinCost();
        System.out.println("The minimum Hamiltonian circuit length is: " + minCost);
    }

    public int findMinCost() {
        visited[0] = true;
        branchAndBound(0, 1, 0);
        return minCost;
    }

    private void branchAndBound(int currPos, int count, int cost) {
        if (count == n && costMatrix[currPos][0] != INF) {
            minCost = Math.min(minCost, cost + costMatrix[currPos][0]);
            return;
        }

        for (int i = 0; i < n; i++) {
            if (!visited[i] && costMatrix[currPos][i] != INF) {
                visited[i] = true;
                branchAndBound(i, count + 1, cost + costMatrix[currPos][i]);
                visited[i] = false;
            }
        }
    }
}