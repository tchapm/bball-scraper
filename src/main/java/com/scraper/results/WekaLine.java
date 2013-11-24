package com.scraper.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class WekaLine {
	private String player;
	private String stat;
	private String team;
	private double line;
	private String book;
	private String vig;
	private HashMap<String, Double> classPrdConf = new HashMap<String, Double>();
	private ArrayList<String> classNames = new ArrayList<String>();
	private double cumPred;
	private int isCorrect = 0;
	
	public WekaLine(String thePlayer, String theStat, String theTeam,
			String theLine, String theBook, String theVig) {
		this.player = thePlayer;
		this.stat = theStat;
		this.team = theTeam;
		this.line = Double.parseDouble(theLine);
		this.book = theBook;
		this.vig = theVig;
	}
	public WekaLine(String thePlayer,
			String theLine, String theBook, String theVig) {
		this.player = thePlayer;
		this.line = Double.parseDouble(theLine);
		this.book = theBook;
		this.vig = theVig;
	}
	public WekaLine(String prevLine, String yestString) {
		String[] lineArr = prevLine.split(",");
		StringTokenizer st = new StringTokenizer(prevLine, ",");
		this.player = lineArr[0];
		this.line = Double.parseDouble(lineArr[1]);
		this.book = lineArr[2];
		this.vig = lineArr[3];
		this.cumPred = Double.parseDouble(lineArr[4]);
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public Double getLine() {
		return line;
	}
	public void setLine(Double line) {
		this.line = line;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getVig() {
		return vig;
	}
	public void setVig(String vig) {
		this.vig = vig;
	}
	public HashMap<String, Double> getClassifierValue() {
		return classPrdConf;
	}
	public void setClassifierValue(HashMap<String, Double> classifierValue) {
		this.classPrdConf = classifierValue;
	}
	public void setOneClassifierValue(String className, double classifierValue) {
		this.classNames.add(className);
		this.classPrdConf.put(className, classifierValue);
	}
	public double getCumPred() {
		return cumPred;
	}
	public void setCumPred(HashMap<String, Double> classAcc) {
		double pred = 0.0;
		for(String name : this.classNames){
			pred+=this.classPrdConf.get(name)*(classAcc.get(name)-50)/100;
		}
		this.cumPred = pred/(double)this.classNames.size();
	}
	
	public int getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(int isCorrect) {
		this.isCorrect = isCorrect;
	}
	
	public String print(){
		String predValues = "";
		for(String name : this.classNames){
			predValues+="," + this.classPrdConf.get(name);
		}
		return(this.player + "," + this.line + "," + this.book + "," + this.vig
				 + "," + this.cumPred + predValues);
	}
	public String printResult(int result, int actStat) {
		if((result>0 && this.cumPred>0) || (result<0 && this.cumPred<0)){
			this.isCorrect=1;
		}else{
			this.isCorrect=-1;
		}
		return (print() + "," + this.isCorrect + "," + actStat);
	}


}
