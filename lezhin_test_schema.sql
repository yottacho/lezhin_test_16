-- --------------------------------------------------------
-- 서버 버전:                        5.5.49-MariaDB - Source distribution
-- 서버 OS:                        Linux
-- --------------------------------------------------------

-- 테이블 lezhin_test.user 구조 내보내기
CREATE TABLE IF NOT EXISTS user (
  userid int(11) NOT NULL AUTO_INCREMENT COMMENT '사용자 고유ID',
  name varchar(20) DEFAULT NULL COMMENT '사용자 로그인ID',
  showname varchar(50) DEFAULT NULL COMMENT '별칭',
  api_accesskey varchar(32) DEFAULT NULL COMMENT 'API KEY (random md5)',
  PRIMARY KEY (userid),
  UNIQUE KEY USER_NAME_UNI1 (name),
  UNIQUE KEY USER_API_UNI2 (api_accesskey)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자 테이블';


-- 테이블 lezhin_test.follower 구조 내보내기
CREATE TABLE IF NOT EXISTS follower (
  userid int(11) NOT NULL COMMENT '사용자ID',
  followid int(11) NOT NULL COMMENT '팔로우하는 사용자ID',
  PRIMARY KEY (userid,followid),
  KEY FOLLOWER_FK2 (followid),
  CONSTRAINT FOLLOWER_FK1 FOREIGN KEY (userid) REFERENCES user (userid),
  CONSTRAINT FOLLOWER_FK2 FOREIGN KEY (followid) REFERENCES user (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='팔로워 테이블';


-- 테이블 lezhin_test.post_data 구조 내보내기
CREATE TABLE IF NOT EXISTS post_data (
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '포스트 고유ID',
  userid int(11) NOT NULL COMMENT '사용자ID',
  date datetime DEFAULT NULL COMMENT '등록일시',
  content text COMMENT '포스트 내용',
  PRIMARY KEY (id),
  KEY USERID_FK1 (userid),
  CONSTRAINT USERID_FK1 FOREIGN KEY (userid) REFERENCES user (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='포스트 테이블';


