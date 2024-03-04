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
import models._
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.{existentials, postfixOps}

trait MobileJsonProtocol extends DefaultJsonProtocol {
  implicit val mobileFormat: RootJsonFormat[Mobile] = jsonFormat4(Mobile)
  implicit val mobileFormFormat: RootJsonFormat[MobileForm] = jsonFormat4(MobileForm)
  implicit val mobileUpdateFormFormat: RootJsonFormat[MobileUpdateForm] = jsonFormat1(MobileUpdateForm)
  implicit val userFormFormat: RootJsonFormat[UserForm] = jsonFormat1(UserForm)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val userWithMobileFormat: RootJsonFormat[UserWithMobile] = jsonFormat1(UserWithMobile)
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

  private def createMobile(mobileForm: MobileForm): Future[HttpEntity.Strict] = {
    val futureMobileCreated = (mobileDbActor ? CreateMobile(mobileForm)).mapTo[MobileCreated]

    for {
      mobileCreated <- futureMobileCreated
    } yield {
      HttpEntity(
        ContentTypes.`text/plain(UTF-8)`,
        s"New mobile is added into the db with id: ${mobileCreated.id}"
      )
    }
  }

  private def createUser(usrForm: UserForm): Future[HttpEntity.Strict] = {
    val futureUserCreated = (mobileDbActor ? CreateUser(usrForm)).mapTo[UserCreated]
    for {
      userCreated <- futureUserCreated
    } yield {
      HttpEntity(
        ContentTypes.`text/plain(UTF-8)`,
        s"New user is added into the db with id: ${userCreated.userId}"
      )
    }

  }

  def getAllUsers: Future[HttpEntity.Strict] = {
    val futureUser = (mobileDbActor ? GetAllUsers).mapTo[List[User]]
    for {
      user <- futureUser
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        user.toJson.prettyPrint
      )
    }

  }

  private def getUsersWithMobile(userId: Int): Future[HttpEntity.Strict] = {
    val futureListOfUserWithMobile = (mobileDbActor ? GetUserWithMobile(userId)).mapTo[List[UserWithMobile]]
    for {
      user <- futureListOfUserWithMobile
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        user.toJson.prettyPrint
      )
    }
  }

  private def deleteById(mobileId: Int): Future[HttpEntity.Strict] = {
    val optionalString = (mobileDbActor ? DeleteById(mobileId)).mapTo[Option[String]]

    for {
      string <- optionalString
    } yield string match {
      case Some(stringValue) =>
        HttpEntity(
          ContentTypes.`text/plain(UTF-8)`,
          stringValue
        )
      case None =>
        HttpEntity(
          ContentTypes.`text/plain(UTF-8)`,
          "No row deleted."
        )
    }
  }

  private def updateById(mobileId: Int, priceToUpdate: Double): Future[HttpEntity.Strict] = {
    val futureOptionalString = (mobileDbActor ? UpdateById(mobileId, priceToUpdate)).mapTo[Option[String]]
    for {
      optionalString <- futureOptionalString
    } yield optionalString match {
      case Some(value) =>
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, value)
      case None => HttpEntity(ContentTypes.`text/plain(UTF-8)`, "No row updated.")
    }
  }


  implicit val defaultTimeout: Timeout = Timeout(2 seconds)

  private val routes: Route =
    path("api" / "mobile" / IntNumber) { (id: Int) =>
      get {
        complete(
          StatusCodes.OK,
          getMobile(id)
        )
      } ~
        delete {
          complete(StatusCodes.OK, deleteById(id))
        } ~
        put {
          entity(as[MobileUpdateForm]) { data =>
            complete(StatusCodes.OK, updateById(id, data.price))
          }
        }
    } ~
      path("api" / "mobile") {
        get {
          complete(StatusCodes.OK, getAllMobiles)
        } ~
          post {
            entity(as[MobileForm]) { mobileFormData =>
              complete(StatusCodes.OK, createMobile(mobileFormData))
            }
          }
      } ~
      path("api" / "user") {
        get {
          complete(StatusCodes.OK, getAllUsers)
        } ~
          post {
            entity(as[UserForm]) { user =>
              complete(StatusCodes.OK,
                createUser(user)
              )

            }
          }
      } ~
      path("api" / "user" / "mobile" / IntNumber) { userId =>
        get {
          complete(StatusCodes.OK, getUsersWithMobile(userId))
        }
      }

  val server: Future[Http.ServerBinding] = Http().newServerAt("localhost", 9000).bind(routes)
}