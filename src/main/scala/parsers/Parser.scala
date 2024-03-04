package parsers

import models.{Mobile, User, UsersMobile}
import scalikejdbc.WrappedResultSet

object Parser {

  def parseMobile(rs: WrappedResultSet): Mobile = {
    Mobile(rs.int("mobile_id"), rs.string("mobile_name"), rs.string("mobile_model"), rs.double("mobile_price"))
  }

  def parseUser(rs: WrappedResultSet): User = {
    User(rs.int("user_id"), rs.string("user_name"))
  }

  def parseUsersMobile(rs: WrappedResultSet): UsersMobile = {
    UsersMobile(rs.int("mobile_id"), rs.string("mobile_name"), rs.string("mobile_model"))
  }
}
