package services

import dao.{MobileDao, UserDao}
import models._

import scala.concurrent.Future

trait MobileService {
  def getAllUsers: Future[List[User]]

  def getUsersMobile(userId: Int): Future[List[UsersMobile]]

  def createUser(userForm: UserForm): Future[List[User]]

  def createMobile(mobile: MobileForm): Future[List[Mobile]]

  def getAllMobiles: Future[List[Mobile]]

  def getMobileById(id: Int): Future[List[Mobile]]

  def deleteById(id: Int): Future[Option[String]]

  def deleteAll(): Future[Option[String]]

  def updateById(id: Int, newPriceToUpdate: Double): Future[Option[String]]

  def deleteUserById(userId: Int): Future[Option[String]]
}

class MobileServiceImple(mobileDao: MobileDao, userDao: UserDao) extends MobileService {

  override def getAllUsers: Future[List[User]] = userDao.getAllUsers

  override def getUsersMobile(userId: Int): Future[List[UsersMobile]] = userDao.getUsersMobile(userId)

  override def createUser(userForm: UserForm): Future[List[User]] = userDao.createNewUser(userForm)

  override def createMobile(mobile: MobileForm): Future[List[Mobile]] = mobileDao.createNewMobile(mobile)

  override def getAllMobiles: Future[List[Mobile]] = mobileDao.getMobiles

  override def getMobileById(id: Int): Future[List[Mobile]] = mobileDao.getMobileById(id)

  override def deleteById(id: Int): Future[Option[String]] = mobileDao.deleteById(id)

  override def deleteAll(): Future[Option[String]] = mobileDao.deleteAll()

  override def updateById(id: Int, newPriceToUpdate: Double): Future[Option[String]] = mobileDao.updateById(id, newPriceToUpdate)

  override def deleteUserById(userId: Int): Future[Option[String]] = userDao.deleteUserById(userId)
}