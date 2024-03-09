package OpenMP;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelMatrixMultiplication {
    // NEED TO TEST WITH LARGER MATRICES STILL

    // using fork join frameworking for divide and conquer
    private static final ForkJoinPool pool = new ForkJoinPool();

    public static void main(String[] args) {
        int[][] matrixA = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        int[][] matrixB = { { 9, 8, 7 }, { 6, 5, 4 }, { 3, 2, 1 } };

        int[][] result = multiplyMatrices(matrixA, matrixB);

        // Print the result matrix
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
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

    // sub class that is extending recursive tasks, part of divide and conquer using fork join
    static class MultiplyTask extends RecursiveTask<Void> {
        private static final int THRESHOLD = 10;

        private final int[][] matrixA;
        private final int[][] matrixB;
        private final int[][] result;
        private final int startRow;
        private final int endRow;
        private final int startCol;
        private final int endCol;

        // overloaded constructor
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
                // using normal matrix multiplication for small submatrices
                for (int i = startRow; i < endRow; i++) {
                    for (int j = startCol; j < endCol; j++) {
                        for (int k = 0; k < matrixB.length; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
            } else {
                // since matrix is above threshold size continue to divide and conquer
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
