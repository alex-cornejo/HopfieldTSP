package com.inaoe.rna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Hopfield {

    private double[][] V;
    private double[][] U;
    private double graph[][];
    private double tao;
    private int n;
    private double u0 = 0.02;
    private double delta = 0.0001;
    private final int u00 = 0;

//    private double A = 1000;
//    private double B = 1000;
//    private double C = 7500;
//    private double D = 1000;
    private double nPrime = 0;
    private  double A = 100;
    private  double B = 100;
    private  double C = 90;
    private  double D = 100;

    public Hopfield(int n, double tao, double[][] graph) {
        this.n = n;
        this.tao = tao;
        this.V = new double[n][n];
        this.U = new double[n][n];
        this.graph = graph;

        init();
    }


    public void init() {
        double min = -0.1 * u0;
        double max = 0.1 * u0;
        var rnd = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
//                U[i][j] = u00 + (max - min) * rnd.nextDouble() + min;
                U[i][j] = rnd.nextDouble();
            }
        }
    }

    public void setConstants(double A, double B, double C, double D, double nPrime){
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        this.nPrime = nPrime;
    }

    public void updateOutputs(double[][] outputs) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                outputs[i][j] = outputNeuron(U[i][j]);
            }
        }
    }

    public Object[] start(int iterations) {
        int iter = 0;
        for (iter = 0; iter < iterations; iter++) {

//        for (; ; iter++) {
            double[][] tmpV = new double[n][n];
            updateOutputs(V);
            for (int x = 0; x < n; x++) {
                for (int i = 0; i < n; i++) {
                    var du = du(x, i);
                    var added = du * delta;
                    U[x][i] = U[x][i] + added;
//                    U[x][i] = du;
                }
            }
//            if(TSPUtils.compare2dArrays(tmpV, V)){
//               break;
//            }
//            V = tmpV;
            System.out.println("Iteration: "+iter);
            printMatrix(V);
        }
        var tour = getTour(V);
        System.out.println(Arrays.toString(getTour(V)));
        System.out.println("Tour valid: " + isValid(tour));
        return new Object[]{tour, isValid(tour)};
    }

    public static void printMatrix(double[][] V) {
        System.out.println("\n");
        for (int i = 0; i < V.length; i++) {
            System.out.println(Arrays.toString(V[i]));
        }
        System.out.println("\n");
    }

    public double outputNeuron(double uxi) {
        var tanh = Math.tanh(uxi / u0);
        var result = 0.5 * (1 + tanh);
        return result;
    }

    public double[][] getState() {
        double[][] state = new double[n][n];
        for (int x = 0; x < n; x++) {
            for (int i = 0; i < n; i++) {
                state[x][i] = outputNeuron(U[x][i]);
            }
        }
        return state;
    }

    public double du(int x, int i) {
        double term1 = 0;
        double term2 = 0;
        double term3 = 0;
        double term4 = 0;
        double term5 = 0;

//        term1 = -U[x][i] / tao;
        term1 = U[x][i];

        //computing second term
        for (int j = 0; j < n; j++) {
            if (j != i) {
                term2 += V[x][j];
            }
        }
        term2 = A * term2;

        //computing third term
        for (int y = 0; y < n; y++) {
            if (y != x) {
                term3 += V[y][i];
            }
        }
        term3 = B * term3;

        //computing fourth term
        for (int x_tmp = 0; x_tmp < n; x_tmp++) {
            for (int j = 0; j < n; j++) {
                term4 += V[x_tmp][j];
            }
        }
        term4 = term4 - (n+1.1);
        term4 = C * term4;

        //computing fifth term
        for (int y = 0; y < n; y++) {
            if (i > 0) {
                term5 += graph[x][y] * (V[y][(i + 1) % n] + V[y][(i - 1) % n]);
            } else {
                term5 += graph[x][y] * (V[y][(i + 1) % n] + V[y][n - 1]);
            }
        }
        term5 = D * term5;

//        var result = -term1 - term2 - term3 - term4 - term5;
        var result = term2 + term3 + term4 + term5;
        return result;
    }

    public int[] getTour(double[][] state) {
        List<Integer> tour = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            double max = 0;
            int city = 0;
            for (int i = 0; i < n; i++) {
                if (state[i][j] != 0 && state[i][j] > max) {
                    max = state[i][j];
                    city = i;
                }
            }
            if (max != 0) tour.add(city);
        }
        return tour.stream().mapToInt(i -> i).toArray();
    }

    public boolean isValid(int[] tour) {
        if (tour.length != n) {
            return false;
        }

        int[] repetitions = new int[n];
        for (int i = 0; i < n; i++) {
            if (repetitions[tour[i]] > 0) {
                return false;
            }
            repetitions[tour[i]]++;
        }
        return true;
    }

    public double energyFunction() {
        double term1 = 0f;
        double term2 = 0f;
        double term3 = 0f;
        double term4 = 0f;

        // computing first term
        for (int x = 0; x < n; x++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        term1 += V[x][i] * V[x][j];
                    }
                }
            }
        }
        term1 = A / 2.0 * term1;

        // computing second term
        for (int i = 0; i < n; i++) {
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    if (y != x) {
                        term2 += V[x][i] * V[y][i];
                    }
                }
            }
        }
        term2 = B / 2.0 * term2;

        // computing second term
        for (int x = 0; x < n; x++) {
            for (int i = 0; i < n; i++) {
                term3 += V[x][i] - n;
            }
        }
        term3 = (float) Math.pow(term3, 2);
        term3 = C / 2.0 * term3;

        // computing fourth term
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (y != x) {
                    for (int i = 0; i < n; i++) {
                        if (i != x) {
                            if (i > 0) {
                                term4 += graph[x][y] * V[x][y] * (V[y][(i + 1) % n] + V[y][(i - 1) % n]);
                            } else {
                                term4 += graph[x][y] * V[x][y] * (V[y][(i + 1) % n] + V[y][n - 1]);
                            }
                        }
                    }
                }
            }
        }

        return term1 + term2 + term3 + term4;

    }
}