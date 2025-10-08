# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Sequence Diagram URL
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAGZT9qBACu2AMQALG4AHABMAJwgMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJxuYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1GEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCeN4fj+NA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfIs3y-P8gJjA2oGCkB-rgeWKlaSC6k7HsDZ6fCvrOl2MAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU7KVyN4+XeS7CmKEpujKcplu8SqYCqwYam6RocBAahoAA5MwVposFvKheJVBIjAPZ9tuXlFf6AByEApUqUYxnGhQ6UmlSiWmTgAIxZssOaqHm8zQUWJb1D40yXtASAAF4oLsMB0U2w4Om1HbFS6G7ulunmCp59RwPEKAgBqpkAjA8T6PECWqhqACSaAgNAKLgDATT6H8OwVSB1T+gA6t4DhvVsOyNSgsYKeh8LJsgqYwOmPWjH1-KDQWYwjdAY0TXqU2zfNi0MTt3kFfyY4Tigz7xOel7Xsti6VA+a4BtT202e2ln1M54oZKoAGYJZ30SWBhGGUNxnkZeVH1qpVytdDJRgDheFKURovfBRkvIdLjYMZgXi+AEXgoOgMRxIkhvG85vhYKJu0-aWDTSBG-ERu0EbdD0cmqApwzq4h6CQ7pjxwvUvtIXzQcYUVJX2fYVtU-BftoB5rNEwuo5GCg3DHpe8eUYnc4hSt9PCjImdMoYofGwAZt4MzGhLicaIl6owOKVAmkg66V4UtMCqte1M3qMCQEhpUQMwVe+E2hNVaWveg+D8ay+1MPYTAACQ8MEf1KPDcWGMKlj8Q43NDb0flacC+ttkU8PicZ1nc1fat7MCUJVvc7z-P93btQjDLdtMKrwVjAXC+FRj40YvrfwKJ1z+GwOKDU-E0QwAAOJKg0DbWe9QGioJdu7ewSofYNzDq1Sor9u7h1hJHaynZ6jIByLnDWydOyCl7n5MAFMmH5wvvue8JcIpdxIcbcU9cE5ISbjdMRedR7ZFbmgduyAGK9yviVMq-YZ60JqPUee0YwbNQDqtDqcNurb2RvmPeo1D4URPrsSBKif50OkRrZ+VJbwkzsmidBOYMQF2JnTIU9RBGVgQGgjBmgdDtnYWEuYyCci8MKlokq3iUBxKwJoh41DSwpLSVQr8V99LLEITmAsDRxjFJQLdaQBYuphDcIEEEmx4i6hQPVIy3xkigDVG01WIIKnVUgtLToADBZAPlorcBIwikYNKeUpUVSal1IacsJpLSenmRBJ0466zVJ9KVAM9pFxhnaygcxDgAB2CITgUBOBiBGNwcAuIADZ4BkxiYYIowDbaCxwa0DoBCiFHw1lmfpSoRmJnIRHUs3cQX7LBXk4CUcNqHjkCgFJGI4BkxSa5NQ7lXEyHcfSGAjJOE527n4y+YUgniiEeIkRaBnGN2uklRlsiBRtw7sowlfckW2XUc-bBBLC7qAXgY5e8BgGdQRlMsYO8LGFn3qWcaNioAzVPgtE5DjeUh2ES1FObjhXpxReidFoK5gUr4VSgMjMUl32JeEjJr9MVHjRUqT+CBAJQoKULCpCz6i1PqeCyOctYZgIIr66p-qlmQN1kxAIlhM72U2CbJACQwAJr7BAZNAApCAojbX+C2WqT58tvnaMaE0ZkMkegVOIXSxSoxsAIGAAmqAcAID2SgGsCNQarKZK-Dq+tWYm0tsoO2zt3b5nSGhF67VMAABWea0DotzVzFAhJcUsOvmw7lHCuHkoSUXQJaCmSxW1LqfUlDm4am7q3dlCjOWHsXHO-lGTQI6O5aKiG4rjGb1MaMWV5ihoKqscqyaqrcZnyWty1RG03j4uiSS01U6LWhWLtSiUFT5H2pzMylutq0oZWyjAXK8StVJI2q+lOgraopK-UvQBRjJUmMmYB3M8q0aKvqHodcKIN05Cg1yw1ZaSoVPxQPAAapy1FMAR2tpgI9TteGTrNtbeO6Ad7h7hj1awxx5b1NQDDEhejOnEwhrXumTMAG5XAc41Yk0ZoazacEwh3dCpsBaBNUqDEFS1hvDWHJsdHboCoaPcuSUHmZNiYFZCrJ9RV3Lrdf+D1CLg1aP0r2sZoalYQJObG6BXgW0prTYV+UiBgywGANgJthA8gFBgCW8wZbJKO2dq7d2xhDGxYHTAJi6IFPcDwL19dblUt9oHiAQbUAMVTZxSSVzhr6iTYq4GBAvin193Q6XR+MAa4QDrm6VQawUlHeNBKCmGsNCOqhftWbyXPVZO9S8TLTHxmgJy--E5QA