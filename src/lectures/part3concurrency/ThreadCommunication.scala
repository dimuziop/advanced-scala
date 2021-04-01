package lectures.part3concurrency

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

  smartProducerConsumer()

}
