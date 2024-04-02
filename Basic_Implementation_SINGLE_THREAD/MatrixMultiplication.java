package Basic_Implementation_SINGLE_THREAD;

public class MatrixMultiplication {
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

        for (int i = 0; i < numRowsA; i++) {
            for (int j = 0; j < numColsB; j++) {
                for (int k = 0; k < numColsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }
}
