package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.collection.parallel.immutable.ParVector

/**
 * User: patricio
 * Date: 2/4/21
 * Time: 11:09
 */
object ParallelUtils extends App {

  // 1 parallel collection

  val parList = List(1,2,3).par

  val aParVector = ParVector[Int](1,2,3)

  /*
  Seq
  Vector
  Array
  Map - Hash, Trie
  Set - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  println("serial time: " + serialTime)

  val parallelTime = measure {
    list.par.map(_ + 1)
  }

  println("parallel time: " + parallelTime)

  /*
  Map-reduce model
  - split elements into chunks - Splitter
  - operation
  - recombine - Combiner
   */

  // map, flatmap, foreach, reduce, fold

  // be carefull with non-associative operations
  println(List(1,2,3).reduce(_ - _) == List(1,2,3).par.reduce(_ - _))

  // synchronization
  var sum = 0
  List(1,2,3).par.foreach(sum += _)
  println(sum) // race conditions

  // configuring
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))

  /*
  alternatives:
  - ThreadfPoolTaskSuppor - deprecated
  - ExecutionContextTaskSupport(EC)
   */

  aParVector.tasksupport = new TaskSupport {
    override val environment: AnyRef = ???

    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    override def parallelismLevel: Int = ???
  }

  // atomic ops and references

  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread safe read
  atomic.set(4) // thread safe write

  atomic.getAndSet(5) // thread-safe combo

  atomic.compareAndSet(38, 56) // if the value is 38 set the new value to 56
  // reference equality

  atomic.updateAndGet(_ + 1) //thread-safe function run
  atomic.getAndUpdate(_ + 1) //thread-safe function run

  atomic.accumulateAndGet(12, _ + _)  // thread-safe accumulation
  atomic.getAndAccumulate(12, _ + _)  // thread-safe accumulation


}
