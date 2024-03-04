package db

import dao.Dao
import models._
import parsers.Parser
import scalikejdbc.{DB, DBSession, SQL}

import scala.util.{Failure, Success, Try}

object Database extends Dao {

  implicit val session: DBSession = Connection.session

  override def createNewUser(user: UserForm): List[User] = {
    val result: List[User] = DB readOnly { implicit session =>
      SQL(BaseQuery.userInsertQuery(user.userName)).map(rs => Parser.parserUser(rs)).list()
    }
    result
  }

  override def createNewMobile(mobile: MobileForm): List[Mobile] = {
    val result: List[Mobile] = DB readOnly { implicit session =>
      SQL(BaseQuery.insertQuery(mobile)).map(rs => Parser.parseMobile(rs)).list()
    }
    result
  }

  override def getMobiles: List[Mobile] = {
    val result: List[Mobile] = DB readOnly { implicit session =>
      println("inside the fetchData method")
      val res = SQL(BaseQuery.selectAllQuery).map(rs => Parser.parseMobile(rs)).list()
      res
    }
    result
  }

  override def getMobileById(id: Int): List[Mobile] = {
    val result: List[Mobile] = DB readOnly { implicit session =>
      println("inside the fetchData method")
      val res = SQL(BaseQuery.selectByIdQuery(id)).map(rs => Parser.parseMobile(rs)).list()
      res
    }
    result
  }

  override def deleteById(id: Int): Option[String] = {
    Try(DB autoCommit { implicit session =>
      println("Inside delete by id method.")
      SQL(BaseQuery.deleteByIdQuery(id)).executeUpdate()
    }) match {
      case Failure(_) =>
        None
      case Success(value) => if (value != 0) Some(s"$value row Deleted!") else Some("No record exists!")
    }
  }

  override def deleteAll(): Option[String] = {
    Try(DB autoCommit { implicit session =>
      println("Inside deleteAll method.")
      SQL(BaseQuery.deleteAllQuery()).executeUpdate()
    }) match {
      case Failure(_) =>
        None
      case Success(_) => Some("All records Deleted!")
    }
  }

  override def updateById(id: Int, newPriceToUpdate: Double): Option[String] = {

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

  override def getAllUsers: List[User] = {
    val result: List[User] = DB readOnly { implicit session =>
      val res = SQL(BaseQuery.userSelectQuery()).map(rs => Parser.parserUser(rs)).list()
      res
    }
    result
  }

  override def getUserWithMobile(userId: Int): List[UserWithMobile] = {
    val result: List[UserWithMobile] = DB readOnly { implicit session =>
      val res = SQL(BaseQuery.userWithMobileSelectQuery(userId)).map(rs => UserWithMobile(Parser.parseMobile(rs))).list()
      res
    }
    result
  }


  private object BaseQuery {

    private type InsertQuery = String
    private type SelectQuery = String
    private type UpdateQuery = String
    private type DeleteQuery = String

    def userInsertQuery(userName: String): InsertQuery = {
      s"""INSERT INTO users(user_name)
         |VALUES (
         |'${userName}'
         |)
         |RETURNING *
         |""".stripMargin
    }

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

    def userSelectQuery(): SelectQuery = {
      """SELECT
        |user_id,
        |user_name
        |FROM users
        |""".stripMargin
    }

    def userWithMobileSelectQuery(userId: Int): SelectQuery = {
      s"""SELECT
         |m.mobile_id,
         |m.mobile_name,
         |m.mobile_model,
         |m.mobile_price
         |FROM mobiles m
         |JOIN users u
         |ON u.user_id = m.user_id
         |WHERE m.user_id = '$userId'
         |""".stripMargin
    }

    def deleteByIdQuery(id: Int): DeleteQuery = {
      s"""DELETE FROM mobiles WHERE mobile_id = '$id';
         |""".stripMargin
    }

    def deleteAllQuery(): DeleteQuery = {
      """TRUNCATE TABLE mobiles;"""
    }

    def updateByIdQuery(): UpdateQuery = {
      "UPDATE mobiles SET mobile_price = ? WHERE mobile_id = ?"
    }
  }
}
