package com.lezhin.test.dao;

import javax.annotation.Resource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.lezhin.test.vo.UserVO;

@Repository
public class UserDao {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    private static final String NAMESPACE = "user";

    @Resource
    private SqlSession sqlSession = null;

    public UserVO select(UserVO userVO) {
        return sqlSession.selectOne(NAMESPACE + ".select", userVO);
    }

    public int insert(UserVO userVO) {
        return sqlSession.insert(NAMESPACE + ".insert", userVO);
    }

    public int delete(UserVO userVO) {
        return sqlSession.delete(NAMESPACE + ".delete", userVO);
    }

    public UserVO selectByName(UserVO userVO) {
        return sqlSession.selectOne(NAMESPACE + ".selectByName", userVO);
    }

}
