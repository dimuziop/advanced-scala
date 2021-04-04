package excercises

import lectures.part4implicits.TypeClasses.User

/**
 * User: patricio
 * Date: 3/4/21
 * Time: 17:14
 */
object EqualityPlayground extends App {

  trait Equal[T] {
    def apply(main: T, other: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(main: User, other: User): Boolean = main.name == other.name
  }

  object Equal {
    def apply[T](main: T, other: T)(implicit evaluator: Equal[T]): Boolean = evaluator.apply(main, other)
  }




  println(NameEquality(User("Braulio", 37, "braulio@braulio.com"), User("Braulio", 37, "braulio@braulio.com")))
  // AD-HOC Polymorphism
  Equal[User](User("Braulio", 37, "braulio@braulio.com"), User("Braulio", 37, "braulio@braulio.com"))

  var braulio1 = User("Braulio", 37, "braulio@braulio.com")
  var braulio2 = User("Braulio", 37, "braulio@braulio.com")


  /*
  Exercise - improve the Equal TC wirth an implicit conversion class
  ===(anotherValue: Y)
  !==(anotherValue: Y)
   */

  implicit class TypeEquality[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer(value, anotherValue)
  }

  println(braulio1 === braulio2)
  //println(braulio1 === 43) // type safe
  println(braulio1 !== braulio2)


}
