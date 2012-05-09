package com.TransitSearch;

public class City {
	private String cityname;
	private String cityurl;
	
	public City(){
		cityname="ÄÏ¾©";
		cityurl="http://nanjing.8684.cn";
	}
	
	public City(String cityname,String cityurl){
		this.cityname=cityname;
		this.cityurl=cityurl;
	}
	
	public String getcityname(){
		return cityname;
	}
	public String getcityurl(){
		return cityurl;
	}

}
