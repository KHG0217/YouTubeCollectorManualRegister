package com.tapacross.sns.crawler.youtube.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.tapacross.sns.crawler.youtube.YouTubeInfluencerData;

public interface YouTubeInfluencerCrawlerDao {
	
	int insertYouTubeChannel(YouTubeInfluencerData entity) throws DataAccessException;	
	List<YouTubeInfluencerData> selectDuplicationYouTubeChannelList() throws DataAccessException;
	List<YouTubeInfluencerData> selectNotDuplicatedYouTubeChannelList() throws DataAccessException;
	int updateYouTubeChannelPriority(YouTubeInfluencerData entity) throws DataAccessException;
	int updateYouTubeChannelStatus(YouTubeInfluencerData entity) throws DataAccessException;
	int updateYouTubeChannelPriorityStatus(YouTubeInfluencerData entity) throws DataAccessException;
	int updateYouTubeChannelInfo(YouTubeInfluencerData entity) throws DataAccessException;
}
