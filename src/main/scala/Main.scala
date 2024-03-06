import controllers.{MobileRoutes, MobileRoutesImplementation, Routes}
import dao.{MobileDao, UserDao}
import db.Connection
import db.mobilesRepo.MobileDaoImpl
import db.usersRepo.UsersDaoImpl
import services.{MobileService, MobileServiceImple}

import scala.annotation.unused
import scala.io.StdIn

object Main extends App {

  val dbConnection = new Connection

  private val mobileDao: MobileDao = new MobileDaoImpl(dbConnection)
  private val userDao: UserDao = new UsersDaoImpl(dbConnection)
  private val mobileService: MobileService = new MobileServiceImple(mobileDao, userDao)
  private val mobileRoutes: MobileRoutes = new MobileRoutesImplementation(mobileService)
  private val routes: Routes = new Routes(mobileRoutes)

  @unused
  private val server = routes.server

  println("Server is running on http://localhost:9000")
  StdIn.readLine()

}




