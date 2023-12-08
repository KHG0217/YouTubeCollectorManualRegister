package com.tapacross.sns.crawler.youtube.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.tapacross.sns.crawler.youtube.YouTubeInfluencerData;

@Repository
public class YouTubeInfluencerCrawlerDaoImpl implements YouTubeInfluencerCrawlerDao {
	@Resource(name = "sqlSessionTemplate") //sqlSessionTemplate2 //dev
	private SqlSession sqlSession;
	
	@Override
	public int insertYouTubeChannel(YouTubeInfluencerData entity) throws DataAccessException {
		return sqlSession.insert("sql.resources.youtubeinfluencerdao.insertYouTubeChannel", entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<YouTubeInfluencerData> selectDuplicationYouTubeChannelList() throws DataAccessException {
		return sqlSession.selectList("sql.resources.youtubeinfluencerdao.selectDuplicationYouTubeChannelList");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<YouTubeInfluencerData> selectNotDuplicatedYouTubeChannelList() throws DataAccessException {
		return sqlSession.selectList("sql.resources.youtubeinfluencerdao.selectNotDuplicatedYouTubeChannelList");
	}

	@Override
	public int updateYouTubeChannelPriority(YouTubeInfluencerData entity) throws DataAccessException {
		return sqlSession.update("sql.resources.youtubeinfluencerdao.updateYouTubeChannelPriority",entity);
	}

	@Override
	public int updateYouTubeChannelStatus(YouTubeInfluencerData entity) throws DataAccessException {
		return sqlSession.update("sql.resources.youtubeinfluencerdao.updateYouTubeChannelStatus",entity);
	}

	@Override
	public int updateYouTubeChannelPriorityStatus(YouTubeInfluencerData entity) throws DataAccessException {
		return sqlSession.update("sql.resources.youtubeinfluencerdao.updateYouTubeChannelPriorityStatus",entity);
	}

	@Override
	public int updateYouTubeChannelInfo(YouTubeInfluencerData entity) throws DataAccessException {
		return sqlSession.update("sql.resources.youtubeinfluencerdao.updateYouTubeChannelInfo",entity);
	}

}
