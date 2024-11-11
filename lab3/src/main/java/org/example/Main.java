package org.example;

public class Main {
    public static void main(String[] args) {
        int[][] costMatrix = {
                {25, 12, 21, 29, 28},
                {29, 21, 19, 28, 28},
                {41, 37, 32, 27, 18},
                {38, 33, 45, 28, 35},
                {44, 17, 25, 22, 52}
        };

        int[] supply = {65, 52, 28, 52, 47};
        int[] demand = {28, 62, 38, 62, 52};

        int[][] allocation = new int[supply.length][demand.length];

        int i = 0, j = 0;
        while (i < supply.length && j < demand.length) {
            int quantity = Math.min(supply[i], demand[j]);
            allocation[i][j] = quantity;
            supply[i] -= quantity;
            demand[j] -= quantity;

            if (supply[i] == 0) {
                i++;
            } else {
                j++;
            }
        }

        int totalCost = 0;
        System.out.println("Начальный базисный план:");
        for (i = 0; i < allocation.length; i++) {
            for (j = 0; j < allocation[i].length; j++) {
                if (allocation[i][j] > 0) {
                    System.out.printf("Поставлено %d единиц из %d в %d по цене %d\n",
                            allocation[i][j], i + 1, j + 1, costMatrix[i][j]);
                    totalCost += allocation[i][j] * costMatrix[i][j];
                }
            }
        }
        System.out.println("Начальная стоимость перевозки: " + totalCost);
    }
}
