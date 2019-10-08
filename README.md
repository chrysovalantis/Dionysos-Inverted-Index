# Dionysos-Inverted-Index

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
