package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneMoreTest {
    private static final int INF = Integer.MAX_VALUE;

    public static void main(String[] args) {
        int[][] cost = {
                {25, 12, 21, 29, 28},
                {29, 21, 19, 28, 28},
                {41, 37, 32, 27, 18},
                {38, 33, 45, 28, 35},
                {44, 17, 25, 22, 52}
        };
        int[] supply = {65, 52, 28, 52, 47};
        int[] demand = {28, 62, 38, 62, 52};

        int[][] result = northwestCornerMethod(cost, supply, demand);
        System.out.println("Initial Solution (Northwest Corner Method):");
        printMatrix(result);

        int minCost = calculateTotalCost(result, cost);
        System.out.println("Initial Cost: " + minCost);

        optimizeUsingMODI(result, cost, supply, demand);
        System.out.println("Optimized Solution (MODI Method):");
        printMatrix(result);

        minCost = calculateTotalCost(result, cost);
        System.out.println("Optimized Cost: " + minCost);
    }

    private static int[][] northwestCornerMethod(int[][] cost, int[] supply, int[] demand) {
        int[][] allocation = new int[supply.length][demand.length];
        int i = 0, j = 0;

        while (i < supply.length && j < demand.length) {
            if (supply[i] < demand[j]) {
                allocation[i][j] = supply[i];
                demand[j] -= supply[i];
                supply[i] = 0;
                i++;
            } else {
                allocation[i][j] = demand[j];
                supply[i] -= demand[j];
                demand[j] = 0;
                j++;
            }
        }
        return allocation;
    }

    private static void optimizeUsingMODI(int[][] allocation, int[][] cost, int[] supply, int[] demand) {
        int m = supply.length;
        int n = demand.length;
        int[] u = new int[m];  // Potentials for rows
        int[] v = new int[n];  // Potentials for columns
        Arrays.fill(u, INF);
        Arrays.fill(v, INF);

        boolean optimized = false;

        while (!optimized) {
            // Step 1: Calculate u and v using allocated cells
            calculatePotentials(allocation, cost, u, v);

            // Step 2: Calculate improvement indices
            int[][] delta = calculateImprovementIndices(allocation, cost, u, v);

            // Step 3: Check if all delta values are non-negative
            int minDelta = 0;
            int[] minCell = {-1, -1};  // Row and column of the minimum delta
            optimized = true;

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (allocation[i][j] == 0 && delta[i][j] < minDelta) {
                        minDelta = delta[i][j];
                        minCell[0] = i;
                        minCell[1] = j;
                        optimized = false;
                    }
                }
            }

            // Step 4: If the solution is optimal, stop. Otherwise, adjust the allocations.
            if (!optimized) {
                adjustAllocations(allocation, minCell[0], minCell[1]);
            }
        }
    }

    private static void calculatePotentials(int[][] allocation, int[][] cost, int[] u, int[] v) {
        u[0] = 0;  // Set the first potential to 0 to start the calculation
        boolean[] uCalculated = new boolean[u.length];
        boolean[] vCalculated = new boolean[v.length];
        uCalculated[0] = true;

        boolean updated;
        do {
            updated = false;
            for (int i = 0; i < allocation.length; i++) {
                for (int j = 0; j < allocation[0].length; j++) {
                    if (allocation[i][j] > 0) {  // Only consider allocated cells
                        if (uCalculated[i] && !vCalculated[j]) {
                            v[j] = cost[i][j] - u[i];
                            vCalculated[j] = true;
                            updated = true;
                        } else if (!uCalculated[i] && vCalculated[j]) {
                            u[i] = cost[i][j] - v[j];
                            uCalculated[i] = true;
                            updated = true;
                        }
                    }
                }
            }
        } while (updated);
    }

    private static int[][] calculateImprovementIndices(int[][] allocation, int[][] cost, int[] u, int[] v) {
        int[][] delta = new int[allocation.length][allocation[0].length];
        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < allocation[0].length; j++) {
                if (allocation[i][j] == 0) {
                    delta[i][j] = cost[i][j] - u[i] - v[j];
                } else {
                    delta[i][j] = INF;
                }
            }
        }
        return delta;
    }

    private static void adjustAllocations(int[][] allocation, int row, int col) {
        // Find the cycle path for the cell (row, col) in the allocation matrix
        List<int[]> cyclePath = findCycle(allocation, row, col);

        // Find the minimum allocation in the negative positions along the cycle path
        int minAllocation = Integer.MAX_VALUE;
        for (int i = 1; i < cyclePath.size(); i += 2) {
            int r = cyclePath.get(i)[0];
            int c = cyclePath.get(i)[1];
            minAllocation = Math.min(minAllocation, allocation[r][c]);
        }

        // Adjust allocations along the cycle path
        for (int i = 0; i < cyclePath.size(); i++) {
            int r = cyclePath.get(i)[0];
            int c = cyclePath.get(i)[1];
            if (i % 2 == 0) {
                allocation[r][c] += minAllocation;  // Add on positive positions
            } else {
                allocation[r][c] -= minAllocation;  // Subtract on negative positions
            }
        }

        // Set to 0 any allocation that became zero to keep the matrix sparse
        for (int i = 1; i < cyclePath.size(); i += 2) {
            int r = cyclePath.get(i)[0];
            int c = cyclePath.get(i)[1];
            if (allocation[r][c] == 0) {
                allocation[r][c] = 0;
            }
        }
    }

    private static List<int[]> findCycle(int[][] allocation, int startRow, int startCol) {
        List<int[]> cyclePath = new ArrayList<>();
        cyclePath.add(new int[]{startRow, startCol});

        boolean found = findCycleHelper(allocation, cyclePath, startRow, startCol, true);
        if (!found) {
            throw new RuntimeException("Cycle not found in the allocation matrix.");
        }

        return cyclePath;
    }

    private static boolean findCycleHelper(int[][] allocation, List<int[]> cyclePath, int currentRow, int currentCol, boolean searchRow) {
        // If we found a cycle that returns to the starting cell
        if (cyclePath.size() > 3 && currentRow == cyclePath.get(0)[0] && currentCol == cyclePath.get(0)[1]) {
            return true;
        }

        // Search either row-wise or column-wise depending on the searchRow flag
        if (searchRow) {
            for (int j = 0; j < allocation[0].length; j++) {
                if (j != currentCol && allocation[currentRow][j] > 0) {
                    int[] nextCell = new int[]{currentRow, j};
                    if (!contains(cyclePath, nextCell)) {
                        cyclePath.add(nextCell);
                        if (findCycleHelper(allocation, cyclePath, currentRow, j, !searchRow)) {
                            return true;
                        }
                        cyclePath.remove(cyclePath.size() - 1);
                    }
                }
            }
        } else {
            for (int i = 0; i < allocation.length; i++) {
                if (i != currentRow && allocation[i][currentCol] > 0) {
                    int[] nextCell = new int[]{i, currentCol};
                    if (!contains(cyclePath, nextCell)) {
                        cyclePath.add(nextCell);
                        if (findCycleHelper(allocation, cyclePath, i, currentCol, !searchRow)) {
                            return true;
                        }
                        cyclePath.remove(cyclePath.size() - 1);
                    }
                }
            }
        }
        return false;
    }

    private static boolean contains(List<int[]> cyclePath, int[] cell) {
        for (int[] pathCell : cyclePath) {
            if (pathCell[0] == cell[0] && pathCell[1] == cell[1]) {
                return true;
            }
        }
        return false;
    }

    private static int calculateTotalCost(int[][] allocation, int[][] cost) {
        int totalCost = 0;
        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < allocation[0].length; j++) {
                if (allocation[i][j] > 0) {
                    totalCost += allocation[i][j] * cost[i][j];
                }
            }
        }
        return totalCost;
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }
}
