package com.example.demo.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;



public class Collection implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private String name;
	private InvertedIndex index;
	private int size;
	
	private HashMap<Integer, String> files;
	
	

	public Collection(String name) {
		this.name = name;
		this.size = 0;
		this.index = new InvertedIndex();
		this.files = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InvertedIndex getIndex() {
		return index;
	}

	public void setIndex(InvertedIndex index) {
		this.index = index;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public HashMap<Integer, String> getFiles() {
		return files;
	}

	public void setFiles(HashMap<Integer, String> files) {
		this.files = files;
	}
	
	public boolean addDocument(Path newPath,String filename) {

        try {
            BufferedReader bf = new BufferedReader(new FileReader(newPath.toFile()));
            String line = "";
            int id=-1;
            line=bf.readLine();
            String csv[] = line.trim().split(","); 
            if(csv.length<1) {
            	bf.close();
                return false;
            }
            try {
                id = Integer.parseInt(csv[0]);
            }catch(NumberFormatException ex){
                ex.printStackTrace();
            }
            
            String split[] = line.split("\"");
            if(split.length<=2) {
            	bf.close();
                return false;
            }

            String description = split[1];
            StringTokenizer tokenizer = new StringTokenizer(description);

            int position = 1;
            while(tokenizer.hasMoreTokens()){
                String word = tokenizer.nextToken().toLowerCase();
                // Skip if the word is a stop word
                List<String> stopwords = Files.readAllLines(Paths.get("english_stopwords.txt"));
                if(stopwords.contains(word)) {
                    position++;
                    continue;
                }
                //System.out.print(term+ ": ");
                index.addTerm(word,id,position);
                position++;
            }

            files.put(id,filename);
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(index);
        this.size++;
		return true;
		
	}
	
	public Boolean deleteFile(String filename) {
		boolean flag = false;
		for (Entry<Integer, String> entry : this.files.entrySet()) {
			Integer key = entry.getKey();
			String value = entry.getValue();
			if ( value.compareTo(filename) == 0) {
				this.files.remove(key);
				this.index.deleteFile(key);
				flag=true;
				break;
			}
		}
		
		if(!flag)
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		
		String ret ="Collection: "+ this.name + " with size: "+ this.size + "\n"+ this.index.toString();
		
		return ret;
	}
	
	
	

}
