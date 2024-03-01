package controllers

import actors.Actors.MobileDbActorMessages._
import actors.Actors.{actorSystem, mobileDbActor}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import models.{Mobile, MobileForm, MobileUpdateForm}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.{existentials, postfixOps}

trait RequestError

case class InvalidId(message: String) extends RequestError

trait MobileJsonProtocol extends DefaultJsonProtocol {
  implicit val mobileFormat: RootJsonFormat[Mobile] = jsonFormat4(Mobile)
  implicit val mobileFormFormat: RootJsonFormat[MobileForm] = jsonFormat3(MobileForm)
  implicit val mobileUpdateFormFormat: RootJsonFormat[MobileUpdateForm] = jsonFormat1(MobileUpdateForm)
}

object Routes extends MobileJsonProtocol {

  import actorSystem.dispatcher


  private def getMobile(id: Int): Future[HttpEntity.Strict] = {
    val futureListMobile = (mobileDbActor ? GetMobileById(id)).mapTo[List[Mobile]]
    for {
      mobileList <- futureListMobile
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        mobileList.toJson.prettyPrint
      )
    }
  }

  private def getAllMobiles: Future[HttpEntity.Strict] = {
    val futureListMobile = (mobileDbActor ? GetAllMobiles).mapTo[List[Mobile]]
    for {
      mobileList <- futureListMobile
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        mobileList.toJson.prettyPrint
      )
    }
  }


  implicit val defaultTimeout: Timeout = Timeout(2 seconds)

  val routes: Route =
    path("api" / "mobile" / IntNumber) { (id: Int) =>
      get {
        complete(
          StatusCodes.OK,
          getMobile(id)
        )
      }
    } ~
      path("api" / "mobile") {
        get {
          complete(
            StatusCodes.OK,
            getAllMobiles
          )
        }
      }

  val server: Future[Http.ServerBinding] = Http().newServerAt("localhost", 9000).bind(routes)
}