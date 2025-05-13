# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DiamanteInvestments is a Java Swing application that allows users to track stock portfolios. Key features include:
- User authentication (login/new account creation)
- Stock symbol lookup
- Stock data visualization with candlestick charts
- Portfolio management

## Development Environment Setup

### Database Setup
```bash
# Start MySQL database
docker compose up -d

# For Unix/Linux/Mac:
docker exec -it diamanteinvestments-db-1 bash
mysql -usa -password
use stock_portfolio;
SHOW TABLES;
```

### Build Commands

```bash
# Build the project
mvn clean package

# Run tests
mvn test

# Run a specific test
mvn test -Dtest=LoginWindowTest
```

## Architecture

### Main Components

1. **UI Components**: 
   - `LoginWindow.java`: Handles user authentication
   - `NewAccountWindow.java`: User registration form
   - `StockForm.java`: Main interface for portfolio management
   - `UICreator.java`: Helper class for UI creation

2. **Data Access**:
   - `DBConnection.java`: Database connection manager
   - `LoginRepository.java`: Handles database operations for login/authentication

3. **Stock Data**:
   - `StockSymbol.java`: Fetches stock data from external sources
   - `CandleStick.java`: Data structure for stock price visualization
   - `StockChartData.java`: Handles chart creation and data formatting

### Testing

The application uses the following testing frameworks:
- JUnit 4 for general testing
- AssertJ Swing for UI component testing
- ApprovalTests for snapshot testing of UI components
- Mockito for mocking dependencies

## Notes

- The application requires MySQL database running in Docker
- Stock symbol lookup is case-sensitive (e.g., TSLA, NFLX, AAPL)
- There are known issues with AlphaVantage API for some intraday timeframes