package counter

import kotlinx.atomicfu.atomic

class Counter {
    private val counter = atomic(0L)

    fun get(): Long = counter.value

    fun getAndIncrement(): Long {
        while (true) {
            val cur = counter.value
            val new = cur + 1
            if (counter.compareAndSet(cur, new)) return cur
        }
    }

    fun incrementAndGet(): Long = counter.incrementAndGet()
}