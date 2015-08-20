package org.kew.pci.recommendations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kew.pci.recommendations.model.Item;
import org.kew.pci.recommendations.model.Review;
import org.kew.pci.recommendations.model.Reviewer;

public class App {

	private static Map<String, Reviewer> reviewers = new HashMap<String,Reviewer>();
	private static Map<String, Item> items = new HashMap<String,Item>();
	private static List<Review> reviews = new ArrayList<Review>();
	
	static{
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

	}
	
	public static void main(String[] args) {
		Recommendations recc = new Recommendations();
		//recc.loadData();
		recc.loadData(reviewers, items, reviews, Recommendations.SIMILARITY_TYPE_DEFAULT);
		System.out.println("Reviewers:");
		for (Reviewer r : recc.getReviewersFromReviews()){
			System.out.println(r.toString());
		}
		System.out.println("Items:");
		for (Item item : recc.getItemsFromReviews()){
			System.out.println(item.toString());
		}
		System.out.println("Toby's reviews:");
		for (Review r : recc.getReviewsByReviewerName("Toby")){
			System.out.println(r.toString());
		}
		System.out.println(recc.getReviewerScoreByItem("Toby", "Snakes on a Plane"));
		System.out.println(recc.getEuclideanSimilarityDistance(recc.getReviewers().get("Lisa Rose"), recc.getReviewers().get("Gene Seymour")));
		System.out.println(recc.getSimilarity(recc.getReviewers().get("Lisa Rose"), recc.getReviewers().get("Gene Seymour"),Recommendations.SIMILARITY_TYPE_EUCLIDEAN));
		System.out.println(recc.getPearsonCorrelationScore(recc.getReviewers().get("Lisa Rose"), recc.getReviewers().get("Gene Seymour")));
		System.out.println(recc.getSimilarity(recc.getReviewers().get("Lisa Rose"), recc.getReviewers().get("Gene Seymour"),Recommendations.SIMILARITY_TYPE_PEARSON));
		
		
		Map<Reviewer, Double> reviewer_rec  = recc.getReviewerRecommendations(recc.getReviewers().get("Toby"), Recommendations.SIMILARITY_TYPE_PEARSON);
		int i = 0;
		for (Reviewer reviewer : reviewer_rec.keySet()){
			System.out.println(reviewer.getName() + "\t" + reviewer_rec.get(reviewer));
			if (++i >= 3)
				break;
		}
		
		Map<Item, Double> item_rec  = recc.getItemRecommendations(recc.getReviewers().get("Toby"), Recommendations.SIMILARITY_TYPE_PEARSON);
		int j = 0;
		for (Item item : item_rec.keySet()){
			System.out.println(item.getTitle() + "\t" + item_rec.get(item));
			if (++j >= 3)
				break;
		}

		System.out.println("===");
		Map<Item, Double> item_rec2 = recc.getItemRecommendations(recc.getItems().get("Superman Returns"), Recommendations.SIMILARITY_TYPE_PEARSON);
		int k = 0;
		for (Item item : item_rec2.keySet()){
			System.out.println(item.getTitle() + "\t" + item_rec2.get(item));
			if (++k >= 3)
				break;
		}

		Map<Reviewer, Double> reviewer_rec2  = recc.getReviwerRecommendations(recc.getItems().get("Just My Luck"), Recommendations.SIMILARITY_TYPE_PEARSON);
		for (Reviewer reviewer : reviewer_rec2.keySet()){
			System.out.println(reviewer.getName() + "\t" + reviewer_rec2.get(reviewer));
		}

		Map<Item,Double> rankedSimilarItems = recc.getItemRecommendationsUsingItemComparisons(recc.getReviewers().get("Toby"));
		for(Item item : rankedSimilarItems.keySet()){
			System.out.println(item + "\t" + rankedSimilarItems.get(item));
		}
	}
	
}