package lectures.part4implicits

/**
 * User: patricio
 * Date: 3/4/21
 * Time: 03:30
 */
object PimpMyLibrary extends App {

  //2.isPrime

  implicit class RichInt(value: Int) {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)

    def times(closure: () => Unit): Unit = {
      (1 to value).foreach(_ => closure())
    }

    def *[A](list: List[A]): List[A] = {
      (1 to value).map(_ => list).toList.flatten
    }

    def stringToInt(string: String): Int = Integer.valueOf(value)

  }

  42.isEven // type enrichment | PIMPING
  println(42.sqrt)

  3.times(() => println("Say_Hello"))

  println(3.*(List(1,2)))

  /*
  * Enrich the string class
    -asInt
    - encrypt // Caesar encrypt
      John -> Lnjp
   * Keep Enriching int class
      - times (function)
      * 3.times (() => ..)
      * *
      * 3 * List(1,2) => List(1,2,1,2,1,2)
   */

  implicit class RichString(value: String) {

    val abc = List('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x', 'y', 'z')

    def asInt: Option[Int] = {
      try {
        Some(Integer.valueOf(value))
      } catch {
        case e: Exception => None
      }
    }
    
    /*def caesarEncrypt(key: Int): String = {
      value.toList.map(char => {
        val i = abc.indexOf(char.toLower)
        abc((i + key) % abc.length)
      }).mkString
    }*/
    def caesarEncrypt(key: Int): String = {
      value.map(c => (c + key).asInstanceOf[Char])
    }
  }

  println("TestZ".caesarEncrypt(5))

  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
  println("6" / 2)

  // equivalent: implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1

  /*
    if (n) do something
    else do something else
   */

  val aConditionedValue = if (3) "OK" else "Something wrong"
  println(aConditionedValue)

}
