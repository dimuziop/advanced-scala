package lectures.part5ts

/**
 * User: patricio
 * Date: 8/4/21
 * Time: 02:02
 */
object StructuralTypes extends App {

  // structural types

  type JavaCloseable = java.io.Closeable

  class HipsterClosable {
    def close(): Unit = println("yeah, ye")

    def closeSilently(): Unit = println("not making noise")
  }

  //def closeQuietly(closable: JavaCloseable OR HipsterClosable): Unit = ???
  type UnifiedClosable = {
    def close(): Unit
  }

  def closeQuietly(unifiedClosable: UnifiedClosable): Unit = unifiedClosable.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = println("clos")
  })

  closeQuietly(new HipsterClosable)

  //TYPE REFINEMENTS

  type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaCloseable {
    def closeSilently(): Unit = println("shhh, closing")

    override def close(): Unit = println("yeah, ye")
  }

  def closeShh(advancedCloseable: AdvancedCloseable): Unit = advancedCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)
  //closeShh(new HipsterClosable)

  //using structural types as stand alone types

  def altClose(closable: {def close(): Unit}): Unit = closable.close()

  // type-checking => duck typing

  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("GUAU GUAU")
  }

  class Car {
    def makeSound(): Unit = println("RUM RUM")
  }

  val dog: SoundMaker = new Dog
  val cat: SoundMaker = new Car

  // static duck typing

  // CAVEAT: based on reflection


  /*
    Exercises
   */

  // 1.
  trait CBL[+T] {
    def head: T

    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithAHead: {def head: T}): Unit = println(somethingWithAHead.head)

  /*
    f is compatible with a CBL and with a Human? Yes.
   */

  case object CBNil extends CBL[Nothing] {
    def head: Nothing = ???

    def tail: CBL[Nothing] = ???
  }

  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new Human) // ?! T = Brain !!

  // 2.
  object HeadEqualizer {
    type Headable[T] = {def head: T}

    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

  /*
    is compatible with a CBL and with a Human? Yes.
   */
  val brainzList = CBCons(new Brain, CBNil)
  val stringsList = CBCons("Brainz", CBNil)

  HeadEqualizer.===(brainzList, new Human)
  // problem:
  HeadEqualizer.===(new Human, stringsList) // not type safe

}
