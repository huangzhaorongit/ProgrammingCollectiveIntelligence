package org.kew.pci.documentfiltering;

import org.kew.pci.documentfiltering.model.Category;
import org.kew.pci.documentfiltering.model.Item;

import junit.framework.TestCase;

public class ClassifierTest extends TestCase {

	Classifier classifier;
	protected void setUp() throws Exception {
		classifier = new Classifier();
		super.setUp();
	}

	public void testClassificiation() {
    	sampleTrain();
    	assertEquals(classifier.classify(new Item("quick rabbit"), new Category("unknown")), new Category("good"));
    	assertEquals(classifier.classify(new Item("quick money"), new Category("unknown")), new Category("bad"));
		classifier.setThreshold(new Category("bad"), 3.0);
		assertEquals(classifier.classify(new Item("quick money"), new Category("unknown")), new Category("unknown"));
		for (int i = 0; i < 10; i++){
			sampleTrain();
		}
		assertEquals(classifier.classify(new Item("quick money"), new Category("unknown")), new Category("bad"));
	}

	private void sampleTrain(){
    	classifier.train(new Item("Nobody owns the water"), new Category("good"));
    	classifier.train(new Item("the quick rabbit jumps fences"), new Category("good"));
    	classifier.train(new Item("buy pharmaceuticals now"), new Category("bad"));
    	classifier.train(new Item("make quick money at the online casino"), new Category("bad"));
    	classifier.train(new Item("the quick brown fox jumps"), new Category("good"));		
	}
}