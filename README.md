**Run the following commands to run the application**
    
1. sudo docker compose up
2. sbt flywayClean
3. sbt flywayMigrate
4. sbt clean compile run

You may get some error realated to the post accupied for postgres (5432), then make sure to stop the postgres 
service on your local machin and the try to up the docker compose.
