package com.tapacross.sns.crawler.youtube;

import java.math.BigDecimal;

public enum YouTubeFollowerType {
	ONE_THOUSAND_UNDER ("명"){	
		@Override
		public String fare(String distance) {
			float num = Float.parseFloat(distance
					.replaceAll("구독자", "")
					.replaceAll("명", ""));
			distance = Float.toString(num).replaceAll("\\.[0-9]+", "");
			return distance;
		}	
	},
	
	ONE_THOUSAND ("천명"){

		@Override
		public String fare(String distance) {
			float num = Float.parseFloat(distance
					.replaceAll("구독자", "")
					.replaceAll("천명", "")) * 1000;
			distance = Float.toString(num).replaceAll("\\.[0-9]+", "");
			return distance;
		}	

	},	

	TNE_THOUSAND ("만명"){	
		@Override
		public String fare(String distance) {
			float num = Float.parseFloat(distance
					.replaceAll("구독자", "")
					.replaceAll("만명", "")) * 10000;
			BigDecimal bigDecimal = new BigDecimal(num);
			distance = bigDecimal.toString().replaceAll("\\.[0-9]+", "");
			return distance;
		}	
	};

	private static YouTubeFollowerType[] YouTubeFollowerEnumList = {ONE_THOUSAND, TNE_THOUSAND, ONE_THOUSAND_UNDER};
	public abstract String fare(String distance);
	
	public String getValue() {
		return value;
	}

	private String value;
	
	private YouTubeFollowerType(String value) {
		this.value = value;
	}

	public static YouTubeFollowerType toType(String value) {
		
		for(YouTubeFollowerType enumData : YouTubeFollowerEnumList) {
			if(value.contains(enumData.value)){
				return enumData;
			} 
		}
		return null;
	}
}
