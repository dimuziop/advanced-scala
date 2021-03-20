package lectures.part1as

import scala.annotation.tailrec

/**
 * User: patricio
 * Date: 17/3/21
 * Time: 05:23
 */
object Recap extends App {

  val aCondition: Boolean = false
  val aConditionalVal = if (aCondition) 42 else 65
  // instructions vs expressions

  val aCodeBlock = {
    if (aCondition) 54
    56
  }

  // Unit -> Side Effect (Void)

  val theUnit: Unit = println("Hello Scala")

  //function
  def aFunction(x: Int): Int = x + 1

  //recursion: stack and tail
  @tailrec
  def factorial(n: Int, accumulator: Int): Int =
    if (n <= 0) accumulator
    else factorial(n-1, n*accumulator)

  //object oriented programming

  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog //subtyping polymorphism

  trait Carnivore {
    def eat(animal: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = ???
  }

  val aCrock = new Crocodile
  aCrock.eat(aDog)
  aCrock eat aDog // natural language

  // anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(animal: Animal): Unit = println("Roar")
  }

  // generics
  abstract class MyList[+A] //covariance | variance and variance problems

  // singleton and companion
  object MyList

  // case Classes
  case class Person(name: String, age: Int)

  // exceptions

  val throwsException = throw new RuntimeException // Nothing

  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some log")
  }

  // packaging and imports

  // functional programming
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  incrementer(1)

  val anonymousIncrementer = (x: Int) => x + 1

  List(1,2,3).map(incrementer) // HOF
  //map flapMap filter

  // for-comprehension
  var pairs = for {
    num <- List(1,2,3)
    char <- List('a','b','c')
  } yield num + "-" + char

  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps

  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  // "Collection": Options, Try
   val anOption = Some(2) //monad

  // pattern matching

  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greetings = bob match {
    case Person(name, _) => s"Hi, my name is $name"
  }

  // all the patterns








}
