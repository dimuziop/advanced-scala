package lectures.part4implicits

/**
 * User: patricio
 * Date: 2/4/21
 * Time: 12:33
 */
object ImplicitsIntro extends App {

  val pair = "Patricio" -> "999"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi my name is $name"
  }

  implicit def fromStringToPerson(name: String): Person = new Person(name)

  println("Peter".greet) // printLn(fromStringToPerson("Peter").greet)

  // implicit parameters

  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10

  println(increment(2))

  // not default args, the compiler passes when it's making the job

}
