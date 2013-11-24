package com.scraper.stats;

public class GameStats {
	String opponent = null;
	double oppPtsAllow = 0.0;
	int minutes = 0;
	int points = 0;
	int rebounds = 0;
	int assists = 0;
	String oppDate = null;
	
	public GameStats(String date){
		this.oppDate = date;
	}
	public GameStats() {
	}
	public String getOpponent() {
		return opponent;
	}
	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}
	public double getOppPtsAllow() {
		return oppPtsAllow;
	}
	public void setOppPtsAllow(double oppPtsAllow) {
		this.oppPtsAllow = oppPtsAllow;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getRebounds() {
		return rebounds;
	}
	public void setRebounds(int rebounds) {
		this.rebounds = rebounds;
	}
	public int getAssists() {
		return assists;
	}
	public void setAssists(int assists) {
		this.assists = assists;
	}
	public String getDay() {
		// TODO Auto-generated method stub
		return oppDate;
	}

	
	
}
