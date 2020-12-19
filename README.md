Spring Boot Java 8+ HSQL Maven
=========================
This is a Java 8+ / Maven / HSQL / Spring Boot application that is used to find long running log events.

***

Requirements
------------
* [Java Platform (JDK) 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

Quick start
-----------
1. Clone this project
2. Run in console 
4. Check `eventdb` and `eventdb.log` to verify results are as expected

Testing Instructions
--------------------

The program should:
1. Take the input file path as input argument. Use following example as test file. Example:
```
{"id":"scsmbstgra", "state":"STARTED", "type":"APPLICATION_LOG",
"host":"12345", "timestamp":1491377495212}
{"id":"scsmbstgrb", "state":"STARTED", "timestamp":1491377495213}
{"id":"scsmbstgrc", "state":"FINISHED", "timestamp":1491377495218}
{"id":"scsmbstgra", "state":"FINISHED", "type":"APPLICATION_LOG",
"host":"12345", "timestamp":1491377495217}
{"id":"scsmbstgrc", "state":"STARTED", "timestamp":1491377495210}
{"id":"scsmbstgrb", "state":"FINISHED", "timestamp":1491377495216}
```

2. Flag any long events that take longer than 4ms with a column in the database called "alert"
3. Write found event details to file-based HSQLDB `eventdb` in the working folder
4. The application should create a new table if necessary and enter the following values:
    a. Event id
    b. Event duration
    c. Type and Host if applicable
    d. "alert" set to True if applicable

In the example above, the event scsmbstgrb duration is 1401377495216 - 1491377495213 = 3ms
The longest event is scsmbstgrc (1491377495218 - 1491377495210 = 8ms)

