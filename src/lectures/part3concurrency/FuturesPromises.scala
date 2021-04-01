package lectures.part3concurrency

import scala.concurrent.Future
import scala.util.{Failure, Success}
// important for futures
//import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * User: patricio
 * Date: 1/4/21
 * Time: 10:55
 */
object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  /*val aFuture = Future {
    calculateMeaningOfLife // calculate3s the meaning od life on anopther thread
  }(global)*/

  val aFuture = Future {
    calculateMeaningOfLife // calculate3s the meaning od life on implicitly thread
  }

  println(aFuture.value) // Option[Try[Int]]

  println("Waiting on the future")

  /*aFuture.onComplete(t => t match {
    case Success(meaningOfLife) => println(s"this is $meaningOfLife")
    case Failure(e) => println(s"have filed on $e")
  }) === */

  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"this is $meaningOfLife")
    case Failure(e) => println(s"have filed on $e")
  }

  Thread.sleep(3000)

}
