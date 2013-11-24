package com.scraper.stats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class TeamStats {
	private String name;
	private String pointsPG;
	private String rebPG;
	private String assPG;
	private String pointsAllow;
	
	public TeamStats(String teamName) throws IOException {
		this.name = teamName;
		fillStats();
	}
	private void fillStats() throws IOException {
		URL url = new URL("http://espn.go.com/nba/team/_/name/" + this.name); 
		URLConnection conn = url.openConnection();
		String encoding = conn.getContentEncoding();
		if (encoding == null) {
			encoding = "ISO-8859-1";
			
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
		String line;
		String[] tempSegments;
		StringTokenizer st;
		int count = 0;
		while ((line = br.readLine()) != null) {
			if(line.contains("OVERALL NBA RANKINGS")){
				tempSegments = line.split("OVERALL");
				
				st = new StringTokenizer(tempSegments[1], ">");
				while(st.hasMoreTokens()){
					count++;
					st.nextToken();
					if(count==8){
						this.pointsPG = parseOutSpan(st.nextToken());
					}
				}
				count = 0;
				st = new StringTokenizer(tempSegments[2], ">");
				while(st.hasMoreTokens()){
					count++;
					st.nextToken();
					if(count==9){
						this.rebPG = parseOutSpan(st.nextToken());
					}
				}
				count = 0;
				st = new StringTokenizer(tempSegments[3], ">");
				while(st.hasMoreTokens()){
					count++;
					st.nextToken();
					if(count==9){
						this.assPG = parseOutSpan(st.nextToken());
					}				}
				count = 0;
				st = new StringTokenizer(tempSegments[4], ">");
				while(st.hasMoreTokens()){
					count++;
					st.nextToken();
					if(count==9){
						this.pointsAllow = parseOutSpan(st.nextToken());
						break;
					}			
				}
				break;
			}
		}
//		System.out.println("done");
	}
	private String parseOutSpan(String nextToken) {
		if(nextToken.endsWith("</span")){
			nextToken = nextToken.replace("</span", "");
		}
		return nextToken;
	}
	public String getPointsPG() {
		return pointsPG;
	}
	public void setPointsPG(String pointsPG) {
		this.pointsPG = pointsPG;
	}
	public String getRebPG() {
		return rebPG;
	}
	public void setRebPG(String rebPG) {
		this.rebPG = rebPG;
	}
	public String getAssPG() {
		return assPG;
	}
	public void setAssPG(String assPG) {
		this.assPG = assPG;
	}
	public String getPointsAllow() {
		return pointsAllow;
	}
	public void setPointsAllow(String pointsAllow) {
		this.pointsAllow = pointsAllow;
	}
	public String printStats(){
		return this.pointsPG + "," + this.rebPG + "," + this.assPG + "," + this.pointsAllow;
	}
	
}
