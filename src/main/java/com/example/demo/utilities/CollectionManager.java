package com.example.demo.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import com.example.demo.model.Collection;
import exceptions.CollectionAlreadyExistsException;
import exceptions.CollectionNotFoundException;

/** This class manages all the collection of the system.
 *
 */
public class CollectionManager implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private static final String root = "C:\\\\Dionysos";
	private static final String filepath = "C:\\Dionysos\\collections";
	
	private HashMap<String, Collection> collections;
	
	public CollectionManager() {
		collections = new HashMap<>();
		this.ReadObjectFromFile();
	}

    /** Read the serializable collection from the disk if exists.
     *
     */
	public void ReadObjectFromFile() {
		 
        try {
 
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
 
            int count = objectIn.readInt();
            for(int i=0;i<count;i++){
                String key = objectIn.readUTF();
                Collection collection = (Collection) objectIn.readObject();
                this.getCollections().put(key,collection);
            }
 
            System.out.println("Collections Loaded!");
            objectIn.close();
 
        } catch (Exception ex) {
        	System.out.println("No available collections! New one created.");
            //ex.printStackTrace();
        }
    }

    /** Save the collection to a specific location C:/Dionysos/collections.
     *
     * @return
     */
	private Boolean saveCollections() {
		
        try {
 
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath));
            out.writeInt(collections.size());
            collections.entrySet().stream().forEachOrdered(entry->{
                String key = entry.getKey();
                Collection collection = entry.getValue();
                try {
                    out.writeUTF(key);
                    out.writeObject(collection);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;
		
	}

    /** Add a new collection
     *
     * @param name
     * @return
     * @throws CollectionNotFoundException
     */
	public String addCollection(String name) throws CollectionNotFoundException {
		
		if(collections.containsKey(name)) {
			throw new CollectionAlreadyExistsException();
		}
		
		Path path = Paths.get(root,name);
        try {
            Files.createDirectories(path);
            this.collections.put(name, new Collection(name));
        } catch (IOException e) {
            //fail to create directory
            e.printStackTrace();
        }
        
        saveCollections();
		return "Collection: "+name+" created!";
	}
	
	public HashMap<String, Collection> getCollections() {
		return collections;
	}


	public void setCollections(HashMap<String, Collection> collections) {
		this.collections = collections;
	}

    /** Add documents to specific collection
     *
     * @param collectionName
     * @param path
     * @param fileName
     * @return
     */
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
		
		collection.addDocument(newPath,fileName);
		
		collections.put(collectionName, collection);
		saveCollections();
		
		return true;
	}

    /** Delete a file or folder from the disk
     *
     * @param file
     * @throws IOException
     */
	private static void delete(File file) throws IOException{
	 
		if (file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
				System.out.println("Directory is deleted : " + file.getAbsolutePath());
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					System.out.println("Directory is deleted : " + file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}

    /** Delete the collection from the disk and remove it from the collections list
     *
     * @param collectionName
     * @return
     * @throws CollectionNotFoundException
     */
	public boolean deleteCollection(String collectionName) throws CollectionNotFoundException {
		
		if(!collections.containsKey(collectionName)) {
			throw new CollectionNotFoundException();
		}
		
		collections.remove(collectionName);
		saveCollections();
		File directory = new File(Paths.get(root, collectionName).toString());
		if (!directory.exists()) {
			throw new CollectionNotFoundException();
		} else {
			try {
				delete(directory);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

    /** Delete a file from the disk and remove it from collection
     *
     * @param collectionName
     * @param filename
     * @return
     */
	public boolean deleteFile(String collectionName, String filename) {
		
		if(!collections.containsKey(collectionName)) {
			return false;
		}
		Collection collection = collections.get(collectionName);
		
		boolean ret = false;
		File file = new File(Paths.get(root, collectionName,filename).toString());
		if (!file.exists()) {
			System.out.println("File does not exist.");
			return false;
		} else {
			try {
				ret = collection.deleteFile(filename);
				saveCollections();
				delete(file);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return ret;
	}

}
