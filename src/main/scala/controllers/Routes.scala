package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import models._
import services.MobileService
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.{existentials, postfixOps}

trait MobileRoutes {
  def getMobile(id: Int): Future[HttpEntity.Strict]

  def getAllMobiles: Future[HttpEntity.Strict]

  def createMobile(mobileForm: MobileForm): Future[HttpEntity.Strict]

  def createUser(usrForm: UserForm): Future[HttpEntity.Strict]

  def getAllUsers: Future[HttpEntity.Strict]

  def getUsersMobile(userId: Int): Future[HttpEntity.Strict]

  def deleteById(mobileId: Int): Future[HttpEntity.Strict]

  def updateById(mobileId: Int, priceToUpdate: Double): Future[HttpEntity.Strict]

  def deleteUserById(userId: Int): Future[HttpEntity.Strict]

}

class MobileRoutesImplementation(mobileService: MobileService) extends MobileRoutes {

  implicit val defaultTimeout: Timeout = Timeout(2 seconds)

  override def getMobile(id: Int): Future[HttpEntity.Strict] = {
    for {
      mob <- mobileService.getMobileById(id)
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        mob.toJson.prettyPrint
      )
    }
  }

  override def getAllMobiles: Future[HttpEntity.Strict] = {
    for {
      mob <- mobileService.getAllMobiles
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        mob.toJson.prettyPrint

      )
    }
  }

  override def createMobile(mobileForm: MobileForm): Future[HttpEntity.Strict] = {
    val mobileList = mobileService.createMobile(mobileForm)

    for {
      mob <- mobileList
    } yield {
      HttpEntity(
        ContentTypes.`text/plain(UTF-8)`,
        s"New mobile is added into the db with id: ${mob.head.id}"
      )
    }
  }

  override def createUser(userForm: UserForm): Future[HttpEntity.Strict] = {
    val userList = mobileService.createUser(userForm)
    for {
      user <- userList
    } yield {
      HttpEntity(
        ContentTypes.`text/plain(UTF-8)`,
        s"New mobile is added into the db with id: ${user.head.userId}"
      )
    }
  }

  override def getAllUsers: Future[HttpEntity.Strict] = {
    for {
      user <- mobileService.getAllUsers
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        user.toJson.prettyPrint
      )
    }
  }

  override def getUsersMobile(userId: Int): Future[HttpEntity.Strict] = {
    for {
      user <- mobileService.getUsersMobile(userId)
    } yield {
      HttpEntity(
        ContentTypes.`application/json`,
        user.toJson.prettyPrint
      )
    }
  }

  override def deleteById(mobileId: Int): Future[HttpEntity.Strict] = {
    for {
      mob <- mobileService.deleteById(mobileId)
    } yield {
      mob match {
        case Some(value) =>
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            value
          )
        case None =>
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            "No row is deleted!"
          )
      }
    }
  }

  override def updateById(mobileId: Int, priceToUpdate: Double): Future[HttpEntity.Strict] = {
    for {
      mob <- mobileService.updateById(mobileId, priceToUpdate)
    } yield {
      mob match {
        case Some(value) =>
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            value
          )
        case None =>
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            "No row is updated!"
          )
      }
    }
  }

  def deleteUserById(userId: Int): Future[HttpEntity.Strict] = {
    for {
      user <- mobileService.deleteUserById(userId)
    } yield {
      user match {
        case Some(value) =>
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            value
          )
        case None =>
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            "No row is deleted!"
          )
      }
    }
  }
}

class Routes(mobileRoutes: MobileRoutes) {

  implicit val actorSystem: ActorSystem = ActorSystem("system")


  private val routes: Route =
    path("api" / "mobile" / IntNumber) { (id: Int) =>
      get {
        complete(
          StatusCodes.OK,
          mobileRoutes.getMobile(id)
        )
      } ~
        delete {
          complete(StatusCodes.OK, mobileRoutes.deleteById(id))
        } ~
        put {
          entity(as[MobileUpdateForm]) { data =>
            complete(StatusCodes.OK, mobileRoutes.updateById(id, data.price))
          }
        }
    } ~
      path("api" / "mobile") {
        get {
          complete(StatusCodes.OK, mobileRoutes.getAllMobiles)
        } ~
          post {
            entity(as[MobileForm]) { mobileFormData =>
              complete(StatusCodes.OK, mobileRoutes.createMobile(mobileFormData))
            }
          }
      } ~
      path("api" / "user") {
        get {
          complete(StatusCodes.OK, mobileRoutes.getAllUsers)
        } ~
          post {
            entity(as[UserForm]) { user =>
              complete(StatusCodes.OK,
                mobileRoutes.createUser(user)
              )
            }
          }
      } ~
      path("api" / "user" / IntNumber) { userId =>
        delete {
          complete(StatusCodes.OK, mobileRoutes.deleteUserById(userId))
        }
      } ~
      path("api" / "user" / "mobile" / IntNumber) { userId =>
        get {
          complete(StatusCodes.OK, mobileRoutes.getUsersMobile(userId))
        }
      }

  val server: Future[Http.ServerBinding] = Http().newServerAt("localhost", 9000).bind(routes)
}