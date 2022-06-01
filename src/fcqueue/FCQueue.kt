package fcqueue

class FCQueue<E> {
    private val q = ArrayDeque<E>()

    /**
     * Adds the specified element [x] to the queue.
     */
    fun enqueue(x: E) {
        q.addLast(x)
    }

    /**
     * Retrieves the first element from the queue
     * and returns it; returns `null` if the queue
     * is empty.
     */
    fun dequeue(): E? {
        return q.removeFirstOrNull()
    }
}