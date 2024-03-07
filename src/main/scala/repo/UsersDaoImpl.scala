package repo

import dao.UserDao
import db.Connection
import models.{User, UserForm, UsersMobile}
import parsers.Parser
import scalikejdbc.{DB, DBSession, SQL}

import scala.util.{Failure, Success, Try}

class UsersDaoImpl(dbConnection: Connection) extends UserDao {

  implicit val session: DBSession = dbConnection.session

  override def createNewUser(user: UserForm): List[User] = {
    val result: List[User] = DB readOnly { implicit session =>
      SQL(BaseQuery.insertQuery(user)).map(rs => Parser.parseUser(rs)).list()
    }
    result
  }


  override def getAllUsers: List[User] = {
    val result: List[User] = DB readOnly { implicit session =>
      val res = SQL(BaseQuery.selectQuery()).map(rs => Parser.parseUser(rs)).list()
      res
    }
    result
  }

  override def getUsersMobile(userId: Int): List[UsersMobile] = {
    val result: List[UsersMobile] = DB readOnly { implicit session =>
      val res = SQL(BaseQuery.usersMobileSelectQuery(userId)).map(rs => Parser.parseUsersMobile(rs)).list()
      res
    }
    result
  }

  override def deleteUserById(userId: Int): Option[String] = {
    Try(DB autoCommit { implicit session =>
      println("Inside delete by id method.")
      SQL(BaseQuery.deleteByIdQuery(userId)).executeUpdate()
    }) match {
      case Failure(_) =>
        None
      case Success(value) => if (value != 0) Some(s"$value row Deleted!") else Some("No record exists!")
    }
  }


  private object BaseQuery {

    private type InsertQuery = String
    private type SelectQuery = String
    private type UpdateQuery = String
    private type DeleteQuery = String

    def insertQuery(user: UserForm): InsertQuery = {
      s"""INSERT INTO users(user_name, role)
         |VALUES (
         |'${user.userName}',
         |'${user.role}'::user_roles
         |)
         |RETURNING *
         |""".stripMargin
    }

    def selectQuery(): SelectQuery = {
      """SELECT
        |user_id,
        |user_name,
        |role
        |FROM users
        |""".stripMargin
    }

    def usersMobileSelectQuery(userId: Int): SelectQuery = {
      s"""SELECT
         |m.mobile_id,
         |m.mobile_name,
         |m.mobile_model
         |FROM mobiles m
         |JOIN users u
         |ON u.user_id = m.user_id
         |WHERE m.user_id = '$userId'
         |""".stripMargin
    }

    def deleteByIdQuery(userId: Int): DeleteQuery = {
      s"""DELETE FROM users WHERE user_id = '$userId'
         |""".stripMargin
    }
  }
}