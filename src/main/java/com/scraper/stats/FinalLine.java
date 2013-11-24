package com.scraper.stats;


public class FinalLine implements Comparable<FinalLine> {
	private String player;
	private String bet;
	private String line;
	private String team;
	private String odds;
	private Double split=0.0;
	private Double scaledSplit = 0.0;
	private Double averages = 0.0;
	private int prevGame = 0;
	private Double stDev = 0.0;
	private Boolean isBack2Back = false;
	private Boolean isHome;
	private String ovrUnd;
	private String theDate;
	private String opponent;
	private String bookName;

//	public FinalLine(String player2, String bet2, String line2, String odds){
//		this.player = player2;
//		this.bet = bet2;
//		this.line = line2;
//		this.odds = odds;
//	}
	
	public FinalLine(String player, String bet, String line, String odds,
			String bookName) {
		this.player = player;
		this.bet = bet;
		this.line = line;
		this.odds = odds;
		this.bookName = bookName;
	}

	public FinalLine(String[] yestArr, String yestString) {
//		player	team	bet	average	line	odds	ov/un	split	scaledSplit	stdDev	b2b
//		StringTokenizer st = new StringTokenizer(yestArr, ",");
		this.player = yestArr[0];
		this.team = yestArr[1];
		this.opponent = yestArr[2];
		this.bet = yestArr[3];
		this.averages = Double.valueOf(yestArr[4]);
		this.prevGame = Integer.valueOf(yestArr[5]);
		this.line = yestArr[6];
		this.bookName = yestArr[7];
		this.odds = yestArr[8];
		this.ovrUnd = yestArr[9];	
		this.split = Double.valueOf(yestArr[10]);
		this.scaledSplit = Double.valueOf(yestArr[11]);
		this.stDev = Double.valueOf(yestArr[12]);
		this.isBack2Back = Boolean.valueOf(yestArr[13]);
		this.isHome = Boolean.valueOf(yestArr[14]);
		this.theDate = yestString;
	}

	public String printToCSV(){
		return this.player + "," + this.team +  "," + this.opponent + "," + this.bet + "," + this.averages + "," + this.prevGame + "," + this.line + "," + this.bookName + "," + this.odds +"," 
				+ this.ovrUnd + "," + this.split.toString() + "," + this.scaledSplit + "," + this.stDev + "," + this.isBack2Back + "," + this.isHome + "\n";
	}
	
	public String print(){
		return this.player + ", " + this.team + " bet: " + this.bet + " average: " + this.averages + " prevGame: " + this.prevGame + " line: " + this.line + " : " + "bookName: " + this.bookName + " odds: " + this.odds
				+ " over/under: " + ovrUnd + " split: " + this.split.toString() + " scaledSplit: " + this.scaledSplit + " StdDev: " + this.stDev + " b2b: " + this.isBack2Back.toString();
	}
	
	public String printResult(int result, int actStat) {
		return this.player + "," + this.team.toUpperCase() +  "," + this.bet + "," + this.averages + "," + this.prevGame + "," + this.line + "," + this.bookName + "," + this.odds +"," 
				+ this.ovrUnd + "," + result + "," + actStat + ","  + this.split.toString() + "," + this.scaledSplit + "," + this.stDev + 
				"," + this.isBack2Back + "," + this.isHome + "," + this.opponent + ","+ this.theDate;		
	}
	
	public String printWekaResult(int result) {
		return  this.bet +  "," + this.team.toUpperCase() + "," + this.averages + "," + this.prevGame + "," + this.line + "," + this.bookName + "," + this.odds +"," 
				+ this.ovrUnd  + ","  + this.split.toString() + "," + this.scaledSplit + "," + this.stDev + 
				"," + this.isBack2Back.toString().toUpperCase() + "," + this.isHome.toString().toUpperCase() + "," + this.opponent.toUpperCase() + "," + result;		
	}
	public String printWekaTest() {
		return   this.averages + "," + this.prevGame + "," + this.line + "," + this.bookName + "," + this.odds +"," 
				+ this.ovrUnd  + ","  + this.split.toString() + "," + this.stDev + 
				"," + this.isBack2Back.toString().toUpperCase() + "," + this.isHome.toString().toUpperCase(); 		
	}
	
	public int compareTo(FinalLine o) {
		int ret = 0;
		if (scaledSplit > o.scaledSplit) {
			ret = -1;
		} else if (scaledSplit < o.scaledSplit) {
			ret = 1;
		}
		return ret;
	}

	public String getOdds() {
		return odds;
	}

	public void setOdds(String odds) {
		this.odds = odds;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getBet() {
		return bet;
	}

	public void setBet(String bet) {
		this.bet = bet;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Double getSplit() {
		return split;
	}

	public void setSplit(Double split) {
		this.split = split;
	}

	public Double getScaledSplit() {
		return scaledSplit;
	}

	public void setScaledSplit(Double scaledSplit) {
		this.scaledSplit = scaledSplit;
	}

	public Double getAverages() {
		return averages;
	}

	public void setAverages(Double averages) {
		this.averages = averages;
	}

	public Double getStDev() {
		return stDev;
	}

	public void setStDev(Double stDev) {
		this.stDev = stDev;
	}

	public Boolean getBack2Back() {
		return isBack2Back;
	}

	public void setBack2Back(Boolean back2Back) {
		this.isBack2Back = back2Back;
	}

	public int getPrevGame() {
		return prevGame;
	}

	public Boolean getIsHome() {
		return isHome;
	}

	public void setIsHome(Boolean isHome) {
		this.isHome = isHome;
	}

	public void setPrevGame(int prevGame) {
		this.prevGame = prevGame;
	}

	public String getOpponent() {
		return opponent;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public String getOvrUnd() {
		return ovrUnd;
	}

	public void setOvrUnd(String ovrUnd) {
		this.ovrUnd = ovrUnd;
	}
	
	public String getBook(){
		return bookName;
	}

	public void setBasicStats(double number, double standardDev, int prev) {
		this.setAverages(number);
		this.setStDev(standardDev);
		this.setPrevGame(prev);
	}

	public boolean isMostlySame(FinalLine newLine) {
		if(this.player.equals(newLine.getPlayer()) && this.bet.equals(newLine.getBet())
				&& this.bookName.equals(newLine.getBook())){
			return true;
		}else{
			return false;
		}
	}

}
//return player + ": " + bet + " average: " + averages + " line: " + line + " split: " + split.toString() + " scaledSplit: " + scaledSplit + " StdDev: " + stDev;

