package com.inaoe.rna;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TSPUtils {
    public static List<double[]> generateInstance(int n) {

        List<double[]> nodes = new ArrayList<>(n);
        var rnd = new Random();
        for (int i = 0; i < n; i++) {
            nodes.add(rnd.doubles(2).toArray());
        }
        return nodes;
    }

    public static double[][] getAdjacencyMatrix(List<double[]> nodes) {
        double[][] adjacencyMatrix = new double[nodes.size()][nodes.size()];
        var eucDistance = new EuclideanDistance();

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i != j) {
                    adjacencyMatrix[i][j] = eucDistance.compute(nodes.get(i), nodes.get(j));
                }
            }
        }
        return adjacencyMatrix;
    }

//    public static boolean compare2dArrays(double[][] arr1, double[][]arr2){
//        for (int i = 0; i < arr1.length; i++) {
//            if(!Arrays.equals(arr1[i], arr2[i])){
//                return false;
//            }
//        }
//        return true;
//    }

    public static boolean compare2dArrays(double[][] arr1, double[][] arr2) {
        double epsilon = 0.00001;
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1.length; j++) {
                if (Math.abs(arr1[i][j] - arr2[i][j]) > epsilon) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double fitnessFunction(double[][] D, int[] tour) {
        int n = tour.length;
        double fitnessValue = 0;
        for (int i = 0; i < n - 1; i++) {
            fitnessValue += D[tour[i]][tour[i + 1]];
        }
        fitnessValue += D[tour[n - 1]][tour[0]];
        return fitnessValue;
    }

    public static int[] nearestNeighbor(double[][] D) {
        int n = D.length;

        int depot = new Random().nextInt(n);
        int[] tour = new int[n];
        tour[0] = depot;
        int[] repetitions = new int[n];
        repetitions[depot]++;

        for (int i = 1; i < n; i++) {
            int nextCity = 0;
            double minD = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                if (tour[i - 1] != j) {
                    if (minD > D[tour[i - 1]][j] && repetitions[j] == 0) {
                        nextCity = j;
                        minD = D[tour[i - 1]][j];
                    }
                }
            }
            tour[i] = nextCity;
            repetitions[nextCity]++;
        }
        return tour;
    }

    public static int[] solveExact(double[][] D) {

        int n = D.length;

        var tspSolver = new HeldKarpTSP<Integer, DefaultWeightedEdge>();
        var graph = GraphTypeBuilder
                .<Integer, DefaultWeightedEdge>undirected().allowingMultipleEdges(false)
                .allowingSelfLoops(false).edgeClass(DefaultWeightedEdge.class).weighted(true).buildGraph();

        // add vertices
        for (int i = 0; i < n; i++) {
            graph.addVertex(i);
        }

        // add edges
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    Graphs.addEdge(graph, i, j, D[i][j]);
                }
            }
        }

        var graphPath = tspSolver.getTour(graph);
        int[] tour = Arrays.copyOfRange(graphPath.getVertexList().stream().mapToInt(i -> i).toArray(), 0, n);
        return tour;
    }
}
