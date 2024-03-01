package models

case class DbConfig(driver: String, url: String, user: String, pass: String)

case class Mobile(id: Int, name: String, model: String, price: Double)

case class MobileForm(name: String, model: String, price: Double)

case class MobileUpdateForm(price: Double)