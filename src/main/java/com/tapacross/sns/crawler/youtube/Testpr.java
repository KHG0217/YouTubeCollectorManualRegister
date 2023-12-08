package com.tapacross.sns.crawler.youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.tapacross.sns.util.ThreadUtil;

public class Testpr {

	public String extractContinuationToken (String tokenContainData) {
		String[] dataArray = tokenContainData.split("\\,");
		String continuationToken = null;
		List<String> tokenList = new ArrayList<>();	
		for(String token : dataArray) {
			if(token.contains("continuationCommand\":{\"token\":")) {
				System.out.println("continuationCommand token find, add list");
				tokenList.add(token);
			}
		}
		continuationToken = tokenList.get(tokenList.size() -1).replaceAll("continuationCommand", "")
				.replaceAll("token", "")
				.replaceAll("\\{", "")
				.replaceAll("\\\"", "")
				.replaceAll("\\:", "");
		return continuationToken;
	} 
	
	public String parseTitle(Document doc) {
		String title = null;
		title = doc.select("body > title").text()
				.replaceAll(".\\-.YouTube", "");
		return title;
	}
	
	public String parserPicture(Document doc) {
		String picture = null;
		picture = doc.select("meta[property=og:image]").attr("content");
		return picture;
	}
	
	public void extractdataTest(JSONObject jsonObject) {	
		System.out.println(jsonObject);
		
		JSONObject aboutChannelViewModelJson = jsonObject.getJSONArray("onResponseReceivedEndpoints").getJSONObject(0)
				.getJSONObject("appendContinuationItemsAction").getJSONArray("continuationItems")
				.getJSONObject(0).getJSONObject("aboutChannelRenderer").getJSONObject("metadata")
				.getJSONObject("aboutChannelViewModel");
		
				if(!aboutChannelViewModelJson.has("subscriberCountText")) {
					System.out.println("구독자: 0");
				}else {
					String youTubdata = aboutChannelViewModelJson.getString("subscriberCountText");
					System.out.println("youTubdata: " + youTubdata);
					String num = YouTubeFollowerType.toType(youTubdata).fare(youTubdata);
					System.out.println("구독자 : " + num );
				}

		System.out.println("bio값: " + aboutChannelViewModelJson.getString("description").replaceAll("\n", ""));
		System.out.println("동영상수: " + aboutChannelViewModelJson.getString("videoCountText").replaceAll("[^0-9]", ""));
		System.out.println("누적조회수: " + aboutChannelViewModelJson.getString("viewCountText").replaceAll("[^0-9]", ""));	
	}
	
	
	
	public static void main(String[] args) {
		List<String> testList = new ArrayList<String>();
		Testpr pr = new Testpr();
		testList.add("https://www.youtube.com/channel/UCXLyuvd-xZWol-j_BBw0Fyw");
		testList.add("https://www.youtube.com/channel/UCX_itCsuTZI4ADxszwQxowg");
		testList.add("https://www.youtube.com/channel/UCnUHFkDkIfrJka6Ohgapy5Q");
		testList.add("https://www.youtube.com/channel/UCnO3jmEh7r6hmFg3qFHyC6g");
		testList.add("https://www.youtube.com/channel/UCDlW7S2z46OXQOx9dKMSj8g");
		testList.add("https://www.youtube.com/channel/UCXVxQahGwGx34ORq2h_JSmQ");
		testList.add("https://www.youtube.com/channel/UClp4EdAwhySzqnMWjKnh0gw");
		
		for(String url: testList) {
			String apiUrl ="https://www.youtube.com/youtubei/v1/browse?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8&prettyPrint=false";
			String id = url.replaceAll("https\\:\\/\\/www\\.youtube\\.com\\/channel\\/", "");
			
			try {
				System.out.println("===================================================");
				Document doc = pr.parseUrl(url);
				String tokenContainData = pr.parseContainContinuationTokenData(doc);
				String continuationToken = pr.extractContinuationToken(tokenContainData);
				JSONObject jsonObject = pr.testParse(apiUrl, id, continuationToken);			
				System.out.println("title: " + pr.parseTitle(doc));
				System.out.println("picture: " + pr.parserPicture(doc));
				pr.extractdataTest(jsonObject);
				System.out.println("===================================================");
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		

//		String url2 ="https://www.youtube.com/channel/UCXLyuvd-xZWol-j_BBw0Fyw"; // 만명대
//		String url2 ="https://www.youtube.com/channel/UCX_itCsuTZI4ADxszwQxowg"; // 1000명대
//		String url2 ="https://www.youtube.com/channel/UCnUHFkDkIfrJka6Ohgapy5Q"; // 100명대
//		String url2 ="https://www.youtube.com/channel/UCnO3jmEh7r6hmFg3qFHyC6g"; // 10명대
//		String url2 ="https://www.youtube.com/channel/UCDlW7S2z46OXQOx9dKMSj8g"; // 1명대
//		String url2 ="https://www.youtube.com/channel/UCXVxQahGwGx34ORq2h_JSmQ"; // 0명
		String url2 ="https://www.youtube.com/channel/UClp4EdAwhySzqnMWjKnh0gw"; //


		

	}
	
	private Document parseUrl(String url) throws IOException {
		Document doc = null;
		ThreadUtil.sleepSec(3);
		doc = Jsoup.connect(url)
		     .header("Cookie", "HSID=AOv-uVShZQ0WwbBij; SSID=AXNPIq7p6I30krsLa; APISID=LBRvucTnQ3YtLMCN/Avb6Er20MlVciEjXL; SAPISID=sX6cho2ChTcr_BVQ/ARwr-of6NqF4fMgAj; __Secure-1PAPISID=sX6cho2ChTcr_BVQ/ARwr-of6NqF4fMgAj; __Secure-3PAPISID=sX6cho2ChTcr_BVQ/ARwr-of6NqF4fMgAj; SID=dwh0cL-HCUVhU_oPfpHzj80ubuFaLV1o8RTnIMVNrP74cttbCtfPBFAlcqvuW18J5VftlA.; __Secure-1PSID=dwh0cL-HCUVhU_oPfpHzj80ubuFaLV1o8RTnIMVNrP74cttb9r5nsptktxpnZxoa4D2Rcw.; __Secure-3PSID=dwh0cL-HCUVhU_oPfpHzj80ubuFaLV1o8RTnIMVNrP74cttbm1TNwSh5awisLhTy0K81hg.; LOGIN_INFO=AFmmF2swRQIgdPK4BjrDut7vReru1j3P0FJpKdZZfxlORbgzmGcDEa8CIQD7MlCrJO1kxZYRs0j0hiUzjO-Pn6gjJPr8ddti7vPbYw:QUQ3MjNmeXNWZHc0MFhSSnhMb2ZQU2NRN1g2RG9iZGYwakY0OG9UZmhEZlBWVHVpTXN4bDQyZW5JOFhIRFlnRnVrakNYdmdVMGtPSUdoeEVHeU56NEtqLWZzdUkzdjlBUEhKdDRpWnRkb29DRzY1ZW96TE51ZFBDbEl5aUdjZnVSekFsMDEySHJkVy15U2hPcDdqc1JNb0xNODM4aV9qTE1n; VISITOR_INFO1_LIVE=QLdEC_vpGVY; VISITOR_PRIVACY_METADATA=CgJLUhICGgA%3D; PREF=f6=80&tz=Asia.Seoul; YSC=GRJ7ftnXMV0; __Secure-1PSIDTS=sidts-CjEBPVxjSnujWWQsEMXZGo8CLMsWNESdOcNKMW6-xzuF-1IF7UWSfTtoyCavIcI0c_apEAA; __Secure-3PSIDTS=sidts-CjEBPVxjSnujWWQsEMXZGo8CLMsWNESdOcNKMW6-xzuF-1IF7UWSfTtoyCavIcI0c_apEAA; SIDCC=ACA-OxPXjOVlJAUZ8FEwFfQxHbm5Rxmhr8J_qrb3o5uV_DiDs34o3xKHANAz61_UYYc1UoEuNw; __Secure-1PSIDCC=ACA-OxPaIJRKUA89LdAfwWfpaYKRQ8NvchO_MQJBC_vgtCpTmoUSMeMU-p-vEVyvQQNoChSVGnA; __Secure-3PSIDCC=ACA-OxOwyPgQ7_UhewTtH6878ZHrwZUCAROE0OTOW8dgM1URdz7jgRgxkAQ2OoxjUOPTelATuw")
			.header("cache-control", "no-cache")
			.header("sec-fetch-mode", "navigate")
			.header("x-client-data", "CIS2yQEIorbJAQjBtskBCKmdygEIrMfKAQj1x8oBCOfIygEI6cjKAQi0y8oBCITPygEI3NXKAQiHmcsBCMiZywEYi8HKAQ==")
			.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
			.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
			.maxBodySize(0)
			.get();		
		return doc;
	}
	
//	private JSONObject parseUrlTest(String url) throws IOException {
//		Document doc = null;
//		ThreadUtil.sleepSec(3);
//		doc = Jsoup.connect(url)
////				.header("Authority", "www.youtube.com")
//				.header("Path", "/channel/UCX_itCsuTZI4ADxszwQxowg/about")
//				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
//				.header("Accept-Encoding", "gzip, deflate")
//				.header("Cache-Control", "max-age=0")
//				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//				.header("Cache-Contro", "max-age=0")
//				.header("Cookie", "HSID=AOv-uVShZQ0WwbBij; SSID=AXNPIq7p6I30krsLa; APISID=LBRvucTnQ3YtLMCN/Avb6Er20MlVciEjXL; SAPISID=sX6cho2ChTcr_BVQ/ARwr-of6NqF4fMgAj; __Secure-1PAPISID=sX6cho2ChTcr_BVQ/ARwr-of6NqF4fMgAj; __Secure-3PAPISID=sX6cho2ChTcr_BVQ/ARwr-of6NqF4fMgAj; SID=dwh0cL-HCUVhU_oPfpHzj80ubuFaLV1o8RTnIMVNrP74cttbCtfPBFAlcqvuW18J5VftlA.; __Secure-1PSID=dwh0cL-HCUVhU_oPfpHzj80ubuFaLV1o8RTnIMVNrP74cttb9r5nsptktxpnZxoa4D2Rcw.; __Secure-3PSID=dwh0cL-HCUVhU_oPfpHzj80ubuFaLV1o8RTnIMVNrP74cttbm1TNwSh5awisLhTy0K81hg.; LOGIN_INFO=AFmmF2swRQIgdPK4BjrDut7vReru1j3P0FJpKdZZfxlORbgzmGcDEa8CIQD7MlCrJO1kxZYRs0j0hiUzjO-Pn6gjJPr8ddti7vPbYw:QUQ3MjNmeXNWZHc0MFhSSnhNb2ZQU2NRN1g2RG9iZGYwakY0OG9UZmhEZlBWVHVpTXN4bDQyZW5JOFhIRFlnRnVrakNYdmdVMGtPSUdoeEVHeU56NEtqLWZzdUkzdjlBUEhKdDRpWnRkb29DRzY1ZW96TE51ZFBDbEl5aUdjZnVSekFsMDEySHJkVy15U2hPcDdqc1JNb0xNODM4aV9qTE1n; VISITOR_INFO1_LIVE=QLdEC_vpGVY; VISITOR_PRIVACY_METADATA=CgJLUhICGgA%3D; PREF=f6=80&tz=Asia.Seoul; YSC=GRJ7ftnXMV0; __Secure-1PSIDTS=sidts-CjEBPVxjSnujWWQsEMXZGo8CLMsWNESdOcNKMW6-xzuF-1IF7UWSfTtoyCavIcI0c_apEAA; __Secure-3PSIDTS=sidts-CjEBPVxjSnujWWQsEMXZGo8CLMsWNESdOcNKMW6-xzuF-1IF7UWSfTtoyCavIcI0c_apEAA; SIDCC=ACA-OxPXjOVlJAUZ8FEwFfQxHbm5Rxmhr8J_qrb3o5uV_DiDs34o3xKHANAz61_UYYc1UoEuNw; __Secure-1PSIDCC=ACA-OxPaIJRKUA89LdAfwWfpaYKRQ8NvchO_MQJBC_vgtCpTmoUSMeMU-p-vEVyvQQNoChSVGnA; __Secure-3PSIDCC=ACA-OxOwyPgQ7_UhewTtH6878ZHrwZUCAROE0OTOW8dgM1URdz7jgRgxkAQ2OoxjUOPTelATuw")
//				.header("Sec-Ch-Ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"")
//				.header("Sec-Ch-Ua-Arch", "\"x86\"")
//				.header("Sec-Ch-Ua-Bitness", "\"64\"")
//				.header("Sec-Ch-Ua-Full-Version", "\"114.0.5735.199\"")
//				.header("Sec-Ch-Ua-Full-Version-List", "\"Not.A/Brand\";v=\"8.0.0.0\", \"Chromium\";v=\"114.0.5735.199\", \"Google Chrome\";v=\"114.0.5735.199\"")
//				.header("Sec-Ch-Ua-Mobile", "?0")
//				.header("Sec-Ch-Ua-Platform", "\"Windows\"")
//				.header("Sec-Ch-Ua-Platform-Version", "\"10.0.0\"")
//				.header("Sec-Ch-Ua-Wow64", "?0")
//				.header("Sec-Fetch-Dest", "document")
//				.header("Sec-Fetch-Mode", "navigate")
//				.header("Sec-Fetch-Site", "none")
//				.header("Sec-Fetch-User", "?1")
//				.maxBodySize(0)
//				.header("Service-Worker-Navigation-", "true")
//				.header("x-client-data", "CIi2yQEIprbJAQipncoBCLnwygEIlqHLAQiFoM0BCNy9zQEIucrNAQ==")
//				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
//				.get();
//		return extractJsonObject(doc);
//	}
	
	private JSONObject testParse(String url, String id, String continuationToken) throws IOException{
	       // 요청 헤더 및 바디 값
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0";
//        String requestBody = "{\"context\":{\"client\":{\"hl\":\"ko\",\"gl\":\"KR\",\"remoteHost\":\"58.233.189.85\",\"deviceMake\":\"\",\"deviceModel\":\"\",\"visitorData\":\"CgtRTGRFQ192cEdWWSitocCrBjIICgJLUhICGgA%3D\",\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36,gzip(gfe)\",\"clientName\":\"WEB\",\"clientVersion\":\"2.20231204.06.00\",\"osName\":\"Windows\",\"osVersion\":\"10.0\",\"originalUrl\":\"https://www.youtube.com/channel/" + id + "\",\"platform\":\"DESKTOP\",\"clientFormFactor\":\"UNKNOWN_FORM_FACTOR\",\"configInfo\":{\"appInstallData\":\"CK2hwKsGEKn3rwUQ65OuBRDqw68FEIPfrwUQ4tSuBRD8hbAFEJj8_hIQmvCvBRCikrAFEOidsAUQiIewBRDbr68FELfvrwUQrLevBRDT4a8FEJmUsAUQlPr-EhCu1P4SEInorgUQzK7-EhDUkrAFEL2ZsAUQvbauBRDr6P4SEOSz_hIQiOOvBRDh8q8FEJ6LsAUQyfevBRCvnrAFENWIsAUQt-r-EhDamLAFEPX5rwUQvvmvBRComrAFEN3o_hIQmZGwBRD1-_4SENDirwUQxp6wBRDcgrAFEOmMsAUQpoGwBRDbmbAFEJeDsAUQzICwBRDM364FEMeDsAUQ1-mvBRCrgrAFENShrwUQppqwBRCigbAFEL6KsAUQ57qvBRDuoq8FEM2VsAUQuIuuBRC8-a8FENnJrwUQpcL-EhD4mrAFEImXsAU%3D\"},\"userInterfaceTheme\":\"USER_INTERFACE_THEME_LIGHT\",\"timeZone\":\"Asia/Seoul\",\"browserName\":\"Chrome\",\"browserVersion\":\"114.0.0.0\",\"acceptHeader\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\",\"deviceExperimentId\":\"ChxOek13T1RNMk1EVXpNVGswTmpRME5EZzVNdz09EK2hwKsGGK2hwKsG\",\"screenWidthPoints\":336,\"screenHeightPoints\":937,\"screenPixelDensity\":1,\"screenDensityFloat\":1,\"utcOffsetMinutes\":540,\"connectionType\":\"CONN_CELLULAR_4G\",\"memoryTotalKbytes\":\"8000000\",\"mainAppWebInfo\":{\"graftUrl\":\"https://www.youtube.com/channel/"+ id + "#\",\"pwaInstallabilityStatus\":\"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\",\"webDisplayMode\":\"WEB_DISPLAY_MODE_BROWSER\",\"isWebNativeShareAvailable\":true}},\"user\":{\"lockedSafetyMode\":false},\"request\":{\"useSsl\":true,\"internalExperimentFlags\":[],\"consistencyTokenJars\":[]},\"clickTracking\":{\"clickTrackingParams\":\"CBkQuy8YACITCNnd4tOT-oIDFTBH9QUd5HIH8g==\"},\"adSignalsInfo\":{\"params\":[{\"key\":\"dt\",\"value\":\"1701843117801\"},{\"key\":\"flash\",\"value\":\"0\"},{\"key\":\"frm\",\"value\":\"0\"},{\"key\":\"u_tz\",\"value\":\"540\"},{\"key\":\"u_his\",\"value\":\"3\"},{\"key\":\"u_h\",\"value\":\"1080\"},{\"key\":\"u_w\",\"value\":\"1920\"},{\"key\":\"u_ah\",\"value\":\"1040\"},{\"key\":\"u_aw\",\"value\":\"1920\"},{\"key\":\"u_cd\",\"value\":\"24\"},{\"key\":\"bc\",\"value\":\"31\"},{\"key\":\"bih\",\"value\":\"937\"},{\"key\":\"biw\",\"value\":\"319\"},{\"key\":\"brdim\",\"value\":\"-1920,0,-1920,0,1920,0,1920,1040,336,937\"},{\"key\":\"vis\",\"value\":\"1\"},{\"key\":\"wgl\",\"value\":\"true\"},{\"key\":\"ca_type\",\"value\":\"image\"}],\"bid\":\"ANyPxKrE1pe1o_f1751y__cMrboj2Kk70Nn9lE-BX2CfRussSEmjlXTn7ZS9C9_9OUYEL_e1fOpsUEbP4qyyrTXQdeJAmEX_fg\"}},\"continuation\":\"4qmFsgJgEhhVQ1hfaXRDc3VUWkk0QUR4c3p3UXhvd2caRDhnWXJHaW1hQVNZS0pEWTJNakV4Wm1WakxUQXdNREF0TWpKbE5DMDVaVFkzTFRFMFl6RTBaV1pqTjJKaVl3JTNEJTNE\"}";
        String requestBody = "{\"context\":{\"client\":{\"hl\":\"ko\",\"gl\":\"KR\",\"remoteHost\":\"58.233.189.85\",\"deviceMake\":\"\",\"deviceModel\":\"\",\"visitorData\":\"CgtRTGRFQ192cEdWWSitocCrBjIICgJLUhICGgA%3D\",\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36,gzip(gfe)\",\"clientName\":\"WEB\",\"clientVersion\":\"2.20231204.06.00\",\"osName\":\"Windows\",\"osVersion\":\"10.0\",\"originalUrl\":\"https://www.youtube.com/channel/" + id + "\",\"platform\":\"DESKTOP\",\"clientFormFactor\":\"UNKNOWN_FORM_FACTOR\",\"configInfo\":{\"appInstallData\":\"CK2hwKsGEKn3rwUQ65OuBRDqw68FEIPfrwUQ4tSuBRD8hbAFEJj8_hIQmvCvBRCikrAFEOidsAUQiIewBRDbr68FELfvrwUQrLevBRDT4a8FEJmUsAUQlPr-EhCu1P4SEInorgUQzK7-EhDUkrAFEL2ZsAUQvbauBRDr6P4SEOSz_hIQiOOvBRDh8q8FEJ6LsAUQyfevBRCvnrAFENWIsAUQt-r-EhDamLAFEPX5rwUQvvmvBRComrAFEN3o_hIQmZGwBRD1-_4SENDirwUQxp6wBRDcgrAFEOmMsAUQpoGwBRDbmbAFEJeDsAUQzICwBRDM364FEMeDsAUQ1-mvBRCrgrAFENShrwUQppqwBRCigbAFEL6KsAUQ57qvBRDuoq8FEM2VsAUQuIuuBRC8-a8FENnJrwUQpcL-EhD4mrAFEImXsAU%3D\"},\"userInterfaceTheme\":\"USER_INTERFACE_THEME_LIGHT\",\"timeZone\":\"Asia/Seoul\",\"browserName\":\"Chrome\",\"browserVersion\":\"114.0.0.0\",\"acceptHeader\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\",\"deviceExperimentId\":\"ChxOek13T1RNMk1EVXpNVGswTmpRME5EZzVNdz09EK2hwKsGGK2hwKsG\",\"screenWidthPoints\":336,\"screenHeightPoints\":937,\"screenPixelDensity\":1,\"screenDensityFloat\":1,\"utcOffsetMinutes\":540,\"connectionType\":\"CONN_CELLULAR_4G\",\"memoryTotalKbytes\":\"8000000\",\"mainAppWebInfo\":{\"graftUrl\":\"https://www.youtube.com/channel/"+ id + "#\",\"pwaInstallabilityStatus\":\"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\",\"webDisplayMode\":\"WEB_DISPLAY_MODE_BROWSER\",\"isWebNativeShareAvailable\":true}},\"user\":{\"lockedSafetyMode\":false},\"request\":{\"useSsl\":true,\"internalExperimentFlags\":[],\"consistencyTokenJars\":[]},\"clickTracking\":{\"clickTrackingParams\":\"CBkQuy8YACITCNnd4tOT-oIDFTBH9QUd5HIH8g==\"},\"adSignalsInfo\":{\"params\":[{\"key\":\"dt\",\"value\":\"1701843117801\"},{\"key\":\"flash\",\"value\":\"0\"},{\"key\":\"frm\",\"value\":\"0\"},{\"key\":\"u_tz\",\"value\":\"540\"},{\"key\":\"u_his\",\"value\":\"3\"},{\"key\":\"u_h\",\"value\":\"1080\"},{\"key\":\"u_w\",\"value\":\"1920\"},{\"key\":\"u_ah\",\"value\":\"1040\"},{\"key\":\"u_aw\",\"value\":\"1920\"},{\"key\":\"u_cd\",\"value\":\"24\"},{\"key\":\"bc\",\"value\":\"31\"},{\"key\":\"bih\",\"value\":\"937\"},{\"key\":\"biw\",\"value\":\"319\"},{\"key\":\"brdim\",\"value\":\"-1920,0,-1920,0,1920,0,1920,1040,336,937\"},{\"key\":\"vis\",\"value\":\"1\"},{\"key\":\"wgl\",\"value\":\"true\"},{\"key\":\"ca_type\",\"value\":\"image\"}],\"bid\":\"ANyPxKrE1pe1o_f1751y__cMrboj2Kk70Nn9lE-BX2CfRussSEmjlXTn7ZS9C9_9OUYEL_e1fOpsUEbP4qyyrTXQdeJAmEX_fg\"}},\"continuation\":\""+ continuationToken + "\"}";

        // Jsoup을 사용하여 요청 생성
        Connection.Response response = Jsoup.connect(url)
                .userAgent(userAgent)
                .header("Content-Type", "application/json")
                .referrer(url)
                .requestBody(requestBody)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        // 응답 출력
        System.out.println("Response Code: " + response.statusCode());
//        System.out.println("Response Body:\n" + response.body());
        
        return new JSONObject(response.body());
	}
	public String parseContainContinuationTokenData(Document doc) {
		String extractedData = null;
			try {
					
			        // 정규표현식 패턴
			        String patternString = "continuationCommand.*\\;";
//
//			        // 패턴 컴파일
			        Pattern pattern = Pattern.compile(patternString);
//
//			        // 매처 생성
			        Matcher matcher = pattern.matcher(doc.html());
//
//			        // 매칭 확인 후 추출
			        if (matcher.find()) {
			            extractedData = matcher.group(0).trim();
			            System.out.println("Extracted Data: " + extractedData.subSequence(0, 100));
			        } else {
			            System.out.println("Pattern not found in the input string.");
			        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extractedData;
	}
}
