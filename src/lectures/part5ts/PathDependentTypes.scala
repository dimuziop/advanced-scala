package lectures.part5ts

/**
 * User: patricio
 * Date: 7/4/21
 * Time: 15:37
 */
object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner): Unit = println(i)
    def printGeneral(i: Outer#Inner): Unit = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String

    2
  }

  // per-instance

  val o = new Outer
  val inner = new o.Inner

  val oo = new Outer
  //val otherInner: oo.Inner = new o.Inner -- DO NOT COMPILES

  o.print(inner)
  //oo.print(inner) --- again Do not compiles

  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)

  /*
     Exercise
     DB keyed by Int or String, but maybe others
    */

  /*
    use path-dependent types
    abstract type members and/or type aliases
   */

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    override type Key = K
  }

  trait StringItem extends Item[String]
  trait IntItem extends Item[Int]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType#Key = key

  get[IntItem](42) // ok
  //get[IntItem]("well") // not OK ok
  get[StringItem]("home") // ok
}
