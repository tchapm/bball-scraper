package com.scraper.stats;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.scraper.util.LineUtil;


public class PlayerStats {
	private double rebounds;
	private double points;
	private double assists;
	private double reboundLine=0.0;
	private double pointLine=0.0;
	private double assistLine=0.0;
	private int lookupCode;
	public ArrayList<GameStats> gameLog = new ArrayList<GameStats>();
	private String teamName;
	private String oppName;
	private Boolean isHome;
	private double stDevPts=0.0;
	private double stDevReb=0.0;
	private double stDevAss=0.0;
	private double stDevPtsReb=0.0;
	private double stDevPtsAst=0.0;
	private double seasonPPG;
	private double seasonRPG;
	private double seasonAPG;
	private double careerPPG;
	private double careerRPG;
	private double careerAPG;
	


	public PlayerStats(int i) {
		this.lookupCode = i;
	}
	public double getRebounds() {
		return rebounds;
	}
	public void setRebounds(double rebounds) {
		this.rebounds = rebounds;
	}
	public double getPoints() {
		return points;
	}
	public void setPoints(double points) {
		this.points = points;
	}
	public double getAssists() {
		return assists;
	}
	public void setAssists(double assists) {
		this.assists = assists;
	}
	public int getLookupCode() {
		return lookupCode;
	}
	public void setLookupCode(int lookupCode) {
		this.lookupCode = lookupCode;
	}
	public double getPointsAssists(){
		return this.points+this.assists;
	}
	public double getPointsRebounds(){
		return this.points+this.rebounds;
	}
	public void printStatistics(){
		System.out.println("Points: " + this.points + " Rebounds: " + this.rebounds + " Assists: "+ this.assists);
	}
	public void setStDev() {
		Iterator<GameStats> it = this.gameLog.iterator();
		GameStats tempStats;
		int totalGames=5;
		while(it.hasNext()){
			tempStats = (GameStats)it.next();
			if(tempStats.getMinutes()!=0){
				this.stDevPts += Math.pow((double)tempStats.getPoints()-this.points, 2);
				this.stDevReb += Math.pow((double)tempStats.getRebounds()-this.rebounds, 2);
				this.stDevAss += Math.pow((double)tempStats.getAssists()-this.assists, 2);
				this.stDevPtsAst += Math.pow((double)tempStats.getPoints()+(double)tempStats.getRebounds() -this.getPointsRebounds(), 2);
				this.stDevPtsReb += Math.pow((double)tempStats.getPoints()+(double)tempStats.getAssists() -this.getPointsAssists(), 2);
			}else{
				totalGames--;
			}
		}
		this.stDevPts = Math.pow(this.stDevPts/totalGames,0.5);
		this.stDevReb = Math.pow(this.stDevReb/totalGames,0.5);
		this.stDevAss = Math.pow(this.stDevAss/totalGames,0.5);
		this.stDevPtsAst = Math.pow(this.stDevPtsAst/totalGames,0.5);
		this.stDevPtsReb = Math.pow(this.stDevPtsReb/totalGames,0.5);

	}
	public double getStDevPtsReb() {
		return stDevPtsReb;
	}
	public void setStDevPtsReb(double stDevPtsReb) {
		this.stDevPtsReb = stDevPtsReb;
	}
	public double getStDevPtsAst() {
		return stDevPtsAst;
	}
	public void setStDevPtsAss(double stDevPtsAss) {
		this.stDevPtsAst = stDevPtsAss;
	}
	public double getStDevPts() {
		return stDevPts;
	}
	public double getStDevReb() {
		return stDevReb;
	}
	public double getStDevAss() {
		return stDevAss;
	}
	public void setTeam(String team) {
		this.teamName = team;
	}
	public String getTeam() {
		return this.teamName;
	}
	public GameStats getSpecificGameStats(int index){
		return gameLog.get(index);
	}
	public double getReboundLine() {
		return reboundLine;
	}
	public void setReboundLine(double reboundLine) {
		this.reboundLine = reboundLine;
	}
	public double getPointLine() {
		return pointLine;
	}
	public void setPointLine(double pointLine) {
		this.pointLine = pointLine;
	}
	public double getAssistLine() {
		return assistLine;
	}
	public void setAssistLine(double assistLine) {
		this.assistLine = assistLine;
	}
	public ArrayList<GameStats> getGameLog() {
		return gameLog;
	}
	public void setGameLog(ArrayList<GameStats> gameLog) {
		this.gameLog = gameLog;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public double getSeasonPPG() {
		return seasonPPG;
	}
	public void setSeasonPPG(double seasonPPG) {
		this.seasonPPG = seasonPPG;
	}
	public double getSeasonRPG() {
		return seasonRPG;
	}
	public void setSeasonRPG(double seasonRPG) {
		this.seasonRPG = seasonRPG;
	}
	public double getSeasonAPG() {
		return seasonAPG;
	}
	public void setSeasonAPG(double seasonAPG) {
		this.seasonAPG = seasonAPG;
	}
	public double getCareerPPG() {
		return careerPPG;
	}
	public void setCareerPPG(double careerPPG) {
		this.careerPPG = careerPPG;
	}
	public double getCareerRPG() {
		return careerRPG;
	}
	public void setCareerRPG(double careerRPG) {
		this.careerRPG = careerRPG;
	}
	public double getCareerAPG() {
		return careerAPG;
	}
	public void setCareerAPG(double careerAPG) {
		this.careerAPG = careerAPG;
	}
	public void setStDevPts(double stDevPts) {
		this.stDevPts = stDevPts;
	}
	public void setStDevReb(double stDevReb) {
		this.stDevReb = stDevReb;
	}
	public void setStDevAss(double stDevAss) {
		this.stDevAss = stDevAss;
	}
	public void setStDevPtsAst(double stDevPtsAst) {
		this.stDevPtsAst = stDevPtsAst;
	}
	public String getOppName() {
		return oppName;
	}
	public void setOppName(String oppName) {
		this.oppName = oppName;
	}
	public Boolean getIsHome() {
		return isHome;
	}
	public void setIsHome(Boolean isHome) {
		this.isHome = isHome;
	}
	public void setGlobalStats(String rawPlayer) {
		StringTokenizer st;
		String tempVal;
		int counter=0;
		st = new StringTokenizer(rawPlayer, ">");
		while(st.hasMoreTokens()){
			tempVal = st.nextToken();
//			try {
				if(!tempVal.contains("text-align:right")){
					counter++;
					//					System.out.println(tempVal + " " + counter);
					if(counter == 40){
						this.seasonRPG = Double.parseDouble(LineUtil.parseEnd(tempVal));
					}else if(counter == 41){
						this.seasonAPG = Double.parseDouble(LineUtil.parseEnd(tempVal));
					}else if(counter == 46){
						this.seasonPPG = Double.parseDouble(LineUtil.parseEnd(tempVal));
					}else if(counter == 59){
						this.careerRPG = Double.parseDouble(LineUtil.parseEnd(tempVal));
					}else if(counter == 60){
						this.careerAPG = Double.parseDouble(LineUtil.parseEnd(tempVal));
					}else if(counter == 65){
						this.careerPPG = Double.parseDouble(LineUtil.parseEnd(tempVal));
						break;
					}
				}
//			} 
//			catch (NumberFormatException e){
//				e.printStackTrace();
//			}
		}		
	}
	public String printPlayerStats() {
//		private double seasonPPG;
//		private double seasonRPG;
//		private double seasonAPG;
//		private double careerPPG;
//		private double careerRPG;
//		private double careerAPG;	
		return this.seasonPPG + "," + this.seasonRPG + "," +
			this.seasonAPG + "," + this.careerPPG + "," + 
		this.careerRPG + "," + this.careerAPG;		
	}

}
