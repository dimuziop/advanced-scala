package lectures.part5ts

/**
 * User: patricio
 * Date: 6/4/21
 * Time: 05:00
 */
object RockingInheritance extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem

  trait Animal { def name: String }
  trait Lion extends Animal { override def name: String = "lion" }
  trait Tiger extends Animal { override def name: String = "tiger" }
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)

  /*
  Mutant extend Animal with { override def name: String = "lion" }
  with { override def name: String = "tiger" }

  LAS OVERRIDES GETS PICKED
   */

  // the super provlem + type linearization

  trait Cold {
    def print(): Unit = println("Cold")
  }

  trait Green extends Cold {
    override def print(): Unit = {
      println("Green")
      super.print()
    }
  }

  trait Blue extends Cold {
    override def print(): Unit = {
      println("Blue")
      super.print()
    }
  }

  class Red {
    def print(): Unit = println("Red")
  }

  class White extends Red with Green with Blue {
    override def print(): Unit = {
      println("White")
      super.print()
    }
  }

  val cold = new White
  cold.print()

}
