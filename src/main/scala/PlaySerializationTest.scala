import CollisionDetectionMode.CollisionDetectionMode
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._

object PlaySerializationTest extends App {

  implicit def tuple2Reads[A, B](implicit aReads: Reads[A], bReads: Reads[B]): Reads[Tuple2[A, B]] = Reads[Tuple2[A, B]] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr(0))
      b <- bReads.reads(arr(1))
    } yield (a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  implicit def tuple2Writes[A, B](implicit aWrites: Writes[A], bWrites: Writes[B]): Writes[Tuple2[A, B]] = new Writes[Tuple2[A, B]] {
    def writes(tuple: Tuple2[A, B]) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
   }

  implicit val colRead = EnumJson.enumFormat(CollisionDetectionMode)

  implicit def genericMapWrite[A ,B](implicit aWrites: Writes[A], bWrites: Writes[B]): Writes[Map[A, B]] = new Writes[Map[A, B]] {
    override def writes(o: Map[A, B]): JsValue = Json.toJson(o.toList)
  }

  implicit def genericMapRead[A, B](implicit aReads: Reads[A], bReads: Reads[B]): Reads[Map[A, B]] = Reads[Map[A, B]] {
    case x: JsArray => {
      val result: JsResult[List[(A, B)]] = Json.fromJson[List[(A, B)]](x)
      JsSuccess(result.get.toMap)
    }
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected json array"))))
  }

  implicit def defaultReadInt(): Reads[Int] = Reads[Int] {
    case x: JsNumber => {
      JsSuccess(x.value.toInt)
    }
    case x: JsUndefined => {
      JsSuccess(0)
    }
    case _ => JsError(Seq(JsPath()-> Seq(ValidationError("Expected json array"))))
  }

  case class TimeOption(startTime: Option[Int], endTime: Option[Int], collisionMode: Option[Map[CollisionDetectionMode, Int]])
  case class Time(startTime: Int = 10, endTime: Int, collisionMode: Map[CollisionDetectionMode, Int])
  object Time{
    def apply(timeOption: TimeOption): Time = {
      Time(timeOption.startTime.getOrElse(10), timeOption.endTime.getOrElse(0), timeOption.collisionMode.get)
    }
  }

//  implicit val timeWrite = Json.writes[Time]
//  val timey = Time(10, 100, List((CollisionDetectionMode.Continuous, 12), (CollisionDetectionMode.ContinuousDynamic, 17)).toMap)
//  println(Json.toJson(timey))

//  implicit val timeRead: Reads[Time] = (
//    (__ \ 'startTime).readNullable[Int] and
//      (__ \ 'endTime).readNullable[Int] and
//      (__ \ 'collisionMode).read[Map[CollisionDetectionMode,Int]]
//    )((startTime, endTime, collisionMode) => new Time(
//    startTime = if (startTime.isEmpty) default.asInstanceOf[Int] else startTime.get,
//    endTime = endTime.get,
//    collisionMode = collisionMode
//  ))
  implicit val timeReads = Json.reads[TimeOption]

  private val timeJsonWrong = """{"startTime": 12, "endTime": 100, "collisionMode":[["Continuous",12],["ContinuousDynamic",17]]}"""
  println(timeJsonWrong)
  private val wrongTime: Time = Time(Json.fromJson[TimeOption](Json.parse(timeJsonWrong)).get)
  println(wrongTime)
}
