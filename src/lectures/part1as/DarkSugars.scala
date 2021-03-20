package lectures.part1as

import scala.util.Try

/**
 * User: patricio
 * Date: 20/3/21
 * Time: 03:35
 */
object DarkSugars extends App {
  // syntax sugar #1 methods with a single param

  def singleArgMethod(arg: Int): String = s"$arg little ducks....."

  val description = singleArgMethod {
    42
  }

  val aTryInstance = Try { // java's try {....}
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val anFunkyInstance: Action = (x: Int) => x + 1

  // example: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello, Scala")
  })

  val aSweeterThread = new Thread(() => println("Hello, Scala"))

  // syntax sugar #3: the :: and #:: methods are special

  val prependList = 2 :: List(3, 4)
  // 2.::(List(3,4))
  // List(3,4)::(2)

  //scala spec: last char decides the associativity of method
  1 :: 2 :: 3 :: List(4,5)
  List(4,5).::(3).::(2).::(1) // equivalent

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual implementation
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4: multi-word method naming

  class Teen(name: String) {
    def `and then said`(gossip: String): Unit = println(s"$name said $gossip")
  }

  val lilly = new Teen("Lilly")
  lilly `and then said` "Scala is so sweet"

  // syntax sugar #5: infix types

  class Composite[A, B]
  val composite: Composite[Int, String] = ???
  val compositeInfix: Int Composite String = ???

  class --> [A,B]
  val towards: Int --> String = ???

  // syntax sugar #6: update() is very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 // reWritten to anArray.update(2,7)
  // used in mutable collections

  // syntax sugar #7: setters and mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member: Int = internalMember // getter
    def member_=(value: Int): Unit = internalMember = value // setter
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewritten as aMutableContainer.member_=(42)


}
