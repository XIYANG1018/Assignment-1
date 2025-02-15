# CS6650-Assignment1

## Server
### WAR file to deploy
The war file's path is **skiers-server/out/artifacts/skiers_server_war/skiers-server_war.war**

![image](https://github.com/user-attachments/assets/0f6434db-e6a9-4cbe-b0c2-96eee1878ee9)

### Java version 
The AWS Tomcat should use **Java 17**.

### Where to change the url
The position where you change the url is inside the **SkierClient.java** file. Just change the server ip address.

![image](https://github.com/user-attachments/assets/22d95dae-c0b9-480d-866f-d3658400bb6d)

Server Design
1. It accepts the parameters for the operations as per the specification
2. It does basic parameter validation, and returns a 400 response code and error message if invalid values/formats supplied
3. If the request is valid, it returns a 200/201 response code and some dummy data as a response body
Notice: To run the server, the Java version installed should be 17. 
Client Design
Overview
The client uses a multi-threaded approach to generate and send HTTP requests, with built-in retry(5 times) logic, Little's Law throughput predictions, and performance monitoring.
Package Structure
All classes reside in the com.xiyang package, including three components: 
- SkierApp: the main application 
- SkierClient: HTTP client for sending lift ride events
- LifeRideEvent: Data model representing a single ski lift event
Major Classes and Relationships
LiftRideEvent
This is a data model class which represents a single lift ride event. It contains:
- Random generation of values like skierID, resortID, liftID, time (for seasonID and dayID, I use fixed values "2025" and "1")
- JSON serialization for API requests
- URL path generator 
SkierClient
It handles the HTTP communication with the server. The key features are:
- It uses Java's modern HttpClient with a connection pool
  - I configured a static HttpClient which can be thread-safely re-used in multithreaded environment. It saves the time cost for http handshaking when creating connection
- It implements 5 attempts retry logic with 100ms sleep time
SkierApp
This is the main application class. The key features are:
- It implements a helper function testAvgLatency() to test the average latency in a single thread request. Then the application uses it to calculate Little's Law predictions.
- In the multithread parts:
  - It uses producer-consumer design pattern:
    - a dedicated single thread used for generating lift ride events and 200 worker threads to send post
    - all events are stored in and takend from a BlockingDeque, which supports concurrent access from both producer and consumer threads, allowing the event generator to add events while the processing threads can safely remove them for handling
  - It uses ExecutorService to submit ???
  - It uses atomic counters for thread-safe statics collection
  - It uses CountDownLatch for synchronization of worker completion
  - It completes all threads cleanly
Workflow
The workflow is that the application initializes with configured number of threads (200) and total requests (200k), performs a latency test and prints out the Little's Law throughput predictions, a producer generates LiftRideEvents and adds them to a blocking deque, woker threads consume events from the queue and send them via SkierClient, each worker handles 1000 requests, and then succes or failures are tracked using atomic counters. After completion, the application calculates and reports throughput statistic metrics.
Client Part 1
Client (Part 1) - This should be a screenshot of your output window with your wall time and throughput. Also make sure you include the client configuration in terms of the number of threads used (print it out in your output window).
It seems that my internet is not good. The latency is very high for single thread. 
[Image]
Client Part 2
Client (Part 2) - run the client as per Part 1, showing the output window for each run with the specified performance statistics listed at the end.


Additional Screenshots 
Postman request
[Image]
[Image]
[Image]
