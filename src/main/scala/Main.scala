import actors.Actors.actorSystem
import controllers.Routes
import flyway.Flyway
import models.DbConfig
import db.Connection._

import scala.io.StdIn

object Main extends App {

  private val flywayMigration = new Flyway(DbConfig(dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass))


  flywayMigration.migrateDatabase()

  private val server = Routes.server
  println("Server is running on http://localhost:9000")
  StdIn.readLine()

  import actors.Actors.actorSystem.dispatcher

  server
    .flatMap(_.unbind())
    .onComplete(_ => actorSystem.terminate())

}
