package dao

import models.{User, UserForm, UsersMobile}

trait UserDao {

  def createNewUser(user: UserForm): List[User]

  def getAllUsers: List[User]

  def getUsersMobile(userId: Int): List[UsersMobile]

  def deleteUserById(userId: Int): Option[String]
}
