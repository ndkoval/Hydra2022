package dijkstra

import kotlinx.atomicfu.atomic
import java.util.*
import java.util.concurrent.Phaser
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

// Returns `Integer.MAX_VALUE` if a path has not been found.
fun shortestPathParallel(start: Node) {
    val workers = Runtime.getRuntime().availableProcessors()
    // The distance to the start node is `0`
    start.distance = 0
    // Create a priority (by distance) queue and add the start node into it
    val q = ConcurrentPriorityQueue(NODE_DISTANCE_COMPARATOR)
    q.insert(start)
    // Run worker threads and wait until the total work is done
    val onFinish = Phaser(workers + 1) // `arrive()` should be invoked at the end by each worker
    repeat(workers) {
        thread {
            while (true) {
                // TODO Write the required algorithm here,
                // TODO break from this loop only when there is no more node to process.
                // TODO Be careful, "empty queue" != "all nodes are processed".
//                val cur: Node? = synchronized(q) { q.delete() }
//                if (cur == null) {
//                    if (workIsDone) break else continue
//                }
//                for (e in cur.outgoingEdges) {
//                    if (e.to.distance > cur.distance + e.weight) {
//                        e.to.distance = cur.distance + e.weight
//                        q.addOrDecreaseKey(e.to)
//                    }
//                }
            }
            onFinish.arrive()
        }
    }
    onFinish.arriveAndAwaitAdvance()
}

private class ConcurrentPriorityQueue<E>(comparator: Comparator<in E>) {
    // TODO implement me with the multi-queue design!
    private val pq = PriorityQueue<E>(comparator)
    private val lock = ReentrantLock()

    fun delete(): E? = lock.withLock {
        pq.poll()
    }

    fun insert(x: E) {
        lock.withLock {
            pq.add(x)
        }
    }
}

class Node {
    private val _outgoingEdges = arrayListOf<Edge>()
    val outgoingEdges: List<Edge> = _outgoingEdges

    private val _distance = atomic(Integer.MAX_VALUE)
    var distance
        get() = _distance.value
        set(value) {
            _distance.value = value
        }

    // TODO: Use me in the parallel Dijkstra algorithm
    // TODO: to update the distance only if it becomes shorter.
    fun casDistance(cur: Int, update: Int) = _distance.compareAndSet(cur, update)

    fun addEdge(edge: Edge) {
        _outgoingEdges.add(edge)
    }
}

private val NODE_DISTANCE_COMPARATOR = Comparator<Node> { o1, o2 -> Integer.compare(o1!!.distance, o2!!.distance) }

data class Edge(val to: Node, val weight: Int)
