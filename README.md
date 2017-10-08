# scalable-web
A REST API which provides 2 http endpoints, both of which only accept JSON base64 encoded binary data,
and a third endpoint that returns the diff information in JSON format.

How to run on localhost:

This is a Spring Boot application, so to execute it follow these steps:

1) Clone the repository
2) Go to the newly created root directory of the project

If on a Unix-based OS:
3) Execute: ./gradlew build && java -jar build/libs/binary-diff-api-v1-0.1.0.jar

If on Windows:
3) Execute the gradlew.bat script and then: java -jar build/libs/binary-diff-api-v1-0.1.0.jar

4) Once the "Application started" message has been displayed you are ready to try the API.

---

How to use on localhost:

1) Send one of the Base64-encoded strings you want to compare as a JSON POST request with a 'data' property
to http://localhost:8080/v1/diff/<DIFF_ID>/left (where <DIFF_ID> is an integer value such as one).

Example:
{
  "data" : "UGxlYXNlIGhpcmUgbWUh"
}

2) Send the other Base64-encoded string you want to compare as a JSON POST request with a 'data' property
to http://localhost:8080/v1/diff/<DIFF_ID>/right (where <DIFF_ID> is THE SAME DIFF_ID sent to /left).

Example:
{
  "data" : "UGxlYXNlIEhpcmUgTWUh"
}

3) Send a GET request to http://localhost:8080/v1/diff/<DIFF_ID> to view the results.
