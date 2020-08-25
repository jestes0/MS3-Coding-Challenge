/*
 * File Name: mainMethod.java
 * Creator: Johnathan Estes
 * Date Created: August 21, 2020
 * Last Updated: August 24, 2020 12:06 P.M.
 * Purpose: Take in a CSV file and export data to its specified location.
 * 
 */

package ms3_Challenge;

//Import Java Elements
import java.io. * ;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

//Import OpenCSV Library
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

//Import SQLite Library
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class mainMethod {

  //declare static filename
  private static final String fileName = "data/ms3Interview.csv";

  //Declare static CSV File Write name
  private static final String csvwrite = "data/csvBad.csv";

  private static boolean validateData(String[] record) {

    //Check each cell for an empty record using parallel processing.
    for (String item: record) {

      //If a cell is empty, return false
      if (item.length() == 0) {
        return false;
      }
    }

    return true;
  }

  private static void createBadCSV() {

    try {
      //Initiate CSVWriter
      CSVWriter header = new CSVWriter(new FileWriter(csvwrite));
      //Declare String Header
      String[] headerString = (new String[] {
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J"
      });
      //Write the header string and close the file.
      header.writeNext(headerString);
      header.close();
    }
    catch(IOException i) {
      i.printStackTrace();
    }
  }

  public static void writeBadCSV(String[] badData) {

    try {

      //Initialize CSV Writer
      CSVWriter writer = new CSVWriter(new FileWriter(csvwrite, true));

      //Append a record to the bad CSV file
      writer.writeNext(badData);
      writer.close();

    }

    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void createSQLTable(Connection conn) throws SQLException {

    try {

      // Create SQL Table string
      final String sqlCreate = "CREATE TABLE IF NOT EXISTS users (" 
      + "	A text NOT NULL," 
      + "	B text NOT NULL," 
      + "	C text NOT NULL," 
      + "	D text NOT NULL," 
      + "	E text NOT NULL," 
      + "   F text NOT NULL," 
      + "	G numeric NOT NULL," 
      + "	H numeric NOT NULL," 
      + "	I numeric NOT NULL," 
      + "	J text NOT NULL" + ");";

      // Create Initiate the connection
      Statement statement = conn.createStatement();
      // Execute the SQL Statement
      statement.executeUpdate("DROP TABLE IF EXISTS users;");
      statement.execute(sqlCreate);
      statement.close();

    }
    catch(SQLException e) {
      System.out.println(e.getMessage());
    }

  }

  public static void insertIntoSQL(List <String[]> data) {
    try {

      //Initiate the uri location
      String url = "jdbc:sqlite:data/data.db";

      //Create the connection
      Connection conn = DriverManager.getConnection(url);

      PreparedStatement prepState = null;

      //Create the Table
      createSQLTable(conn);

      //Create the SQL String
      String insertSQL = "INSERT INTO users(A, B, C, D, E," + "F, G, H, I, J) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

      //Prepare the statements
      prepState = conn.prepareStatement(insertSQL);

      //Write each cell into the SQL Statement values
      for (String[] record: data) {
    	prepState.setString(1, record[0]);
    	prepState.setString(2, record[1]);
    	prepState.setString(3, record[2]);
        prepState.setString(4, record[3]);
        prepState.setString(5, record[4]);
        prepState.setString(6, record[5]);
        prepState.setString(7, record[6].substring(1));
        prepState.setString(8, record[7]);
        prepState.setString(9, record[8]);
        prepState.setString(10, record[9]);
        prepState.addBatch();
      }

      //Turn off AutoCommit to allow quick processing.
      conn.setAutoCommit(false);
      //Execute Batch processing
      prepState.executeBatch();
      //Commit items
      conn.commit();

      prepState.close();
      conn.close();
    }

    catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public static void createLog(int recieved, int good, int bad) {

    try {
      //Initiate the Printer Writer to output log information to log.txt
      PrintWriter logStats = new PrintWriter("logData.log");

      //Output log information to file.
      logStats.println("Total Recieved Records: " + recieved);
      logStats.println("Sucessful Records: " + good);
      logStats.println("Failed Records: " + bad);
      logStats.close();
    }
    catch(FileNotFoundException f) {

}
  }

  public static void main(String[] args) {

    //Timer to track execution time.
    long startTime = System.nanoTime();

    //Counters to 
    int recieved = 0,
    successful = 0,
    unsucessful = 0;

    //Declare String to hold a record row
    String[] nextRecord;
    //Create a list to store good data
    List <String[]> data = new ArrayList < String[] > ();

    //Attempt to Read through the data
    try {

      //Declare FileReader using UTF8 CharSet
      FileReader reader = new FileReader(fileName, StandardCharsets.UTF_8);
      //Initiate the reader to skip the first line, ensuring the header is skipped.
      CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();

      //Create bad CSV file
      createBadCSV();

      //Iterate through CSV data file.
      while ((nextRecord = csvReader.readNext()) != null) {
        recieved++;
        //Check to see if cells have any missing data.
        if (validateData(nextRecord)) {
          //Add record to List if all columns exist.
          data.add((nextRecord));
          successful++;
        }

        else {
          //Process using OpenCSV if data is missing
          writeBadCSV(nextRecord);
          unsucessful++;

        }
      }

    }

    catch(Exception e) {
      e.printStackTrace();
    }

    //Insert the valid CSV data into a SQLite Database
    insertIntoSQL(data);

    //Create a log file 
    createLog(recieved, successful, unsucessful);

    //End time elapsed
    long endTime = System.nanoTime();

    //Get time elapses from start and end times
    double timeElapsed = endTime - startTime;

    System.out.println("Data has been sucessfully entered. Database has been stored in (data/data.db).");
    System.out.println("Unsucessful entries have been stored in (data/csvBad.csv).\n");

    System.out.println("Log file has been created (log.txt).\n");

    System.out.println("Program Execution time: " + timeElapsed / 1000000 + " ms");

  }

}