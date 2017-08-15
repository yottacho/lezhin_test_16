package com.lezhin.test;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.lezhin.test.dao.FollowerDao;
import com.lezhin.test.dao.UserDao;
import com.lezhin.test.module.Authorization;
import com.lezhin.test.vo.FollowerVO;
import com.lezhin.test.vo.UserVO;

/**
 * 팔로우 기능 다루는 컨트롤러
 */
@RestController
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Inject
    private Authorization authorization = null;

    @Inject
    private FollowerDao followerDao = null;

    @Inject
    private UserDao userDao = null;

    /**
     * 내가 팔로우하는 사용자 목록, 인증필요
     * 
     * @param auth Authorization 사용자 정보
     * @return
     */
    @RequestMapping(value = "/follow/my", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getFollowList(
            @RequestHeader(value = "Authorization", required = true) String auth) {
        logger.info("GET /follow/my");

        Map<String, Object> result = new Hashtable<String, Object>();
        UserVO user = authorization.getUserFromHeader(auth);
        if (user == null) {
            logger.error("User is null");
            result.clear();
            result.put("ERROR_MESSAGE", "authorize");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        logger.info("User: " + user.getName());

        return getFollowListUser(user.getName());
    }

    /**
     * 특정 사용자의 팔로우 목록 조회, 인증 불필요
     * 
     * @param auth Authorization 사용자 정보
     * @return
     */
    @RequestMapping(value = "/follow/user/{userid}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getFollowListUser(
            @PathVariable(value = "userid") String userid) {
        logger.info("GET /follow/user/" + userid);

        Map<String, Object> result = new Hashtable<String, Object>();

        // 조회할 사용자 정보
        UserVO followUser = new UserVO();
        followUser.setName(userid);
        followUser = userDao.selectByName(followUser);
        if (followUser == null) {
            logger.error("target user not found");
            result.clear();
            result.put("ERROR_MESSAGE", "not found");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        List<Map<String, Object>> followers = followerDao.selectMyFollowers(followUser.getUserId());

        result.put("followers", followers);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 나를 팔로우하는 사용자 목록, 인증필요
     * 
     * @param auth Authorization 사용자 정보
     * @return
     */
    @RequestMapping(value = "/follow/me", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getFollowListMe(
            @RequestHeader(value = "Authorization", required = true) String auth) {
        logger.info("GET /follow/me");

        Map<String, Object> result = new Hashtable<String, Object>();
        UserVO user = authorization.getUserFromHeader(auth);
        if (user == null) {
            logger.error("User is null");
            result.clear();
            result.put("ERROR_MESSAGE", "authorize");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        logger.info("User: " + user.getName());

        List<Map<String, Object>> followers = followerDao.selectFollowMe(user.getUserId());

        result.put("followers", followers);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 팔로우 등록, 인증필요
     * 
     * @param auth Authorization 사용자 정보
     * @return
     */
    @RequestMapping(value = "/follow/{userid}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> postFollow(
            @RequestHeader(value = "Authorization", required = true) String auth,
            @PathVariable(value = "userid") String userid) {
        logger.info("POST /follow/" + userid);

        Map<String, Object> result = new Hashtable<String, Object>();
        UserVO user = authorization.getUserFromHeader(auth);
        if (user == null) {
            logger.error("User is null");
            result.clear();
            result.put("ERROR_MESSAGE", "authorize");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        logger.info("User: " + user.getName());

        // 자기 자신을 팔로우하려고 하는지 체크
        if (userid.equals(user.getName())) {
            logger.error("follow me");
            result.clear();
            result.put("ERROR_MESSAGE", "follow me");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // 팔로우할 사용자 정보
        UserVO followUser = new UserVO();
        followUser.setName(userid);
        followUser = userDao.selectByName(followUser);
        if (followUser == null) {
            logger.error("target user not found");
            result.clear();
            result.put("ERROR_MESSAGE", "not found");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        FollowerVO follower = new FollowerVO();
        follower.setUserId(user.getUserId());
        follower.setFollowId(followUser.getUserId());

        // 팔로우 맺어져 있는지 확인
        if (followerDao.select(follower) != null) {
            logger.error("already followered");
            result.clear();
            result.put("ERROR_MESSAGE", "already followered");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // 팔로우 등록
        followerDao.insert(follower);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 팔로우 해제, 인증필요
     * 
     * @param auth Authorization 사용자 정보
     * @return
     */
    @RequestMapping(value = "/follow/{userid}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteFollow(
            @RequestHeader(value = "Authorization", required = true) String auth,
            @PathVariable(value = "userid") String userid) {
        logger.info("DELETE /follow/" + userid);

        Map<String, Object> result = new Hashtable<String, Object>();
        UserVO user = authorization.getUserFromHeader(auth);
        if (user == null) {
            logger.error("User is null");
            result.clear();
            result.put("ERROR_MESSAGE", "authorize");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        logger.info("User: " + user.getName());

        // 자기 자신을 삭제하려 하는지 체크
        if (userid.equals(user.getName())) {
            logger.error("unfollow me");
            result.clear();
            result.put("ERROR_MESSAGE", "unfollow me");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // 언팔 사용자 정보
        UserVO unfollowUser = new UserVO();
        unfollowUser.setName(userid);
        unfollowUser = userDao.selectByName(unfollowUser);
        if (unfollowUser == null) {
            logger.error("target user not found");
            result.clear();
            result.put("ERROR_MESSAGE", "not found");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        FollowerVO follower = new FollowerVO();
        follower.setUserId(user.getUserId());
        follower.setFollowId(unfollowUser.getUserId());

        // 팔로우 맺어져 있는지 확인
        follower = followerDao.select(follower);
        if (follower == null) {
            logger.error("not followered");
            result.clear();
            result.put("ERROR_MESSAGE", "not found");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        // 팔로우 해제
        followerDao.delete(follower);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
