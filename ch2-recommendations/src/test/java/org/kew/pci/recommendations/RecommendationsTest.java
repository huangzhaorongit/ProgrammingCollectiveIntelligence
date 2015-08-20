package org.kew.pci.recommendations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kew.pci.recommendations.model.Item;
import org.kew.pci.recommendations.model.Review;
import org.kew.pci.recommendations.model.Reviewer;

import junit.framework.TestCase;

public class RecommendationsTest extends TestCase {

	Recommendations r = new Recommendations();
	
	protected void setUp() throws Exception {
		Map<String, Reviewer> reviewers = new HashMap<String,Reviewer>();
		Map<String, Item> items = new HashMap<String,Item>();
		List<Review> reviews = new ArrayList<Review>();

		String[] reviewerNames = {"Toby", "Jack Matthews", "Claudia Puig", "Gene Seymour", "Michael Phillips", "Mick LaSalle", "Lisa Rose"};
		for (String reviewerName : reviewerNames){
			reviewers.put(reviewerName, new Reviewer(reviewerName));
		}
		
		String[] itemNames = {"Just My Luck","Superman Returns","You, Me and Dupree", "The Night Listener", "Snakes on a Plane", "Lady in the Water"};
		for (String itemName : itemNames){
			items.put(itemName, new Item(itemName));
		}
	
		reviews.add(new Review(reviewers.get("Lisa Rose")			,items.get("Lady in the Water"	), 2.5));
		reviews.add(new Review(reviewers.get("Lisa Rose")			,items.get("Snakes on a Plane"	), 3.5));
		reviews.add(new Review(reviewers.get("Lisa Rose")			,items.get("Just My Luck"		), 3.0));
		reviews.add(new Review(reviewers.get("Lisa Rose")			,items.get("Superman Returns"	), 3.5));
		reviews.add(new Review(reviewers.get("Lisa Rose")			,items.get("You, Me and Dupree"	), 2.5));
		reviews.add(new Review(reviewers.get("Lisa Rose")			,items.get("The Night Listener"	), 3.0));
			
		reviews.add(new Review(reviewers.get("Gene Seymour")		,items.get("Lady in the Water"	), 3.0));
		reviews.add(new Review(reviewers.get("Gene Seymour")		,items.get("Snakes on a Plane"	), 3.5));
		reviews.add(new Review(reviewers.get("Gene Seymour")		,items.get("Just My Luck"		), 1.5));
		reviews.add(new Review(reviewers.get("Gene Seymour")		,items.get("Superman Returns"	), 5.0));
		reviews.add(new Review(reviewers.get("Gene Seymour")		,items.get("The Night Listener"	), 3.0));
		reviews.add(new Review(reviewers.get("Gene Seymour")		,items.get("You, Me and Dupree"	), 3.5));
			
		reviews.add(new Review(reviewers.get("Michael Phillips")	,items.get("Lady in the Water"	), 2.5));
		reviews.add(new Review(reviewers.get("Michael Phillips")	,items.get("Snakes on a Plane"	), 3.0));
		reviews.add(new Review(reviewers.get("Michael Phillips")	,items.get("Superman Returns"	), 3.5));
		reviews.add(new Review(reviewers.get("Michael Phillips")	,items.get("The Night Listener"	), 4.0));
			
		reviews.add(new Review(reviewers.get("Claudia Puig")		,items.get("Snakes on a Plane"	), 3.5));
		reviews.add(new Review(reviewers.get("Claudia Puig")		,items.get("Just My Luck"		), 3.0));
		reviews.add(new Review(reviewers.get("Claudia Puig")		,items.get("The Night Listener"	), 4.5));
		reviews.add(new Review(reviewers.get("Claudia Puig")		,items.get("Superman Returns"	), 4.0));
		reviews.add(new Review(reviewers.get("Claudia Puig")		,items.get("You, Me and Dupree"	), 2.5));
			
		reviews.add(new Review(reviewers.get("Mick LaSalle")		,items.get("Lady in the Water"	), 3.0));
		reviews.add(new Review(reviewers.get("Mick LaSalle")		,items.get("Snakes on a Plane"	), 4.0));
		reviews.add(new Review(reviewers.get("Mick LaSalle")		,items.get("Just My Luck"		), 2.0));
		reviews.add(new Review(reviewers.get("Mick LaSalle")		,items.get("Superman Returns"	), 3.0));
		reviews.add(new Review(reviewers.get("Mick LaSalle")		,items.get("The Night Listener"	), 3.0));
		reviews.add(new Review(reviewers.get("Mick LaSalle")		,items.get("You, Me and Dupree"	), 2.0));
			
		reviews.add(new Review(reviewers.get("Jack Matthews")		,items.get("Lady in the Water"	), 3.0));
		reviews.add(new Review(reviewers.get("Jack Matthews")		,items.get("Snakes on a Plane"	), 4.0));
		reviews.add(new Review(reviewers.get("Jack Matthews")		,items.get("The Night Listener"	), 3.0));
		reviews.add(new Review(reviewers.get("Jack Matthews")		,items.get("Superman Returns"	), 5.0));
		reviews.add(new Review(reviewers.get("Jack Matthews")		,items.get("You, Me and Dupree"	), 3.5));
			
		reviews.add(new Review(reviewers.get("Toby")				,items.get("Snakes on a Plane"	), 4.5));
		reviews.add(new Review(reviewers.get("Toby")				,items.get("You, Me and Dupree"	), 1.0));
		reviews.add(new Review(reviewers.get("Toby")				,items.get("Superman Returns"	), 4.0));

		r.loadData(reviewers, items, reviews, Recommendations.SIMILARITY_TYPE_DEFAULT);
		super.setUp();
	}

	public void testGetEuclideanSimilarityDistanceReviewerReviewer() {
		assertEquals(r.getEuclideanSimilarityDistance(r.getReviewers().get("Lisa Rose"), r.getReviewers().get("Gene Seymour")), 0.29429805508554946);
	}

	public void testGetPearsonCorrelationScoreReviewerReviewer() {
		assertEquals(r.getPearsonCorrelationScore(r.getReviewers().get("Lisa Rose"), r.getReviewers().get("Gene Seymour")), 0.39605901719066977);
	}

	public void testGetReviewerRecommendationsByItem() {
		Map<Reviewer, Double> reviewer_rec  = r.getReviewerRecommendationsByItem(r.getItems().get("Just My Luck"), Recommendations.SIMILARITY_TYPE_PEARSON);
		// First
		Reviewer firstReviewer = reviewer_rec.keySet().toArray(new Reviewer[reviewer_rec.size()])[0];
		assertEquals(firstReviewer.getName(), "Michael Phillips");
		assertEquals(reviewer_rec.get(firstReviewer), 4.0);
		// Second
		Reviewer secondReviewer = reviewer_rec.keySet().toArray(new Reviewer[reviewer_rec.size()])[1];
		assertEquals(secondReviewer.getName(), "Jack Matthews");
		assertEquals(reviewer_rec.get(secondReviewer), 3.0);
	}

	public void testGetItemRecommendationsItemInt() {
		Map<Item, Double> item_rec = r.getItemRecommendations(r.getItems().get("Superman Returns"), Recommendations.SIMILARITY_TYPE_PEARSON);
		// First
		Item firstItem = item_rec.keySet().toArray(new Item[item_rec.size()])[0];
		assertEquals(firstItem.getTitle(), "You, Me and Dupree");
		assertEquals(item_rec.get(firstItem), 0.6579516949597686);
		// Second
		Item secondItem = item_rec.keySet().toArray(new Item[item_rec.size()])[1];
		assertEquals(secondItem.getTitle(), "Lady in the Water");
		assertEquals(item_rec.get(secondItem), 0.4879500364742717);
		// Third
		Item thirdItem = item_rec.keySet().toArray(new Item[item_rec.size()])[2];
		assertEquals(thirdItem.getTitle(), "Snakes on a Plane");
		assertEquals(item_rec.get(thirdItem), 0.11180339887498636);
	}

	public void testGetItemRecommendationsReviewerInt() {
		Map<Item, Double> item_rec  = r.getItemRecommendations(r.getReviewers().get("Toby"), Recommendations.SIMILARITY_TYPE_PEARSON);
		// First
		Item firstItem = item_rec.keySet().toArray(new Item[item_rec.size()])[0];
		assertEquals(firstItem.getTitle(), "The Night Listener");
		assertEquals(item_rec.get(firstItem), 3.3477895267131013);
		// Second
		Item secondItem = item_rec.keySet().toArray(new Item[item_rec.size()])[1];
		assertEquals(secondItem.getTitle(), "Lady in the Water");
		assertEquals(item_rec.get(secondItem), 2.832549918264162);
		// Third
		Item thirdItem = item_rec.keySet().toArray(new Item[item_rec.size()])[2];
		assertEquals(thirdItem.getTitle(), "Just My Luck");
		assertEquals(item_rec.get(thirdItem), 2.5309807037655645);
	}

	public void testGetReviewerRecommendationsByReviewer() {
		Map<Reviewer, Double> reviewer_rec  = r.getReviewerRecommendationsByReviewer(r.getReviewers().get("Toby"), Recommendations.SIMILARITY_TYPE_PEARSON);
		// First
		Reviewer firstReviewer = reviewer_rec.keySet().toArray(new Reviewer[reviewer_rec.size()])[0];
		assertEquals(firstReviewer.getName(), "Lisa Rose");
		assertEquals(reviewer_rec.get(firstReviewer), 0.9912407071619299);
		// Second
		Reviewer secondReviewer = reviewer_rec.keySet().toArray(new Reviewer[reviewer_rec.size()])[1];
		assertEquals(secondReviewer.getName(), "Mick LaSalle");
		assertEquals(reviewer_rec.get(secondReviewer), 0.9244734516419049);
		// Third
		Reviewer thirdReviewer = reviewer_rec.keySet().toArray(new Reviewer[reviewer_rec.size()])[2];
		assertEquals(thirdReviewer.getName(), "Claudia Puig");
		assertEquals(reviewer_rec.get(thirdReviewer), 0.8934051474415647);
	}

	public void testGetItemRecommendationsUsingItemComparisons() {
		Map<Item,Double> rankedSimilarItems = r.getItemRecommendationsUsingItemComparisons(r.getReviewers().get("Toby"));
		// First
		Item firstItem = rankedSimilarItems.keySet().toArray(new Item[rankedSimilarItems.size()])[0];
		assertEquals(firstItem.getTitle(), "Lady in the Water");
		assertEquals(rankedSimilarItems.get(firstItem), 3.610031066802182);
		// Second
		Item secondItem = rankedSimilarItems.keySet().toArray(new Item[rankedSimilarItems.size()])[1];
		assertEquals(secondItem.getTitle(), "The Night Listener");
		assertEquals(rankedSimilarItems.get(secondItem), 3.531395034185981);
		// Third
		Item thirdItem = rankedSimilarItems.keySet().toArray(new Item[rankedSimilarItems.size()])[2];
		assertEquals(thirdItem.getTitle(), "Just My Luck");
		assertEquals(rankedSimilarItems.get(thirdItem), 2.960999860724268);
	}

}
