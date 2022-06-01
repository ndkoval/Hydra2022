# Introduction to Concurrent Programming, Hydra 2022, Assignments

In this repository, you will find the assignments for the "Introduction to Concurrent Programming" series at Hydra'22.
All assignments are in [Kotlin](https://kotlinlang.org) and leverage the [kotlinx.atomicfu](https://github.com/Kotlin/kotlinx.atomicfu/)
library that simplifies using the concurrent primitives, such as `Compare-and-Set` and `Fetch-and-Add`.
Please see the example of the atomic counter implementation in `src/counter` to get the intuition on how to use the library.

## 1. Classic Stack and Queue Algorithms
Please complete the Treiber stack and Michael-Scott queue algorithms in the `src/stack` and `src/msqueue` folders.
To check the implementations for correctness, run `./gradlew test --tests "stack.*"` and `./gradlew test --tests "msqueue.*"`, correspondingly.

## 2. Modern Queues and Flat Combining
Please complete the `Fetch-and-Add`-based queue implementation in `src/faaqueue` and implement a concurrent queue 
that leverages the flat-combining technique in `src/fcqueue`.
To run the tests, use `./gradlew test --tests "faaqueue.*"` and `./gradlew test --tests "fcqueue.*"`.

## 3. Relaxed Data Structures for Parallel Algorithms
Please make the classic Dijkstra algorithm concurrent with a Multi-Queue or Stealing Multi-Queue under the hood 
in the `src/dijkstra` folder. To run the tests, use `./gradlew test --tests "dijkstra.*"`.
