# DiamanteInvestments

To preview the application I have included an executable .jar file called DiamanteInvestments that you may download and run.

Any files created by myself was done using Eclipse Oxygen and the Java programming language.
New users may create new profiles and begin adding stocks to their portfolios.

Note: you must know the stock symbol of the company you are searching for (i.e. TSLA, NFLX, AAPL).

The candle stick chart can be viewed in an external window and supports several different timeframes that are controlled using radio buttons.

There have been issues with AlphaVantage in getting some of the intraday timeframes so at this time if a plot is unavailable for a specific interval it will be blank.


## Running locally

Start up MySQL by running: `docker compose up -d`

The scripts in sql-scripts/ will create the necessary tables

## Viewing SQL database

Enter container: `winpty docker exec -it diamanteinvestments-db-1 bash`

Enter creds: `mysql -usa -ppassword`

Use database: `use stock_portfolio;`

Show tables: `SHOW TABLES;`

## AssertJ Swing

The unit tests use AssertJ Swing to interact with the GUI and assert behavior.

Reference: https://joel-costigliola.github.io/assertj/assertj-swing.html

### Swing Resources

https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html