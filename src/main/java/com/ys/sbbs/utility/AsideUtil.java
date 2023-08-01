package com.ys.sbbs.utility;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AsideUtil {
	@Value("${roadAddrKey}") private String roadAddrKey;
	@Value("${kakaoApiKey}") private String kakaoApiKey;
	@Value("${weatherApiKey}") private String weatherApiKey;
	
	public String getTodayQuote(String filename) {
		String result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename), 1024);
			int index = (int) Math.floor(Math.random() * 100);
			for (int i=0; i<=index; i++)
				result = br.readLine();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String squareImage(String profilePath, String fname) {
		String newFname = null;
		try {
			File file = new File(profilePath + fname);
			BufferedImage buffer = ImageIO.read(file);
			int width = buffer.getWidth();
			int height = buffer.getHeight();
			int size = width, x = 0, y = 0;
			if (width > height) {
				size = height;
				x = (width - size) / 2;
			} else if (width < height) {
				size = width;
				y = (height - size) / 2;
			}
			
			String now = LocalDateTime.now().toString().substring(0,22).replaceAll("[-T:.]", "");
			int idx = fname.lastIndexOf('.');
			String format = fname.substring(idx+1);
			newFname = now + fname.substring(idx);
			
			BufferedImage dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = dest.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(buffer, 0, 0, size, size, x, y, x + size, y + size, null);
			g.dispose();
			
			File dstFile = new File(profilePath + newFname);
			OutputStream os = new FileOutputStream(dstFile);
			ImageIO.write(dest, format, os);
			os.close();
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFname;
	}
	
	// 행안부 도로명 주소 API
	public String getRoadAddr(String place) {
		String apiUrl = "https://www.juso.go.kr/addrlink/addrLinkApiJsonp.do";
		String roadAddr = null;
		try {
			String keyword = URLEncoder.encode(place, "utf-8");		
			apiUrl += "?confmKey=" + roadAddrKey
					+ "&currentPage=1&countPerPage=10"
					+ "&keyword=" + keyword + "&resultType=json";
			URL url = new URL(apiUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result.substring(1, result.length()-1));
			JSONObject results = (JSONObject) obj.get("results");
			JSONArray juso = (JSONArray) results.get("juso");
			JSONObject jusoItem = (JSONObject) juso.get(0);
			roadAddr = (String) jusoItem.get("roadAddr");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roadAddr;
	}
	
	// 카카오 로컬 API - 위도, 경도
	public List<String> getGeoCode(String roadAddr) {
		List<String> list = new ArrayList<>();
		String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
		try {
			String keyword = URLEncoder.encode(roadAddr, "utf-8");		
			apiUrl += "?query=" + keyword;
			URL url = new URL(apiUrl);
			// 헤더 설정
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "KakaoAK " + kakaoApiKey);
			conn.setDoInput(true);

			// 데이터 수신
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));			
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);
			JSONArray documents = (JSONArray) obj.get("documents");
			JSONObject localItem = (JSONObject) documents.get(0);
			list.add((String) localItem.get("x"));		// 경도
			list.add((String) localItem.get("y"));		// 위도
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	// OpenWeather API
	public String getWeahter(String lon, String lat) {
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
		String weatherStr = null;
		try {
			apiUrl += "?lat=" + lat + "&lon=" + lon
					+ "&appid=" + weatherApiKey + "&units=metric&lang=kr";
			URL url = new URL(apiUrl);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));			
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);
			JSONArray weather = (JSONArray) obj.get("weather");
			JSONObject weatherItem = (JSONObject) weather.get(0);
			String description = (String) weatherItem.get("description");
			String iconUrl = "https://api.openweathermap.org/img/w/" + (String) weatherItem.get("icon") + ".png";
			JSONObject main = (JSONObject) obj.get("main");
			double temp_ = (double) main.get("temp");
			String temp = String.format("%.1f", temp_ + 0.01);
			weatherStr = "<img src=\"" + iconUrl + "\" height=\"28\">" + description
						+ " / " + temp + "&#8451";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return weatherStr;
	}
	
}
