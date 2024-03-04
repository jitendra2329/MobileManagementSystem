package services

import dao.Dao
import db.Database
import models._

trait MobileService {
  def getAllUsers: List[User]
  def getUsersMobile(userId: Int): List[UsersMobile]

  def createUser(userForm: UserForm): List[User]
  def createMobile(mobile: MobileForm): List[Mobile]
  def getAllMobiles: List[Mobile]
  def getMobileById(id: Int): List[Mobile]
  def deleteById(id: Int): Option[String]
  def updateById(id: Int, newPriceToUpdate: Double): Option[String]
}
class MobileServiceImple extends MobileService {

  private val database: Dao = new Database
  override def getAllUsers: List[User] = database.getAllUsers

  override def getUsersMobile(userId: Int): List[UsersMobile] = database.getUsersMobile(userId)

  override def createUser(userForm: UserForm): List[User] = database.createNewUser(userForm)

  override def createMobile(mobile: MobileForm): List[Mobile] = database.createNewMobile(mobile)

  override def getAllMobiles: List[Mobile] = database.getMobiles

  override def getMobileById(id: Int): List[Mobile] = database.getMobileById(id)

  override def deleteById(id: Int): Option[String] = database.deleteById(id)

  override def updateById(id: Int, newPriceToUpdate: Double): Option[String] = database.updateById(id, newPriceToUpdate)
}