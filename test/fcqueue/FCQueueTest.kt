package fcqueue

import hydra2022.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.*
import org.jetbrains.kotlinx.lincheck.strategy.stress.*
import org.junit.*

class FCQueueTest {
    private val q = FCQueue<Int>()

    @Operation
    fun enqueue(x: Int): Unit = q.enqueue(x)

    @Operation
    fun dequeue(): Int? = q.dequeue()

    @Test
    fun modelCheckingTest() = ModelCheckingOptions()
        .iterations(100)
        .invocationsPerIteration(10_000)
        .threads(THREADS)
        .actorsPerThread(ACTORS_PER_THREAD)
        .checkObstructionFreedom()
        .sequentialSpecification(IntQueueSequential::class.java)
        .check(this::class.java)

    @Test
    fun stressTest() = StressOptions()
        .iterations(100)
        .invocationsPerIteration(50_000)
        .threads(THREADS)
        .actorsPerThread(ACTORS_PER_THREAD)
        .sequentialSpecification(IntQueueSequential::class.java)
        .check(this::class.java)
}