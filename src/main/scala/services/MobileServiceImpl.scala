package services

import dao.{MobileDao, UserDao}
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

class MobileServiceImple(mobileDao: MobileDao, userDao: UserDao) extends MobileService {

  override def getAllUsers: List[User] = userDao.getAllUsers

  override def getUsersMobile(userId: Int): List[UsersMobile] = userDao.getUsersMobile(userId)

  override def createUser(userForm: UserForm): List[User] = userDao.createNewUser(userForm)

  override def createMobile(mobile: MobileForm): List[Mobile] = mobileDao.createNewMobile(mobile)

  override def getAllMobiles: List[Mobile] = mobileDao.getMobiles

  override def getMobileById(id: Int): List[Mobile] = mobileDao.getMobileById(id)

  override def deleteById(id: Int): Option[String] = mobileDao.deleteById(id)

  override def updateById(id: Int, newPriceToUpdate: Double): Option[String] = mobileDao.updateById(id, newPriceToUpdate)
}