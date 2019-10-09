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
import java.util.Set;


/** Represent a directory. The collection hold the name of it, the inverted index, the number of files inside,
 *  and the name of files.
 *
 * @author Chrysovalantis Christodoulou
 */
public class Collection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;                        //name of the directory
	private InvertedIndex index;                //the inverted index
	private int size;                           //the size of the directory
	
	private HashMap<Integer, String> files;     //the id and the name of the file inside the directory
	

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

    /** Read a document, extract the description and add the documents.
     *
     * @param newPath
     * @param filename
     * @return
     */
	public boolean addDocument(Path newPath,String filename) {

        try {
            BufferedReader bf = new BufferedReader(new FileReader(newPath.toFile()));
            String line = "";
            int id=-1;
            line=bf.readLine();
            String csv[] = line.trim().split(",");      //split the csv file
            if(csv.length<1) {
            	bf.close();
                return false;
            }
            try {
                id = Integer.parseInt(csv[0]);
            }catch(NumberFormatException ex){
                ex.printStackTrace();
            }
            
            String split[] = line.split("\"");          //split using " to get the description
            if(split.length<=2) {
            	bf.close();
                return false;
            }

            String description = split[1];
            StringTokenizer tokenizer = new StringTokenizer(description);       //tokenize the description

            int position = 1;
            while(tokenizer.hasMoreTokens()){
                String word = tokenizer.nextToken().toLowerCase();
                // Skip if the word is a stop word
                List<String> stopwords = Files.readAllLines(Paths.get("english_stopwords.txt"));    // take the list of the stopwords
                if(stopwords.contains(word)) {                                                           // ignore the stopwords but increase the position
                    position++;
                    continue;
                }
                //System.out.print(term+ ": ");
                index.addTerm(word,id,position);                                                         // add the word to the inverted index
                position++;
            }
			this.size++;
            files.put(id,filename);
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(index);

		return true;
		
	}

    /** Delete a file from the collection and update the inverted index.
     *
     * @param filename
     * @return
     */
	public Boolean deleteFile(String filename) {
		boolean flag = false;
		for (Entry<Integer, String> entry : this.files.entrySet()) {
			Integer key = entry.getKey();
			String value = entry.getValue();
			if ( value.compareTo(filename) == 0) {
				this.files.remove(key);
				this.index.deleteFile(key);
				flag=true;
				this.size--;
				break;
			}
		}
		
		if(!flag)
			return false;
		
		return true;
	}
	
	public Set<String> printTerms() {
		return this.index.getInvertedIndex().keySet();
	}

	@Override
	public String toString() {
		
		String ret ="Collection: "+ this.name + " with size: "+ this.size + "\n"+ this.index.toString();
		
		return ret;
	}
	
	
	

}
