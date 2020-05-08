package com.inaoe.rna;

import com.inaoe.rna.utils.FileUtil;
import com.inaoe.rna.utils.HopfieldUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Hello world!
 */
public class App {


    public static void main(String[] args) throws IOException {
        double tao = 1;
        int n = 10;
        var d = TSPUtils.getAdjacencyMatrix(TSPUtils.generateInstance(n));
        var tuple = HopfieldUtils.getDistanceBounds(d);
        var dL = (double) tuple[0];
        var dU = (double) tuple[1];
//        String instance = "/home/alex/Documents/inaoe/RNA/HopfieldTSP/data/bays29.tsp";
//        var nodes = FileUtil.readNodes(instance);
//        var D = TSPUtils.getAdjacencyMatrix(nodes);
//        var tour = TSPUtils.solveExact(D);
//        System.out.println(TSPUtils.fitnessFunction(D, tour));
//        System.out.println(TSPUtils.fitnessFunction(D, TSPUtils.nearestNeighbor(D)));
//        TSPUtils.nearestNeighbor(graph);

//        var hopfield = new Hopfield(n, tao, graph);
//        hopfield.start(2000);

//        double[] array = {0, 0};
//        System.out.println(Arrays.toString(array));
//        train(array);
//        System.out.println(Arrays.toString(array));

//        double C = 1;
//        double D = 1 / dU;
//        double B = 3 * dU + C;
//        double A = B - D * dL;
//        double nPrime = n + (3 * D * dU / C);
        var hopfield = new Hopfield(n, d);


//        double D = 1 / dU;
//        double A = D;
//        double B = D;
//        double C = A * .4;
//        double nPrime = n + 1;
//        hopfield.setConstants(A, B, C, D, nPrime);
//        tuple = hopfield.start();
//        int[] tour = (int[]) tuple[0];
//        boolean isValid = (boolean) tuple[1];
//        System.out.println(Arrays.toString(tour));
//        System.out.println(isValid);

        System.out.println(Arrays.toString(TSPUtils.randomTour(10)));
        System.out.println(Arrays.toString(TSPUtils.randomTour(10)));

    }

    public static void train(double[] array) {
        array[0] = -3;
    }
}
