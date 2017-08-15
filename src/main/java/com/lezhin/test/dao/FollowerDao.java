package com.lezhin.test.dao;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.lezhin.test.vo.FollowerVO;

@Repository
public class FollowerDao {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(FollowerDao.class);

    private static final String NAMESPACE = "follower";

    @Resource
    private SqlSession sqlSession = null;

    public FollowerVO select(FollowerVO followerVO) {
        return sqlSession.selectOne(NAMESPACE + ".select", followerVO);
    }

    public int insert(FollowerVO followerVO) {
        return sqlSession.insert(NAMESPACE + ".insert", followerVO);
    }

    public int delete(FollowerVO followerVO) {
        return sqlSession.delete(NAMESPACE + ".delete", followerVO);
    }

    public List<Map<String,Object>> selectMyFollowers(int userid) {
        return sqlSession.selectList(NAMESPACE + ".selectMyFollowers", userid);
    }

    public List<Map<String,Object>> selectFollowMe(int userid) {
        return sqlSession.selectList(NAMESPACE + ".selectFollowMe", userid);
    }

}
