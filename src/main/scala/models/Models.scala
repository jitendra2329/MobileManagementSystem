package models

import spray.json._

case class Mobile(id: Int, name: String, model: String, price: Double)

case class MobileForm(name: String, model: String, price: Double, userId: Int)

case class MobileUpdateForm(price: Double)

case class UserForm(userName: String, role: String)

case class User(userId: Int, userName: String, userRole: String)

case class UsersMobile(mobileId: Int, mobileName: String, mobileModel: String)

object Mobile extends DefaultJsonProtocol {
  implicit val mobileFormat: RootJsonFormat[Mobile] = jsonFormat4(Mobile.apply)
}

object User extends DefaultJsonProtocol {
  implicit val userFormat: RootJsonFormat[User] = jsonFormat3(User.apply)
}

object MobileForm extends DefaultJsonProtocol {
  implicit val mobileFormFormat: RootJsonFormat[MobileForm] = jsonFormat4(MobileForm.apply)
}

object UserForm extends DefaultJsonProtocol {
  implicit val userFormFormat: RootJsonFormat[UserForm] = jsonFormat2(UserForm.apply)
}

object UsersMobile extends DefaultJsonProtocol {
  implicit val userWithMobileFormat: RootJsonFormat[UsersMobile] = jsonFormat3(UsersMobile.apply)
}

object MobileUpdateForm extends DefaultJsonProtocol {
  implicit val mobileUpdateFormFormat: RootJsonFormat[MobileUpdateForm] = jsonFormat1(MobileUpdateForm.apply)
}