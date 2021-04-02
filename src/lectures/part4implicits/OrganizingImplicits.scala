package lectures.part4implicits

/**
 * User: patricio
 * Date: 2/4/21
 * Time: 13:13
 */
object OrganizingImplicits extends App {

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  println(List(1, 4, 5, 3, 2).sorted)

  /*
  Implicits:
    -val / var
    - object
    - accessor methods = defs with no parenthesis
   */

  // Exercise

  //implicit val sortPersonsByName: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
  object Person {
    implicit val sortPersonsByName: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  implicit val sortPersonsByAge: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)


  case class Person(name: String, age: Int)

  val personsList = List(
    Person("Steve", 30),
    Person("Marie", 22),
    Person("Anny", 27),
  )

  //println(personsList.sorted)

  /*
  Implicit scope
    - normal scope = LOCAL SCOPE
    - imported scope
    - companion of all types involved in the method signature
      - List
      - Ordering
      - all types involved = A or any supertype
   */
  //def sorted[B >: A](implicit ord : scala.math.Ordering[B]) : Repr

  object AlphabeticNameOrdering {
    implicit val sortPersonsByName: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering {
    implicit val sortPersonsByAge: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  }

  // import AlphabeticNameOrdering._
  println(personsList.sorted)

  /*
  Exercise
    - totalPrice = most used (50%)
    - by unt count = 25%
    - by unit price = 25%
   */

  case class Purchase(nUnits: Int, unitPrice: Double) {
    def totalAmount(): Double = nUnits * unitPrice
  }

  object Purchase {
    implicit val sortByTotalPrice: Ordering[Purchase] = Ordering.fromLessThan(_.totalAmount() < _.totalAmount())
  }

  object UnitCountOrdering {
    implicit val sortByUnitsQuantity: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits )
  }

  object UnitPriceOrdering {
    implicit val sortByUnitPrice: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice )
  }

}
