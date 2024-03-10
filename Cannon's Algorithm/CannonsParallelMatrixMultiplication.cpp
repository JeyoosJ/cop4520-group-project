#include <iostream>
#include <vector>
#include <omp.h>

using namespace std;

// Function to print a matrix
void printMatrix(const vector<vector<int>>& matrix) {
    for (const auto& row : matrix) {
        for (int value : row) {
            cout << value << " ";
        }
        cout << endl;
    }
}

// Function to perform local matrix multiplication
void localMatrixMultiply(const vector<vector<int>>& A, const vector<vector<int>>& B, vector<vector<int>>& C, int block_size, int startRow, int startCol, int endRow, int endCol) {
    for (int i = startRow; i < endRow; ++i) {
        for (int j = startCol; j < endCol; ++j) {
            C[i][j] = 0;
            for (int k = 0; k < block_size; ++k) {
                C[i][j] += A[i][k + startCol] * B[k + startRow][j];
            }
        }
    }
}

// Function to perform Cannon's matrix multiplication
void cannonMatrixMultiply(const vector<vector<int>>& A, const vector<vector<int>>& B, vector<vector<int>>& C, int block_size) {
    int matrixSize = A.size();
    int numBlocks = matrixSize / block_size;

    // Perform Cannon's algorithm
    #pragma omp parallel for collapse(2)
    for (int i = 0; i < numBlocks; ++i) {
        for (int j = 0; j < numBlocks; ++j) {
            // Calculate the position of the blocks
            int aRow = (i + j) % numBlocks;
            int bCol = (i + j) % numBlocks;

            // Calculate the starting indices for the block
            int startRow = i * block_size;
            int startCol = j * block_size;

            // Calculate the ending indices for the block
            int endRow = startRow + block_size;
            int endCol = startCol + block_size;

            // Perform local matrix multiplication
            localMatrixMultiply(A, B, C, block_size, startRow, startCol, endRow, endCol);
        }
    }
}

int main() {
    vector<vector<int>> matrixA = {{1, 2, 3},
                                   {4, 5, 6},
                                   {7, 8, 9}};

    vector<vector<int>> matrixB = {{9, 8, 7},
                                   {6, 5, 4},
                                   {3, 2, 1}};

    int matrixSize = matrixA.size();
    int block_size = 1;

    vector<vector<int>> result(matrixSize, vector<int>(matrixSize, 0));

    cannonMatrixMultiply(matrixA, matrixB, result, block_size);

    cout << "Result Matrix:" << endl;
    printMatrix(result);

    return 0;
}
