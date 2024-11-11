package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        int[][] costMatrix = {
                {25, 12, 21, 29, 28},
                {29, 21, 19, 28, 28},
                {41, 37, 32, 27, 18},
                {38, 33, 45, 28, 35},
                {44, 17, 25, 22, 52}
        };

        int[] supply = {65, 52, 28, 52, 47}; // Емкости поставщиков
        int[] demand = {28, 62, 38, 62, 52}; // Потребности районов

        int[][] allocation = initialSolution(costMatrix, supply, demand);
        printSolution(allocation, costMatrix);

        optimizeSolution(allocation, costMatrix, supply, demand);
    }

    // Метод начального базиса, например, северо-западного угла
    private static int[][] initialSolution(int[][] costMatrix, int[] supply, int[] demand) {
        int[][] allocation = new int[supply.length][demand.length];
        int i = 0, j = 0;

        while (i < supply.length && j < demand.length) {
            int quantity = Math.min(supply[i], demand[j]);
            allocation[i][j] = quantity;
            supply[i] -= quantity;
            demand[j] -= quantity;

            if (supply[i] == 0) i++;
            else j++;
        }
        return allocation;
    }

    // Функция оптимизации методом потенциалов
    private static void optimizeSolution(int[][] allocation, int[][] costMatrix, int[] supply, int[] demand) {
        // 1. Вычисляем потенциалы для базисных клеток.
        int[] u = new int[supply.length];
        int[] v = new int[demand.length];
        Arrays.fill(u, Integer.MAX_VALUE);
        Arrays.fill(v, Integer.MAX_VALUE);
        u[0] = 0; // Задаем начальное значение для одного из потенциалов

        // Находим потенциалы на основе базисных клеток
        calculatePotentials(allocation, costMatrix, u, v);

        // 2. Проверка оптимальности
        while (true) {
            int[] minNegCell = findMinNegative(allocation, costMatrix, u, v);
            if (minNegCell == null) break; // Если отрицательных оценок нет, план оптимален

            // 3. Построение цикла пересчета и пересчет поставок
            adjustCycle(allocation, minNegCell);
            calculatePotentials(allocation, costMatrix, u, v);
            System.out.println(minNegCell);
        }

        System.out.println("Оптимизированный план:");
        printSolution(allocation, costMatrix);
    }

    // Метод для вычисления потенциалов
    private static void calculatePotentials(int[][] allocation, int[][] costMatrix, int[] u, int[] v) {
        boolean[] uCalculated = new boolean[u.length];
        boolean[] vCalculated = new boolean[v.length];
        u[0] = 0; // Устанавливаем потенциал для первой строки как 0
        uCalculated[0] = true;

        boolean updated;
        do {
            updated = false;
            for (int i = 0; i < allocation.length; i++) {
                for (int j = 0; j < allocation[i].length; j++) {
                    if (allocation[i][j] > 0) { // Базисная клетка
                        if (uCalculated[i] && !vCalculated[j]) {
                            v[j] = costMatrix[i][j] - u[i];
                            vCalculated[j] = true;
                            updated = true;
                        } else if (!uCalculated[i] && vCalculated[j]) {
                            u[i] = costMatrix[i][j] - v[j];
                            uCalculated[i] = true;
                            updated = true;
                        }
                    }
                }
            }
        } while (updated);
    }

    // Метод для нахождения ячейки с наибольшей отрицательной оценкой
    private static int[] findMinNegative(int[][] allocation, int[][] costMatrix, int[] u, int[] v) {
        int minDelta = 0;
        int[] minNegCell = null;

        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[i].length; j++) {
                if (allocation[i][j] == 0) { // Небазисная клетка
                    int delta = costMatrix[i][j] - (u[i] + v[j]);
                    if (delta < minDelta) {
                        minDelta = delta;
                        minNegCell = new int[]{i, j};
                    }
                }
            }
        }
        return minNegCell;
    }

    // Метод для пересчета значений в цикле
    private static void adjustCycle(int[][] allocation, int[] minNegCell) {
        int i = minNegCell[0];
        int j = minNegCell[1];

        // Шаг 1: Построение цикла
        List<int[]> cycle = new ArrayList<>();
        boolean[][] visited = new boolean[allocation.length][allocation[0].length];

        // Добавляем начальную небазисную клетку в цикл
        cycle.add(new int[]{i, j});
        visited[i][j] = true;

        // Строим цикл, используя принцип чередования
        boolean direction = true;  // true - вверх или влево, false - вниз или вправо
        while (true) {
            boolean found = false;
            for (int di = -1; di <= 1; di += 2) {  // Для строк
                for (int dj = -1; dj <= 1; dj += 2) {  // Для столбцов
                    int ni = i + di;
                    int nj = j + dj;
                    if (ni >= 0 && nj >= 0 && ni < allocation.length && nj < allocation[0].length
                            && !visited[ni][nj] && allocation[ni][nj] > 0) {
                        cycle.add(new int[]{ni, nj});
                        visited[ni][nj] = true;
                        found = true;
                        i = ni;
                        j = nj;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) break;
        }

        // Шаг 2: Нахождение минимального значения в цикле
        int theta = Integer.MAX_VALUE; // Минимальное значение для пересчета цикла
        for (int[] cell : cycle) {
            int x = cell[0];
            int y = cell[1];
            theta = Math.min(theta, allocation[x][y]);
        }

        // Шаг 3: Корректировка плана
        // Чередование увеличения и уменьшения
        boolean decrease = true;
        for (int[] cell : cycle) {
            int x = cell[0];
            int y = cell[1];
            if (decrease) {
                allocation[x][y] -= theta;
            } else {
                allocation[x][y] += theta;
            }
            decrease = !decrease;  // Чередуем знаки
        }
    }

    // Вывод плана и стоимости
    private static void printSolution(int[][] allocation, int[][] costMatrix) {
        int totalCost = 0;
        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < allocation[i].length; j++) {
                if (allocation[i][j] > 0) {
                    System.out.printf("Поставлено %d единиц из %d в %d по цене %d\n",
                            allocation[i][j], i + 1, j + 1, costMatrix[i][j]);
                    totalCost += allocation[i][j] * costMatrix[i][j];
                }
            }
        }
        System.out.println("Общая стоимость перевозки: " + totalCost);
    }
}

