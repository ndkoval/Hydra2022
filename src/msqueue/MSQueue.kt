package msqueue

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

class MSQueue<E> {
    private val head: AtomicRef<Node<E>>
    private val tail: AtomicRef<Node<E>>

    init {
        val dummy = Node<E>(null)
        head = atomic(dummy)
        tail = atomic(dummy)
    }

    /**
     * Adds the specified element [x] to the queue.
     */
    fun enqueue(x: E) {
        val newTail = Node(x)
        val tail = this.tail.value
        tail.next = newTail
        this.tail.value = newTail
    }

    /**
     * Retrieves the first element from the queue
     * and returns it; returns `null` if the queue
     * is empty.
     */
    fun dequeue(): E? {
        val head = this.head.value
        val headNext = head.next
        if (headNext == null) return null
        this.head.value = headNext
        return headNext.x
    }
}

private class Node<E>(val x: E?) {
    var next: Node<E>? = null
}