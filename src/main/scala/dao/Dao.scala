package dao

import models.{Mobile, MobileForm, User, UserForm, UsersMobile}

trait Dao {

  def createNewMobile(mobile: MobileForm): List[Mobile]

  def getMobiles: List[Mobile]

  def getMobileById(id: Int): List[Mobile]

  def deleteById(id: Int): Option[String]

  def deleteAll(): Option[String]

  def updateById(id: Int, newPriceToUpdate: Double): Option[String]

  def createNewUser(user: UserForm): List[User]

  def getAllUsers: List[User]

  def getUsersMobile(userId: Int): List[UsersMobile]
}
