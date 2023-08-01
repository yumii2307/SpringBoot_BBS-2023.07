package com.ys.sbbs.utility;

import java.util.List;

public class UtilTest {

	public static void main(String[] args) {
		AsideUtil au = new AsideUtil();
		String roadAddr = au.getRoadAddr("경기도 수원시 장안구청");
		System.out.println(roadAddr);
		
		List<String> list = au.getGeoCode(roadAddr);
		String lon = list.get(0);
		String lat = list.get(1);
		System.out.println("경도: " + lon + ", 위도: " + lat);
		
		String weatherStr = au.getWeahter(lon, lat);
		System.out.println(weatherStr);
	}

}
