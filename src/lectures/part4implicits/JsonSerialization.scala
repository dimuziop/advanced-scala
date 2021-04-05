package lectures.part4implicits


import java.util.Date

/**
 * User: patricio
 * Date: 5/4/21
 * Time: 06:36
 */
object JsonSerialization extends App {

  /*
  Users, posts, fees
  Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
  1 - Intermediate data types: Int, String, List, Date
  2 - type classes for conversion to intermediate data types
  3 - serialize to JSON
   */

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(value: List[JSONValue]) extends JSONValue {
    override def stringify: String = value.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(value: Map[String, JSONValue]) extends JSONValue {
    /*
    {
      name: "John"
      age: 22
      friends: [...]
      latestPost: {
        content:  "Scala rocks"
      }
    }
     */
    override def stringify: String = value.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    } mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Me"),
    "posts" -> JSONArray(List(
      JSONString("Scala Rocks!"),
      JSONNumber(453)
    ))
  ))

  println(data.stringify)

  // type class to convert
  /*
  1 - type class
  2 - type class instances (implicit)
  3 - pimp library to use type class instances
   */

  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // 2.3 conversion
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue = {
      converter.convert(value)
    }
  }

  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  implicit object UserConverter extends JSONConverter[User] {
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "date" -> JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
      "user" -> feed.user.toJSON,
      "posts" -> JSONArray(feed.posts.map(_.toJSON))
    ))
  }



  val now = new Date(System.currentTimeMillis())
  val me = User("Patricio", 35, "me@dimauzio.dev")
  val feed = Feed(me, List(
    Post("Hello", now),
    Post("Nice stuff have been made here", now)
  ))

  println(feed.toJSON.stringify)

  // class stringify on result

}
