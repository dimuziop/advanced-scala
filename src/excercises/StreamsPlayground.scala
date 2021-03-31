package excercises

import scala.annotation.tailrec

/**
 * User: patricio
 * Date: 30/3/21
 * Time: 16:41
 */

/*
  Exercise: implement a lazy evaluated,  singly STREAM of element.

  naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite)
  naturals.take(100).foreach(println)// lazily evaluated stream of the first 100 naturals (finite stream)
  natural.foreach(println) // will crash - infinite!
  natural.map(_ * 2) // stream of all even numbers (potential infinite)


   */

abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // Prepend operator
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate 2 streams

  def foreach(f: A => Unit): Unit

  def map[B](f: A => B): MyStream[B]

  def flatMap[B](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // take the fist n elements out of this stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  /*
  [1 2 3].toList([])
  [2 3].toList([1])
  [3].toList([2 1])
  [].toList([3 2 1])
  [1 2 3]
   */
  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
  def isEmpty: Boolean = true

  def head: Nothing = throw new NoSuchElementException

  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)

  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  def foreach(f: Nothing => Unit): Unit = ()

  def map[B](f: Nothing => B): MyStream[B] = this

  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this

}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false

  override val head: A = hd

  override lazy val tail: MyStream[A] = tl //call by need

  /*
    val s = new Cons(1, EmptyStream)
    val prepend = 1 #:: s = new Cons(1, s) -> S will be NOT evaluated since is LAZY
   */
  override def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)

  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream) // still preserve lazy eval

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
  s = new Cons(1, ?)
  mapped = s.map(_ + 1) = new Cons(2, s.tail.map(_+1))
    ...mapped.tail --------- does means that wouldn't be executed until is needed
   */
  override def map[B](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f)) // preserves lazy evaluation

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): MyStream[A] =
    if (predicate(head)) new Cons(head, tail.filter(predicate))
    else tail.filter(predicate) // preserves lazy evaluation

  override def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n - 1)) // lazy eval too

}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons(start, MyStream.from(generator(start))(generator))
}


object StreamsPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)

  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals
  println(startFrom0.head)

  startFrom0.take(10000).foreach(println)
  println(startFrom0.map(_ * 2).take(100).toList())

  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.take(10).filter(_ < 10).toList())

  // Exercise on streams
  /*
  1 - stream of Fibonacci numbers
  2 - stream of prime numbers with Eratostenes sieve

  [2 3 4 ...]
  filter out all numbers divisible by 2
  [2 3 5 7 9 11 ...]
  filter out all numbers divisible by 3
  [2 3 5 7 11 13 17 ...]
  filter out all numbers divisible by 5

   */

  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] = new Cons(first, fibonacci(second, first + second))

  println(fibonacci(1,1).take(100).toList())

  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] =
    if (numbers.isEmpty) numbers
    else new Cons(numbers.head, eratosthenes(numbers.tail.filter(_ % numbers.head != 0)))

  println(eratosthenes(MyStream.from(2)(_ + 1)).take(100).toList())


}
