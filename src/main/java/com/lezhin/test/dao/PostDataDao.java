package com.lezhin.test.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.lezhin.test.vo.PostDataVO;

@Repository
public class PostDataDao {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(PostDataDao.class);

    private static final String NAMESPACE = "postdata";

    @Resource
    private SqlSession sqlSession = null;

    public PostDataVO select(PostDataVO postDataVO) {
        return sqlSession.selectOne(NAMESPACE + ".select", postDataVO);
    }

    public int insert(PostDataVO postDataVO) {
        return sqlSession.insert(NAMESPACE + ".insert", postDataVO);
    }

    public int update(PostDataVO postDataVO) {
        return sqlSession.update(NAMESPACE + ".update", postDataVO);
    }

    public int delete(PostDataVO postDataVO) {
        return sqlSession.delete(NAMESPACE + ".delete", postDataVO);
    }

    public List<Map<String, Object>> selectTimeline(int userid, int start,
            int count) {
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("userId", new Integer(userid));
        param.put("start", new Integer(start));
        param.put("count", new Integer(count));

        return sqlSession.selectList(NAMESPACE + ".selectTimeline", param);
    }

}
