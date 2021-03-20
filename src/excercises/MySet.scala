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

  /*def head(): A

  def tail(): MySet[A]*/

  def apply(v1: A): Boolean = contains(v1)
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

  /*override def head(): A = throw new EmptySetException

  override def tail(): MySet[A] = throw new EmptySetException*/
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  /*override def contains(elem: A): Boolean = {
    @tailrec
    def auxContains(value: MySet[A]): Boolean =
      if (value.head == elem) true
      else if (value.tail().isEmpty()) false
      else auxContains(value.tail())
    auxContains(this)
  }*/

  override def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)

  /*override def +(elem: A): MySet[A] = {
    @tailrec
    def add(mySet: MySet[A], acc: MySet[A]): MySet[A] ={
      if (mySet.isEmpty()) acc
      else add(mySet.tail(), new NonEmptySet[A](mySet.head(), acc))
    }
    if(this.contains(elem)) this
    else add(this, new NonEmptySet[A](elem, new EmptySet[A]))
  }*/

  override def +(elem: A): MySet[A] = {
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)
  }

  /*override def ++(anotherSet: MySet[A]): MySet[A] = {
    @tailrec
    def add(mySet: MySet[A], acc: MySet[A]): MySet[A] ={
      if (mySet.isEmpty()) acc
      else add(mySet.tail(), acc + mySet.head())
    }
    add(this, anotherSet)

  }*/

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head



  /*override def map[B](f: A => B): MySet[B] = {
    @tailrec
    def helperFn(mySet: MySet[A], acc: MySet[B]): MySet[B] = {
      if (mySet.tail().isEmpty()) new NonEmptySet[B](f(mySet.head()), acc)
      else helperFn(mySet.tail(), new NonEmptySet[B](f(mySet.head()), acc))
    }

    helperFn(this, new EmptySet[B])
  }*/

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  /*override def flatMap[B](f: A => MySet[B]): MySet[B] = {

    @tailrec
    def flatMapHelper(value: MySet[A], acc: MySet[B]): MySet[B] = {
      if (value.tail().isEmpty()) f(value.head())
      else flatMapHelper(value.tail(), f(value.head()))
    }

    flatMapHelper(this, new EmptySet[B])

  }*/

  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  /*override def filter(predicate: A => Boolean): MySet[A] = {
    @tailrec
    def filterAux(subject: MySet[A], acc: MySet[A]): MySet[A] = {
      if (subject.isEmpty()) acc
      else if (predicate(subject.head())) filterAux(subject.tail(), acc + subject.head())
      else filterAux(subject.tail(), acc)
    }

    filterAux(this, new EmptySet[A])
  }*/

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  /*override def foreach(f: A => Unit): Unit = {
    @tailrec
    def feAux(head: A, tail: MySet[A]): Unit =
      if (tail.isEmpty()) f(head)
      else {
        f(head)
        feAux(tail.head(), tail.tail())
      }

    feAux(this.head, this.tail)
  }*/

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  override def isEmpty(): Boolean = false

  /*override def head(): A = this.head

  override def tail(): MySet[A] = this.tail*/

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
  val mySet1 = new NonEmptySet[Int](8, new NonEmptySet[Int](35, new EmptySet[Int]))

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
}
