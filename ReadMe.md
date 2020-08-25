# MS3 Coding Challenge

This is a coding challenge from MS3.

## Requirements:

1. Create a Java Application to Read and PArse a CSV formatted file
2. Verify Records to ensure they are complete, ensuring nothing is empty.
3. Insert valid data into a SQLite Database.
4. If the Data is bad, Write the data into a separate CSV file.
5. Write a log file that outputs the amount of records recieved, the number of successful records, and the number of        unsucessful records.

## Instructions to Run Program

1. Download the Github folder zip from the repository.
2. Extract the zip folder and open the contents.

From there you have two options of running the program:

### Using The Jar File:

1. Locate The MS3Challenge Executable within the folder.
2. Run the Executable, and the program will run automatically.

### Using Eclipse

1. Open Eclipse and add the folder directory to the list.
2. Ensure all libraries have been carried over sucessfully.
3. Compile and Test Using Eclipse

## Design Choices

I thought of a few options when I first read through the problem. My intial solution involed using two lists to sort through good and bad data before writing them to their respective locations. The problem I realized with it is that a lot of memory would be required involving huge amounts of data. So, thinking of that, I decided to implement a line-by-line processor and use a list only for the good data, which comes in handy on performing batch SQL statements.

For Reading and writing the files, I used an Open Source Library called OpenCSV. I chose it over hard-coded Java methods, such as scanner, to improve readability of the content and to ensure that I did not use loops that would cause an O(n^2) algorithm. For reading in the SQlite data, I used JDBC and the SQLite jar files for functionality and to implement SQL queries.

I wanted to process as much data as I could within a short amount of time to ensure the program could scale to huge data sets. By utilizing the list of good data as well as the batch commands in Sqlite, I was able to quickly process data in a short amount of time while reducing the amount of memory required by ommitting the bad data items. Upon testing, the algorithm's worst case complexity is O(n), and the execution time for 6002 lines of code came to an average of 670 milliseconds, which is .67 seconds.

## Assumptions

1. The File directories where the CVS files and output are static. These can be changed to different directories within the code.
2. A header will always exist within a CVS file.
3. Data does not have to be modified before being palced in the database file.
