package com.example.demo.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.form.*;
import exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.model.Collection;
import com.example.demo.model.PostingList;
import com.example.demo.service.FileStorageService;
import com.example.demo.utilities.CollectionManager;
import com.example.demo.utilities.QueryManager;

import exceptions.CollectionNotFoundException;

/** This class is the main controller of my project. Contains all the endpoints.
 *
 * @author Chrysovalantis Christodoulous
 */
@RestController
@RequestMapping("/collections")
public class MainController {

	
	@Autowired
	private FileStorageService fileStorageService;							 //Initialize file storage service
	
	private static CollectionManager collections = new CollectionManager();	 //Handles all the collections

	/** Just a Hello World test example.
	 *
	 * @param model
	 * @return
	 */
    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {
 
        return "index";
    }

	/** Print the inverted index of a given collection. You have the choice of JSON or string representation.
	 *
	 *
	 * @param directory
	 * @param jsonOrText
	 * @return
	 * @throws CollectionNotFoundException
	 */
	@RequestMapping(value = { "/print/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> print(@PathVariable String directory, @RequestParam(value= "json", required = false) Boolean jsonOrText) throws CollectionNotFoundException{
    	Boolean checkJson = false;
    	if(jsonOrText != null)
    		checkJson = jsonOrText;
    	if (!collections.getCollections().containsKey(directory)){				//check if the collection exists
    		throw new CollectionNotFoundException();
    	}
    	Collection col = collections.getCollections().get(directory);

    	if(checkJson)
    		return new ResponseEntity<Object>(new DirectoryJSONResponse(true,col), HttpStatus.OK);
        return new ResponseEntity<Object>(col.toString(), HttpStatus.OK);
    }

	/** Prints just the terms of the inverted index of the given collection.
	 *
	 * @param directory
	 * @return
	 * @throws CollectionNotFoundException
	 */
	@RequestMapping(value = { "/printTerms/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> printTerms(@PathVariable String directory) throws CollectionNotFoundException{
    	if (!collections.getCollections().containsKey(directory)){
    		throw new CollectionNotFoundException();
    	}
    	Collection col = collections.getCollections().get(directory);
    	
        return new ResponseEntity<Object>(col.printTerms(), HttpStatus.OK);
    }

	/** Print the files of the given collection.
	 *
	 * @param directory
	 * @return
	 * @throws CollectionNotFoundException
	 */
	@RequestMapping(value = { "/files/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> printFiles(@PathVariable String directory) throws CollectionNotFoundException {
    	
    	if (!collections.getCollections().containsKey(directory)){
    		throw new CollectionNotFoundException();
    	}
    	Collection col = collections.getCollections().get(directory);
    	
        return new ResponseEntity<>(col.getFiles(),HttpStatus.OK);
    }

	/** Create new collection.
	 *
	 * @param name
	 * @return
	 * @throws CollectionAlreadyExistsException
	 */
	@RequestMapping(value = { "/{name}" }, method = RequestMethod.POST)
    public ResponseEntity<Object> createNewCollection(@PathVariable String name) throws CollectionAlreadyExistsException {
    	String response = collections.addCollection(name);

		return new ResponseEntity<Object>(new DirectoryResponse(true, response),HttpStatus.OK);
    }

	/** Upload a file to a given directory. The files uploaded first to an uploaded folder inside the server and
	 *  then moved to a specific directory (C://Dionysos).
	 *
	 * @param directory
	 * @param file
	 * @return
	 * @throws FileStorageException
	 */
	@RequestMapping(value = { "/uploadfile/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@PathVariable String directory, @RequestParam("file") MultipartFile file) throws FileStorageException {
    	if (!collections.getCollections().containsKey(directory)){
    		collections.addCollection(directory);
    	}
        String fileName = null;
		fileName = fileStorageService.storeFile(file,directory,collections);

        return new ResponseEntity<Object>(new UploadFileResponse(fileName,
                file.getContentType(), file.getSize()), HttpStatus.OK);
    }

	/** Upload multiple files to a given directory.
	 *
	 * @param directory
	 * @param files
	 * @return
	 * @throws FileStorageException
	 */
	@RequestMapping(value = { "/uploadMultipleFiles/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<List<UploadFileResponse>> uploadMultipleFiles(@PathVariable String directory, @RequestParam("files") MultipartFile[] files) throws FileStorageException{
		List<UploadFileResponse> list = new ArrayList<>();
		for (MultipartFile file : Arrays.asList(files)) {
			ResponseEntity<Object> uploadFileResponse = uploadFile(directory, file);
			list.add((UploadFileResponse) uploadFileResponse.getBody());
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
    }

	/** Search inside a given directory. The user gives a query as a parameter and get the answer as a response.
	 *  Examples:
	 *  1. termA
	 *  2. termA AND termB
	 *  3. termA OR termB
	 *  4. NOT termA
	 *  5. termA AND termB AND termC OR NOT termD
	 *
	 *  NOTE: The operation must be in capital letters.
	 *
	 * @param directory
	 * @param query
	 * @return
	 * @throws QueryParserException
	 * @throws CollectionNotFoundException
	 */
    @RequestMapping(value = { "/search/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<Object> search(@PathVariable String directory, @RequestParam("query") String query, @RequestParam(value= "json", required = false) Boolean jsonOrText) throws QueryParserException, CollectionNotFoundException {
    	PostingList result;
    	if (!collections.getCollections().containsKey(directory)){
    		throw new CollectionNotFoundException();
    	}
		Boolean checkJson = true;
		if(jsonOrText != null)
			checkJson = jsonOrText;

    	Collection col = collections.getCollections().get(directory);
    	QueryManager qm = new QueryManager(query);
		long startTime = System.nanoTime();
    	result = qm.queryParser(col.getIndex());
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		double timeMS = timeElapsed/ 1000000;
		if (!checkJson)
			return new ResponseEntity<>(result.toString(),HttpStatus.OK);
    	return new ResponseEntity<>(new SearchJSONResponse(timeMS,query,result),HttpStatus.OK);
    }

	/** Delete a given collection. Everything inside the collection is deleted.
	 *
	 * @param directory
	 * @return
	 * @throws CollectionNotFoundException
	 */
	@RequestMapping(value = { "/deleteDirectory/{directory}" }, method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteDirectory(@PathVariable String directory) throws CollectionNotFoundException{
        
    	Boolean ret = collections.deleteCollection(directory);
    	
    	if(ret == false) {
    		return new ResponseEntity<>(new DirectoryResponse(false,"Directory Cannot Be Deleted!"),HttpStatus.OK);
    	}
		return new ResponseEntity<>(new DirectoryResponse(true,"Directory Deleted!"),HttpStatus.OK);
    }

	/** Delete a file inside a collection and update the inverted index of this collection.
	 *
	 * @param directory
	 * @param filename
	 * @return
	 */
	@RequestMapping(value = { "/deleteFile/" }, method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteFile(@RequestParam String directory, @RequestParam String filename) {
        
    	Boolean ret = collections.deleteFile(directory, filename);
    	
    	if(ret == false) {
			return new ResponseEntity<>(new DirectoryResponse(false,"File Cannot Be Deleted!"),HttpStatus.OK);
    	}
		return new ResponseEntity<>(new DirectoryResponse(true,"File Deleted!"),HttpStatus.OK);
    }
    
    
    

}
