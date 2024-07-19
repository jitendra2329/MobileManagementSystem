**Run the following commands to run the application**
    1. sudo docker compose up
    2. sbt flywayClean
    3. sbt flywayMigrate
    4. sbt clean compile run