#include <iostream>
#include <vector>
#include <thread>

using namespace std;

void multiply(const vector<vector<int>>& A, const vector<vector<int>>& B,
              vector<vector<int>>& result, int start, int end) {
    for (int i = start; i < end; ++i) {
        for (int j = 0; j < B[0].size(); ++j) {
            for (int k = 0; k < B.size(); ++k) {
                result[i][j] += A[i][k] * B[k][j];
            }
        }
    }
}

void parallelMatrixMultiply(const vector<vector<int>>& A,
                            const vector<vector<int>>& B,
                            vector<vector<int>>& result,
                            int numThreads) {
    int numRowsA = A.size();
    int numRowsResult = result.size();

    int rowsPerThread = numRowsResult / numThreads;

    vector<thread> threads;

    for (int i = 0; i < numThreads; ++i) {
        int start = i * rowsPerThread;
        int end = (i == numThreads - 1) ? numRowsResult : (i + 1) * rowsPerThread;

        threads.emplace_back(multiply, ref(A), ref(B), ref(result), start, end);
    }

    for (auto& thread : threads) {
        thread.join();
    }
}

int main() {
    vector<vector<int>> A = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
    vector<vector<int>> B = {{9, 8, 7}, {6, 5, 4}, {3, 2, 1}};

    if (A[0].size() != B.size()) {
        cerr << "Matrix dimensions are not compatible for multiplication." << endl;
        return 1;
    }

    vector<vector<int>> result(A.size(), vector<int>(B[0].size(), 0));

    int numThreads = thread::hardware_concurrency();

    parallelMatrixMultiply(A, B, result, numThreads);

    for (const auto& row : result) {
        for (int value : row) {
            cout << value << " ";
        }
        cout << endl;
    }

    return 0;
}