package com.scraper.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

import com.scraper.results.WekaLine;
import com.scraper.stats.FinalLine;
import com.scraper.stats.GameStats;
import com.scraper.stats.PlayerStats;
import com.scraper.stats.TeamStats;

public class LineUtil {
	private HashMap<String, String> playerSBLines = new HashMap<String, String>();
	private HashMap<String, String> playerBULines = new HashMap<String, String>(); 
	private HashMap<String, Integer> playerCode = new HashMap<String, Integer>(); 
	private HashMap<String, PlayerStats> playerStatistics = new HashMap<String, PlayerStats>(); 
	PriorityQueue<FinalLine> lineList = new PriorityQueue<FinalLine>();

	public void getAllPlayerStats(HashMap<String, String> playerLines) throws IOException {
		Set<String> kSet = playerLines.keySet();
		Iterator<String> itrPlayers = kSet.iterator();
		String player;
		URL url;
		while(itrPlayers.hasNext()){
			player = (String)itrPlayers.next();
			url = new URL("http://espn.go.com/nba/player/_/id/" + playerStatistics.get(player).getLookupCode()); 
			getPlayerStats(url, player);
			//			System.out.println(pageString);
		}
	}

	public void setPlayerLines(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		String encoding = conn.getContentEncoding();
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
		try {
			String line;
			Boolean player = false;
			String playerName = null;
			while ((line = br.readLine()) != null) {
				if(line.contains("<td valign=\"top\" style=\"white-space:nowrap\">")){
					player = true;
				}else if (player){
					playerName = setPlayer(line);
					player = false;
				}else if (playerStatistics.containsKey(playerName) && line.equals("                <td align=\"left\" class=\"item\" valign=\"top\">")){
					line = br.readLine();
					setOdds(playerName,line);
				}
			}
		} finally {
			br.close();
		}
	}

	public void setBUPlayerLines(URL betUsUrlSearch) throws IOException {
		ArrayList<URL> urlList = LineUtil.getBUurlList(betUsUrlSearch);

		for(URL betUsUrl : urlList){
			URLConnection conn = betUsUrl.openConnection();
			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				encoding = "ISO-8859-1";
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
			try {
				String line;
				String playerName = null;
				while ((line = br.readLine()) != null) {
					if(line.contains("txtContestDesc")){
						playerName = setBUPlayer(line, br);
					}
				}
			} finally {
				br.close();
			}		
		}
	}

	private static ArrayList<URL> getBUurlList(URL betUsUrlSearch) throws IOException {
		ArrayList<URL> betUsUrls = new ArrayList<URL>();
		URLConnection conn = betUsUrlSearch.openConnection();
		String encoding = conn.getContentEncoding();
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if(line.contains("basketball_props.aspx")){
					String[] value = line.split("href=\"");
					String urlName = value[1]; 
					if(urlName.contains("Leagues_ctl04")){
						continue;}
					urlName = urlName.substring(0,urlName.indexOf("\" id="));
					urlName = "https://www.betus.com.pa/sportsbook/" + urlName;
					betUsUrls.add(new URL(urlName));
				}
			}
		} finally {
			br.close();
		}				
		return betUsUrls;
	}

	private String setBUPlayer(String line, BufferedReader br) throws IOException {
		String[] value = line.split("value=\"");
		String[] rawLineArr = value[1].split(" ");
		String playerName = rawLineArr[0].charAt(0) +  "." + rawLineArr[1];
		String number = null,odds = null;
		int lineCount = 0;

		if(playerStatistics.containsKey(playerName)){
			String bet = getBet(rawLineArr,true);
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(lineCount==2){
					if(line.contains("Under")){
						return playerName;
					}
				}
				if(lineCount==4){
					value = line.split("value=\"");
					if(value.length<2){return null;}
					number = value[1];
					number = number.substring(0, number.indexOf("\""));
				}if(lineCount==7){
					value = line.split("value=\"");
					odds = value[1];
					odds = odds.substring(0, odds.indexOf("\""));
					break;
				}
			}
			bet =  odds + " " + bet + " " + number;
			if(playerBULines.containsKey(playerName)){
				bet+= " " + playerBULines.get(playerName);
			}
			playerBULines.put(playerName,bet);
		}
		return playerName;
	}
	private String getBet(String[] betArray, boolean isBU) {
		String bet = "";
		if(betArray[betArray.length-3].equals("+")){
			bet = betArray[betArray.length-4] + betArray[betArray.length-3] + betArray[betArray.length-2];
		}else{
			bet = betArray[betArray.length-2];
		}
		try{
			bet = bet.substring(0, bet.lastIndexOf("\""));
		}catch(Exception e){
			System.out.print("help");
		}
		bet = bet.toLowerCase();
		if(bet.contains("and")){
			bet = bet.replace("and", "+");
		}
		return bet;
	}

	public String setPlayer(String line) {
		StringTokenizer st = new StringTokenizer(line);
		String[] lineWords = line.split(" ");
		String playerName = null;
		String bet;
		String number = null;
//		while(st.hasMoreElements()){
//			playerName = st.nextToken();
//			if(playerName.equals("Deron")){
//				playerName+= " " + st.nextToken();
//			}
			playerName = lineWords[20];
			if(playerStatistics.containsKey(playerName)){
				bet = lineWords[21];
//				while(st.hasMoreElements()){
//					number = st.nextToken();
//				}
				number = lineWords[lineWords.length-1];
				if(number!=null){
					if(number.endsWith("Â½")){
						number = number.replace("Â½", ".5");
					}else if(number.endsWith("Â?")){
						number = number.replace("Â?", ".5");
					}
					bet += " " + number; 
					if(playerSBLines.containsKey(playerName)){
						bet += " " + playerSBLines.get(playerName);
					}
					playerSBLines.put(playerName,bet);
					System.out.println(playerName + " " + bet);
				}

			}
//		}
		return playerName;
	}

	public void setPrevNightPlayers(HashMap<String, ArrayList<FinalLine>> resultList) throws IOException {
		Set<String> kSet = resultList.keySet();
		Iterator<String> itResults = kSet.iterator();
		String player;
		URL url;
		while(itResults.hasNext()){
			player = (String)itResults.next();
			url = new URL("http://espn.go.com/nba/player/_/id/" + playerStatistics.get(player).getLookupCode()); 
			getPlayerStats(url, player);
			//			System.out.println(pageString);
		}
	}

	public int getGameStat(FinalLine tempFinal, GameStats indGameStats) {
		String bet = tempFinal.getBet();
		return getGameStat(bet, indGameStats);
	}

	public int getGameStat(WekaLine tempFinal, GameStats indGameStats) {
		String bet = tempFinal.getStat();
		return getGameStat(bet, indGameStats);
	}
	public int getGameStat(String bet, GameStats indGameStats){
		if(bet==null || bet.contentEquals("points")){
			return indGameStats.getPoints();
		}else if(bet.contentEquals("assists")){
			return indGameStats.getAssists();
		}else if(bet.contentEquals("rebounds")){
			return indGameStats.getRebounds();
		}else if(bet.contentEquals("points+assists")){
			return indGameStats.getPoints()+indGameStats.getAssists();
		}else if(bet.contentEquals("points+rebounds")){
			return indGameStats.getPoints()+indGameStats.getRebounds();
		}
		return -1;
	}

	public int getResult(FinalLine tempFinal, int actResult) {
		String bet = tempFinal.getBet();
		String line = tempFinal.getLine();
		return getResult(bet, line, actResult);
	}

	public int getResult(WekaLine tempFinal, int actResult) {
		String bet = tempFinal.getStat();
		String line = Double.toString(tempFinal.getLine());
		return getResult(bet, line, actResult);
	}

	public int getResult(String bet, String line, int actResult){
		if(actResult==0.0){
			return 0;
		}else{
			if(bet==null || bet.contentEquals("points")){
				if(actResult>Double.valueOf(line)){
					return 1;
				}else if(actResult<Double.valueOf(line)){
					return -1;
				}
			}else{
				if(bet.contentEquals("assists")){
					if(actResult>Double.valueOf(line)){
						return 1;
					}else if (actResult<Double.valueOf(line)){
						return -1;
					}
				}if(bet.contentEquals("rebounds")){
					if(actResult>Double.valueOf(line)){
						return 1;
					}else if (actResult<Double.valueOf(line)){
						return -1;
					}
				}if(bet.contentEquals("points+assists")){
					if(actResult>Double.valueOf(line)){
						return 1;
					}else if (actResult<Double.valueOf(line)){
						return -1;
					}
				}if(bet.contentEquals("points+rebounds")){
					if(actResult>Double.valueOf(line)){
						return 1;
					}else if (actResult<Double.valueOf(line)){
						return -1;
					}
				}
			}
		}
		return 0;
	}

	private void setOdds(String name, String line) {
		String odds;
		if(playerSBLines.containsKey(name)){
			line = line.trim();
			odds =line + " " + playerSBLines.get(name);
			playerSBLines.put(name, odds);
		}
	}

	public void getPlayerStats(URL url, String player) throws IOException {
		URLConnection conn = url.openConnection();
		String encoding = conn.getContentEncoding();
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
		String[] games;
		String[] teams;
		try {
			String line;
			while ((line = br.readLine()) != null) {
				//								System.out.println(line);
				if(line.contains("Last 5 Games")){
					//					System.out.println(line);
					//					games = line.split("team-name");
					games = line.split("team-46");
					line = line.substring(line.indexOf("Last 5 Games"));
					System.out.println(player + ": ");
					fillPlayerStats(line, player);
					fillGameLog(games,player);
				}
				else if(line.contains("team-away")){
					setTeamNames(line, player);
					//					playerStatistics.get(player).setTeam(teams[0]);
					//					playerStatistics.get(player).setOpp(teams[1]);
				}
			}

		} catch (IOException e){
			e.printStackTrace();
		} 
		finally {
			br.close();
		}
		return;
	}

	private String[] setTeamNames(String line, String player) {

		String[] teamNames = line.split("team/_/name");
		String[] teams = teamNames;
		for(int i=0;i<teamNames.length;i++){
			String[] tempCorLine = teamNames[i].split("/");
			teamNames[i] = tempCorLine[1];
		}
		playerStatistics.get(player).setTeam(teamNames[1]);
		if(teamNames[2].equals(teamNames[1])){
			playerStatistics.get(player).setIsHome(false);
			playerStatistics.get(player).setOppName(teamNames[3]);
		}else{
			playerStatistics.get(player).setIsHome(true);
			playerStatistics.get(player).setOppName(teamNames[2]);
		}
		//		line.substring(line.indexOf("_/name/"), endIndex)line.indexOf("_/name/");
		//		while(st1.hasMoreTokens()){
		//			teams[0] = st1.nextToken();
		//			if(teams.contains("espn.go.com/nba/team/_/name/")){
		//				st2 = new StringTokenizer(teams, "/");
		//				while(st2.hasMoreTokens()){
		//					teams = st2.nextToken();
		//					if(teams.equals("name")){
		//						teams = st2.nextToken();
		//						//						System.out.println(team);
		//						break;
		//					}
		//				}
		//				break;
		//			}
		//		}		
		return teams;
	}

	private void fillGameLog(String[] games, String player) {
		StringTokenizer st;
		String tempVal;
		int counter=0;
		PlayerStats tempPlayer = playerStatistics.get(player);	
		//		ArrayList<GameStats> tempGameLog = new ArrayList<GameStats>();
		GameStats tempGame = null;
		tempPlayer.setGlobalStats(games[0]);
		int currentGame = 0;
		for(int i=1; i<games.length; i++){
			st = new StringTokenizer(games[i], ">");
			while(st.hasMoreTokens()){
				tempVal = st.nextToken();
				//				try {
				if(!tempVal.contains("text-align:right")){
					counter++;
					//					System.out.println(tempVal + " " + counter);
					if(counter == 3){
						tempGame = new GameStats(parseEnd(tempVal));
					}else if(counter == 14){
						tempGame.setOpponent(parseEnd(tempVal));
					}else if(counter == 20){
						if(!(tempVal.contains("W") || tempVal.contains("L"))){
							break;
							//								currentGame = 2;
						}
					}
					else if(counter == 24-currentGame){
						tempGame.setMinutes(Integer.parseInt(parseEnd(tempVal)));
					}else if(counter == 31-currentGame){
						tempGame.setRebounds(Integer.parseInt(parseEnd(tempVal)));
					}else if(counter == 32-currentGame){
						tempGame.setAssists(Integer.parseInt(parseEnd(tempVal)));
					}else if(counter == 37-currentGame){
						tempGame.setPoints(Integer.parseInt(parseEnd(tempVal)));
						break;
					}
				}
			}
			tempPlayer.gameLog.add(tempGame);
			counter = 0;
		}
		tempPlayer.setStDev();
		playerStatistics.put(player, tempPlayer);
	}

	public static String parseEnd(String tempVal) {
		if(tempVal.endsWith("</a")){
			tempVal = tempVal.replace("</a", "");
		}else if(tempVal.endsWith("</td")){
			tempVal = tempVal.replace("</td", "");
		}
		return tempVal;
	}

	private void fillPlayerStats(String line, String player) {
		StringTokenizer st = new StringTokenizer(line, ">");
		String tempVal;
		int counter=0;
		PlayerStats tempPlayer = playerStatistics.get(player);
		while(st.hasMoreElements() && counter < 16){
			tempVal = st.nextToken();
			if(!tempVal.startsWith("<td style")){
				counter++;
				if(tempVal.endsWith("</td")){
					tempVal = tempVal.replace("</td", "");
				}
				if(counter==2){
					if(Float.parseFloat(tempVal)<20){
						break;
					}
				}
				if(counter==9){
					tempPlayer.setRebounds(Double.parseDouble(tempVal));
					System.out.println("Rebounds: " + Double.parseDouble(tempVal));
				}else if(counter==10){
					tempPlayer.setAssists(Double.parseDouble(tempVal));
					System.out.println("Assists: " + Double.parseDouble(tempVal));
				}else if(counter==15){
					tempPlayer.setPoints(Double.parseDouble(tempVal));
					System.out.println("Points: " + Double.parseDouble(tempVal));
				}

				//				System.out.println(tempVal + " " + counter);
			}
		}
		playerStatistics.put(player, tempPlayer);
		return;
	}



	public void getSplits(HashMap<String,String> playerLines, String bookName){
		Set<String> kSet = playerLines.keySet();
		Iterator<String> itrPlayers = kSet.iterator();
		String bet, line, odds;
		String player;
		FinalLine tempLine;
		while(itrPlayers.hasNext()){
			player = (String)itrPlayers.next();
			bet = playerLines.get(player);
			StringTokenizer st = new StringTokenizer(bet);
			while(st.hasMoreTokens()){
				odds = st.nextToken();
				if(odds.length()==3) { odds= "+" + odds;}

				System.out.println(bet);
				bet = st.nextToken();
				if(st.hasMoreElements() && (odds.startsWith("+") || odds.startsWith("-"))){
					line = st.nextToken();
					tempLine = compareLines(player, bet, line, odds, bookName);
					if(!(tempLine.getStDev() == 0.0)){
						lineList.add(tempLine);
					}
				}
			}
		}

	}
	public void addOldLines(BufferedReader reader) throws IOException {
		String line;
		FinalLine tempLine;
		String player;
		//		PriorityQueue<FinalLine>;
		while ((line = reader.readLine()) != null) {
			String[] lineArr = line.split(",");
			player = lineArr[0];
			if(player.equals("player")){
				continue;
			}
			tempLine = new FinalLine(lineArr, null);
			Boolean isCopy = false;
			for(FinalLine newLine : lineList){
				if(tempLine.isMostlySame(newLine)){
					isCopy = true; 
					break;
				}
			}
			if(!isCopy){
				lineList.add(tempLine);
			}

		}
		return;

	}

	public void writeFinalLines(BufferedWriter out, BufferedWriter wekaOut) throws IOException{
		FinalLine tempLine;
		out.write("player,team,opp,bet,average,previous,line,book,odds,ov/un," +
				"split,scaledSplit,stdDev,b2b,home\n");
		while(!lineList.isEmpty()){
			tempLine = lineList.remove();
			out.write(tempLine.printToCSV());
			TeamStats opponent = new TeamStats(tempLine.getOpponent());
			String player = tempLine.getPlayer();
			if(tempLine.getBet().equals("points")){
				wekaOut.write(player + "," + playerStatistics.get(player).printPlayerStats() + "," + 
						opponent.printStats() + "," + tempLine.printWekaTest() + ",?\n");
			}
			System.out.println(tempLine.print());
		}
	}


	private FinalLine compareLines(String player, String bet, String line, String odds, String bookName){
		FinalLine results = new FinalLine(player, bet, line, odds, bookName);
		PlayerStats playerStat = playerStatistics.get(player);
		ArrayList<GameStats> stats = playerStatistics.get(player).getGameLog();
		GameStats tempStat = new GameStats();
		if(stats.size()>0){
			tempStat = stats.get(0);
		}
		if(bet.contentEquals("points")){
			results.setBasicStats(playerStat.getPoints(),
					playerStat.getStDevPts(),tempStat.getPoints());
		}else if(bet.contentEquals("assists")){
			results.setBasicStats(playerStat.getAssists(),
					playerStat.getStDevAss(),tempStat.getAssists());
		}else if(bet.contentEquals("rebounds")){
			results.setBasicStats(playerStat.getRebounds(),
					playerStat.getStDevReb(),tempStat.getRebounds());
		}else if(bet.contentEquals("points+assists")){
			results.setBasicStats(playerStat.getPointsAssists(),
					playerStat.getStDevPtsAst(),tempStat.getPoints()+tempStat.getAssists());
		}else if(bet.contentEquals("points+rebounds")){
			results.setBasicStats(playerStat.getPointsRebounds(),
					playerStat.getStDevPtsReb(),tempStat.getPoints()+tempStat.getRebounds());
		}
		if(!(line.matches("first|over|series|MVP|game|block|steal|3-Pt|FT|attempt"))){
			results.setSplit(results.getAverages()-Double.parseDouble(line));
		}
		if(results.getSplit()<0){
			results.setOvrUnd("-1");
			//			String testOdd = results.getOdds();
			//			int oddVal = 0;
			//			if(results.getOdds().contains("+")){
			//				oddVal = -30 - Integer.parseInt(results.getOdds().substring(1));
			//			}else {
			//				oddVal = Integer.parseInt(results.getOdds());
			//				if(oddVal<=-105 && oddVal>-130){
			//					oddVal = -oddVal - 230;
			//				}else {
			//					oddVal = -oddVal - 30;
			//				}
			//			}
			//			results.setOdds(String.valueOf(oddVal));
		}else{
			results.setOvrUnd("1");
		}
		results.setScaledSplit(results.getSplit()/results.getAverages());
		results.setTeam(playerStat.getTeam());
		results.setOpponent(playerStat.getOppName());
		results.setIsHome(playerStat.getIsHome());
		results.setBack2Back(getBack2Back(player));

		return results;
	}

	private Boolean getBack2Back(String player) {
		if(playerStatistics.get(player).gameLog.size()==0){
			return false;
		}
		String dayOfPrevGame = playerStatistics.get(player).gameLog.get(0).getDay();
		String dayString;
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal1 = Calendar.getInstance();
		dfm.format(cal1.getTime());
		cal1.add(Calendar.DAY_OF_WEEK, -1);

		switch (cal1.get(Calendar.DAY_OF_WEEK)) {
		case 1:  dayString = "Sun";
		break;
		case 2:  dayString = "Mon";
		break;
		case 3:  dayString = "Tue";
		break;
		case 4:  dayString = "Wed";
		break;
		case 5:  dayString = "Thu";
		break;
		case 6:  dayString = "Fri";
		break;
		case 7:  dayString = "Sat";
		break;
		default: dayString = "Invalid day";
		break;
		}
		//		System.out.println(dayString + " " + dayOfPrevGame);
		if(dayOfPrevGame.startsWith(dayString)){
			return true;
		}else{
			return false;
		}
	}
	
	public void loadPlayers() {
		//atlanta
		playerStatistics.put("Z.Pachulia", new PlayerStats(2016));
		playerStatistics.put("J.Johnson", new PlayerStats(1007));
		playerStatistics.put("Joe Johnson", new PlayerStats(1007));
		playerStatistics.put("A.Horford", new PlayerStats(3213));
		playerStatistics.put("J.Teague", new PlayerStats(4015));
		//boston
		playerStatistics.put("P.Pierce", new PlayerStats(662));
		playerStatistics.put("R.Allen", new PlayerStats(9));
		playerStatistics.put("K.Garnett", new PlayerStats(261));
		playerStatistics.put("R.Rondo", new PlayerStats(3026));
		playerStatistics.put("J.Terry", new PlayerStats(841));
		playerStatistics.put("J.Green", new PlayerStats(3209));
		//charlotte
		playerStatistics.put("G.Henderson", new PlayerStats(3993));
		playerStatistics.put("C.Maggette", new PlayerStats(497));
		playerStatistics.put("K.Walker", new PlayerStats(6479));
		playerStatistics.put("DJ.Augustin", new PlayerStats(3415));
		//chicago
		playerStatistics.put("D.Rose", new PlayerStats(3456));
		playerStatistics.put("C.Boozer", new PlayerStats(1703));
		playerStatistics.put("J.Noah", new PlayerStats(3224));
		playerStatistics.put("L.Deng", new PlayerStats(2429));
		playerStatistics.put("R.Hamilton", new PlayerStats(294));
		//clippers
		playerStatistics.put("B.Griffin", new PlayerStats(3989));
		playerStatistics.put("C.Paul", new PlayerStats(2779));
		playerStatistics.put("C.Butler", new PlayerStats(1705));
		//cleveland
		playerStatistics.put("A.Jamison", new PlayerStats(385));
		playerStatistics.put("K.Irving", new PlayerStats(6442));
		playerStatistics.put("A.Parker", new PlayerStats(635));
		playerStatistics.put("A.Varejao", new PlayerStats(2419));
		playerStatistics.put("D.Waiters", new PlayerStats(6628));
		playerStatistics.put("A.Gee", new PlayerStats(4232));
		//dallas
		playerStatistics.put("D.Nowitzki", new PlayerStats(609));
		playerStatistics.put("D.Collison", new PlayerStats(3973)); //???
		playerStatistics.put("V.Carter", new PlayerStats(136));
		playerStatistics.put("E.Brand", new PlayerStats(91)); //???
		playerStatistics.put("S.Marion", new PlayerStats(510)); //???
		playerStatistics.put("O.Mayo", new PlayerStats(3450)); 
		//denver
		playerStatistics.put("T.Lawson", new PlayerStats(4000));
		playerStatistics.put("Nene", new PlayerStats(1713));
		playerStatistics.put("A.Afflalo", new PlayerStats(3187));
		playerStatistics.put("D.Gallinari", new PlayerStats(3428));
		playerStatistics.put("K.Faried", new PlayerStats(6433));
		//detroit
		playerStatistics.put("R.Stuckey", new PlayerStats(3235));
		playerStatistics.put("B.Knight", new PlayerStats(6448));
		playerStatistics.put("G.Monroe", new PlayerStats(4260));
		playerStatistics.put("T.Prince", new PlayerStats(1724));
		//golden state
		playerStatistics.put("D.Lee", new PlayerStats(2772));//???
		playerStatistics.put("S.Curry", new PlayerStats(3975));
		playerStatistics.put("K.Thompson", new PlayerStats(6475));
		//houston
		playerStatistics.put("K.Lowry", new PlayerStats(3012));
		playerStatistics.put("L.Scola", new PlayerStats(1781));
		playerStatistics.put("C.Lee", new PlayerStats(3445));
		playerStatistics.put("J.Harden", new PlayerStats(3992));
		playerStatistics.put("J.Lin", new PlayerStats(4299));
		playerStatistics.put("O.Asik", new PlayerStats(3414));
		playerStatistics.put("C.Parsons", new PlayerStats(6466));
		//indiana
		playerStatistics.put("R.Jefferson", new PlayerStats(1006));
		playerStatistics.put("A.Harrington", new PlayerStats(308));
		playerStatistics.put("D.Granger", new PlayerStats(2760));
		playerStatistics.put("D.West", new PlayerStats(2177));
		playerStatistics.put("R.Hibbert", new PlayerStats(3436));
		playerStatistics.put("P.George", new PlayerStats(4251));
		//lakers
		playerStatistics.put("K.Bryant", new PlayerStats(110));
		playerStatistics.put("S.Nash", new PlayerStats(592));
		playerStatistics.put("P.Gasol", new PlayerStats(996));
		playerStatistics.put("R.Sessions", new PlayerStats(3231));
		playerStatistics.put("D.Howard", new PlayerStats(2384));
		//memphis
		playerStatistics.put("M.Gasol", new PlayerStats(3206));
		playerStatistics.put("R.Gay", new PlayerStats(3005));
		playerStatistics.put("M.Conley", new PlayerStats(3195));
		playerStatistics.put("Z.Randolph", new PlayerStats(1017));
		playerStatistics.put("T.Allen", new PlayerStats(2367));
		//miami
		playerStatistics.put("L.James", new PlayerStats(1966));
		playerStatistics.put("C.Bosh", new PlayerStats(1977));
		playerStatistics.put("D.Wade", new PlayerStats(1987));
		playerStatistics.put("M.Chalmers", new PlayerStats(3419));
		playerStatistics.put("S.Battier", new PlayerStats(976));
		//milwaukee
		playerStatistics.put("B.Jennings", new PlayerStats(3997));
		playerStatistics.put("M.Ellis", new PlayerStats(2751));
		playerStatistics.put("D.Gooden", new PlayerStats(1711));
		playerStatistics.put("M.Dunleavy", new PlayerStats(1708));
		//minnesota
		playerStatistics.put("K.Love", new PlayerStats(3449));
		playerStatistics.put("L.Ridnour", new PlayerStats(1985));
		playerStatistics.put("N.Pekovic", new PlayerStats(3453));
		playerStatistics.put("A.Kirilenko", new PlayerStats(434));
		//new york
		playerStatistics.put("A.Stoudemire", new PlayerStats(1727));
		playerStatistics.put("C.Anthony", new PlayerStats(1975));
		playerStatistics.put("T.Chandler", new PlayerStats(984));
		playerStatistics.put("L.Fields", new PlayerStats(4274));
		playerStatistics.put("J.Smith", new PlayerStats(2444));
		//new jersey
		playerStatistics.put("Deron.Williams", new PlayerStats(2798));
		playerStatistics.put("D.Williams", new PlayerStats(2798));
		playerStatistics.put("B.Lopez", new PlayerStats(3448));
		playerStatistics.put("K.Humphries", new PlayerStats(2433));
		playerStatistics.put("G.Wallace", new PlayerStats(1026));
		//new orleans
		playerStatistics.put("J.Jack", new PlayerStats(2768));
		playerStatistics.put("E.Gordon", new PlayerStats(3431));
		playerStatistics.put("C.Kaman", new PlayerStats(1982));
		playerStatistics.put("G.Vasquez", new PlayerStats(4291));
		playerStatistics.put("A.Davis", new PlayerStats(6583));
		//oaklahoma
		playerStatistics.put("K.Durant", new PlayerStats(3202));
		playerStatistics.put("R.Westbrook", new PlayerStats(3468));
		playerStatistics.put("K.Martin", new PlayerStats(2394));
		playerStatistics.put("S.Ibaka", new PlayerStats(3439));
		playerStatistics.put("K.Perkins", new PlayerStats(2018));
		//orlando
		playerStatistics.put("G.Davis", new PlayerStats(3200));
		playerStatistics.put("JJ.Redick", new PlayerStats(3024));
		playerStatistics.put("R.Anderson", new PlayerStats(3412));
		playerStatistics.put("J.Nelson", new PlayerStats(2439));
		//pheonix 
		playerStatistics.put("M.Beasley", new PlayerStats(3418));
		playerStatistics.put("M.Gortat", new PlayerStats(2758));
		playerStatistics.put("G.Dragic", new PlayerStats(3423));
		//philly
		playerStatistics.put("A.Iguodala", new PlayerStats(2386));
		playerStatistics.put("E.Turner", new PlayerStats(4239));
		playerStatistics.put("J.Holiday", new PlayerStats(3995));
		playerStatistics.put("L.Williams", new PlayerStats(2799));
		playerStatistics.put("S.Hawes", new PlayerStats(3211));
		playerStatistics.put("T.Young", new PlayerStats(3244));
		playerStatistics.put("A.Bynum", new PlayerStats(2748));
		//portland
		playerStatistics.put("L.Aldridge", new PlayerStats(2983));
		playerStatistics.put("R.Felton", new PlayerStats(2753));
		playerStatistics.put("D.Lillard", new PlayerStats(6606));
		playerStatistics.put("N.Batum", new PlayerStats(3416));
		//sacramento
		playerStatistics.put("D.Cousins", new PlayerStats(4258));
		playerStatistics.put("T.Evans", new PlayerStats(3983));
		playerStatistics.put("M.Thornton", new PlayerStats(4017)); //???
		//san antonio
		playerStatistics.put("T.Duncan", new PlayerStats(215));
		playerStatistics.put("T.Parker", new PlayerStats(1015));
		playerStatistics.put("M.Ginobili", new PlayerStats(272));
		playerStatistics.put("K.Leonard", new PlayerStats(6450));
		playerStatistics.put("G.Neal", new PlayerStats(4300));
		playerStatistics.put("D.Green", new PlayerStats(3988));
		playerStatistics.put("T.Splitter", new PlayerStats(3233));
		//toronto
		playerStatistics.put("D.DeRozan", new PlayerStats(3978));
		playerStatistics.put("A.Bargnani", new PlayerStats(2987)); //???
		playerStatistics.put("J.Calderon", new PlayerStats(2806));
		//utah
		playerStatistics.put("A.Jefferson", new PlayerStats(2389));
		playerStatistics.put("P.Millsap", new PlayerStats(3015));
		playerStatistics.put("D.Harris", new PlayerStats(2382));
		playerStatistics.put("M.Williams", new PlayerStats(2178));
		//washington
		playerStatistics.put("J.Wall", new PlayerStats(4237));
		playerStatistics.put("N.Young", new PlayerStats(3243));
		playerStatistics.put("E.Okafor", new PlayerStats(2399));
		playerStatistics.put("T.Ariza", new PlayerStats(2426));
		playerStatistics.put("K.Seraphin", new PlayerStats(4289));
		playerStatistics.put("B.Beal", new PlayerStats(6580));
		playerStatistics.put("N.Hilario", new PlayerStats(1713));
		playerStatistics.put("J.Crawford", new PlayerStats(4243));

	}

	public HashMap<String, String> getSBPlayerLines() {
		return playerSBLines;
	}

	public HashMap<String, String> getBUPlayerLines() {
		return playerBULines;
	}
	public void setPlayerLine(HashMap<String, String> playerLine) {
		this.playerSBLines = playerLine;
	}

	public HashMap<String, Integer> getPlayerCode() {
		return playerCode;
	}

	public void setPlayerCode(HashMap<String, Integer> playerCode) {
		this.playerCode = playerCode;
	}

	public HashMap<String, PlayerStats> getPlayerStatistics() {
		return playerStatistics;
	}

	public void setPlayerStatistics(
			HashMap<String, PlayerStats> playerStatistics) {
		this.playerStatistics = playerStatistics;
	}

	public String getPlayersForSQL() {
		Iterator<String> it = playerStatistics.keySet().iterator(); 
		String query = "INSERT into players (id, name) values ";//1007,'J.Johnson'),(3213, 'A.Horford')
				
		while(it.hasNext()){
			String name = it.next();
			int code = playerStatistics.get(name).getLookupCode();
			query+= "("+ code + " ,'" + name + "'),";
			
		}
		return query;
	}


	
	
	
}
