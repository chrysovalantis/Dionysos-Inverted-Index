package com.example.demo.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.form.DirectoryJSONResponse;
import com.example.demo.form.DirectoryResponse;
import com.example.demo.form.SearchResponse;
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

import com.example.demo.form.UploadFileResponse;
import com.example.demo.model.Collection;
import com.example.demo.model.PostingList;
import com.example.demo.service.FileStorageService;
import com.example.demo.utilities.CollectionManager;
import com.example.demo.utilities.QueryManager;

import exceptions.CollectionNotFoundException;

@RestController
@RequestMapping("/collections")
public class MainController {
	
	
	@Autowired
	private FileStorageService fileStorageService;
	
	private static CollectionManager collections = new CollectionManager();
	

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {
 
        return "index";
    }
    
    @RequestMapping(value = { "/print/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> print(@PathVariable String directory, @RequestParam(value= "json", required = false) Boolean jsonOrText) throws CollectionNotFoundException{
    	Boolean checkJson = false;
    	if(jsonOrText != null)
    		checkJson = jsonOrText;
    	if (!collections.getCollections().containsKey(directory)){
    		throw new CollectionNotFoundException();
    	}
    	Collection col = collections.getCollections().get(directory);

    	if(checkJson)
    		return new ResponseEntity<Object>(new DirectoryJSONResponse(true,col), HttpStatus.OK);
        return new ResponseEntity<Object>(col.toString(), HttpStatus.OK);
    }
    
    @RequestMapping(value = { "/printTerms/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> printTerms(@PathVariable String directory) throws CollectionNotFoundException{
    	if (!collections.getCollections().containsKey(directory)){
    		throw new CollectionNotFoundException();
    	}
    	Collection col = collections.getCollections().get(directory);
    	
        return new ResponseEntity<Object>(col.printTerms(), HttpStatus.OK);
    }
    
    @RequestMapping(value = { "/files/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> printFiles(@PathVariable String directory) throws CollectionNotFoundException {
    	
    	if (!collections.getCollections().containsKey(directory)){
    		throw new CollectionNotFoundException();
    	}
    	Collection col = collections.getCollections().get(directory);
    	
        return new ResponseEntity<>(col.getFiles(),HttpStatus.OK);
    }
    
    /**
     * Create a new collection
     * @return
     */
    @RequestMapping(value = { "/{name}" }, method = RequestMethod.POST)
    public ResponseEntity<Object> createNewCollection(@PathVariable String name) throws CollectionAlreadyExistsException {
    	String response = collections.addCollection(name);

		return new ResponseEntity<Object>(new DirectoryResponse(true, response),HttpStatus.OK);
    }
    
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
    
    @RequestMapping(value = { "/uploadMultipleFiles/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<List<UploadFileResponse>> uploadMultipleFiles(@PathVariable String directory, @RequestParam("files") MultipartFile[] files) throws FileStorageException{
		List<UploadFileResponse> list = new ArrayList<>();
		for (MultipartFile file : Arrays.asList(files)) {
			ResponseEntity<Object> uploadFileResponse = uploadFile(directory, file);
			list.add((UploadFileResponse) uploadFileResponse.getBody());
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @RequestMapping(value = { "/search/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<Object> search(@PathVariable String directory, @RequestParam("query") String query) throws QueryParserException, CollectionNotFoundException {
    	PostingList result;
    	if (!collections.getCollections().containsKey(directory)){
    		throw new CollectionNotFoundException();
    	}
    	Collection col = collections.getCollections().get(directory);
    	QueryManager qm = new QueryManager(query);
		long startTime = System.nanoTime();
    	result = qm.queryParser(col.getIndex());
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		double timeMS = timeElapsed/ 1000000;
    	return new ResponseEntity<>(new SearchResponse(timeMS,query,result),HttpStatus.OK);
    }
    
    @RequestMapping(value = { "/deleteDirectory/{directory}" }, method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteDirectory(@PathVariable String directory) throws CollectionNotFoundException{
        
    	Boolean ret = collections.deleteCollection(directory);
    	
    	if(ret == false) {
    		return new ResponseEntity<>(new DirectoryResponse(false,"Directory Cannot Be Deleted!"),HttpStatus.OK);
    	}
		return new ResponseEntity<>(new DirectoryResponse(true,"Directory Deleted!"),HttpStatus.OK);
    }
    
    @RequestMapping(value = { "/deleteFile/" }, method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteFile(@RequestParam String directory, @RequestParam String filename) {
        
    	Boolean ret = collections.deleteFile(directory, filename);
    	
    	if(ret == false) {
			return new ResponseEntity<>(new DirectoryResponse(false,"File Cannot Be Deleted!"),HttpStatus.OK);
    	}
		return new ResponseEntity<>(new DirectoryResponse(true,"File Deleted!"),HttpStatus.OK);
    }
    
    
    

}
