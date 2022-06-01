package faaqueue

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.atomicArrayOfNulls

class FAAQueue<E> {
    private val head: AtomicRef<Segment> // Head pointer, similarly to the Michael-Scott queue (but the first node is _not_ sentinel)
    private val tail: AtomicRef<Segment> // Tail pointer, similarly to the Michael-Scott queue

    private val enqIdx = atomic(0L) // Global index for the next enqueue operation
    private val deqIdx = atomic(0L) // Global index for the next dequeue operation

    init {
        val firstNode = Segment(0)
        head = atomic(firstNode)
        tail = atomic(firstNode)
    }

    /**
     * Adds the specified element [x] to the queue.
     */
    fun enqueue(x: E) {
        val enqIdx = this.enqIdx.getAndIncrement()
        var tail = this.tail.value
        if (tail.id < enqIdx / SEGMENT_SIZE) {
            if (tail.next == null) tail.next = Segment(tail.id + 1)
            tail = tail.next!!
            this.tail.value = tail
        }
        val i = (enqIdx % SEGMENT_SIZE).toInt()
        tail.elements[i].value = x
    }

    /**
     * Retrieves the first element from the queue
     * and returns it; returns `null` if the queue
     * is empty.
     */
    @Suppress("UNCHECKED_CAST")
    fun dequeue(): E? {
        if (enqIdx.value == deqIdx.value) return null
        val deqIdx = this.deqIdx.getAndIncrement()
        var head = this.head.value
        if (head.id < deqIdx / SEGMENT_SIZE) {
            head = head.next!!
            this.head.value = head
        }
        val i = (deqIdx % SEGMENT_SIZE).toInt()
        return head.elements[i].value as E
    }
}

private class Segment(val id: Long) {
    var next: Segment? = null
    val elements = atomicArrayOfNulls<Any>(SEGMENT_SIZE)
}

const val SEGMENT_SIZE = 2 // DO NOT CHANGE, IMPORTANT FOR TESTS

