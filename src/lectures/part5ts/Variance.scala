package lectures.part5ts

/**
 * User: patricio
 * Date: 6/4/21
 * Time: 11:11
 */
object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // What is variance?
  // "inheritance" - type substitution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val cCage: CCage[Animal] = new CCage[Cat]

  // no - invariance
  class ICage[T]
  //val iCage: ICage[Animal] = new ICage[Cat]
  //val x: Int = "Hello"

  // hell no - oposite = contravariance
  class XCage[-T]
  val xCage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](val animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T) // COVARIANT POSITION

  //class ContravariantCage[-T](val animal: T) // COVARIANT POSITION
  /*
  This will be allowing
  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile) // WRONG
   */

  //class CovariantVariableCage[+T](var animal: T) // types of vars are in CONTRAVARIANT POSITION
  /*
  This will be allowing
  val cCage: CCage[Animal] = new CCage[Cat](new Cat)
  cCage.animal = new Crocodile// WRONG
   */

  //class ContravariantVariableCage[-T](var animal: T) // types of vars are also in COVARIANT POSITION

  /*
  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile) // WRONG
   */

  class InvariantVariableCage[T](var animal: T) // OK

  //  trait AnotherCovariantCage[+T] {
  //    def addAnimal(animal: T) // CONTRAVARIANT POSITION
  //  }
  /*
    val ccage: CCage[Animal] = new CCage[Dog]
    ccage.add(new Cat)
   */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION.

  // return types
  class PetShop[-T] {
    //    def get(isItaPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
      val catShop = new PetShop[Animal] {
        def get(isItaPuppy: Boolean): Animal = new Cat
      }
      val dogShop: PetShop[Dog] = catShop
      dogShop.get(true)   // EVIL CAT!
     */

    def get[S <: T](isItaPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]
  //  val evilCat = shop.get(true, new Cat)
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
   Big rule
   - method arguments are in CONTRAVARIANT position
   - return types are in COVARIANT position
  */

  /**
   * 1. Invariant, covariant, contravariant
   *   Parking[T](things: List[T]) {
   *     park(vehicle: T)
   *     impound(vehicles: List[T])
   *     checkVehicles(conditions: String): List[T]
   *   }
   *
   * 2. used someone else's API: IList[T]
   * 3. Parking = monad!
   *     - flatMap
   */

  class Vehicle
  class MotorBike extends Vehicle
  class Car extends Vehicle

  class IList[T]

  class IParking[T](vehicle: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }
  class Parking[+T](things: List[T]) {
    def park[A >: T](vehicle: A): Parking[A] = ???
    def impound[A >: T](vehicles: List[A]): Parking[A] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[A](f: T => Parking[A]): Parking[A] = ???
  }
  class CParking[-T](things: List[T]) {
    def park(vehicle: T): CParking[T] = ???
    def impound(vehicles: List[T]): CParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???

    def flatMap[A <: T, S](f: (A) => CParking[S]): CParking[S] = ???
    //def flatMap[A <: T, S](f: Function1[A , CParking[S]]): CParking[S] = ???
  }

  /*
    Rule of thumb
    - use covariance = COLLECTION OF THINGS
    - use contravariance = GROUP OF ACTIONS
   */


  class Parking2[+T](things: IList[T]) {
    def park[A >: T](vehicle: A): Parking2[A] = ???
    def impound[A >: T](vehicles: IList[A]): Parking2[A] = ???
    def checkVehicles[A >: T](conditions: String): IList[A] = ???
  }
  class CParking2[-T](things: IList[T]) {
    def park(vehicle: T): CParking2[T] = ???
    def impound[S <: T](vehicles: IList[S]): CParking2[T] = ???
    def checkVehicles[S <: T](conditions: String): IList[S] = ???
  }

}
