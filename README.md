# Dionysos-Inverted-Index
This API refers to an exercise for the CS660 course. The API gives the ability to the user to create directories, add files to them and generate an inverted index for each directory. It allows you to send some queries and get responses and delete the files and directories you create. The inverted index updated after any change.


##  Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

1. You should download and install [STS buddle](https://spring.io/tools/sts/all) or [InteliJ](https://www.jetbrains.com/idea/)
### Installing

A step by step series of examples that tell you have to get a development env running

1. Clone this repository to your local machine

```
git clone https://github.com/Chrysovalantis/Dionysos-Inverted-Index
```

2. Open STS or InteliJ and then open the project

```
File -> Open Project from File System -> Directory
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Running
* Run Spring Boot App
* Test the app using [My Postman Collection](https://www.getpostman.com/collections/35f4a6289701ac320b10)

## End Points

|Endpoint                               |Description                        |
|---------------------------------------|-----------------------------------|
|**GET**  /collections/print/{collection}| Print the inverted index of the collection **(Parameter JSON Needed)** |
|**GET**  /collections/printTerms{collection} | Print the terms of the inverted index  |
|**GET**  /collections/files{collection}     | Print all the files in a collection |
|**POST** /collections/{collection}     | Create a new collection  |
|**POST** /collections/index                  | Retrieve the index of the  *_collection_*|
|**POST**  /collections/uploadfile/{collection} | Upload a file to the collection|
|**POST** /collections/uploadMultipleFiles/{collection}| Upload multiple files to the collection  |
|**POST** /collections/search/| Search through the collection **(Parameter query Needed)**|
|**DELETE** /collections/deleteDirectory/{collection}   | Delete the collection |
|**DELETE** /collections/deleteFile    | Delete a file in the collection **(Parameters directory, filename Needed)** |

## Query Language:
```bash
QUERY := QUERY OPER TERM | TERM  
TERM := STR | NEG STR
STR := [A-Za-z0-9]+
OPER := "AND"  | "OR" | NEG
NEG  := "NOT"
