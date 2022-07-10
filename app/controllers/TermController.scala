package controllers

import models.Term
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{
  Action,
  AnyContent,
  BaseController,
  ControllerComponents,
  Request
}
import reactivemongo.bson.BSONObjectID
import repositories.TermRepository

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class TermController @Inject() (implicit
    executionContext: ExecutionContext,
    val termRepository: TermRepository,
    val controllerComponents: ControllerComponents
) extends BaseController {
  // controller actions goes here...

  def findAll(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      termRepository.findAll().map { terms =>
        Ok(Json.toJson(terms))
      }
  }

  def findOne(id: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val objectIdTryResult = BSONObjectID.parse(id)
      objectIdTryResult match {
        case Success(objectId) =>
          termRepository.findOne(objectId).map { term =>
            Ok(Json.toJson(term))
          }
        case Failure(_) =>
          Future.successful(BadRequest("Cannot parse the term id"))
      }
  }

  def create(): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { implicit request =>
      {

        request.body
          .validate[Term]
          .fold(
            _ => Future.successful(BadRequest("Cannot parse request body")),
            term =>
              termRepository.create(term).map { _ =>
                Created(Json.toJson(term))
              }
          )
      }
    }

  def update(id: String): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { implicit request =>
      {
        request.body
          .validate[Term]
          .fold(
            _ => Future.successful(BadRequest("Cannot parse request body")),
            term => {
              val objectIdTryResult = BSONObjectID.parse(id)
              objectIdTryResult match {
                case Success(objectId) =>
                  termRepository.update(objectId, term).map { result =>
                    Ok(Json.toJson(result.ok))
                  }
                case Failure(_) =>
                  Future.successful(BadRequest("Cannot parse the term id"))
              }
            }
          )
      }
    }

  def delete(id: String): Action[AnyContent] = Action.async {
    implicit request =>
      {
        val objectIdTryResult = BSONObjectID.parse(id)
        objectIdTryResult match {
          case Success(objectId) =>
            termRepository.delete(objectId).map { _ =>
              NoContent
            }
          case Failure(_) =>
            Future.successful(BadRequest("Cannot parse the term id"))
        }
      }
  }
}
