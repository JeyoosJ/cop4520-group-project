package OpenMP;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelMatrixMultiplication {
    private static final ForkJoinPool pool = new ForkJoinPool();
    private static int MATRIX_SIZE = 250;


    public static void main(String[] args) {
        int[][] matrixA = new int[MATRIX_SIZE][MATRIX_SIZE];
        int[][] matrixB = new int[MATRIX_SIZE][MATRIX_SIZE];

        // Populate matrixA and matrixB with sample values
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrixA[i][j] = i * MATRIX_SIZE + j + 1;
                matrixB[i][j] = (MATRIX_SIZE - i) * MATRIX_SIZE + (MATRIX_SIZE - j);
            }
        }

        long startTime = System.currentTimeMillis();
        int[][] result = multiplyMatrices(matrixA, matrixB);
        long endTime = System.currentTimeMillis();

        // Print the result matrix
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }

        // Print total runtime
        System.out.println("Total runtime: " + (endTime - startTime) + " milliseconds");
    }

    public static int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) {
        int numRowsA = matrixA.length;
        int numColsA = matrixA[0].length;
        int numColsB = matrixB[0].length;

        int[][] result = new int[numRowsA][numColsB];

        MultiplyTask task = new MultiplyTask(matrixA, matrixB, result, 0, numRowsA, 0, numColsB);
        pool.invoke(task);

        return result;
    }

    static class MultiplyTask extends RecursiveTask<Void> {
        private static final int THRESHOLD = 10;

        private final int[][] matrixA;
        private final int[][] matrixB;
        private final int[][] result;
        private final int startRow;
        private final int endRow;
        private final int startCol;
        private final int endCol;

        MultiplyTask(int[][] matrixA, int[][] matrixB, int[][] result,
                     int startRow, int endRow, int startCol, int endCol) {
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
        }

        @Override
        protected Void compute() {
            if ((endRow - startRow) < THRESHOLD || (endCol - startCol) < THRESHOLD) {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = startCol; j < endCol; j++) {
                        for (int k = 0; k < matrixB.length; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
            } else {
                int midRow = (startRow + endRow) / 2;
                int midCol = (startCol + endCol) / 2;

                invokeAll(
                        new MultiplyTask(matrixA, matrixB, result, startRow, midRow, startCol, midCol),
                        new MultiplyTask(matrixA, matrixB, result, startRow, midRow, midCol, endCol),
                        new MultiplyTask(matrixA, matrixB, result, midRow, endRow, startCol, midCol),
                        new MultiplyTask(matrixA, matrixB, result, midRow, endRow, midCol, endCol));
            }
            return null;
        }
    }
}
