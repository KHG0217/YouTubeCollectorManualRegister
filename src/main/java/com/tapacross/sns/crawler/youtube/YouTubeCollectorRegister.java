package com.tapacross.sns.crawler.youtube;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.tapacross.sns.crawler.youtube.service.YouTubeInfluencerCrawlerServiceImpl;
import com.tapacross.sns.util.FileUtil;
import com.tapacross.sns.util.ThreadUtil;

public class YouTubeCollectorRegister {
	
	private GenericXmlApplicationContext applicationContext;
	private YouTubeInfluencerCrawlerServiceImpl service;
	List<YouTubeInfluencerData> youTubeDBDataList;
	List<YouTubeInfluencerData> youTubeDataLogList = new ArrayList<>();
	
	public void init() {
		applicationContext = new GenericXmlApplicationContext(
				"classpath:spring/application-context.xml");
		service = applicationContext.getBean(YouTubeInfluencerCrawlerServiceImpl.class);			
		/* DB youTube Data ( service에서 중복을 제거)*/
		 youTubeDBDataList = service.selectYouTubeChannelAll();		
	}


	
	/**
	 * request_youtube_collector.txt 에 기록 되어있는 url과 카테고리값
	 * List<Map>에 담아 반환한다.
	 * 	Key값 : [URL]
	 * 	Value값 : [카테고리번호]
	 * 불필요한 [,] 값은 main 메소드에서 제거한다.
	 * 
	 * !request_youtube_collector.txt 에 값을 넣는 방식
	 * 		1. 구글시트에 요청된 행을 복사한다. ex) 		1	V	곽튜브	https://www.youtube.com/channel/UClRNDVO8093rmRTtLe4GEPw	2023.10.24	주윤하	신규 수집원 추가	2023.03.06			1004
	 * 		2. 복사한행을 엑셀파일에 붙여넣고, url과 마지막 카테고리값을 제외한 모든열을 삭제한다. ex)https://www.youtube.com/channel/UClRNDVO8093rmRTtLe4GEPw	1004
	 * 		3. 해당 행들을 복사하여 request_youtube_collector.txt 에 붙여넣는다.													

	 */
	private List<Map<String,String>> readList() {
		String listIndexPath ="../youtube-collector-manual-register/src/main/resources/data/request_youtube_collector.txt";	
		
		String lines = null;
		try {
			lines = FileUtil.readFromFile(listIndexPath, "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String[] splitStrings = lines.split("\r\n");
		
		List<Map<String, String>> mapList = new ArrayList<>();
		
		for (String splitString : splitStrings ) {
			Map<String,String> map = new HashMap<>();
			
			String[] splitStrings2 = splitString.split("	");
			map.put(splitStrings2[0], splitStrings2[1]);
			mapList.add(map);
		}
		
		return mapList;
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
	
	/**
	 * url에서 유튜브 id값을 추출한다.
	 */
	private String extractCollectorId(String url) {
		return url.replaceAll("https\\:\\/\\/www\\.youtube\\.com\\/channel\\/", "");		
	}
	
	/**
	 * 유튜브 API를 호출하고 json데이터를 반환한다.
	 *	인자로 apiurl과 수집원 url, 수집원 id, 수집원 고유 continuationToken이 필요하다.
	 * @param apiUrl, url, collectorId, continuationToken
	 * 
	 */
	private JSONObject parseYouTubeAPI(String apiUrl, String url, String collectorId, String continuationToken) throws IOException	{
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0";
        String requestBody = "{\"context\":{\"client\":{\"hl\":\"ko\",\"gl\":\"KR\",\"remoteHost\":\"58.233.189.85\",\"deviceMake\":\"\",\"deviceModel\":\"\",\"visitorData\":\"CgtRTGRFQ192cEdWWSitocCrBjIICgJLUhICGgA%3D\",\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36,gzip(gfe)\",\"clientName\":\"WEB\",\"clientVersion\":\"2.20231204.06.00\",\"osName\":\"Windows\",\"osVersion\":\"10.0\",\"originalUrl\":\"https://www.youtube.com/channel/" + collectorId + "\",\"platform\":\"DESKTOP\",\"clientFormFactor\":\"UNKNOWN_FORM_FACTOR\",\"configInfo\":{\"appInstallData\":\"CK2hwKsGEKn3rwUQ65OuBRDqw68FEIPfrwUQ4tSuBRD8hbAFEJj8_hIQmvCvBRCikrAFEOidsAUQiIewBRDbr68FELfvrwUQrLevBRDT4a8FEJmUsAUQlPr-EhCu1P4SEInorgUQzK7-EhDUkrAFEL2ZsAUQvbauBRDr6P4SEOSz_hIQiOOvBRDh8q8FEJ6LsAUQyfevBRCvnrAFENWIsAUQt-r-EhDamLAFEPX5rwUQvvmvBRComrAFEN3o_hIQmZGwBRD1-_4SENDirwUQxp6wBRDcgrAFEOmMsAUQpoGwBRDbmbAFEJeDsAUQzICwBRDM364FEMeDsAUQ1-mvBRCrgrAFENShrwUQppqwBRCigbAFEL6KsAUQ57qvBRDuoq8FEM2VsAUQuIuuBRC8-a8FENnJrwUQpcL-EhD4mrAFEImXsAU%3D\"},\"userInterfaceTheme\":\"USER_INTERFACE_THEME_LIGHT\",\"timeZone\":\"Asia/Seoul\",\"browserName\":\"Chrome\",\"browserVersion\":\"114.0.0.0\",\"acceptHeader\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\",\"deviceExperimentId\":\"ChxOek13T1RNMk1EVXpNVGswTmpRME5EZzVNdz09EK2hwKsGGK2hwKsG\",\"screenWidthPoints\":336,\"screenHeightPoints\":937,\"screenPixelDensity\":1,\"screenDensityFloat\":1,\"utcOffsetMinutes\":540,\"connectionType\":\"CONN_CELLULAR_4G\",\"memoryTotalKbytes\":\"8000000\",\"mainAppWebInfo\":{\"graftUrl\":\"https://www.youtube.com/channel/"+ collectorId + "#\",\"pwaInstallabilityStatus\":\"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\",\"webDisplayMode\":\"WEB_DISPLAY_MODE_BROWSER\",\"isWebNativeShareAvailable\":true}},\"user\":{\"lockedSafetyMode\":false},\"request\":{\"useSsl\":true,\"internalExperimentFlags\":[],\"consistencyTokenJars\":[]},\"clickTracking\":{\"clickTrackingParams\":\"CBkQuy8YACITCNnd4tOT-oIDFTBH9QUd5HIH8g==\"},\"adSignalsInfo\":{\"params\":[{\"key\":\"dt\",\"value\":\"1701843117801\"},{\"key\":\"flash\",\"value\":\"0\"},{\"key\":\"frm\",\"value\":\"0\"},{\"key\":\"u_tz\",\"value\":\"540\"},{\"key\":\"u_his\",\"value\":\"3\"},{\"key\":\"u_h\",\"value\":\"1080\"},{\"key\":\"u_w\",\"value\":\"1920\"},{\"key\":\"u_ah\",\"value\":\"1040\"},{\"key\":\"u_aw\",\"value\":\"1920\"},{\"key\":\"u_cd\",\"value\":\"24\"},{\"key\":\"bc\",\"value\":\"31\"},{\"key\":\"bih\",\"value\":\"937\"},{\"key\":\"biw\",\"value\":\"319\"},{\"key\":\"brdim\",\"value\":\"-1920,0,-1920,0,1920,0,1920,1040,336,937\"},{\"key\":\"vis\",\"value\":\"1\"},{\"key\":\"wgl\",\"value\":\"true\"},{\"key\":\"ca_type\",\"value\":\"image\"}],\"bid\":\"ANyPxKrE1pe1o_f1751y__cMrboj2Kk70Nn9lE-BX2CfRussSEmjlXTn7ZS9C9_9OUYEL_e1fOpsUEbP4qyyrTXQdeJAmEX_fg\"}},\"continuation\":\""+ continuationToken + "\"}";

      // Jsoup을 사용하여 요청 생성
      Connection.Response response = Jsoup.connect(apiUrl)
              .userAgent(userAgent)
              .header("Content-Type", "application/json")
              .referrer(url)
              .requestBody(requestBody)
              .method(Connection.Method.POST)
              .ignoreContentType(true)
              .execute();

      // 응답 출력
//      System.out.println("Response Code: " + response.statusCode());
//      System.out.println("Response Body:\n" + response.body());
      
      return new JSONObject(response.body());
	}
	
	/**
	 * 유튜브 본문에서 continuationCommand값이 들어간 String값을 반환한다.
	 * 
	 * @param doc
	 */
	public String parseContainContinuationTokenData(Document doc) {
		String extractedData = null;
			try {				
			        String patternString = "continuationCommand.*\\;";
			        Pattern pattern = Pattern.compile(patternString);
			        Matcher matcher = pattern.matcher(doc.html());

			        if (matcher.find()) {
			            extractedData = matcher.group(0).trim();
//			            System.out.println("Extracted Data: " + extractedData.subSequence(0, 100));
			        } else {
			            System.out.println("Pattern not found in the input string.");
			        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extractedData;
	}
	
	/**
	 * parseContainContinuationTokenData메소드에서 받은 String값에서 continuationCommand:{"token:토큰값 을 모두 찾는다.
	 * 찾은 token값을 List에 담고 마지막 token값만 사용한다(사용자 token은 항상 마지막값에 있는 패턴 확인)
	 * 
	 * @param tokenContainData
	 */
	public String extractContinuationToken (String tokenContainData) {
		String[] dataArray = tokenContainData.split("\\,");
		String continuationToken = null;
		List<String> tokenList = new ArrayList<>();	
		for(String token : dataArray) {
			if(token.contains("continuationCommand\":{\"token\":")) {
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

	/**
	 * Document에서 유튜브 제목을 수집한다.
	 * @param doc
	 */
	public String parseTitle(Document doc) {
		String title = null;
		title = doc.select("body > title").text()
				.replaceAll(".\\-.YouTube", "");
		return title;
	}

	/**
	 * Document에서 수집원 사진 url을 수집한다.
	 * @param doc
	 */
	public String parserPicture(Document doc) {
		String picture = null;
		picture = doc.select("meta[property=og:image]").attr("content");
		return picture;
	}
	
	/**
	 * 유튜브 api로 받은 jsondata 에서 수집원 등록에 필요한 구독자수, 채널소개글, 동영상 수, 누적조회수를 파싱한다.
	 * @param jsonObject
	 */
	public YouTubeInfluencerData extractdataTest(JSONObject jsonObject) {
		YouTubeInfluencerData youTubeChannelInfo = new YouTubeInfluencerData();
		JSONObject aboutChannelViewModelJson = jsonObject.getJSONArray("onResponseReceivedEndpoints").getJSONObject(0)
				.getJSONObject("appendContinuationItemsAction").getJSONArray("continuationItems")
				.getJSONObject(0).getJSONObject("aboutChannelRenderer").getJSONObject("metadata")
				.getJSONObject("aboutChannelViewModel");
		
		/* 구독자 수 */
		int follower = 0;
		if(!aboutChannelViewModelJson.has("subscriberCountText")) {
			follower = 0;
		}else {
			String youTubFollowerData = aboutChannelViewModelJson.getString("subscriberCountText");
			String convertedYouTubFollowerData = YouTubeFollowerType.toType(youTubFollowerData).fare(youTubFollowerData);
			follower = Integer.parseInt(convertedYouTubFollowerData);
		}
		youTubeChannelInfo.setFollower(follower);
		
		/* 채널 소개글  */
		String bio = null;
		if(!aboutChannelViewModelJson.has("description")) {
			System.out.println("bio is null");
		}else {
			bio = aboutChannelViewModelJson.getString("description").replaceAll("\n", "");
		}

		youTubeChannelInfo.setBio(bio);
		
		/* 동영상 수 */
		int videoListCount = Integer.parseInt(aboutChannelViewModelJson.getString("videoCountText").replaceAll("[^0-9]", ""));
		youTubeChannelInfo.setListed(videoListCount);
		
		
		/* 누적 조회수 */
		Long totalViewCount = Long.parseLong(aboutChannelViewModelJson.getString("viewCountText").replaceAll("[^0-9]", ""));
		youTubeChannelInfo.setTotalView(totalViewCount);
		
		return youTubeChannelInfo;
	}
	
	/**
	 * YouTubeInfluencerData 객체에 수집원 정보를 받아 DB에 존재하는 수집원과 비교후 상태값을 변경한다
	 * 상태값변경 - updateYouTubeChannelPriorityStatus, updateYouTubeChannelPriority, 
	 * updateYouTubeChannelStatus, updateYouTubeChannelInfo, insertNewYouTubeChannel
	 * 현재 DB에는 중복 데이터가 많아 같은 데이터일경우 site_id값이 높은 수집원이 최신 데이터로 판단하고 중복을 제거한뒤, url로 중복체크를한다.
	 * @param jsonObject
	 */
	public String changeStateYouTubeChannels(YouTubeInfluencerData readYouTubeData) throws IOException {	
		
		String result = "";

		for(YouTubeInfluencerData data : youTubeDBDataList ) {
			if(data.getUrl().equals(readYouTubeData.getUrl())) {
				//  Priority 2번 , Status가 F일 경우 -> 1번, T 변경
				if(data.getPriority() == 2 && data.getStatus().equals("F")) {
					updateYouTubeChannelPriorityStatus(readYouTubeData);
					
					if(!data.getSiteName().equals(readYouTubeData.getSiteName())) {
						System.out.println("update siteName: " + data.getSiteName() + " -> " + readYouTubeData.getSiteName());
					}					
					System.out.println("updateYouTubeChannelPriorityStatus : " + readYouTubeData.getSiteName());
					
					result = "0";
					data.setLog(result);
					youTubeDataLogList.add(data);
					return result;
										
				}						
				// Priority 2번일경우 1로 변경
				if(data.getPriority() == 2) {
					// Priority 2번을 1번으로 바꿔주는 로직 
					updateYouTubeChannelPriority(readYouTubeData);
					System.out.println("updateYouTubeChannelPriority : " + readYouTubeData.getSiteName());
					
					if(!data.getSiteName().equals(readYouTubeData.getSiteName())) {
						System.out.println("update siteName: " + data.getSiteName() + " -> " + readYouTubeData.getSiteName());
					}
					System.out.println("updateYouTubeChannelPriority : " + readYouTubeData.getSiteName());
						
					result = "1";
					data.setLog(result);
					youTubeDataLogList.add(data);

					return result;
				}
				
				// Status가 F일 경우 T로 변경 -> pri 1 f인 data
				if(data.getStatus().equals("F")) {
					// T로 바꿔주는 로직 
					updateYouTubeChannelStatus(readYouTubeData);
					
					if(!data.getSiteName().equals(readYouTubeData.getSiteName())) {
						System.out.println("update siteName: " + data.getSiteName() + " -> " + readYouTubeData.getSiteName());
					}					
					System.out.println("updateYouTubeChannelStatus : " + readYouTubeData.getSiteName());
					
					result = "2";
					data.setLog(result);
					youTubeDataLogList.add(data);
					return result;
				}
				
				if(data.getStatus().equals("T") && data.getPriority() == 1) {
					result = "3";
					data.setLog(result);
					// sitename, bio, list, follower, totalView, picture 업데이트
					updateYouTubeChannelInfo(readYouTubeData);
					if(!data.getSiteName().equals(readYouTubeData.getSiteName())) {
						System.out.println("update siteName: " + data.getSiteName() + " -> " + readYouTubeData.getSiteName());
					}
					System.out.println("WorkingData : " + readYouTubeData.getSiteName());
					youTubeDataLogList.add(data);
					return result;
				}				
			}
		}
		
		// insert 문 
		try {
			result = "4";
			readYouTubeData.setLog(result);
			insertNewYouTubeChannel(readYouTubeData);	
			System.out.println("insertNewYouTubeChannel : " + readYouTubeData.getSiteName());
			youTubeDataLogList.add(readYouTubeData);
			return result;
		}catch (Exception e) {
			result ="5";
			readYouTubeData.setLog(result);
			youTubeDataLogList.add(readYouTubeData);
			e.printStackTrace();
			// fail log 남겨주기 
		}

		return result;
	}
	
	/**
	 * changeStateYouTubeChannels 메소드를 실행하여 DB에 변경된 유형에 따라 result 값을 받아온후
	 * (UpdatePriorityStatus - 0, UpdatePriority - 1, UpdateStatus - 2, NotUpdate - 3, Insert - 4, ERROR - 5)
	 * result값 에 따라 로그파일을 생성한다.
	 * 해당경로 C:\home\youTube_manual_register_log\ 폴더에 년월일_시분초 이름의 폴더를 생성후
	 * DB 변경된 유형에 해당하는 로그를 생성한다.
	 * @param list
	 * @throws IOException
	 */
	public void createLog(List<YouTubeInfluencerData> list) throws IOException {
		BufferedWriter changePriorityStatusWriter = null;
		BufferedWriter changePriorityWriter = null;
		BufferedWriter changeStatusWriter = null;
		BufferedWriter workingDataWriter = null;
		BufferedWriter newDataWriter = null;
		BufferedWriter errorDataWriter = null;
		
		int changePriorityStatusCnt = 0;
		int changePriorityCnt = 0;
		int changeChangeStatusCnt = 0;
		int changeWorkingDataCnt = 0;
		int newDataCnt = 0;
		int errorDataDataCnt = 0;
		
		SimpleDateFormat format = new SimpleDateFormat("YYMMdd_HHmmss");
		Date time = new Date();
		String fmtDate=format.format(time);
		
		String mkPath = "C:\\home\\youTube_manual_register_log\\";
		String fileName = "";
				
		File folder = new File(mkPath);
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		String mkPath2 =mkPath+fmtDate+"\\";
		File folder2 = new File(mkPath2);
		if(!folder2.exists()) {
			folder2.mkdir();
		}
				
		for(YouTubeInfluencerData data : list) {
		
			switch (data.getLog()) {
			case "0":
				fileName ="ChangePriorityAndStatus";
				String filePathChangePriorityStatus = mkPath2 + fileName+".txt";
				File ChangePriorityStatus = new File(filePathChangePriorityStatus);
				
		        if(!ChangePriorityStatus.exists()){ 
		        	ChangePriorityStatus.createNewFile();
		        }
		        
		        changePriorityStatusWriter = new BufferedWriter(new FileWriter(ChangePriorityStatus, true));
		        changePriorityStatusWriter.write("[siteName = " + data.getSiteName()+","+
							        		"url = " + data.getUrl()+","+
							        		"siteId = " + data.getSiteId()+"]");
		        changePriorityStatusCnt++;
		        
		        changePriorityStatusWriter.newLine();
		        changePriorityStatusWriter.flush(); 
		        changePriorityStatusWriter.close();
		        
		        
				break;
			case "1":
				fileName ="ChangePriority";
				String filePathChangePriority = mkPath2 + fileName+".txt";
				File ChangePriority = new File(filePathChangePriority);
				
		        if(!ChangePriority.exists()){ 
		        	ChangePriority.createNewFile();
		        }
		        
		        changePriorityWriter = new BufferedWriter(new FileWriter(ChangePriority, true));
		        changePriorityWriter.write("[siteName = " + data.getSiteName()+","+
		        							"url = " + data.getUrl()+","+
		        							"siteId = " + data.getSiteId()+"]");
		        changePriorityCnt++;
		        
		        changePriorityWriter.newLine();
		        changePriorityWriter.flush(); 
		        changePriorityWriter.close();
		        
		        
				break;
				
			case "2":
				fileName ="ChangeStatus";
				String filePathChangeStatusList = mkPath2 + fileName+".txt";
				File ChangeStatus = new File(filePathChangeStatusList);
				
		        if(!ChangeStatus.exists()){ 
		        	ChangeStatus.createNewFile();
		        }
		        
		        changeStatusWriter = new BufferedWriter(new FileWriter(ChangeStatus, true));
		        changeStatusWriter.write("[siteName = " + data.getSiteName()+ ","+
		        		"url = " + data.getUrl()+","+
		        		"siteId = " + data.getSiteId()+"]");
		        
		        changeChangeStatusCnt++;
		        
		        changeStatusWriter.newLine();
		        changeStatusWriter.flush(); 
		        changeStatusWriter.close();
		        
				break;
				
			case "3":
				fileName ="WorkingData";
				String filePathWorkingDataList = mkPath2 + fileName+".txt";
				File WorkingData = new File(filePathWorkingDataList);
				
		        if(!WorkingData.exists()){ 
		        	WorkingData.createNewFile();
		        }
		        
		        workingDataWriter = new BufferedWriter(new FileWriter(WorkingData, true));
		        workingDataWriter.write("[siteName = " + data.getSiteName()+ ","+
		        		"url = " + data.getUrl()+","+
		        		"siteId = " + data.getSiteId()+"]");
		        
		        changeWorkingDataCnt++;
		        
		        workingDataWriter.newLine();
		        workingDataWriter.flush(); 
		        workingDataWriter.close();
		        
				break;
				
			case "4":
				fileName ="NewData";
				String filePathNewDataList =  mkPath2 + fileName+".txt";
				File NewData = new File(filePathNewDataList);
				
		        if(!NewData.exists()){ 
		        	NewData.createNewFile();
		        }
		        
		        newDataWriter = new BufferedWriter(new FileWriter(NewData, true));
		        newDataWriter.write("[siteName = " + data.getSiteName()+ ","+
		        		"url = " + data.getUrl()+"]");
		        
		        newDataCnt++;
		        
		        newDataWriter.newLine();
		        newDataWriter.flush(); 
		        newDataWriter.close();
				break;
			case "5":
				fileName ="ErrorData";
				String filePathErrorDataList =  mkPath2 + fileName+".txt";
				File ErrorData = new File(filePathErrorDataList);
				
		        if(!ErrorData.exists()){ 
		        	ErrorData.createNewFile();
		        }
		        
		        errorDataWriter = new BufferedWriter(new FileWriter(ErrorData, true));
		        errorDataWriter.write("[siteName = " + data.getSiteName()+ ","+
		        		"url = " + data.getUrl()+","+
		        		"siteId = " + data.getSiteId()+"]");
		        
		        errorDataDataCnt++;
		        
		        errorDataWriter.newLine();
		        errorDataWriter.flush(); 
		        errorDataWriter.close();
				break;
			default:
				break;
			}
		}
		System.out.println(
				"changePriorityStatusCnt = "+changePriorityStatusCnt+"\r\n" +
				"changePriorityCnt = "+changePriorityCnt+"\r\n" + 
				"changeChangeStatusCnt = "+changeChangeStatusCnt+"\r\n" +
				"WorkingDataCnt = "+changeWorkingDataCnt+"\r\n" +
				"NewDataCnt = "+newDataCnt+"\r\n" +
				"errorDataDataCnt =" +errorDataDataCnt
				);


	}
	
	private void insertNewYouTubeChannel(YouTubeInfluencerData data) {
		service.insertYouTubeChannel(data);		
	}
	
	private void updateYouTubeChannelPriority(YouTubeInfluencerData data) {
		service.updateYouTubeChannelPriority(data);
	}
	
	private void updateYouTubeChannelStatus(YouTubeInfluencerData data) {
		service.updateYouTubeChannelStatus(data);
	}
	
	private void updateYouTubeChannelPriorityStatus(YouTubeInfluencerData data) {
		service.updateYouTubeChannelPriorityStatus(data);
	
	}
	
	private void updateYouTubeChannelInfo(YouTubeInfluencerData data) {
		service.updateYouTubeChannelInfo(data);
	
	}
		
	public static void main(String[] args) {
		YouTubeCollectorRegister pr = new YouTubeCollectorRegister();
		pr.init();
		List<Map<String, String>> requestYouTubeCollectorList = pr.readList();
		String apiUrl ="https://www.youtube.com/youtubei/v1/browse?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8&prettyPrint=false";

		for(Map<String,String> requestYouTubeCollector : requestYouTubeCollectorList) {
			String url = requestYouTubeCollector.keySet().toString()
					.replaceAll("\\[", "")
					.replaceAll("\\]", "");
			String category = requestYouTubeCollector.values().toString()
					.replaceAll("\\[", "")
					.replaceAll("\\]", "");
			String collectorId = pr.extractCollectorId(url);
			try {
				Document doc = pr.parseUrl(url);
				String tokenContainData = pr.parseContainContinuationTokenData(doc);
				if(tokenContainData == null) {
					System.out.println("tokenContainData is null, url: " + url);
					YouTubeInfluencerData youTubeInfluencerError = new YouTubeInfluencerData();
					youTubeInfluencerError.setUrl(url);
					youTubeInfluencerError.setLog("5");
					youTubeInfluencerError.setSiteName("tokenContainData is null");
					pr.youTubeDataLogList.add(youTubeInfluencerError);
					continue;
				}
				String continuationToken = pr.extractContinuationToken(tokenContainData);
				JSONObject jsonObject = pr.parseYouTubeAPI(apiUrl, url, collectorId, continuationToken);
				YouTubeInfluencerData youTubeInfluencer = pr.extractdataTest(jsonObject);
				youTubeInfluencer.setSiteCategory(category);
				youTubeInfluencer.setSiteName(pr.parseTitle(doc));			
				youTubeInfluencer.setPicture(pr.parserPicture(doc));
				youTubeInfluencer.setUrl(url);
				
				System.out.println(youTubeInfluencer.toString());
				
				/* insert or update */
				pr.changeStateYouTubeChannels(youTubeInfluencer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				YouTubeInfluencerData youTubeInfluencerError = new YouTubeInfluencerData();
				youTubeInfluencerError.setUrl(url);
				youTubeInfluencerError.setLog("5");
				youTubeInfluencerError.setSiteName("tokenContainData is null");
				pr.youTubeDataLogList.add(youTubeInfluencerError);
				e.printStackTrace();
			}			
		}	
		
		try {
			pr.createLog(pr.youTubeDataLogList);
		}catch (Exception e) {
			e.printStackTrace();
		}

	}	
}
