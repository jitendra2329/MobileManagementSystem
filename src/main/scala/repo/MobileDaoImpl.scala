package repo

import dao.MobileDao
import db.Connection
import models._
import parsers.Parser
import scalikejdbc.{DB, DBSession, SQL}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class MobileDaoImpl(dbConnection: Connection) extends MobileDao {

  implicit val session: DBSession = dbConnection.session

  override def createNewMobile(mobile: MobileForm): Future[List[Mobile]] = Future {
    val result: List[Mobile] = DB readOnly { implicit session =>
      SQL(BaseQuery.insertQuery(mobile)).map(rs => Parser.parseMobile(rs)).list()
    }
    result
  }

  override def getMobiles: Future[List[Mobile]] = Future {
    val result: List[Mobile] = DB readOnly { implicit session =>
      println("inside the fetchData method")
      val res = SQL(BaseQuery.selectAllQuery).map(rs => Parser.parseMobile(rs)).list()
      res
    }
    result
  }

  override def getMobileById(id: Int): Future[List[Mobile]] = Future {
    val result: List[Mobile] = DB readOnly { implicit session =>
      println("inside the fetchData method")
      val res = SQL(BaseQuery.selectByIdQuery(id)).map(rs => Parser.parseMobile(rs)).list()
      res
    }
    result
  }

  override def deleteById(id: Int): Future[Option[String]]= Future {
    Try(DB autoCommit { implicit session =>
      println("Inside delete by id method.")
      SQL(BaseQuery.deleteByIdQuery(id)).executeUpdate()
    }) match {
      case Failure(_) =>
        None
      case Success(value) => if (value != 0) Some(s"$value row Deleted!") else Some("No record exists!")
    }
  }

  override def deleteAll(): Future[Option[String]] =  Future {
    Try(DB autoCommit { implicit session =>
      println("Inside deleteAll method.")
      SQL(BaseQuery.deleteAllQuery()).executeUpdate()
    }) match {
      case Failure(_) =>
        None
      case Success(_) => Some("All records Deleted!")
    }
  }

  override def updateById(id: Int, newPriceToUpdate: Double): Future[Option[String]] = Future {

    Try {
      DB autoCommit { implicit session =>
        SQL(BaseQuery.updateByIdQuery())
          .bind(newPriceToUpdate, id)
          .executeUpdate()
      }
    } match {
      case Failure(_) =>
        None
      case Success(value) => Some(s"$value row Updated")
    }
  }

  private object BaseQuery {

    private type InsertQuery = String
    private type SelectQuery = String
    private type UpdateQuery = String
    private type DeleteQuery = String



    def insertQuery(mobileForm: MobileForm): InsertQuery = {
      s"""INSERT INTO mobiles(mobile_name, mobile_model, mobile_price, user_id)
         |VALUES (
         |'${mobileForm.name}',
         |'${mobileForm.model}',
         |'${mobileForm.price}',
         |'${mobileForm.userId}'
         |)
         |RETURNING *
         |""".stripMargin
    }

    def selectAllQuery: SelectQuery = {
      """SELECT
        |mobile_id,
        |mobile_name,
        |mobile_model,
        |mobile_price
        |FROM mobiles;
        |""".stripMargin
    }

    def selectByIdQuery(id: Int): SelectQuery = {
      s"""SELECT
         |mobile_id,
         |mobile_name,
         |mobile_model,
         |mobile_price
         |FROM mobiles
         |where mobile_id = '$id';
         |""".stripMargin
    }

    def deleteByIdQuery(id: Int): DeleteQuery = {
      s"""DELETE FROM mobiles WHERE mobile_id = '$id'
         |""".stripMargin
    }

    def deleteAllQuery(): DeleteQuery = {
      """TRUNCATE TABLE mobiles"""
    }

    def updateByIdQuery(): UpdateQuery = {
      "UPDATE mobiles SET mobile_price = ? WHERE mobile_id = ?"
    }
  }
}
