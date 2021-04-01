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

  prodConsLargeBuffer()

}
