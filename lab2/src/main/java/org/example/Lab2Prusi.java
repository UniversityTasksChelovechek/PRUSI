package org.example;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.List;

public class Lab2Prusi {

    public static void main(String[] args) {
        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(new double[]{9, 5, 4, 6}, 0);

        List<LinearConstraint> constraints = new ArrayList<>();
        constraints.add(new LinearConstraint(new double[]{7, 3, 1, 1}, Relationship.LEQ, 60));
        constraints.add(new LinearConstraint(new double[]{1, 1, 4, 5}, Relationship.LEQ, 45));
        constraints.add(new LinearConstraint(new double[]{2, 1, 2, 1}, Relationship.LEQ, 30));

        constraints.add(new LinearConstraint(new double[]{1, 0, 0, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[]{0, 1, 0, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[]{0, 0, 1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[]{0, 0, 0, 1}, Relationship.GEQ, 0));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(
                new MaxIter(100),
                objectiveFunction,
                new LinearConstraintSet(constraints),
                GoalType.MAXIMIZE,
                new NonNegativeConstraint(true)
        );

        if (solution != null) {
            double maxF = solution.getValue();
            double[] variables = solution.getPoint();
            System.out.printf(""" 
                    Максимальное значение функции: %s
                    Значение переменных:
                        x1 = %s
                        x2 = %s
                        x3 = %s
                        x4 = %s
                    """, maxF, variables[0], variables[1], variables[2], variables[3]);
        } else {
            System.out.println("Решение не найдено.");
        }
    }
}