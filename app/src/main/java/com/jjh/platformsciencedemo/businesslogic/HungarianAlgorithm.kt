package com.jjh.platformsciencedemo.businesslogic

import java.lang.Double
import java.util.*
import kotlin.Array
import kotlin.BooleanArray
import kotlin.DoubleArray
import kotlin.Int
import kotlin.IntArray
import kotlin.require

/**
 * This code can be found here:
 * https://github.com/KevinStern/software-and-algorithms/blob/master/src/main/java/blogspot/software_and_algorithms/stern_library/optimization/HungarianAlgorithm.java
 *
 * Converted to kotlin by Android Studio with minor fixes to some member names but no algorithmic changes
 *
 *
 */


/* Copyright (c) 2012 Kevin L. Stern
 *
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/* Copyright (c) 2012 Kevin L. Stern
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * An implementation of the Hungarian algorithm for solving the assignment
 * problem. An instance of the assignment problem consists of a number of
 * workers along with a number of jobs and a cost matrix which gives the cost of
 * assigning the i'th worker to the j'th job at position (i, j). The goal is to
 * find an assignment of workers to jobs so that no job is assigned more than
 * one worker and so that no worker is assigned to more than one job in such a
 * manner so as to minimize the total cost of completing the jobs.
 *
 *
 *
 * An assignment for a cost matrix that has more workers than jobs will
 * necessarily include unassigned workers, indicated by an assignment value of
 * -1; in no other circumstance will there be unassigned workers. Similarly, an
 * assignment for a cost matrix that has more jobs than workers will necessarily
 * include unassigned jobs; in no other circumstance will there be unassigned
 * jobs. For completeness, an assignment for a square cost matrix will give
 * exactly one unique worker to each job.
 *
 *
 *
 * This version of the Hungarian algorithm runs in time O(n^3), where n is the
 * maximum among the number of workers and the number of jobs.
 *
 * @author Kevin L. Stern
 */
internal class HungarianAlgorithm(costMatrix: Array<DoubleArray>) {
    private val costMatrix: Array<DoubleArray>
    private val rows: Int
    private val cols: Int
    private val dim: Int
    private val labelByWorker: DoubleArray
    private val labelByJob: DoubleArray
    private val minSlackWorkerByJob: IntArray
    private val minSlackValueByJob: DoubleArray
    private val matchJobByWorker: IntArray
    private val matchWorkerByJob: IntArray
    private val parentWorkerByCommittedJob: IntArray
    private val committedWorkers: BooleanArray

    /**
     * Compute an initial feasible solution by assigning zero labels to the
     * workers and by assigning to each job a label equal to the minimum cost
     * among its incident edges.
     */
    protected fun computeInitialFeasibleSolution() {
        for (j in 0 until dim) {
            labelByJob[j] = kotlin.Double.POSITIVE_INFINITY
        }
        for (w in 0 until dim) {
            for (j in 0 until dim) {
                if (costMatrix[w][j] < labelByJob[j]) {
                    labelByJob[j] = costMatrix[w][j]
                }
            }
        }
    }

    /**
     * Execute the algorithm.
     *
     * @return the minimum cost matching of workers to jobs based upon the
     * provided cost matrix. A matching value of -1 indicates that the
     * corresponding worker is unassigned.
     */
    fun execute(): IntArray {
        /*
     * Heuristics to improve performance: Reduce rows and columns by their
     * smallest element, compute an initial non-zero dual feasible solution and
     * create a greedy matching from workers to jobs of the cost matrix.
     */
        reduce()
        computeInitialFeasibleSolution()
        greedyMatch()
        var w = fetchUnmatchedWorker()
        while (w < dim) {
            initializePhase(w)
            executePhase()
            w = fetchUnmatchedWorker()
        }
        val result = Arrays.copyOf(matchJobByWorker, rows)
        w = 0
        while (w < result.size) {
            if (result[w] >= cols) {
                result[w] = -1
            }
            w++
        }
        return result
    }

    /**
     * Execute a single phase of the algorithm. A phase of the Hungarian algorithm
     * consists of building a set of committed workers and a set of committed jobs
     * from a root unmatched worker by following alternating unmatched/matched
     * zero-slack edges. If an unmatched job is encountered, then an augmenting
     * path has been found and the matching is grown. If the connected zero-slack
     * edges have been exhausted, the labels of committed workers are increased by
     * the minimum slack among committed workers and non-committed jobs to create
     * more zero-slack edges (the labels of committed jobs are simultaneously
     * decreased by the same amount in order to maintain a feasible labeling).
     *
     *
     *
     * The runtime of a single phase of the algorithm is O(n^2), where n is the
     * dimension of the internal square cost matrix, since each edge is visited at
     * most once and since increasing the labeling is accomplished in time O(n) by
     * maintaining the minimum slack values among non-committed jobs. When a phase
     * completes, the matching will have increased in size.
     */
    protected fun executePhase() {
        while (true) {
            var minSlackWorker = -1
            var minSlackJob = -1
            var minSlackValue = kotlin.Double.POSITIVE_INFINITY
            for (j in 0 until dim) {
                if (parentWorkerByCommittedJob[j] == -1) {
                    if (minSlackValueByJob[j] < minSlackValue) {
                        minSlackValue = minSlackValueByJob[j]
                        minSlackWorker = minSlackWorkerByJob[j]
                        minSlackJob = j
                    }
                }
            }
            if (minSlackValue > 0) {
                updateLabeling(minSlackValue)
            }
            parentWorkerByCommittedJob[minSlackJob] = minSlackWorker
            if (matchWorkerByJob[minSlackJob] == -1) {
                /*
         * An augmenting path has been found.
         */
                var committedJob = minSlackJob
                var parentWorker = parentWorkerByCommittedJob[committedJob]
                while (true) {
                    val temp = matchJobByWorker[parentWorker]
                    match(parentWorker, committedJob)
                    committedJob = temp
                    if (committedJob == -1) {
                        break
                    }
                    parentWorker = parentWorkerByCommittedJob[committedJob]
                }
                return
            } else {
                /*
         * Update slack values since we increased the size of the committed
         * workers set.
         */
                val worker = matchWorkerByJob[minSlackJob]
                committedWorkers[worker] = true
                for (j in 0 until dim) {
                    if (parentWorkerByCommittedJob[j] == -1) {
                        val slack = (costMatrix[worker][j] - labelByWorker[worker]
                                - labelByJob[j])
                        if (minSlackValueByJob[j] > slack) {
                            minSlackValueByJob[j] = slack
                            minSlackWorkerByJob[j] = worker
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @return the first unmatched worker or [.dim] if none.
     */
    protected fun fetchUnmatchedWorker(): Int {
        var w: Int
        w = 0
        while (w < dim) {
            if (matchJobByWorker[w] == -1) {
                break
            }
            w++
        }
        return w
    }

    /**
     * Find a valid matching by greedily selecting among zero-cost matchings. This
     * is a heuristic to jump-start the augmentation algorithm.
     */
    protected fun greedyMatch() {
        for (w in 0 until dim) {
            for (j in 0 until dim) {
                if (matchJobByWorker[w] == -1 && matchWorkerByJob[j] == -1 && costMatrix[w][j] - labelByWorker[w] - labelByJob[j] == 0.0) {
                    match(w, j)
                }
            }
        }
    }

    /**
     * Initialize the next phase of the algorithm by clearing the committed
     * workers and jobs sets and by initializing the slack arrays to the values
     * corresponding to the specified root worker.
     *
     * @param w
     * the worker at which to root the next phase.
     */
    protected fun initializePhase(w: Int) {
        Arrays.fill(committedWorkers, false)
        Arrays.fill(parentWorkerByCommittedJob, -1)
        committedWorkers[w] = true
        for (j in 0 until dim) {
            minSlackValueByJob[j] = (costMatrix[w][j] - labelByWorker[w]
                    - labelByJob[j])
            minSlackWorkerByJob[j] = w
        }
    }

    /**
     * Helper method to record a matching between worker w and job j.
     */
    protected fun match(w: Int, j: Int) {
        matchJobByWorker[w] = j
        matchWorkerByJob[j] = w
    }

    /**
     * Reduce the cost matrix by subtracting the smallest element of each row from
     * all elements of the row as well as the smallest element of each column from
     * all elements of the column. Note that an optimal assignment for a reduced
     * cost matrix is optimal for the original cost matrix.
     */
    protected fun reduce() {
        for (w in 0 until dim) {
            var min = kotlin.Double.POSITIVE_INFINITY
            for (j in 0 until dim) {
                if (costMatrix[w][j] < min) {
                    min = costMatrix[w][j]
                }
            }
            for (j in 0 until dim) {
                costMatrix[w][j] -= min
            }
        }
        val min = DoubleArray(dim)
        for (j in 0 until dim) {
            min[j] = kotlin.Double.POSITIVE_INFINITY
        }
        for (w in 0 until dim) {
            for (j in 0 until dim) {
                if (costMatrix[w][j] < min[j]) {
                    min[j] = costMatrix[w][j]
                }
            }
        }
        for (w in 0 until dim) {
            for (j in 0 until dim) {
                costMatrix[w][j] -= min[j]
            }
        }
    }

    /**
     * Update labels with the specified slack by adding the slack value for
     * committed workers and by subtracting the slack value for committed jobs. In
     * addition, update the minimum slack values appropriately.
     */
    protected fun updateLabeling(slack: kotlin.Double) {
        for (w in 0 until dim) {
            if (committedWorkers[w]) {
                labelByWorker[w] += slack
            }
        }
        for (j in 0 until dim) {
            if (parentWorkerByCommittedJob[j] != -1) {
                labelByJob[j] -= slack
            } else {
                minSlackValueByJob[j] -= slack
            }
        }
    }

    /**
     * Construct an instance of the algorithm.
     *
     * @param costMatrix
     * the cost matrix, where matrix[i][j] holds the cost of assigning
     * worker i to job j, for all i, j. The cost matrix must not be
     * irregular in the sense that all rows must be the same length; in
     * addition, all entries must be non-infinite numbers.
     */
    init {
        dim = Math.max(costMatrix.size, costMatrix[0].size)
        rows = costMatrix.size
        cols = costMatrix[0].size
        this.costMatrix = Array(dim) { DoubleArray(dim) }
        for (w in 0 until dim) {
            if (w < costMatrix.size) {
                require(costMatrix[w].size == cols) { "Irregular cost matrix" }
                for (j in 0 until cols) {
                    require(!Double.isInfinite(costMatrix[w][j])) { "Infinite cost" }
                    require(!Double.isNaN(costMatrix[w][j])) { "NaN cost" }
                }
                this.costMatrix[w] = Arrays.copyOf(costMatrix[w], dim)
            } else {
                this.costMatrix[w] = DoubleArray(dim)
            }
        }
        labelByWorker = DoubleArray(dim)
        labelByJob = DoubleArray(dim)
        minSlackWorkerByJob = IntArray(dim)
        minSlackValueByJob = DoubleArray(dim)
        committedWorkers = BooleanArray(dim)
        parentWorkerByCommittedJob = IntArray(dim)
        matchJobByWorker = IntArray(dim)
        Arrays.fill(matchJobByWorker, -1)
        matchWorkerByJob = IntArray(dim)
        Arrays.fill(matchWorkerByJob, -1)
    }
}