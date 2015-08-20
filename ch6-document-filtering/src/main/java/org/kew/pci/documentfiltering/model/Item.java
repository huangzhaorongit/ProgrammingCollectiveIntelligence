package org.kew.pci.documentfiltering.model;

import java.util.ArrayList;
import java.util.List;

public class Item {

	private String content;

	public Item(){
		
	}
	
	public Item(String content){
		this.content = content;
	}
	
	public List<Feature> getFeatures(){
		return getFeatures(content);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private static List<Feature> getFeatures(String content){
		List<Feature> features = new ArrayList<Feature>();
		for (String s : content.split("\\b+")){
			if (!s.trim().equals("")){
				// Only use lower case words:
				if (s.trim().toLowerCase().equals(s.trim()))
					features.add(new Feature(s.trim()));
			}
		}
		return features;
	}
	
	// Added word-gram functionality
	private static List<String> ngrams(int n, String content) {
        List<Feature> ngrams = new ArrayList<Feature>();
        String[] words = content.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(new Feature(concat(words, i, i+n)));
        //System.out.println((String[])ngrams.toArray(new String[0]));
        
        // Test removing any n-grams with a single word less than 3 characters in length:
        List<String> longNgrams = new ArrayList<String>();
        for (Feature f : ngrams){
        	try{
        		boolean usableNgram = true;
	        	usableNgram = (f.getContent().split(" ")[0].length() > 3 || f.getContent().split(" ")[1].length() > 3);
	        	if (usableNgram) usableNgram = (f.getContent().toLowerCase().equals(f.getContent()));
	        	if (usableNgram)
	        		longNgrams.add(f.getContent());
        	}
        	catch(Exception e){
        		;
        	}
        }
        return longNgrams;
    }

    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++){
            //sb.append((i > start ? " " : "") + words[i]);
        	sb.append((i > start ? " " : "") + words[i].replaceAll("[^a-z]", " ").trim());
        }
        return sb.toString();
    }
    // End word-gram section

}