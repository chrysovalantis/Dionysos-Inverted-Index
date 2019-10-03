package com.example.demo.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.example.demo.model.Collection;



public class CollectionManager {
	
	private static final String root = "C:\\\\Dionysos";
	
	private HashMap<String, Collection> collections;
	
	public CollectionManager() {
		collections = new HashMap<>();
	}
	
	
	public String addCollection(String name) {
		
		if(collections.containsKey(name)) {
			return "Collection already exists!";
		}
		
		Path path = Paths.get(root,name);
        try {
            Files.createDirectories(path);
            this.collections.put(name, new Collection(name));
        } catch (IOException e) {
            //fail to create directory
            e.printStackTrace();
        }
        
		
		return "Collection: "+name+" created!";
	}
	
	public HashMap<String, Collection> getCollections() {
		return collections;
	}


	public void setCollections(HashMap<String, Collection> collections) {
		this.collections = collections;
	}


	public boolean addDocumentToCollection(String collectionName, String path, String fileName) {
		
		if(!collections.containsKey(collectionName)) {
			return false;
		}
		Collection collection = collections.get(collectionName);
		Path newPath = null;
		try {
			newPath = Files.move 
			        (Paths.get(path),  
			        Paths.get(root,collectionName,fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (FileAlreadyExistsException e ) {
			System.out.println("File already exists " +Paths.get(root,collectionName) + " Source: "+Paths.get(path));
			return false;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		collection.addDocument(newPath,collectionName);
		
		collections.put(collectionName, collection);
		
//		File fp = temp.toFile();
//		
//		try {
//			String content=Files.readAllLines(fp.toPath()).stream().collect(Collectors.joining("\n"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return true;
	}

}
