import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;

import wordcloud.CollisionMode;
import wordcloud.PolarBlendMode;
import wordcloud.PolarWordCloud;
import wordcloud.WordCloud;
import wordcloud.WordFrequency;
import wordcloud.bg.CircleBackground;
import wordcloud.bg.RectangleBackground;
import wordcloud.font.scale.LinearFontScalar;
import wordcloud.font.scale.SqrtFontScalar;
import wordcloud.nlp.FrequencyAnalyzer;
import wordcloud.palette.ColorPalette;

public class TestRunner {
	private static final Random RANDOM = new Random();
	
	public TestRunner(){
		
	}
	
	
	public void top20Words(){
		FileReader fr = new FileReader();
		
		String emailTrainingDataFilename = "spam_detection/train_email.txt";
		ArrayList<TextDocument> trainingEmails = fr.readDocumentData(emailTrainingDataFilename);
		DocumentDataset emailTrainingData = new DocumentDataset(trainingEmails);

		String movieReviewTrainingDataFilename = "movie_reviews/rt-train.txt";
		ArrayList<TextDocument> trainingReviews = fr.readDocumentData(movieReviewTrainingDataFilename);
		DocumentDataset reviewTrainingData = new DocumentDataset(trainingReviews);
		
		
		
		Map<String, Double> sortedMap = reviewTrainingData.getUnflaggedLikelihoods();
		
		sortedMap = MapUtil.sortByValue(sortedMap);
			
		Object[] keys = sortedMap.keySet().toArray();
		ArrayUtils.reverse(keys);
		
		
		for(int i = 0; i < 20; i++){
			String key  =  (String) keys[i];
			//System.out.println(""+ key + " = " + sortedMap.get(key));
			System.out.println(key);
		}
		
				
	}
	
	
	
	public double testSpamDetectionAccuracy(boolean bBernoulli){
		
		System.out.println("testing spam detection with bernoulli: " + bBernoulli);
		FileReader fr = new FileReader();
		
		String emailTrainingDataFilename = "spam_detection/train_email.txt";
		ArrayList<TextDocument> trainingEmails = fr.readDocumentData(emailTrainingDataFilename);
		String emailTestingDataFilename = "spam_detection/test_email.txt";
		ArrayList<TextDocument> testingEmails = fr.readDocumentData(emailTestingDataFilename);
		
		DocumentDataset emailTrainingData = new DocumentDataset(trainingEmails);

		FlagDetector spamDetector = new FlagDetector(emailTrainingData);
		double totalCountCorrectlyClassified = 0;
		double spamCountCorrectlyClassified = 0;
		double spamCountTotal = 0;
		double validCountCorrectlyClassified = 0;
		double validCountTotal = 0;
		
		for(TextDocument email : testingEmails){
			boolean calculated = spamDetector.detectFlag(email, bBernoulli);
			boolean expected = email.isFlagged();
			
			if(calculated == expected){totalCountCorrectlyClassified++;};
			//System.out.println("Expected " + expected + ", determined " + calculated);
			
			if(expected){
				spamCountTotal++;
				if(calculated == expected){spamCountCorrectlyClassified++;};
			}
			else{
				validCountTotal++;
				if(calculated == expected){validCountCorrectlyClassified++;};
			}
		}
		double accuracy = totalCountCorrectlyClassified / testingEmails.size();
		System.out.println("Spam classified as spam:" + (spamCountCorrectlyClassified/spamCountTotal));
		System.out.println("Spam classified as valid:" + (1-(spamCountCorrectlyClassified/spamCountTotal)));
		System.out.println("Valid classified as Valid:" + (validCountCorrectlyClassified/validCountTotal));
		System.out.println("Valid classified as Spam:" + (1-(validCountCorrectlyClassified/validCountTotal)));
		
		System.out.println("Email Spam Detection Accuracy: " + accuracy);
		return accuracy; 
	}
	
	
	public double testMovieReviewDetectionAccuracy(boolean bBernoulli){
		System.out.println("testing movei review detection with bernoulli: " + bBernoulli);
		FileReader fr = new FileReader();
		
		String movieReviewTrainingDataFilename = "movie_reviews/rt-train.txt";
		ArrayList<TextDocument> trainingReviews = fr.readDocumentData(movieReviewTrainingDataFilename);
		String movieReviewTestingDataFilename = "movie_reviews/rt-test.txt";
		ArrayList<TextDocument> testingReviews = fr.readDocumentData(movieReviewTestingDataFilename);
		
		DocumentDataset reviewTrainingData = new DocumentDataset(trainingReviews);
		
		FlagDetector positiveMovieReviewDetector = new FlagDetector(reviewTrainingData);
		double totalCountCorrectlyClassified=0;
		double positiveCountCorrectlyClassified = 0;
		double positiveCountTotal = 0;
		double negatveCountCorrectlyClassified = 0;
		double negativeCountTotal = 0;
		
		for(TextDocument review : testingReviews){
			boolean calculated = positiveMovieReviewDetector.detectFlag(review, bBernoulli);
			boolean expected = review.isFlagged();
			if(calculated == expected){totalCountCorrectlyClassified++;};
			
			if(expected){
				positiveCountTotal++;
				if(calculated == expected){positiveCountCorrectlyClassified++;};
			}
			else{
				negativeCountTotal++;
				if(calculated == expected){negatveCountCorrectlyClassified++;};
			}
			
		}
		double accuracy = totalCountCorrectlyClassified / testingReviews.size();
		System.out.println("Movie Review Positive Review Detection Accuracy: " + accuracy);
		System.out.println("Positive classified as Positive:" + (positiveCountCorrectlyClassified/positiveCountTotal));
		System.out.println("Positive classified as Negative:" + (1-(positiveCountCorrectlyClassified/positiveCountTotal)));
		System.out.println("Negative classified as Negative:" + (negatveCountCorrectlyClassified/negativeCountTotal));
		System.out.println("Negative classified as Positive:" + (1-(negatveCountCorrectlyClassified/negativeCountTotal)));
		
		return accuracy;
	}
	
	public void testIntermediateWordCloudFileGenerator() {
		FileReader fr = new FileReader();
		
		String emailTrainingDataFilename = "spam_detection/train_email.txt";
		ArrayList<TextDocument> trainingEmails = fr.readDocumentData(emailTrainingDataFilename);
		DocumentDataset emailTrainingData = new DocumentDataset(trainingEmails);
		
		String allDocFilename = "src/main/resources/wordMapData/emailTrainingDataForMap_all.txt";
		String flaggedDocFilename = "src/main/resources/wordMapData/emailTrainingDataForMap_spam.txt";
		String unflaggedDocFilename = "src/main/resources/wordMapData/emailTrainingDataForMap_valid.txt";
		emailTrainingData.generateIntermediateWordMapFile(allDocFilename, flaggedDocFilename, unflaggedDocFilename);
		
		
		String movieReviewTrainingDataFilename = "movie_reviews/rt-train.txt";
		ArrayList<TextDocument> trainingReviews = fr.readDocumentData(movieReviewTrainingDataFilename);
		DocumentDataset reviewTrainingData = new DocumentDataset(trainingReviews);
		
		allDocFilename = "src/main/resources/wordMapData/movieReviewTrainingDataForMap_all.txt";
		flaggedDocFilename = "src/main/resources/wordMapData/movieReviewTrainingDataForMap_positive.txt";
		unflaggedDocFilename = "src/main/resources/wordMapData/movieReviewTrainingDataForMap_negative.txt";
		reviewTrainingData.generateIntermediateWordMapFile(allDocFilename, flaggedDocFilename, unflaggedDocFilename);
		
		
	}
	public void testWordCloudGenerator(String intermediateFlaggedFilename, String intermediateUnflaggedFilename, String dstImgFilename){
		File flaggedFile = new File(intermediateFlaggedFilename);
		File unflaggedFile = new File(intermediateUnflaggedFilename);
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setWordFrequencesToReturn(200);
		frequencyAnalyzer.setMinWordLength(4);
		List<WordFrequency> wordFrequencies;
		List<WordFrequency> wordFrequencies2;
		PolarWordCloud wordCloud;
		try {
			wordFrequencies = frequencyAnalyzer.load(new FileInputStream(flaggedFile));
			wordFrequencies2 = frequencyAnalyzer.load(new FileInputStream(unflaggedFile));
			wordCloud = new PolarWordCloud(600, 600, CollisionMode.PIXEL_PERFECT, PolarBlendMode.BLUR);
			wordCloud.setPadding(2);
			wordCloud.setBackground(new CircleBackground(300));
			wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
			wordCloud.build(wordFrequencies, wordFrequencies2);
			wordCloud.writeToFile(dstImgFilename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static ColorPalette buildRandomColorPallete(int n) {
		final Color[] colors = new Color[n];
        for(int i = 0; i < colors.length; i++) {
            colors[i] = new Color(RANDOM.nextInt(230) + 25, RANDOM.nextInt(230) + 25, RANDOM.nextInt(230) + 25);
        }
        return new ColorPalette(colors);
    }
	
	public static void main(String[] args) {

		TestRunner tr = new TestRunner();
		//tr.top20Words();
		
		
		double spamAccuracy = tr.testSpamDetectionAccuracy(false);
		double movieAccuracy = tr.testMovieReviewDetectionAccuracy(false);
		System.out.println("Testing Multinomial Bayes...");
		System.out.println("Email Spam Detection Accuracy: " + spamAccuracy);
		System.out.println("Movie Review Positive Review Detection Accuracy: " + movieAccuracy);
		
		spamAccuracy = tr.testSpamDetectionAccuracy(true);
		movieAccuracy = tr.testMovieReviewDetectionAccuracy(true);
		System.out.println("Testing Bernoulli Bayes...");
		System.out.println("Email Spam Detection Accuracy: " + spamAccuracy);
		System.out.println("Movie Review Positive Review Detection Accuracy: " + movieAccuracy);
		
		//generate the text files for the word cloud generator to use
		tr.testIntermediateWordCloudFileGenerator();
		
		String intermediateFlaggedFilename, intermediateUnflaggedFilename, dstImgFilename;

		//generate the word cloud comparing the spam and valid email training data
		intermediateFlaggedFilename = "src/main/resources/wordMapData/emailTrainingDataForMap_spam.txt";
		intermediateUnflaggedFilename = "src/main/resources/wordMapData/emailTrainingDataForMap_valid.txt";
		dstImgFilename = "src/main/resources/wordMapData/polar_Spam_vs_Valid.png";
		//tr.testWordCloudGenerator(intermediateFlaggedFilename, intermediateUnflaggedFilename, dstImgFilename);
		
		
		//generate the word cloud comparing the positive and negative review training data
		intermediateFlaggedFilename = "src/main/resources/wordMapData/movieReviewTrainingDataForMap_positive.txt";
		intermediateUnflaggedFilename = "src/main/resources/wordMapData/movieReviewTrainingDataForMap_negative.txt";
		dstImgFilename = "src/main/resources/wordMapData/polar_Positive_vs_Negative.png";
		//tr.testWordCloudGenerator(intermediateFlaggedFilename, intermediateUnflaggedFilename, dstImgFilename);
		
	}


}
