import controllers.{MobileRoutes, MobileRoutesImplementation, Routes}
import db.{Connection, Database}
import flyway.Flyway
import models.DbConfig
import services.{MobileService, MobileServiceImple}

import scala.annotation.unused
import scala.io.StdIn

object Main extends App {

  val dbConnection = new Connection
  val dbConfig = dbConnection.dbConfig

  private val flywayMigration = new Flyway(DbConfig(dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass))
  flywayMigration.migrateDatabase()

  private val db: Database = new Database(dbConnection)
  private val mobileService: MobileService = new MobileServiceImple(db)
  private val mobileRoutes: MobileRoutes = new MobileRoutesImplementation(mobileService)
  private val routes: Routes = new Routes(mobileRoutes)

  @unused
  private val server = routes.server

  println("Server is running on http://localhost:9000")
  StdIn.readLine()

}




