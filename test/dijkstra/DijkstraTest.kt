package dijkstra

import org.junit.Test
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals

class DijkstraSimpleTest {

    @Test(timeout = 1_000)
    fun `test on a very very small graph`() {
        val a = Node()
        val b = Node()
        val c = Node()
        val d = Node()
        val e = Node()
        a.addEdge(Edge(b, 2))
        a.addEdge(Edge(d, 1))
        b.addEdge(Edge(c, 4))
        b.addEdge(Edge(e, 5))
        c.addEdge(Edge(e, 1))
        d.addEdge(Edge(c, 3))
        val nodes = listOf(a, b, c, d, e)

        shortestPathSequential(a)
        assertEquals(0, a.distance)
        assertEquals(2, b.distance)
        assertEquals(4, c.distance)
        assertEquals(1, d.distance)
        assertEquals(5, e.distance)
        clearNodes(nodes)

        shortestPathParallel(a)
        assertEquals(0, a.distance)
        assertEquals(2, b.distance)
        assertEquals(4, c.distance)
        assertEquals(1, d.distance)
        assertEquals(5, e.distance)
        clearNodes(nodes)
    }
}

class DijkstraConcurrentStressTest {

    @Test(timeout = 1_000)
    fun `test on trees`() {
        testOnRandomGraphs(100, 99)
    }

    @Test(timeout = 1_000)
    fun `test on very small graphs`() {
        testOnRandomGraphs(16, 25)
    }

    @Test(timeout = 10_000)
    fun `test on small graphs`() {
        testOnRandomGraphs(100, 1000)
    }

    private fun testOnRandomGraphs(nodes: Int, edges: Int) {
        repeat(GRAPHS) {
            val nodesList = randomConnectedGraph(nodes, edges)
            repeat(SEARCHES) {
                val from = nodesList[Random.nextInt(nodes)]
                shortestPathSequential(from)
                val seqRes = nodesList.map { it.distance }
                clearNodes(nodesList)
                shortestPathParallel(from)
                val parRes = nodesList.map { it.distance }
                clearNodes(nodesList)
                assertEquals(seqRes, parRes)
            }
        }
    }
}

private const val GRAPHS = 10
private const val SEARCHES = 100


// Returns `Integer.MAX_VALUE` if a path has not been found.
fun shortestPathSequential(start: Node) {
    start.distance = 0
    val q = PriorityQueue(NODE_DISTANCE_COMPARATOR)
    q.add(start)
    while (q.isNotEmpty()) {
        val cur = q.poll()
        for (e in cur.outgoingEdges) {
            if (e.to.distance > cur.distance + e.weight) {
                e.to.distance = cur.distance + e.weight
                q.remove(e.to) // inefficient, but used for tests only
                q.add(e.to)
            }
        }
    }
}

private val NODE_DISTANCE_COMPARATOR = Comparator<Node> { o1, o2 -> Integer.compare(o1!!.distance, o2!!.distance) }

fun clearNodes(nodes: List<Node>) {
    nodes.forEach { it.distance = Int.MAX_VALUE }
}

fun randomConnectedGraph(nodes: Int, edges: Int, maxWeight: Int = 100): List<Node> {
    require(edges >= nodes - 1)
    val r = java.util.Random()
    val nodesList = List(nodes) { Node() }
    // generate a random connected graph with `nodes-1` edges
    val s = ArrayList(nodesList)
    var cur = s.removeAt(r.nextInt(s.size))
    val visited = mutableSetOf<Node>(cur)
    while (s.isNotEmpty()) {
        val neighbor = s.removeAt(r.nextInt(s.size))
        if (visited.add(neighbor)) {
            cur.addEdge(Edge(neighbor, r.nextInt(maxWeight)))
        }
        cur = neighbor
    }
    // add `edges - nodes + 1` random edges
    repeat(edges - nodes + 1) {
        while (true) {
            val first = nodesList[r.nextInt(nodes)]
            val second = nodesList[r.nextInt(nodes)]
            if (first == second) continue
            if (first.outgoingEdges.any { e -> e.to == second }) continue
            val weight = r.nextInt(maxWeight)
            first.addEdge(Edge(second, weight))
            second.addEdge(Edge(first, weight))
            break
        }
    }
    return nodesList
}