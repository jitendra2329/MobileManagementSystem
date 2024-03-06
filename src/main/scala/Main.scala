import controllers.{MobileRoutes, MobileRoutesImplementation, Routes}
import dao.{MobileDao, UserDao}
import db.Connection
import repo.{MobileDaoImpl, UsersDaoImpl}
import services.{MobileService, MobileServiceImple}

import scala.annotation.unused
import scala.io.StdIn

object Main extends App {

  val dbConnection = new Connection

   val mobileDao: MobileDao = new MobileDaoImpl(dbConnection)
   val userDao: UserDao = new UsersDaoImpl(dbConnection)
   val mobileService: MobileService = new MobileServiceImple(mobileDao, userDao)
   val mobileRoutes: MobileRoutes = new MobileRoutesImplementation(mobileService)
   val routes: Routes = new Routes(mobileRoutes)

  @unused
   val server = routes.server

  println("Server is running on http://localhost:9000")
  StdIn.readLine()

}




