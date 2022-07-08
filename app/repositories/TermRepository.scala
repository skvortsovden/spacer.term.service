package repositories

import javax.inject.Singleton
import models.Term
import org.joda.time.DateTime
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import javax.inject._
import reactivemongo.api.bson.collection.BSONCollection
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteResult

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TermRepository @Inject() (implicit
    executionContext: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  def collection: Future[BSONCollection] =
    reactiveMongoApi.database.map(db => db.collection("terms"))
  // Repository methods ...

  def findAll(limit: Int = 100): Future[Seq[Term]] = {

    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Term])
        .cursor[Term](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Term]]())
    )
  }

  def findOne(id: BSONObjectID): Future[Option[Term]] = {
    collection.flatMap(
      _.find(BSONDocument("_id" -> id), Option.empty[Term]).one[Term]
    )
  }

  def create(Term: Term): Future[WriteResult] = {
    collection.flatMap(
      _.insert(ordered = false)
        .one(
          Term.copy(
            _creationDate = Some(new DateTime()),
            _updateDate = Some(new DateTime())
          )
        )
    )
  }

  def update(id: BSONObjectID, Term: Term): Future[WriteResult] = {

    collection.flatMap(
      _.update(ordered = false).one(
        BSONDocument("_id" -> id),
        Term.copy(_updateDate = Some(new DateTime()))
      )
    )
  }

  def delete(id: BSONObjectID): Future[WriteResult] = {
    collection.flatMap(
      _.delete().one(BSONDocument("_id" -> id), Some(1))
    )
  }

}
