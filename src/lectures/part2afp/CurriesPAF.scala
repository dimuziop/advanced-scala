package lectures.part2afp

/**
 * User: patricio
 * Date: 25/3/21
 * Time: 15:11
 */
object CurriesPAF extends App {

  // curried functions
  val supperAdder: Int => Int => Int =
    x => y => x + y

  val add3 = supperAdder(3)
  println(add3(5))
  println(supperAdder(3)(5)) // curried function

  // METHOD!
  def curriedAdder(x: Int)(y: Int): Int = x + y

  val add4: Int => Int = curriedAdder(4) // if we dont specify explicitly the type, this would not compile
  // ^^^ lifting = ETA-EXPANSION
  // functions != methods (JVM limitation)

  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // ETA expansions -> (x) => inc(x)

  // Partial function applications
  val add5 = curriedAdder(4) _ // Int -> Int [Sugar]

  // Exercise
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => 7 + y
  // as many different implementations of add7 using the above
  // be creative!

  val add7: Int => Int = (x: Int) => simpleAddFunction(7, x) //simplest
  val add7_2: Int => Int = simpleAddFunction.curried(7)
  val add7_6: Int => Int = simpleAddFunction(7, _: Int) // works as well

  val add7_3: Int => Int = curriedAddMethod(7) _ //PAF
  val add7_4: Int => Int = curriedAddMethod(7)(_) //PAF = alternative syntax

  val add7_5: Int => Int = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values


  // underscores are powerful
  def concatenator(a: String, b: String, c: String): String = a + b + c

  val insertName = concatenator("Hello I'm ", _: String, " How are you?") // x => concatenator("Hello I'm ", x , " How are you?")
  println(insertName("Waldo Wolf"))

  val fillTheBlanks = concatenator("Hello I'm ", _: String, _: String) // (x, y) => concatenator("Hello I'm ", x , y)
  println(fillTheBlanks("Me", " Scala is awesome"))

  //EXERCISES
  /*
  1. Process a lists of number and return their string representation with different formats
     Use the %4.2f, %8.6g and %14.12f with a curried formatter function
     "%4.2f".format(Math.PI)
   */

  def formattedString(format: String)(number: Double): String = format.format(number)

  val formattedOne: Double => String = formattedString("%4.2f")
  val formattedTwo = formattedString("%8.6f") _ //lift
  val formattedThree: Double => String = formattedString("%14.12f")

  println(formattedOne(Math.PI))
  println(formattedTwo(Math.PI))
  println(formattedThree(Math.PI))

  /*
  2. difference between
      - functions vs methods
      - parameters: by-name vs 0-lambda
   */
  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parentMethod(): Int = 42

  /*
  calling by name and by function
    - int
    - method
    - parenMethod
    - lambda
    - PAF
   */

  byName(23) //ok
  byName(method) //ok
  byName(parentMethod())
  byName(parentMethod) // okk but beware ==> byName(parenMethod())
  // byName(() => 42) // not ok
  byName((() => 42)()) // ok
  // byName(parentMethod _) //not ok
  //byFunction(45) // not ok
  // byFunction(method) // not ok - does not do ETA xpansion
  byFunction(parentMethod) // compiler does ETA xpnsiopn
  byFunction(() => 42) // works
  byFunction(parentMethod _) // also works, but warning unnecessary


}
