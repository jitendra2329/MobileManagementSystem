package flyway

import models.DbConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

import scala.util.Try

class Flyway(dbConfig: DbConfig) {

  private val flyway = Flyway.configure()
    .dataSource(dbConfig.url,dbConfig.user,dbConfig.pass)
    .baselineOnMigrate(true)
    .locations("db/migration")
    .load()

  def migrateDatabase(): MigrateResult = {
    Try(flyway.migrate()).getOrElse {
      flyway.repair()
      flyway.migrate()
    }
  }
}
