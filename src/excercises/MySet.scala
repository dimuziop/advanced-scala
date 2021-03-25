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

  override def unary_! : MySet[A] = new AllInclusiveSet[A]
}

class AllInclusiveSet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = true

  override def +(elem: A): MySet[A] = this

  override def ++(anotherSet: MySet[A]): MySet[A] = this

  override def map[B](f: A => B): MySet[B] = ???

  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???

  override def filter(predicate: A => Boolean): MySet[A] = ??? // property based set

  override def foreach(f: A => Unit): Unit = ???

  override def isEmpty(): Boolean = ???

  override def -(elem: A): MySet[A] = ???

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def unary_! : MySet[A] = new EmptySet[A]
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

  override def unary_! : MySet[A] = ???
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

}
