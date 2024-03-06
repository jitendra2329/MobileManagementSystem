package dao

import models.{Mobile, MobileForm, User, UserForm, UsersMobile}

trait UserDao {

  def createNewUser(user: UserForm): List[User]

  def getAllUsers: List[User]

  def getUsersMobile(userId: Int): List[UsersMobile]
}
