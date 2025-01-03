# Software Design - Group assignment

## Treasure Tracker
This is a group project for the course COMP.SE.110 Software Design.\
It searches cryptoprices from CoinGecko API and metal prices, such as Gold and Silver from Metals.dev API.\
The prices are displayed in the form of charts and a table.

## The API keys used to retrive the data might not be active!

### Environment requirements

- Java Development Kit (JDK) 17 or newer
- Maven (latest release recommended: 3.9.9)

### Running the application

1. Clone the project Git repository.
2. Move to the project root directory: `cd java3`.
3. Compile and start the application with: `mvn clean javafx:run`.

#### Running the unit tests

1. Ensure you are in the project root directory: `cd java3`.
2. Use Maven to run tests:
   - `mvn test` for all tests.
   - `mvn test -Dtest='TestClassName'` for a specific test class.
   - `mvn test -Dtest='TestClassName#TestMethodName'` for a specific test method.
3. Alternatively, use your IDE to run tests by right-clicking on test folders, classes or methods and selecting "Run Tests".

### Help with using the application

The top toolbar includes an Info menu, which contains a Help option.
Clicking Info → Help opens a dialog box with instructions on how to use the 
application and its features.

### Javadoc

You can generate the javadoc documents by running: `mvn javadoc:javadoc`.
The generated javadoc can be found at: target/reports/apidocs/index.html