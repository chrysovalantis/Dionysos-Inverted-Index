package com.example.demo.controller;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import exceptions.FileStorageException;
import exceptions.QueryParserException;

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
    public String index(@PathVariable String directory) {
    	
    	if (!collections.getCollections().containsKey(directory)){
    		return "Collection Not Found";
    	}
    	Collection col = collections.getCollections().get(directory);
    	
        return col.toString();
    }
    
    @RequestMapping(value = { "/files/{directory}" }, method = RequestMethod.GET)
    public HashMap<Integer, String> printFiles(@PathVariable String directory) {
    	
    	if (!collections.getCollections().containsKey(directory)){
    		HashMap<Integer, String> resp = new HashMap<Integer, String>();
    		resp.put(-1, "Directory Not Found");
    		return resp;
    	}
    	Collection col = collections.getCollections().get(directory);
    	
        return col.getFiles();
    }
    
    /**
     * Create a new collection
     * @param collection
     * @return
     */
    @RequestMapping(value = { "/{name}" }, method = RequestMethod.POST)
    String createNewCollection(@PathVariable String name) {
    	String response = collections.addCollection(name);
		return response;
    }
    
    @RequestMapping(value = { "/uploadfile/{directory}" }, method = RequestMethod.POST)
    public UploadFileResponse uploadFile(@PathVariable String directory, @RequestParam("file") MultipartFile file) {
    	if (!collections.getCollections().containsKey(directory)){
    		collections.addCollection(directory);
    	}
        String fileName = null;
		try {
			fileName = fileStorageService.storeFile(file,directory,collections);
		} catch (FileStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/"+directory+"/")
                .path(fileName)
                .toUriString();
        
        
        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }
    
    @RequestMapping(value = { "/uploadMultipleFiles/{directory}" }, method = RequestMethod.POST)
    public List<UploadFileResponse> uploadMultipleFiles(@PathVariable String directory, @RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(directory, file))
                .collect(Collectors.toList());
    }
    
    @RequestMapping(value = { "/search/{directory}" }, method = RequestMethod.POST)
    public String search(@PathVariable String directory, @RequestParam("query") String query) throws QueryParserException {
    	PostingList result = new PostingList();
    	if (!collections.getCollections().containsKey(directory)){
    		HashMap<Integer, String> resp = new HashMap<Integer, String>();
    		resp.put(-1, "Directory Not Found");
    		return result.toString();
    	}
    	Collection col = collections.getCollections().get(directory);
    	QueryManager qm = new QueryManager(query);
    	result = qm.queryParser(col.getIndex());
    	
    	return result.toString();
    	
    }
    
    @RequestMapping(value = { "/deleteDirectory/{directory}" }, method = RequestMethod.DELETE)
    public String deleteDirectory(@PathVariable String directory) {
        
    	Boolean ret = collections.deleteCollection(directory);
    	
    	if(ret == false) {
    		return "Directory Cannot Be Deleted";
    	}
    	return "Directory Deleted!";
    }
    
    @RequestMapping(value = { "/deleteFile/" }, method = RequestMethod.DELETE)
    public String deleteDirectory(@RequestParam String directory, @RequestParam String filename) {
        
    	Boolean ret = collections.deleteFile(directory, filename);
    	
    	if(ret == false) {
    		return "File Cannot Be Deleted";
    	}
    	return "File Deleted!";
    }
    
    
    

}
