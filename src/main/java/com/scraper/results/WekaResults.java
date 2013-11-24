package com.scraper.results;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.KStar;
import weka.classifiers.misc.VFI;
import weka.classifiers.rules.NNge;
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaResults {

	//	public AsrrayList<String> abbrevLine = new ArrayList<String>();
	private static final String CSV = ".csv";
	private static final String ARFF = ".arff";
	public static void main(String[] args) throws Exception {
		WekaResults wekaResults = new WekaResults();
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
		String todayString = format.format(now.getTime()); 
		String testPath = "/Users/tchap/Dropbox/betting/WekaTestLines/WekaPtTest" + todayString;
		BufferedReader reader = new BufferedReader(new FileReader(testPath + CSV));
		HashMap<String,Double> classAcc = new HashMap<String, Double>();
		ArrayList<WekaLine> lines = wekaResults.setAbbrevLine(reader);
		reader.close();
		reader = new BufferedReader(new FileReader(testPath + CSV));
		BufferedWriter arfOut = new BufferedWriter(new FileWriter(testPath + ARFF));
		wekaResults.createArrf(reader, arfOut);
		reader.close();
		arfOut.close();
		DataSource trainSource = new DataSource("/Users/tchap/Dropbox/betting/wekaPtLines.arff");

		DataSource testSource = new DataSource(testPath + ARFF);

		Instances train = trainSource.getDataSet();  
		Instances test = testSource.getDataSet();   
		if (train.classIndex() == -1)
			train.setClassIndex(train.numAttributes() - 1);
		if (test.classIndex() == -1)
			test.setClassIndex(test.numAttributes() - 1);
													
		// train classifier
		//RndForest
		Classifier cl = new RandomForest();
		String className = "RandomForest";
		String[] forOpt = new String[6];
		forOpt[0] = "-I";
		forOpt[1] = "10"; 
		forOpt[2] = "-K"; 
		forOpt[3] = "0";
		forOpt[4] = "-S";
		forOpt[5] = "1";
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, forOpt, train));
		wekaResults.setPrediction(cl, train, test, forOpt, className, lines);

		//IB1
		cl = new IB1();
		className = "IB1";
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, train));
		wekaResults.setPrediction(cl, train, test, className, lines);
		//Simple cart
		cl = new SimpleCart();
		className = "SimpleCart";
		String[] simpOpt = new String[8];
		simpOpt[0] = "-S";
		simpOpt[1] = "1"; 
		simpOpt[2] = "-M"; 
		simpOpt[3] = "2.0";
		simpOpt[4] = "-N";
		simpOpt[5] = "5";
		simpOpt[6] = "-C";
		simpOpt[7] = "1.0";
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, simpOpt, train));
		wekaResults.setPrediction(cl, train, test, simpOpt, className, lines);
		//KStar
		cl = new KStar();
		className = "KStar";
		String[] kOpt = new String[4];
		kOpt[0] = "-B";
		kOpt[1] = "20"; 
		kOpt[2] = "-M"; 
		kOpt[3] = "a"; 
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, kOpt, train));
		wekaResults.setPrediction(cl, train, test, kOpt, className, lines);

		//BFTree
		cl = new BFTree();
		className = "BFTree";
		String[] bftOpt = new String[10];
		bftOpt[0] = "-S";
		bftOpt[1] = "1"; 
		bftOpt[2] = "-M"; 
		bftOpt[3] = "2";
		bftOpt[4] = "-N";
		bftOpt[5] = "5";
		bftOpt[6] = "-C";
		bftOpt[7] = "1.0";
		bftOpt[8] = "-P";
		bftOpt[9] = "POSTPRUNED";
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, bftOpt, train));
		wekaResults.setPrediction(cl, train, test, bftOpt, className, lines);

		//MultiLayer Percept
		cl = new MultilayerPerceptron();
		className = "MultiPercept";
		String[] multOpt = new String[14];
		multOpt[0] = "-L";
		multOpt[1] = "0.3"; 
		multOpt[2] = "-M"; 
		multOpt[3] = "0.2";
		multOpt[4] = "-N";
		multOpt[5] = "500";
		multOpt[6] = "-V";
		multOpt[7] = "0";
		multOpt[8] = "-S";
		multOpt[9] = "0";
		multOpt[10] = "-E";
		multOpt[11] = "20";
		multOpt[12] = "-H";
		multOpt[13] = "a";
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, multOpt, train));
		wekaResults.setPrediction(cl, train, test, multOpt, className, lines);
		
		//NNge
		cl = new NNge();
		className = "NNge";
		String[] nNgeOpt = new String[4];
		nNgeOpt[0] = "-G";
		nNgeOpt[1] = "5"; 
		nNgeOpt[2] = "-I"; 
		nNgeOpt[3] = "5";
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, nNgeOpt, train));
		wekaResults.setPrediction(cl, train, test, nNgeOpt, className, lines);
		
		//Logistic
		cl = new Logistic();
		className = "Logistic";
		System.out.println(className);
		classAcc.put(className, wekaResults.evaluateClass(cl, train));
		wekaResults.setPrediction(cl, train, test, className, lines); 

		//VFI
		cl = new VFI();
		className = "VFI";
		String[] vOpt = new String[2];
		vOpt[0] = "-B";
		vOpt[1] = "0.6"; 
		System.out.println("\n" +className);
		classAcc.put(className, wekaResults.evaluateClass(cl, vOpt, train));
		wekaResults.setPrediction(cl, train, test, vOpt, className, lines);

		//NeiveBayes
		cl = new NaiveBayesUpdateable();
		className = "NaiveBayes";
		System.out.println("\n" + className);
		classAcc.put(className, wekaResults.evaluateClass(cl, train));
		wekaResults.setPrediction(cl,train, test, className, lines);

		//write to file
		String outPut1 = "/Users/tchap/Dropbox/betting/ClassificationResults/ClassRes" + todayString + ".csv";
		BufferedWriter out = new BufferedWriter(new FileWriter(outPut1));
		
		out.write(WekaResults.getTitleLine() + "\n");
		for(WekaLine compLine : lines){
			compLine.setCumPred(classAcc);
		}
		Collections.sort(lines,new Comparator<WekaLine>() {
			public int compare(WekaLine a, WekaLine b) {
				return Math.abs(a.getCumPred())>Math.abs(b.getCumPred()) ? -1 : 1;
			}
		});
		for(WekaLine compLine : lines){
			out.write(compLine.print() + "\n");
		}
		out.write("\n\nClassifier,PctCorr\n");
		Iterator<String> it = classAcc.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			System.out.println(key + ": " + classAcc.get(key));
			out.write(key + "," + classAcc.get(key) + "\n");
		}
		out.close();
	}
	
	
	
	private void createArrf(BufferedReader reader, BufferedWriter out) throws IOException {

		String line = "";
		String header = "@relation overUnder\n" + 
				"\n" + 
				"@attribute seasonPPG   real\n" + 
				"@attribute seasonReb  real\n" + 
				"@attribute seasonAss  real\n" + 
				"@attribute careerPPG  real\n" + 
				"@attribute careerReb  real\n" + 
				"@attribute careerAss  real\n" + 
				"@attribute oppPts  real\n" + 
				"@attribute oppReb  real\n" + 
				"@attribute oppAss  real\n" + 
				"@attribute oppPtsAllow  real\n" + 
				"@attribute 5gameAve	real\n" + 
				"@attribute prevNight	real\n" + 
				"@attribute overUnder   real\n" + 
				"@attribute bookName	{SB, BU}\n" + 
				"@attribute vig    real\n" + 
				"@attribute predicted           {-1,1}\n" + 
				"@attribute distFromSpread   real\n" + 
				"@attribute 5gameStdDev    real\n" + 
				"@attribute b2b  			{TRUE, FALSE}\n" + 
				"@attribute isHome			{TRUE, FALSE}\n" + 
				"@attribute result  	{1,-1}\n" + 
				"\n" + 
				"@data\n";
		out.write(header);
		while ((line = reader.readLine()) != null){
			out.write(line.substring(line.indexOf(",")+1) + "\n");
		}
		
	}



	private static String getTitleLine() {
		return "name,line,book,vig,sum,RForPred,IB1,SimpleCart,KStarPred,BFTree,MultiPercept,NNg,LogPred,VFI,BayesPred";
	}
	private void setPrediction(Classifier cl, Instances train, Instances test, String[] ops, String className, ArrayList<WekaLine> lines) throws Exception {
		cl.setOptions(ops); 
		setPrediction(cl, train, test, className, lines);
	}
	private void setPrediction(Classifier cl, Instances train, Instances test, String className, ArrayList<WekaLine> lines) throws Exception {
		cl.buildClassifier(train);
		// evaluate classifier and print some statistics
		Evaluation evaluator = new Evaluation(train);
		evaluator.evaluateModel(cl, test);

		for (int i = 0; i < test.numInstances(); i++) {
			double pred = cl.classifyInstance(test.instance(i));
			double[] dist = cl.distributionForInstance(test.instance(i));
			String prediction = test.classAttribute().value((int) pred);
			//			System.out.print("ID: " + test.instance(i).value(0));
			//			this.abbrevLine.set(i, this.abbrevLine.get(i) + "," + prediction + ","
			//					+ dist[(int) pred]);
			lines.get(i).setOneClassifierValue(className, Integer.parseInt(prediction)*dist[(int) pred]);
		}

	}

	private ArrayList<WekaLine> setAbbrevLine(BufferedReader reader) throws IOException {
		String line;
		ArrayList<WekaLine> lines = new ArrayList<WekaLine>();
//		while ((line = reader.readLine()) != null && !line.equals("@data")) {
//		}

		while ((line = reader.readLine()) != null){

			String[] lineAttr = line.split(",");
//			WekaLine tempWekLine = new WekaLine(lineAttr[0], lineAttr[11], lineAttr[12],
//					lineAttr[15], lineAttr[16], lineAttr[17]);
			WekaLine tempWekLine = new WekaLine(lineAttr[0], lineAttr[13],
					lineAttr[14], lineAttr[15]);
			lines.add(tempWekLine);

		}
		return lines;
	}

	public double evaluateClass(Classifier cl, String[] options, Instances train) throws Exception{
		cl.setOptions(options); 
		return(evaluateClass(cl, train));

	}

	public double evaluateClass(Classifier cl, Instances train) throws Exception{
		Evaluation evaluator = new Evaluation(train);
		cl.buildClassifier(train);
		evaluator.crossValidateModel(cl, train, 10, new Random(1));
		System.out.println(evaluator.toSummaryString("Results\n======\n", false));
		System.out.println("percentage correct " + evaluator.pctCorrect());
		return evaluator.pctCorrect();
	}



}
