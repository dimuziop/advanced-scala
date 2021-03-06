package lectures.part3concurrency

import java.lang
import scala.collection.mutable
import scala.util.Random

/**
 * User: patricio
 * Date: 1/4/21
 * Time: 05:01
 */
object ThreadCommunication extends App {

  /*
  THE PRODUCER CONSUMER PROBLEM

  producer -> [ x? ] -> consumer
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int): Unit = value = newValue

    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProducerConsumer() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer] waiting...")
      while (container.isEmpty) {
        println("[Consumer] actively waiting...")
      }
      println("[Consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[Producer] I produced after a long work  " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()

  }

  //naiveProducerConsumer()

  // wait and notify

  def smartProducerConsumer(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer] waiting...")
      container.synchronized {
        container.wait()
      }
      // conbtainer nust have some value
      println("[Consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] Hard at work...")
      Thread.sleep(2000)
      val value = 42
      container.synchronized {
        println("[Producer] I produced after a long work  " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  //smartProducerConsumer()

  /*
  producer -> [ ? ? ? ] -> consumer
   */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[Consumer] buffer empty, waiting...")
            buffer.wait()
          }
          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println("[Consumer] I have consumed " + x)

          // hey producer, there is an empty space come on!
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[Producer] buffer is full, waiting...")
            buffer.wait()
          }
          // there must be at least ONE empty space in the buffer
          println("[Produced] I have produced " + i)
          buffer.enqueue(i)

          // consumer, wake up dude
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    })
    consumer.start()
    producer.start()
  }

  //prodConsLargeBuffer()

  /*
  producer1 -> [? ? ?] -> consumer1
  producerN -->-^   ^----> consumerN
   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[Consumer $id] buffer empty, waiting...")
            buffer.wait()
          }
          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println(s"[Consumer $id] I have consumed " + x)

          // hey producer, there is an empty space come on!
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[Producer $id] buffer is full, waiting...")
            buffer.wait()
          }
          // there must be at least ONE empty space in the buffer
          println(s"[Producer $id] I have produced " + i)
          buffer.enqueue(i)

          // consumer, wake up dude
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())

  }

  //multiProdCons(3,3)

  /*
  Exercises.
  1) think of an example where notifyAll acts in a different way than notify
  2) create a deadlock
  3) create a livelock
   */

  // notify all

  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread $i] waiting...")
        bell.wait()
        println(s"[thread $i] hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] Rock'n roll!")
      bell.synchronized {
        bell.notifyAll()
      }
    }).start()
  }

  //testNotifyAll()

  // 2 - deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other, feel free to pass...")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

  //new Thread(() => sam.bow(pierre)).start() // sam's lock,    |  then pierre's lock
  //new Thread(() => pierre.bow(sam)).start() // pierre's lock  |  then sam's lock

  // 3 - livelock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()

}
