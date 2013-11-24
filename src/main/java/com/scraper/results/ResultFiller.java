package com.scraper.results;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.scraper.stats.FinalLine;
import com.scraper.stats.GameStats;
import com.scraper.stats.PlayerStats;
import com.scraper.stats.TeamStats;
import com.scraper.util.LineUtil;




public class ResultFiller {

	private HashMap<String,ArrayList<FinalLine>> resultList = new HashMap<String,ArrayList<FinalLine>>();
	private HashMap<String,ArrayList<WekaLine>> wekaResultList = new HashMap<String,ArrayList<WekaLine>>();
	private HashMap<String, String> classifierAcc = new HashMap<String, String>();

	public static void main(String[] args) throws IOException {
		Calendar yesterday = Calendar.getInstance();
		ResultFiller theFiller = new ResultFiller();
		int gamesBack = 0;
		yesterday.add(Calendar.DATE, (-1-gamesBack));
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
		String yestString = dateFormat.format(yesterday.getTime()); 
		String input = "/Users/tchap/Dropbox/betting/completeLines/Lines" + yestString + ".csv";
		String output = "/Users/tchap/Dropbox/betting/totalLines.csv";
		String wekaIn = "/Users/tchap/Dropbox/betting/ClassificationResults/ClassRes" + dateFormat.format(yesterday.getTime()) + ".csv";
		System.out.println(input);
		LineUtil lines = new LineUtil();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(input));
			theFiller.fillResults(reader, yestString);
			lines.loadPlayers();
			lines.setPrevNightPlayers(theFiller.resultList);
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("No previous days lines");
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(wekaIn));
			theFiller.fillWekaResults(reader, yestString);
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("No previous weka");
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(output,true));
		theFiller.writeResults(lines, writer, theFiller.resultList, gamesBack);
		writer = new BufferedWriter(new FileWriter(wekaIn,false));

		theFiller.writePrevWekaResults(lines, writer, theFiller.wekaResultList, theFiller.classifierAcc, gamesBack);
		String wekaOut = "/Users/tchap/Dropbox/betting/wekaTotLines.arff";
		BufferedWriter wekaWriter = new BufferedWriter(new FileWriter(wekaOut,true));
		String wekaPtsOut = "/Users/tchap/Dropbox/betting/wekaPtLines.arff";
		BufferedWriter wekaPtsWriter = new BufferedWriter(new FileWriter(wekaPtsOut,true));
		theFiller.writeWekaResults(lines, wekaWriter, wekaPtsWriter, theFiller.resultList);


	}



	public void writeResults(LineUtil lines, BufferedWriter writer,
			HashMap<String, ArrayList<FinalLine>> resultList, int gamesBack) throws IOException {
		HashMap<String, PlayerStats> playerStatistics = lines.getPlayerStatistics();
		Set<String> kSet = resultList.keySet();
		Iterator<String> itResults = kSet.iterator();
		String player;
		ArrayList<FinalLine> individualLines;
		FinalLine tempFinal;
		GameStats indGameStats;
		int result;
		int actStat;
		TeamStats opponent;
		//		writer.write("player,team,bet,average,line,odds,ov/un,result,gameStat,split,scaledSplit,stdDev,b2b\n");
//		writer.write("\n");
		while(itResults.hasNext()){
			player = (String)itResults.next();
			individualLines = resultList.get(player);
			indGameStats = playerStatistics.get(player).getSpecificGameStats(gamesBack);
			for(int i=0; i<individualLines.size(); i++){
				tempFinal = individualLines.get(i);
//				tempFinal.setOpponent(indGameStats.getOpponent());
				opponent = new TeamStats(tempFinal.getOpponent());
				actStat = lines.getGameStat(tempFinal, indGameStats);
				result = lines.getResult(tempFinal, actStat);
				if(result!=0){
					writer.write(tempFinal.printResult(result, actStat) + "," + playerStatistics.get(player).printPlayerStats() + "," + opponent.printStats() + "\n");
				}
			}
		}
		writer.close();
	}


	private void writePrevWekaResults(LineUtil lines, BufferedWriter writer,
			HashMap<String, ArrayList<WekaLine>> wekaResults, HashMap<String,String> classAcc, int gamesBack) throws IOException {
		HashMap<String, PlayerStats> playerStatistics = lines.getPlayerStatistics();
		Set<String> kSet = resultList.keySet();
		Iterator<String> itResults = kSet.iterator();
		String player;
		ArrayList<WekaLine> individualLines;
		GameStats indGameStats;
		WekaLine tempFinal;
		int actStat;
		int result;
		writer.write("name,line,book,vig,sum,correct,actval\n");

		while(itResults.hasNext()){
			player = (String)itResults.next();
			individualLines = wekaResults.get(player);
			indGameStats = playerStatistics.get(player).getSpecificGameStats(gamesBack);
			if(!(individualLines==null)){
				for(int i=0; i<individualLines.size(); i++){
					tempFinal = individualLines.get(i);
					actStat = lines.getGameStat(tempFinal, indGameStats);
					result = lines.getResult(tempFinal, actStat);
					if(result!=0){
						writer.write(tempFinal.printResult(result, actStat) + "\n");
					}
				}
			}
		}
		int cumResults = 0;
		int totalResults = 0;
		double perCorr = 0.0;
		itResults = kSet.iterator();
		while(itResults.hasNext()){
			player = (String)itResults.next();
			individualLines = wekaResults.get(player);
			if(!(individualLines==null)){
				for(int i=0; i<individualLines.size(); i++){
					tempFinal = individualLines.get(i);
					if(tempFinal.getIsCorrect()>0){
						cumResults++;
					}
					totalResults++;
				}
			}

		}
		perCorr = (double)cumResults/totalResults;
		writer.write(",,,,," + cumResults + "," + totalResults + "," +(double)cumResults/totalResults + "\n");
		writer.write("\nClassifier,PctCorr\n");
		for(String classifier : classAcc.keySet()){
			writer.write(classifier + "," + classAcc.get(classifier) + "\n");
		}
		writer.close();
	}

	public void writeWekaResults(LineUtil lines, BufferedWriter wekaWriter,
			BufferedWriter wekaPtsWriter, HashMap<String, ArrayList<FinalLine>> resultList) throws IOException {
		HashMap<String, PlayerStats> playerStatistics = lines.getPlayerStatistics();
		Set<String> kSet = resultList.keySet();
		Iterator<String> itResults = kSet.iterator();
		String player;
		ArrayList<FinalLine> individualLines;
		FinalLine tempFinal;
		GameStats indGameStats;
		int result;
		int actStat;
		TeamStats opponent;
		wekaWriter.write("\n");
		while(itResults.hasNext()){
			player = (String)itResults.next();
			individualLines = resultList.get(player);
			indGameStats = playerStatistics.get(player).getSpecificGameStats(0);
			for(int i=0; i<individualLines.size(); i++){
				tempFinal = individualLines.get(i);
				tempFinal.setOpponent(indGameStats.getOpponent());
				opponent = new TeamStats(indGameStats.getOpponent());
				actStat = lines.getGameStat(tempFinal, indGameStats);
				result = lines.getResult(tempFinal, actStat);
				if(result!=0){
					wekaWriter.write(player + "," + playerStatistics.get(player).printPlayerStats() 
							+ "," + opponent.printStats() + "," + tempFinal.printWekaResult(result) + "\n");
					if(tempFinal.getBet().equals("points")){
						wekaPtsWriter.write(playerStatistics.get(player).printPlayerStats() 
								+ "," + opponent.printStats() + "," + tempFinal.printWekaTest() + "," + result + "\n");
					}
				}
			}
		}
		wekaWriter.close();
		wekaPtsWriter.close();

	}

	private void fillResults(BufferedReader reader, String yestString) throws IOException {
		String prevLine;
		FinalLine tempLine;
		StringTokenizer st;
		String player;
		ArrayList<FinalLine> tempList = new ArrayList<FinalLine>();
		while ((prevLine = reader.readLine()) != null) {
			String[] yestArr = prevLine.split(",");
//			st = new StringTokenizer(prevLine, ",");
			player = yestArr[0];
			if(player.equals("player")){
				continue;
			}
			tempLine = new FinalLine(yestArr, yestString);
			if(this.resultList.containsKey(player)){
				tempList = this.resultList.get(player);
			}
			tempList.add(tempLine);
			this.resultList.put(player, tempList);
			tempList = new ArrayList<FinalLine>();
		}		
	}


	private void fillWekaResults(BufferedReader reader, String yestString) throws IOException {
		String prevLine;
		WekaLine tempLine;
		StringTokenizer st;
		String player;
		String classifier;
		ArrayList<WekaLine> tempList = new ArrayList<WekaLine>();
		while ((prevLine = reader.readLine()) != null) {
			st = new StringTokenizer(prevLine, ",");
			if(st.countTokens()>2){
				player = st.nextToken();
				if(player.equals("name")){
					continue;
				}
				tempLine = new WekaLine(prevLine, yestString);
				if(this.wekaResultList.containsKey(player)){
					tempList = this.wekaResultList.get(player);
				}
				tempList.add(tempLine);
				this.wekaResultList.put(player, tempList);
				tempList = new ArrayList<WekaLine>();
			}else if(st.countTokens()==2){
				classifier = st.nextToken();
				if(classifier.equals("Classifier")){
					continue;
				}
				classifierAcc.put(classifier, st.nextToken());
			}
		}			
	}

}
