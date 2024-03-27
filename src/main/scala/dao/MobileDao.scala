package dao

import models._

import scala.concurrent.Future

trait MobileDao {

  def createNewMobile(mobile: MobileForm): Future[List[Mobile]]

  def getMobiles: Future[List[Mobile]]

  def getMobileById(id: Int): Future[List[Mobile]]

  def deleteById(id: Int): Future[Option[String]]

  def deleteAll(): Future[Option[String]]

  def updateById(id: Int, newPriceToUpdate: Double): Future[Option[String]]
}
