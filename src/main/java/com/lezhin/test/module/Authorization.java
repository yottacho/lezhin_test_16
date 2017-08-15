package com.lezhin.test.module;

import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.lezhin.test.vo.UserVO;

@Component
public class Authorization {
    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);

    @Inject
    private JdbcTemplate jdbcTemplate = null;

    /**
     * Authorization 헤더에서 사용자 정보를 가져온다.
     * 
     * @param header Authorization header
     * @return UserVO or null
     */
    public UserVO getUserFromHeader(String header) {
        // Authorization check

        // Authorization type
        if (!header.toLowerCase().startsWith("bearer ")) {
            logger.debug("Authorization format error [" + header + "]");
            return null;
        }

        if (header.length() < 8) {
            logger.debug("Authorization format error [" + header + "]");
            return null;
        }

        String apiKey = header.substring(7).trim();
        return getUserFromApiKey(apiKey);
    }

    /**
     * api key 로 사용자 정보를 가져온다.
     * 
     * @param apiKey api key
     * @return UserVO or null
     */
    public UserVO getUserFromApiKey(String apiKey) {
        UserVO user = null;

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT userid, name, showname, api_accesskey FROM user WHERE api_accesskey = ?",
                apiKey);
            logger.debug("user [" + result.get("name") + "]");

            user = new UserVO();
            user.setUserId((int) result.get("userid"));
            user.setName((String) result.get("name"));
            user.setShowName((String) result.get("showname"));
            user.setApiAccessKey((String) result.get("api_accesskey"));
        }
        catch (EmptyResultDataAccessException ee) {
            logger.error("Api key[" + apiKey + "] not found");
            user = null;
        }

        return user;
    }

}
