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

}

private object MobileRoutesImplementation extends DefaultJsonProtocol {
  implicit val mobileFormat: RootJsonFormat[Mobile] = jsonFormat4(Mobile)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val userWithMobileFormat: RootJsonFormat[UsersMobile] = jsonFormat3(UsersMobile)
}

class MobileRoutesImplementation(mobileService: MobileService) extends MobileRoutes {

  implicit val defaultTimeout: Timeout = Timeout(2 seconds)

  import MobileRoutesImplementation._

  override def getMobile(id: Int): Future[HttpEntity.Strict] = {
    Future(
      HttpEntity(
        ContentTypes.`application/json`,
        mobileService.getMobileById(id).toJson.prettyPrint
      )
    )
  }

  override def getAllMobiles: Future[HttpEntity.Strict] = {
    Future(
      HttpEntity(
        ContentTypes.`application/json`,
        mobileService.getAllMobiles.toJson.prettyPrint
      )
    )
  }

  override def createMobile(mobileForm: MobileForm): Future[HttpEntity.Strict] = {
    val mobileList = mobileService.createMobile(mobileForm)

    Future(
      HttpEntity(
        ContentTypes.`text/plain(UTF-8)`,
        s"New mobile is added into the db with id: ${mobileList.head.id}"
      )
    )
  }

  override def createUser(userForm: UserForm): Future[HttpEntity.Strict] = {
    val userList = mobileService.createUser(userForm)

    Future(
      HttpEntity(
        ContentTypes.`text/plain(UTF-8)`,
        s"New mobile is added into the db with id: ${userList.head.userId}"
      )
    )

  }

  override def getAllUsers: Future[HttpEntity.Strict] = {
    Future(
      HttpEntity(
        ContentTypes.`application/json`,
        mobileService.getAllUsers.toJson.prettyPrint
      )
    )
  }

  override def getUsersMobile(userId: Int): Future[HttpEntity.Strict] = {
    Future(
      HttpEntity(
        ContentTypes.`application/json`,
        mobileService.getUsersMobile(userId).toJson.prettyPrint
      )
    )
  }

  override def deleteById(mobileId: Int): Future[HttpEntity.Strict] = {
    mobileService.deleteById(mobileId) match {
      case Some(value) =>
        Future(
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            value
          ))
      case None =>
        Future(
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            "No row is deleted!"
          ))
    }

  }

  override def updateById(mobileId: Int, priceToUpdate: Double): Future[HttpEntity.Strict] = {
    mobileService.updateById(mobileId, priceToUpdate) match {
      case Some(value) =>
        Future(
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            value
          ))
      case None =>
        Future(
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            "No row is updated!"
          ))
    }
  }
}

object Routes extends DefaultJsonProtocol {
  implicit val mobileFormFormat: RootJsonFormat[MobileForm] = jsonFormat4(MobileForm)
  implicit val mobileUpdateFormFormat: RootJsonFormat[MobileUpdateForm] = jsonFormat1(MobileUpdateForm)
  implicit val userFormFormat: RootJsonFormat[UserForm] = jsonFormat1(UserForm)
}

class Routes(mobileRoutes: MobileRoutes) {

  import Routes._
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
      path("api" / "user" / "mobile" / IntNumber) { userId =>
        get {
          complete(StatusCodes.OK, mobileRoutes.getUsersMobile(userId))
        }
      }

  val server: Future[Http.ServerBinding] = Http().newServerAt("localhost", 9000).bind(routes)
}