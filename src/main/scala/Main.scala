import controllers.Routes
import db.Connection._
import flyway.Flyway
import models.DbConfig

import scala.io.StdIn

object Main extends App {
  private val flywayMigration = new Flyway(DbConfig(dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass))
  flywayMigration.migrateDatabase()

  val routes = new Routes
  val server = routes.server
  println("Server is running on http://localhost:9000")
  StdIn.readLine()

}




