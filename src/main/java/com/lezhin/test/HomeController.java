package com.lezhin.test;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.lezhin.test.dao.FollowerDao;
import com.lezhin.test.dao.UserDao;
import com.lezhin.test.vo.FollowerVO;
import com.lezhin.test.vo.UserVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Inject
    private JdbcTemplate jdbcTemplate = null;

    @Inject
    private UserDao userDao = null;

    @Inject
    private FollowerDao followerDao = null;

    /**
     * 테스트를 위한 사용자ID 목록과 api 키 리스트
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT userid, name, showname, api_accesskey FROM user ORDER BY userid");

        logger.info(result.size() + " rows fetched");

        model.addAttribute("userList", result);

        return "home";
    }

    /**
     * 사용자 추가 요청 폼
     */
    @RequestMapping(value = "/adduser.do", method = RequestMethod.GET)
    public String addUserForm(Model model) {
        return "adduser";
    }

    /**
     * 사용자 등록 처리
     */
    @RequestMapping(value = "/adduser.do", method = RequestMethod.POST)
    public String addUser(Model model,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "showname", required = true) String showname)
            throws Exception {

        logger.info("name [" + name + "] showname [" + showname + "]");
        if (name.length() == 0 || showname.length() == 0)
            throw new Exception("empty field(s) found. fill `id` and `name`.");

        // create random access key
        String api_accesskey = jdbcTemplate.queryForObject("SELECT md5(?)",
            String.class, String.valueOf(System.currentTimeMillis()));

        logger.info("api_key [" + api_accesskey + "]");

        UserVO userVO = new UserVO();
        userVO.setName(name);
        userVO.setShowName(showname);
        userVO.setApiAccessKey(api_accesskey);

        userDao.insert(userVO);

        int id = userVO.getUserId();
        logger.info("Insert key " + id);

        // 자기자신 팔로우 추가 (이거 없으면 뉴스피드에 자기 글이 안 나옴)
        FollowerVO follower = new FollowerVO();
        follower.setUserId(id);
        follower.setFollowId(id);

        followerDao.insert(follower);

        return "redirect:/";
    }

}
