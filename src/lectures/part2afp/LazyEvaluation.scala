package lectures.part2afp

/**
 * User: patricio
 * Date: 27/3/21
 * Time: 12:43
 */
object LazyEvaluation extends App {

  // lazy DELAYS evaluation of values
  //lazy val x: Int = throw new RuntimeException
  lazy val x: Int = {
    println("Hello")
    42
  }
  println(x)
  println(x)

  // examples

  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no") // lazyCondition is nevere executed

  // in conjunction with call by name
  //def byNameMethod(n: => Int): Int = n + n + n + 1
  def byNameMethod(n: => Int): Int = {
    lazy val t = n // only evaluated once
    t + t + t + 1
  }

  def retrieveMagicValue: Int = {
    // side effect or a long computation
    println("String")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))

  //filtering with lazy values
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30)
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println
  gt20Lazy.foreach(println)

  // for-comprehensions use withFilter with guards
  for {
    a <- List(1,2,3) if a % 2 == 0 // use lazy vals!
  } yield a + 1
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1) // List[Int]



}
