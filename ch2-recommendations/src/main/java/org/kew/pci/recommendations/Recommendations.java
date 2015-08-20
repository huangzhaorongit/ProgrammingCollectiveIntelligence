package org.kew.pci.recommendations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.kew.pci.recommendations.model.Item;
import org.kew.pci.recommendations.model.Review;
import org.kew.pci.recommendations.model.Reviewer;

public class Recommendations {

	public static final int SIMILARITY_TYPE_EUCLIDEAN = 1;
	public static final int SIMILARITY_TYPE_PEARSON = 2;
	public static final int SIMILARITY_TYPE_DEFAULT = SIMILARITY_TYPE_PEARSON;
	
	private Map<String, Reviewer> reviewers = new HashMap<String,Reviewer>();
	
	public Map<String, Reviewer> getReviewers() {
		return reviewers;
	}

	private Map<String, Item> items = new HashMap<String,Item>();
	
	public Map<String, Item> getItems() {
		return items;
	}
	
	List<Review> reviews = new ArrayList<Review>();
	
	Map<Item,Map<Item,Double>> itemComparisons = new HashMap<Item,Map<Item,Double>>();
	
	public void loadData(Map<String,Reviewer> reviewers, Map<String, Item> items, List<Review> reviews, int similarityAlgorithm){
		this.reviewers = reviewers;
		this.items = items;
		this.reviews = reviews;
		// Populate item comparison data structure:
		for (Item item : getItemsFromReviews()){
			Map<Item,Double> similarItems = getItemRecommendations(item, similarityAlgorithm);
			itemComparisons.put(item, similarItems);
		}		
	}
	
	// Methods to get reviewers
	public Set<Reviewer> getReviewersFromReviews(){
		Set<Reviewer> reviewers = new HashSet<Reviewer>();
		for (Review review : reviews)
			reviewers.add(review.getReviewer());
		return reviewers;
	}

	public List<Reviewer> getReviewersByItem(Item item){
		List<Reviewer> itemReviewers = new ArrayList<Reviewer>();
		for (Review review : reviews){
			if (review.getItem().equals(item)){
				itemReviewers.add(review.getReviewer());
			}
		}
		return itemReviewers;
	}

	// Methods to get items
	public Set<Item> getItemsFromReviews(){
		Set<Item> items = new HashSet<Item>();
		for (Review review : reviews)
			items.add(review.getItem());
		return items;
	}

	public List<Item> getItemsByReviewer(Reviewer reviewer){
		List<Item> items = new ArrayList<Item>();
		for (Review review : reviews){
			if (review.getReviewer().equals(reviewer)){
				items.add(review.getItem());
			}
		}
		return items;		
	}
	
	public List<Item> getItemsByReviewerName(String reviewerName){
		Reviewer reviewer = reviewers.get(reviewerName);
		return getItemsByReviewer(reviewer);
	}

	
	// Methods to get reviews
	public List<Review> getReviewsByReviewerName(String reviewerName){
		Reviewer reviewer = reviewers.get(reviewerName);
		return getReviewsByReviewer(reviewer);
	}
	public List<Review> getReviewsByReviewer(Reviewer reviewer){
		List<Review> reviewersReviews = new ArrayList<Review>();
		for (Review review : reviews){
			if (review.getReviewer().equals(reviewer)){
				reviewersReviews.add(review);
			}
		}
		return reviewersReviews;
	}
	public List<Review> getReviewsByItem(Item item){
		List<Review> itemReviews = new ArrayList<Review>();
		for (Review review : reviews){
			if (review.getItem().equals(item)){
				itemReviews.add(review);
			}
		}
		return itemReviews;
	}


	// Methods to return the score applied by a reviewer to an item in a review
	public Double getReviewerScoreByItem(String reviewerName, Item item){
		Reviewer reviewer = reviewers.get(reviewerName);
		return getReviewerScoreByItem(reviewer, item);
	}

	public Double getReviewerScoreByItem(String reviewerName, String itemTitle){
		Reviewer reviewer = reviewers.get(reviewerName);
		return getReviewerScoreByItem(reviewer, itemTitle);
	}

	public Double getReviewerScoreByItem(Reviewer reviewer, String itemTitle){
		Item item  = items.get(itemTitle);
		return getReviewerScoreByItem(reviewer, item);		
	}
	
	public Double getReviewerScoreByItem(Reviewer reviewer, Item item){
		Double score = null;
		for (Review review : reviews){
			if (review.getReviewer().equals(reviewer) && review.getItem().equals(item)){
				score = new Double(review.getScore());
				break;
			}
		}		
		return score;

	}

	// Similarity measures btw reviewers
	public Double getSimilarity(Reviewer reviewer1, Reviewer reviewer2, int similarityType){
		Double similarity = new Double(0);
		switch (similarityType) {
		case SIMILARITY_TYPE_EUCLIDEAN:
			similarity = getEuclideanSimilarityDistance(reviewer1, reviewer2);
			break;
		case SIMILARITY_TYPE_PEARSON:
			similarity = getPearsonCorrelationScore(reviewer1, reviewer2);
			break;
		default:
			similarity = getSimilarity(reviewer1, reviewer2, Recommendations.SIMILARITY_TYPE_DEFAULT);
		}
		return similarity;
	}

	public Double getEuclideanSimilarityDistance(Reviewer reviewer1, Reviewer reviewer2){
		Collection<Item> sharedItems = CollectionUtils.intersection(getItemsByReviewer(reviewer1), getItemsByReviewer(reviewer2));
		Double sum_of_squares = new Double(0);
		for (Item item : sharedItems){
			Double reviewer1_score = getReviewerScoreByItem(reviewer1, item);
			Double reviewer2_score = getReviewerScoreByItem(reviewer2, item);
			sum_of_squares += Math.pow(reviewer1_score - reviewer2_score,2);
		}
		return 1/ (1 + Math.sqrt(sum_of_squares));
	}

	public Double getPearsonCorrelationScore(Reviewer reviewer1, Reviewer reviewer2){
		Double score = new Double(0);
		Collection<Item> sharedItems = CollectionUtils.intersection(getItemsByReviewer(reviewer1), getItemsByReviewer(reviewer2));
		int numShared = sharedItems.size();
		if (numShared != 0){
			double reviewer1_sum = 0, reviewer2_sum = 0;
			double reviewer1_sum_sq = 0, reviewer2_sum_sq = 0;
			double products_sum = 0;
			for (Item item : sharedItems){
				double reviewer1_score = getReviewerScoreByItem(reviewer1, item.getTitle());
				double reviewer2_score = getReviewerScoreByItem(reviewer2, item.getTitle());
				// Sums:
				reviewer1_sum += reviewer1_score;
				reviewer2_sum += reviewer2_score;
				// Sums of squares:
				reviewer1_sum_sq += Math.pow(reviewer1_score,2);
				reviewer2_sum_sq += Math.pow(reviewer2_score,2);
				// Sums of products
				products_sum += reviewer1_score * reviewer2_score;
			}
			double num = products_sum - (reviewer1_sum * reviewer2_sum / numShared);
			double den = Math.sqrt( (reviewer1_sum_sq-Math.pow(reviewer1_sum,2)/numShared) * (reviewer2_sum_sq-Math.pow(reviewer2_sum,2)/numShared) );
			if (den != 0)
				score = num/den;
		}
		return score;
	}	

	// Similarity measures btw items
	public Double getSimilarity(Item item1, Item item2, int similarityType){
		Double similarity = new Double(0);
		switch (similarityType) {
		case SIMILARITY_TYPE_EUCLIDEAN:
			similarity = getEuclideanSimilarityDistance(item1, item2);
			break;
		case SIMILARITY_TYPE_PEARSON:
			similarity = getPearsonCorrelationScore(item1, item2);
			break;
		default:
			similarity = getSimilarity(item1, item2, Recommendations.SIMILARITY_TYPE_DEFAULT);
		}
		return similarity;
	}

	public Double getEuclideanSimilarityDistance(Item item1, Item item2){
		Collection<Reviewer> sharedReviewers = CollectionUtils.intersection(getReviewersByItem(item1), getReviewersByItem(item2));
		Double sum_of_squares = new Double(0);
		for (Reviewer reviewer : sharedReviewers){
			Double item1_score = getReviewerScoreByItem(reviewer, item1);
			Double item2_score = getReviewerScoreByItem(reviewer, item2);
			sum_of_squares += Math.pow(item1_score - item2_score,2);
		}
		//return 1/ (1 + sum_of_squares);
		return 1/ (1 + Math.sqrt(sum_of_squares));
		//return 1/ (1 + Math.sqrt(sum_of_squares/sharedReviewers.size()));
	}

	public Double getPearsonCorrelationScore(Item item1, Item item2){
		Double score = new Double(0);
		Collection<Reviewer> sharedReviewers = CollectionUtils.intersection(getReviewersByItem(item1), getReviewersByItem(item2));
		int numShared = sharedReviewers.size();
		if (numShared != 0){
			double item1_sum = 0, item2_sum = 0;
			double item1_sum_sq = 0, item2_sum_sq = 0;
			double products_sum = 0;
			for (Reviewer reviewer : sharedReviewers){
				double item1_score = getReviewerScoreByItem(reviewer, item1);
				double item2_score = getReviewerScoreByItem(reviewer, item2);
				// Sums:
				item1_sum += item1_score;
				item2_sum += item2_score;
				// Sums of squares:
				item1_sum_sq += Math.pow(item1_score,2);
				item2_sum_sq += Math.pow(item2_score,2);
				// Sums of products
				products_sum += item1_score * item2_score;
			}
			// As shown in book:
			//double num = products_sum - (item1_sum * item2_sum / numShared);
			//double den = Math.sqrt( (item1_sum_sq-Math.pow(item1_sum,2)/numShared) * (item2_sum_sq-Math.pow(item2_sum,2)/numShared) );

			//
			/*
			 * See http://stackoverflow.com/questions/13558529/pearson-algorithm-from-programming-collective-intelligence-still-not-working/13562198#13562198
			*/	      
			double num = (products_sum/numShared) - (1.0 * item1_sum * item2_sum / Math.pow(numShared,2));
			double den = Math.sqrt(((item1_sum_sq/numShared)-Math.pow(item1_sum,2)/Math.pow(numShared,2))*((item2_sum_sq/numShared)-Math.pow(item2_sum,2)/Math.pow(numShared,2)));
			if (den != 0)
				score = num/den;
		}
		return score;
	}

	// Get recommendations: reviewers by reviewer
	public Map<Reviewer,Double> getReviewerRecommendationsByReviewer(Reviewer reviewer, int similarityAlgorithm){
		Map<Reviewer,Double> m = new HashMap<Reviewer,Double>();
		for (Reviewer r : getReviewersFromReviews()){
			if (!r.equals(reviewer)){
				Double similarity = getSimilarity(reviewer, r, similarityAlgorithm);
				m.put(r, similarity);
			}
		}
		return MapUtil.sortByValueDesc(m);
	}

	// Get recommendations: items by item
	public Map<Item,Double> getItemRecommendations(Item item, int similarityAlgorithm){
		Map<Item,Double> m = new HashMap<Item,Double>();
		for (Item reviewedItem  : getItemsFromReviews()){
			if (!reviewedItem.equals(item)){
				Double similarity = getSimilarity(reviewedItem, item, similarityAlgorithm);
				m.put(reviewedItem, similarity);
			}
		}
		return MapUtil.sortByValueDesc(m);
	}

	/**
	 * Use a weighted average to get item recommendations for the specified reviewer
	 * @param reviewerName
	 * @param similarityAlgorithm
	 * @return
	 */
	// Get recommendations: items by reviewer
	public Map<Item,Double> getItemRecommendations(Reviewer reviewer, int similarityAlgorithm){
		Map<Item,Double> rankings = new HashMap<Item,Double>();
		
		Map<Item,Double> totals = new HashMap<Item,Double>();
		Map<Item,Double> sumSimilarities = new HashMap<Item,Double>();
		
		List<Item> my_items = getItemsByReviewer(reviewer);
		for (Reviewer r : getReviewersFromReviews()){
			if (!r.equals(reviewer)){
				Double similarity = getSimilarity(reviewer, r, similarityAlgorithm);
				if (similarity < 0)
					continue;
				// Get all the items reviewed by the other reviewer
				List<Item> items = getItemsByReviewerName(r.getName());
				
				// We're only interested in items not rated by the specified reviewer
				Collection<Item> newItems= CollectionUtils.subtract(items, my_items);
				for (Item item : newItems){
					// Totals
					Double total = totals.get(item);
					if (total == null)
						total = new Double(0);
					Double score = getReviewerScoreByItem(r, item);
					totals.put(item, total+=(score*similarity));
					// Sum of similarities
					Double simSum = sumSimilarities.get(item);
					if (simSum == null)
						simSum = new Double(0);
					sumSimilarities.put(item, simSum +=similarity);
				}
			}
		}
		for (Item item : totals.keySet()){
			Double ranking = totals.get(item) / sumSimilarities.get(item);
			rankings.put(item, ranking);
		}
		return MapUtil.sortByValueDesc(rankings);
	}

	/**
	 * Use a weighted average to get reviewer recommendations for the specified item
	 * @param reviewerName
	 * @param similarityAlgorithm
	 * @return
	 */
	// Get recommendations: reviewers by item
	public Map<Reviewer,Double> getReviewerRecommendationsByItem(Item item, int similarityAlgorithm){
		Map<Reviewer,Double> rankings = new HashMap<Reviewer,Double>();
		
		Map<Reviewer,Double> totals = new HashMap<Reviewer,Double>();
		Map<Reviewer,Double> sumSimilarities = new HashMap<Reviewer,Double>();
		
		List<Reviewer> my_reviewers = getReviewersByItem(item);
		for (Item i : getItemsFromReviews()){
			if (!i.equals(item)){
				Double similarity = getSimilarity(item, i, similarityAlgorithm);
				if (similarity < 0)
					continue;
				// Get all the reviewers who have reviewed this item
				List<Reviewer> reviewers = getReviewersByItem(i);
				
				// We're only interested in reviewers who haven;t rated the specified item
				Collection<Reviewer> newReviewers= CollectionUtils.subtract(reviewers, my_reviewers);
				for (Reviewer reviewer : newReviewers){
					// Totals
					Double total = totals.get(reviewer);
					if (total == null)
						total = new Double(0);
					Double score = getReviewerScoreByItem(reviewer, i);
					totals.put(reviewer, total+=(score*similarity));
					// Sum of similarities
					Double simSum = sumSimilarities.get(reviewer);
					if (simSum == null)
						simSum = new Double(0);
					sumSimilarities.put(reviewer, simSum +=similarity);
				}
			}
		}
		for (Reviewer reviewer: totals.keySet()){
			Double ranking = totals.get(reviewer) / sumSimilarities.get(reviewer);
			rankings.put(reviewer, ranking);
		}
		return MapUtil.sortByValueDesc(rankings);
	}

	/**
	 * Use item based data structure
	 * @param reviewer
	 * @param similarityAlgorithm
	 * @return
	 * 
	 * itemComparisons is pre-built, so no similarityAlgorithm parameter passed in method signature
	 */
	// Get recommendations items by reviewer, using pre-computed item comparisons
	public Map<Item,Double> getItemRecommendationsUsingItemComparisons(Reviewer reviewer){
		// Used to keep running totals:
		Map<Item, Double> scores = new HashMap<Item, Double>();
		Map<Item, Double> totalSims = new HashMap<Item, Double>();
		// Used to return values:
		Map<Item, Double> rankings = new HashMap<Item, Double>();
		
		List<Item> reviewedItems = getItemsByReviewer(reviewer);
		for (Item item : reviewedItems){
			Double rating = getReviewerScoreByItem(reviewer, item);
			for (Item similarItem : itemComparisons.get(item).keySet()){
				Double similarityRating = itemComparisons.get(item).get(similarItem);
				if (reviewedItems.contains(similarItem))
					continue;
				// Weighted sum of rating times similarity
				Double score = scores.get(similarItem);
				if (score == null)
					score= new Double(0);				
				scores.put(similarItem, score += similarityRating * rating);
				// Sum of all the similarities
				Double totalSim = totalSims.get(similarItem);
				if (totalSim == null)
					totalSim= new Double(0);				
				totalSims.put(similarItem, totalSim += similarityRating);
			}
		}
		// Divide each total score by total weighting to get average:
		for (Item i : scores.keySet()){
			rankings.put(i, scores.get(i)/totalSims.get(i));
		}
		return MapUtil.sortByValueDesc(rankings);
	}
	
}