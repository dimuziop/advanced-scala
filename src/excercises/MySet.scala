package excercises

import scala.annotation.tailrec

/**
 * User: patricio
 * Date: 20/3/21
 * Time: 08:11
 */
trait MySet[A] extends (A => Boolean) {

  /*
  Exercise -> implement a functional set
   */

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  def isEmpty(): Boolean

  def apply(v1: A): Boolean = contains(v1)

  /*
  Exercise
  + removing an element
  - intersection with another set
  - difference with another set
   */

  def -(elem: A): MySet[A]

  def &(anotherSet: MySet[A]): MySet[A] // intersection

  def --(anotherSet: MySet[A]): MySet[A] // difference

  def unary_! : MySet[A]

}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def isEmpty(): Boolean = true

  override def -(elem: A): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

// All elements of a type A witch satisfy a property
// {x in A | property(x)}
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {

  override def contains(elem: A): Boolean = property(elem)

  // {x in A | property(x)} + element = {x in A | property(x) || x == element}
  override def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem)

  // {x in A | property(x)} ++ set = {x in A | property(x) || x == set contains x }
  override def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x)) // property based set

  override def foreach(f: A => Unit): Unit = politelyFail

  override def isEmpty(): Boolean = ???

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}


class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  override def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] = {
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)
  }

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  override def isEmpty(): Boolean = false

  override def -(elem: A): MySet[A] = {
    if (head == elem) tail
    else tail - elem + head
    //filter(subject => subject != elem)
  }

  override def &(anotherSet: MySet[A]): MySet[A] = {
    //filter(subject => anotherSet.contains(subject))
    filter(anotherSet)
  }

  override def --(anotherSet: MySet[A]): MySet[A] = {
    //filter(subject => !anotherSet(subject)) ++ anotherSet.filter(elem => !contains(elem))
    filter(!anotherSet)
  }

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if(valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values, new EmptySet[A])
  }
}

class EmptySetException extends RuntimeException


object Test extends App {
  val mySet = new NonEmptySet[Int](5, new NonEmptySet[Int](35, new NonEmptySet[Int](18, new EmptySet[Int])))
  val mySet1 = new NonEmptySet[Int](8, new NonEmptySet[Int](35, new NonEmptySet[Int](18, new EmptySet[Int])))

  val mySetIntersection = mySet ++ mySet1

  val newSet = mySet + 35
  val newSetII = mySet + 35

  println(mySet.contains(42))
  println(newSet.contains(35))
  println(newSetII == newSet)
  println(mySetIntersection.contains(5))
  println(mySetIntersection.contains(8))

  mySet.map(x => println(x + 1))

  val flattenSet = mySet.flatMap(x => new NonEmptySet[Int](x, new NonEmptySet[Int](x * 2, new EmptySet[Int])))
  flattenSet.map(println)

  mySet.filter(x => x % 2 == 0).foreach(println)

  println(mySet1(12))

  println("--------------")

  mySet1.foreach(println)
  println("Intersec")
  (mySet & mySet1).foreach(println)
  println("Diff")
  (mySet -- mySet1).foreach(println)

  println("Minus")
  (((mySet - 5) - 35) - 18).foreach(println)


  // property set
  println("More test")

  val set = MySet(1,2,3,4)
  set + 5 ++ MySet(-1, -2) + 3 flatMap(x => MySet(x, 10 * x)) filter(_ % 2 == 0) foreach println

  println("Property set")
  val negative = !set

  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))
  println((negativeEven + 5)(5));

  new NonEmptySet[Int](1, new NonEmptySet[Int](5, new EmptySet[Int]))


}
