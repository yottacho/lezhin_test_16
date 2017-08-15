package com.lezhin.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lezhin.test.dao.PostDataDao;
import com.lezhin.test.dao.UserDao;
import com.lezhin.test.module.Authorization;
import com.lezhin.test.vo.PostDataVO;
import com.lezhin.test.vo.UserVO;

/**
 * feed 다루는 REST API 컨트롤러
 */
@RestController
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Inject
    private Authorization authorization = null;

    @Inject
    private PostDataDao postDataDao = null;

    @Inject
    private UserDao userDao = null;

    /**
     * 기본 뉴스피드, 인증 필수
     * 
     * @param auth Authorization 사용자 정보
     * @return
     */
    @RequestMapping(value = "/feed/timeline", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTimelineOwner(
            @RequestHeader(value = "Authorization", required = true) String auth,
            @RequestParam(value = "start", required = false, defaultValue = "-1") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "100") int count) {
        logger.info("GET /feed/timeline?start=" + start + "&limit=" + count);

        Map<String, Object> result = new Hashtable<String, Object>();

        UserVO user = authorization.getUserFromHeader(auth);
        if (user == null) {
            logger.error("User is null");
            result.clear();
            result.put("ERROR_MESSAGE", "authorize");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        logger.info("User: " + user.getName());

        return getTimelineUser(user.getName(), start, count);
    }

    /**
     * 특정 사용자의 뉴스피드, 인증 불필요
     * 
     * @param userid 사용자ID
     * @return
     */
    @RequestMapping(value = "/feed/timeline/{userid}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTimelineUser(
            @PathVariable(value = "userid") String userid,
            @RequestParam(value = "start", required = false, defaultValue = "-1") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "100") int count) {
        logger.info("GET /feed/timeline/" + userid + "?start=" + start
                + "&limit=" + count);

        if (start < 0)
            start = Integer.MAX_VALUE;

        if (count <= 0 || count > 500)
            count = 100;

        Map<String, Object> result = new Hashtable<String, Object>();

        // 작성자 정보
        UserVO user = new UserVO();
        user.setName(userid);
        user = userDao.selectByName(user);
        if (user == null) {
            logger.error("userid not found");
            result.clear();
            result.put("ERROR_MESSAGE", "userid not found");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        List<Map<String, Object>> timeline = postDataDao.selectTimeline(user.getUserId(), start, count);
        result.put("timeline", timeline);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 특정 포스트 상세보기, 인증 불필요
     * 
     * @param id 포스트ID
     * @return
     */
    @RequestMapping(value = "/feed/show/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getStatus(
            @PathVariable(value = "id") int id) {
        logger.info("GET /feed/show/" + id);

        Map<String, Object> result = new Hashtable<String, Object>();

        PostDataVO postVO = new PostDataVO();
        postVO.setId(id);

        postVO = postDataDao.select(postVO);
        if (postVO == null) {
            logger.error("postid not found");
            result.clear();
            result.put("ERROR_MESSAGE", "postid not found");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        result.put("id", postVO.getId());

        // 작성자 정보
        UserVO user = new UserVO();
        user.setUserId(postVO.getUserId());
        user = userDao.select(user);
        if (user == null) {
            result.put("userid", "?");
            result.put("dispname", "?");
        }
        result.put("userid", user.getName());
        result.put("dispname", user.getShowName());
        result.put(
            "date",
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(postVO.getDate()));
        result.put("content", postVO.getContent());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 포스팅 등록, 인증 필요
     * 
     * @param auth Authorization key
     * @param data {"content" : ":data"}
     * @return 201: Created
     */
    @RequestMapping(value = "/feed", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> postStatus(
            @RequestHeader(value = "Authorization", required = true) String auth,
            @RequestBody(required = true) Hashtable<String, Object> data) {
        logger.info("POST /feed");

        Map<String, Object> result = new Hashtable<String, Object>();
        UserVO user = authorization.getUserFromHeader(auth);
        if (user == null) {
            logger.error("User is null");
            result.clear();
            result.put("ERROR_MESSAGE", "authorize");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        logger.info("User: " + user.getName());

        String content = (String) data.get("content");
        if (content == null) {
            logger.error("Content is null");
            result.clear();
            result.put("ERROR_MESSAGE", "null content");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        PostDataVO postData = new PostDataVO();
        postData.setUserId(user.getUserId());
        postData.setDate(Calendar.getInstance().getTime());
        postData.setContent(content);

        postDataDao.insert(postData);

        logger.info("Insert success: " + postData.getId());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 포스팅 삭제, 인증 필요
     * 
     * @param auth Authorization key
     * @return 201: Created
     */
    @RequestMapping(value = "/feed/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteStatus(
            @RequestHeader(value = "Authorization", required = true) String auth,
            @PathVariable(value = "id") String id) {
        logger.info("DELETE /feed/" + id);

        Map<String, Object> result = new Hashtable<String, Object>();
        UserVO user = authorization.getUserFromHeader(auth);
        if (user == null) {
            logger.error("User is null");
            result.clear();
            result.put("ERROR_MESSAGE", "authorize");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        logger.info("User: " + user.getName());

        PostDataVO postData = new PostDataVO();
        postData.setId(Integer.parseInt(id));

        postData = postDataDao.select(postData);
        // post not found or user missmatch
        if (postData == null || user.getUserId() != postData.getUserId()) {
            logger.error("Post id not found or user missmatch: "
                    + (postData == null ? "null" : "user"));
            result.clear();
            result.put("ERROR_MESSAGE", "post not found");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        postDataDao.delete(postData);

        logger.info("Delete success: " + postData.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
