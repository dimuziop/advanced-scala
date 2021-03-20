package lectures.part1as

import lectures.part1as.AdvancedPatternMatching.Person

/**
 * User: patricio
 * Date: 20/3/21
 * Time: 05:02
 */
object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description: Unit = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
   - constants
   - wildcards
   - case classes
   - tuples
   - some special magic like above
   */

  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] = {
      if (person.age < 21) None
      else Some((person.name, person.age))
    }

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", age = 20)
  val greeting = bob match {
    case Person(n,a ) => s"Hi, my name is $n and I am $a yo"
    case _ => "Not applied"
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(legalStatus)

  /*
  Exercise
   */

  object MathProperty {
    def unapply(x: Int): Option[String] = {
      Some (if (x < 10) "Single digit"
      else if (x % 2 == 0) "an even number"
      else "")
    }
  }

  val number = 2

  number match {
    case MathProperty(prop) => println(prop)
  }

  object even {
    def unapply(x: Int): Boolean = x % 2 == 0

  }

  object singleDigit {
    def unapply(x: Int): Option[Boolean] = {
      if (x > -10 && x < 10) Some(true)
      else None
    }
  }

  val mathProperty = number match {
    case even() => "an even number"
    case singleDigit(_) => "Single digit"
    case _ => "no explicit prop"
  }

  println(mathProperty)

  // INFIX PATTERNS

  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val numberDescription = either match {
    case number Or string => s"$number is written as $string"
  }

  println(numberDescription)

  // decomposing sequences

  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))

  val decomposed = myList match {
    case MyList(1,2,_*) => "Starting with 1,2"
    case _ => "somethng else"
  }

  println(decomposed)

  // custom return types for unapply
  // isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false

      override def get: String = person.name
    }

  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An alien"
  })

}
