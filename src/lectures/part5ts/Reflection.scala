package lectures.part5ts

/**
 * User: patricio
 * Date: 9/4/21
 * Time: 03:43
 */
object Reflection extends App {

  // reflection + macros + quasiquotes => METAPROGRAMMING

  case class Person(name: String) {
    def sayName(): Unit = println(s"Hi, my name is $name")
  }

  // 0 - import
  import scala.reflect.runtime.{universe => ru}

  //1 - MIRROR - runtime mirror
  val m = ru.runtimeMirror(getClass.getClassLoader)

  // 2 - create a class object

  val clazz = m.staticClass("lectures.part5ts.Reflection.Person") // creating a class object by name

  //3 - create a reflected mirror
  val cm = m.reflectClass(clazz)

  // 4 - get the constructor
  val constructor = clazz.primaryConstructor.asMethod

  // 5 - reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)

  //6 - invoke the contructor
  val instance = constructorMirror.apply("John")

  println(instance)

  // I have an instance
  val p = Person("Mary") // from the wire as serialized onbject
  // method computed for somewhere else
  val methodName = "sayName"

  //1 - mirror
  // 2 - reflect the instance
  val reflected = m.reflect(p)
  //3 mthod symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  //4 reflect the method
  val method = reflected.reflectMethod(methodSymbol)
  // 5 invoke the method
  method.apply()


  // Reflection on the Erasure Context

  //pp #1: differentiate types at runtime

  val numbers = List(1,2,3)
  numbers match {
    case listOfStrings: List[String] => println("list of string")
    case listOfNumbers: List[Int] => println("list of numbers")
  }


  //pp #1: limitaitons on overloads

  /*def processList(list: List[Int]): Int = 35
  def processList(list: List[String]): Int = 42

  println(processList(List(1,2,3)))*/

  // Type Tags

  // 0 imports
  import ru._

  val ttag = typeTag[Person]
  println(ttag)

  class MyMap[K, V]

  // 2 - pass type tags as implicit parameters
  def getTypeArguments[T](value: T)(implicit typeTag: TypeTag[T]) = typeTag.tpe match {
    case TypeRef(_, _, typeArguments) => typeArguments
    case _ => List()
  }

  val myMap = new MyMap[Int, String]
  val typeArgs = getTypeArguments(myMap)//(typeTag: TypeTag[MyMap[Int,String]])
  println(typeArgs)

  def isSubtype[A, B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean = {
    ttagA.tpe <:< ttagB.tpe
  }

  class Animal
  class Dog extends Animal
  println(isSubtype[Dog, Animal])

  // I have an instance


  // 3 - method symbol
  val anotherMethodSymbol = typeTag[Person].tpe.decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method = can DO things
  val sameMethod = reflected.reflectMethod(anotherMethodSymbol)
  // 5 - invoke the method
  sameMethod.apply()




}
