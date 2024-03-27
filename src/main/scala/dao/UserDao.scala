package dao

import models.{User, UserForm, UsersMobile}

import scala.concurrent.Future

trait UserDao {

  def createNewUser(user: UserForm): Future[List[User]]

  def getAllUsers: Future[List[User]]

  def getUsersMobile(userId: Int): Future[List[UsersMobile]]

  def deleteUserById(userId: Int): Future[Option[String]]
}
