package models

import org.joda.time.DateTime
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.{Format, Json}
import reactivemongo.bson.{BSONObjectID, _}
import reactivemongo.play.json._

case class Term(
    _id: Option[BSONObjectID],
    _creationDate: Option[DateTime],
    _updateDate: Option[DateTime],
    text: String,
    definition: String,
    example: String,
    tags: List[String]
)
object Term {
  implicit val fmt: Format[Term] = Json.format[Term]
  implicit object TermBSONReader extends BSONDocumentReader[Term] {
    def read(doc: BSONDocument): Term = {
      Term(
        doc.getAs[BSONObjectID]("_id"),
        doc
          .getAs[BSONDateTime]("_creationDate")
          .map(dt => new DateTime(dt.value)),
        doc
          .getAs[BSONDateTime]("_updateDate")
          .map(dt => new DateTime(dt.value)),
        doc.getAs[String]("text").get,
        doc.getAs[String]("definition").get,
        doc.getAs[String]("example").get,
        doc.getAs[List[String]]("tags").get
      )
    }
  }

  implicit object TermBSONWriter extends BSONDocumentWriter[Term] {
    def write(Term: Term): BSONDocument = {
      BSONDocument(
        "_id" -> Term._id,
        "_creationDate" -> Term._creationDate.map(date =>
          BSONDateTime(date.getMillis)
        ),
        "_updateDate" -> Term._updateDate.map(date =>
          BSONDateTime(date.getMillis)
        ),
        "text" -> Term.text,
        "definition" -> Term.definition,
        "example" -> Term.example,
        "tags" -> Term.tags
      )
    }
  }
}
