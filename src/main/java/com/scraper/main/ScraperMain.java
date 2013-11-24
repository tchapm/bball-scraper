package com.scraper.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.scraper.util.LineUtil;



public class ScraperMain {

	public static void main(String[] args) throws IOException {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
		String outPut1 = "/Users/tchap/Dropbox/betting/completeLines/Lines" + format.format(now) + ".csv";
		String outPut2 = "/Users/tchap/Dropbox/betting/Lines" + format.format(now) + "/WeekendMorn.csv";

		URL url = new URL("http://www.sportbet.com/print/basketball_props");
		System.out.println("Output: " + outPut1);
		LineUtil lines = new LineUtil();
		lines.loadPlayers();
		//sql stuff
//		String sqlPlayerInsert = lines.getPlayersForSQL();
		
		
//		String testPlayer = "D.West +110 points over 14";
//		lines.setPlayer(testPlayer);
//		testPlayer = "N.Young points over 20 -105";
//		lines.setPlayer(testPlayer);

		
		lines.setPlayerLines(url);
		URL betUsUrlSearch = new URL("https://www.betus.com.pa/sportsbook/basketball-lines.aspx");
		String wekaTest = "/Users/tchap/Dropbox/betting/WekaTestLines/WekaPtTest" + format.format(now) + ".csv";
		lines.setBUPlayerLines(betUsUrlSearch);
//		System.out.println(pageString);

		lines.getAllPlayerStats(lines.getSBPlayerLines());
		lines.getAllPlayerStats(lines.getBUPlayerLines());
		
		lines.getSplits(lines.getSBPlayerLines(), "SB");
		lines.getSplits(lines.getBUPlayerLines(), "BU");
		
		Calendar rightNow = Calendar.getInstance();
		File f = new File(outPut1);
		if(f.exists()){
			BufferedReader reader = new BufferedReader(new FileReader(outPut1));
			lines.addOldLines(reader);
			reader.close();
		}
		if(rightNow.get(Calendar.HOUR_OF_DAY)<16){
			BufferedWriter out = new BufferedWriter(new FileWriter(outPut1));
			BufferedWriter wekaOut = new BufferedWriter(new FileWriter(wekaTest));
			lines.writeFinalLines(out, wekaOut);
			wekaOut.close();
			out.close();
		}else {
			BufferedWriter out = new BufferedWriter(new FileWriter(outPut1));
			BufferedWriter wekaOut = new BufferedWriter(new FileWriter(wekaTest));
			lines.writeFinalLines(out, wekaOut);
			wekaOut.close();
			out.close();
		}
//		}else{
//			outPut2 = "/Users/tchap/Dropbox/betting/Lines" + format.format(now) + "2.csv";
//			BufferedWriter out = new BufferedWriter(new FileWriter(outPut2));
//			lines.writeFinalLines(out);
//			out.close();
//		}
//		if(rightNow.get(Calendar.HOUR_OF_DAY)<12 && (rightNow.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
//				|| rightNow.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
//			BufferedWriter out = new BufferedWriter(new FileWriter(outPut2));
//			lines.writeFinalLines(out);
//			out.close();
////			System.out.println(rightNow.HOUR_OF_DAY + " " + rightNow.get(rightNow.DAY_OF_WEEK) + " " + rightNow.SUNDAY);
//		}
		
		

		//			  System.out.println(pageString);

	}


}
