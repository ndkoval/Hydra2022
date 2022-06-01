package stack

import kotlinx.atomicfu.atomic

class TreiberStack<E> {
    private val top = atomic<Node<E>?>(null)

    /**
     * Adds the specified element [x] to the stack.
     */
    fun push(x: E) {
        val top = this.top.value
        val newTop = Node(x, top)
        this.top.value = newTop
    }

    /**
     * Retrieves the first element from the stack
     * and returns it; returns `null` if the stack
     * is empty.
     */
    fun pop(): E? {
        val top = this.top.value
        if (top == null) return null
        this.top.value = top.next
        return top.x
    }
}

private class Node<E>(val x: E, val next: Node<E>?)