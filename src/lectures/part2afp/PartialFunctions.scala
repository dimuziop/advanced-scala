package lectures.part2afp

/**
 * User: patricio
 * Date: 20/3/21
 * Time: 06:52
 */
object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function[Int, Int] === Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value

  println(aPartialFunction(2))
 // println(aPartialFunction(999))

  // PF utilities
  println(aPartialFunction.isDefinedAt(67))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(98))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }
  println(pfChain(2))
  println(pfChain(45))

  // PF extends total function

  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOF's accepts partial functions as well

  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }

  println(aMappedList)

  /*
  Note: PF can only have one parameter type
   */

  /**
   * Exercise
   * 1 - construct a partial function instance yourself (anonymous class)
   * 2 - dumb chatbot as a PF
   */

  val myPartialFunction: Int => String = {
    case 1 => "One"
    case 5 => "Two"
    case 8 => "Three"
  }

  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 5 || x == 8

    override def apply(v1: Int): Int = v1
  }

  val myChatBot: String PartialFunction String = {
    case "hello" => "Hello, how are you"
    case "fine" => "Great, how an i help you"
  }



  scala.io.Source.stdin.getLines().map(myChatBot).foreach(println)

}
