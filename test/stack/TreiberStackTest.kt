package stack

import hydra2022.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.*
import org.jetbrains.kotlinx.lincheck.strategy.stress.*
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.junit.*

class TreiberStackTest {
    private val q = TreiberStack<Int>()

    @Operation
    fun push(x: Int): Unit = q.push(x)

    @Operation
    fun pop(): Int? = q.pop()

    @Test
    fun modelCheckingTest() = ModelCheckingOptions()
        .iterations(100)
        .invocationsPerIteration(10_000)
        .threads(THREADS)
        .actorsPerThread(ACTORS_PER_THREAD)
        .checkObstructionFreedom()
        .sequentialSpecification(IntStackSequential::class.java)
        .check(this::class.java)

    @Test
    fun stressTest() = StressOptions()
        .iterations(100)
        .invocationsPerIteration(50_000)
        .threads(THREADS)
        .actorsPerThread(ACTORS_PER_THREAD)
        .sequentialSpecification(IntStackSequential::class.java)
        .check(this::class.java)
}

class IntStackSequential : VerifierState() {
    private val q = ArrayDeque<Int>()

    fun push(x: Int) {
        q.addLast(x)
    }

    fun pop(): Int? = q.removeLastOrNull()

    override fun extractState() = q
}