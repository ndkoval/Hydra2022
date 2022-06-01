package hydra2022

import org.jetbrains.kotlinx.lincheck.verifier.VerifierState

const val THREADS = 3
const val ACTORS_PER_THREAD = 3

class IntQueueSequential : VerifierState() {
    private val q = ArrayDeque<Int>()

    fun enqueue(x: Int) {
        q.addLast(x)
    }

    fun dequeue(): Int? = q.removeFirstOrNull()

    override fun extractState() = q
}