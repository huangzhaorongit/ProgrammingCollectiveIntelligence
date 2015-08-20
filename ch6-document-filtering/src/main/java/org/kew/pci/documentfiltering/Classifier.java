package org.kew.pci.documentfiltering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kew.pci.documentfiltering.model.Category;
import org.kew.pci.documentfiltering.model.Feature;
import org.kew.pci.documentfiltering.model.Item;

public class Classifier {

	
	// How many times has a feature appeared in different categories?
	Map<Feature,Map<Category, Integer>> featureInCategoryCounts = new TreeMap<Feature,Map<Category,Integer>>();
	
	// How many times has each category has been used?
	Map<Category, Integer> categoryUseCounts = new TreeMap<Category,Integer>();

	// Thresholds - a map of threshold values by category
	Map<Category, Double> thresholds = new TreeMap<Category,Double>();
		
	
	public Classifier(){
		
	}
	
	public void train(Item item, Category category){
		List<Feature> features = item.getFeatures();
		for (Feature feature : features){
			incrementFeatureCategoryPairCount(feature, category);
		}
		incrementCategoryCount(category);
	}
	
	/**
	 * Increase the count stored for specified feature in specified category
	 */
	public void incrementFeatureCategoryPairCount(Feature feature, Category category){
		Map<Category,Integer> featureWithCategories = featureInCategoryCounts.get(feature);
		if (featureWithCategories == null){
			featureWithCategories = new TreeMap<Category,Integer>();
			featureWithCategories.put(category, 1);
		}
		else{
			Integer current = featureWithCategories.get(category);
			if (current == null)
				current = new Integer(0);
			featureWithCategories.put(category, ++current);
		}
		featureInCategoryCounts.put(feature, featureWithCategories);
	}
	
	/**
	 * Increase the count stored for specified category
	 */
	public void incrementCategoryCount(Category category){
		Integer i = categoryUseCounts.get(category);
		if (i == null)
			i = new Integer(0);
		categoryUseCounts.put(category, ++i);
	}
	
	/**
	 * How many times has the specified feature appeared in the specified category?
	 */
	public int getFeatureInCategoryCount(Feature feature, Category category){
		int i = 0;
		Map<Category,Integer> featureWithCategories = featureInCategoryCounts.get(feature);
		if (featureWithCategories == null)
			i = 0;
		else{
			Integer tmp = featureWithCategories.get(category);
			if (tmp == null)
				i = 0;
			else 
				i = tmp.intValue();
		}
		return i;
	}

	/**
	 * How many items are in the specified category?
	 * @param args
	 */
	public int getCategorySize(Category category){
		Integer i = 0;
		i = categoryUseCounts.get(category);
		if (i == null)
			i = 0;
		return i;
	}
	
	public int getTotalNumberItems(){
		// A sum of all the items in all the categories
		int i = 0;
		for (Category category : categoryUseCounts.keySet())
			i += categoryUseCounts.get(category);
		return i;
	}
	
	public List<Category> getCategories(){
		List<Category> categories = new ArrayList<Category>();
		categories.addAll(categoryUseCounts.keySet());
		return categories;
	}

	public void printFeatureCategories(){
		for (Feature feature : featureInCategoryCounts.keySet()){
			System.out.print(feature + ":\t");
			Map<Category, Integer> categoryCounts = featureInCategoryCounts.get(feature);
			for (Category category : categoryCounts.keySet()){
				System.out.print(category + " (" + categoryCounts.get(category) + ") ");
			}
			System.out.println("");
		}
		
	}
	
	public double getFeatureProbability(Feature feature, Category category){
		double probability = 0;
		if (getCategorySize(category) == 0)
			probability = 0;
		else{
			// the probability is the number of times this feature has appeared in this category divided by the total number of items in this category
			//System.out.println(getFeatureInCategoryCount(feature, category));
			//System.out.println(getCategorySize(category));
			probability = (double)getFeatureInCategoryCount(feature, category) / (double)getCategorySize(category);
		}
		return probability;
	}

	double weight = 1.0;
	double assumedProbability = 0.5;
	public double getWeightedFeatureProbability(Feature feature, Category category){
		// Calculate current probability
		double basicProbability= getFeatureProbability(feature, category);
		// Count number of time this feature has appeared in all categories
		int totalAppearances = 0;
		for (Category c : getCategories()){
			totalAppearances += getFeatureInCategoryCount(feature, c);
		}
		double weightedAverage = ( (weight * assumedProbability) + (totalAppearances * basicProbability) )/ (weight + totalAppearances);
		//Calculate weighted average
		return weightedAverage;
	}
	
	// From p.124
	public double getDocumentProbability(Item item, Category category){
		double documentProbability = 1;
		List<Feature> features = item.getFeatures();
		for (Feature feature : features){
			documentProbability *= getWeightedFeatureProbability(feature, category);
		}
		return documentProbability;
	}
	
	// From page 125
	public double getProbability(Item item, Category category){
		double categoryProbability = (double)getCategorySize(category) / (double)getTotalNumberItems();
		double documentProbability = getDocumentProbability(item, category);
		return categoryProbability * documentProbability;
	}

	// from page 126 - thresholds
	public void setThreshold(Category category, double threshold){
		thresholds.put(category, threshold);
	}

	public double getThreshold(Category category){
		double threshold = 1.0;
		if (thresholds.get(category) != null)
			threshold = thresholds.get(category);
		return threshold;
	}
	
	// From page 126-7 - classify
	public Category classify(Item item, Category defaultCategory){
		Map<Category,Double> probs = new TreeMap<Category,Double>();
		Category best = defaultCategory;
		double max = 0.0;
		for (Category category : getCategories()){
			double thisProbability = getProbability(item, category);
			probs.put(category, thisProbability);
			if (thisProbability > max){
				max = thisProbability;
				best = category;
			}
		}
		
		// Make sure that the probability exceeds threshold * next best
		for(Category category : probs.keySet()){
			if (category.equals(best))
				continue;
			if (probs.get(category)*getThreshold(best) > probs.get(best))
				return defaultCategory;
		}
		return best;
	}

}