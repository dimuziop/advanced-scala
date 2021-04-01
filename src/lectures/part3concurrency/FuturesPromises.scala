package lectures.part3concurrency

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
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

  // mini social network

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit = println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.3-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill",
    )

    val random = new Random()

    //API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriendProfile(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  //client: mark to poke bill

  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  /*mark.onComplete({
    case Success(markProfile) =>
      val bill = SocialNetwork.fetchBestFriendProfile(markProfile)
      bill.onComplete{
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(exception) => exception.printStackTrace()
      }
    case Failure(exception) => exception.printStackTrace()
  })*/
  // CALLBACK FROM HELL!!!!!
  //Thread.sleep(3000)

  // funcitonal composition of futures
  // map, flatMap, filter

  val nameOnTheWall = mark.map(profile => profile.name)
  val markBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriendProfile(profile))

  val zucksBestFriendRestricted = markBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for-comprehesions

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriendProfile(mark)
  } mark.poke(bill)

  Thread.sleep(3000)

  // fallbacks

  val aProfileNoMatherWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case error: Throwable => Profile("fb.id.3-dummy", "Forever Alone")
  }

  val aFetchProfileNoMatherWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case error: Throwable => SocialNetwork.fetchProfile("fb.id.3-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.3-dummy"))

  // inline backing app (transactional stuff)

  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount , status = "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost:Double): String = {
      // fetch user from db
      // create a transaction
      //WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(name)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds)// implicit conversions -> pimp my library

    }
  }

  println(BankingApp.purchase("Patricio", "Ferrari f50", "Enzo", 250000))

  // promises

  val promise = Promise[Int]() // "controller" over a future
  val future = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // "fulfilling" the promise
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)



}
