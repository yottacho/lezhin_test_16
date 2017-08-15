# 레진 코딩테스트

2016년에 도전해서 서류 통과하고 기술면접까지는 봤는데 떨어졌다.

지금도 동일한 문제를 내진 않겠지 싶어서 Spring 4의 편리한 점을 추후 찾기 위해 저장함.

Spring 4의 `@RequestMapping` 어노테이션 참 편리한 듯 한데 잘못하면 다 꼬일 것 같다.

떨어진 원인은 회사에서 확실하게 말하진 않았지만,
면접시 이 과제에 대해 튜닝이 필요할 때 어디를 수정했으면 좋겠냐라는 질문이 있었고
쿼리 어딘가에 사용된 IN을 JOIN으로 변형하는 대답을 원한 것 같았는데,
내가 JOIN을 쓸 줄 모른게 아니라(JOIN 쓴 쿼리도 물론 있다) 별 생각없이 IN 구문도 쓸 줄 안다는
어필 정도로 담아놓은 거라 그걸 문제삼을 줄은 몰라서 어설프게 빅데이터 따위나 읇은 것이 원인이 아닐까 싶다.

운영 중 규모의 이슈가 생기면 빅데이터로 가는 게 맞지만,
면접에서 너도 나도 써 본 적이 없어서 잘 모르는 것에 대해 썰을 나누는 것 보다는,
걍 안전하게 쿼리 튜닝하는게 정답이기는 한데 쿼리를 문제삼을 줄은 생각 못 함.

여기 면접보기 전에 다닌 회사에서 프로그램 튜닝으로 먹고 살은 기간이 너무 길었던 듯.

면접관이 답답했는지 질문 의도를 설명해 줬는데 그때서야 개발 할 때 일부러 JOIN과 IN을 골고루 쓴 사실이 기억이 났지만 이미 늦었지.

여하튼 괜찮은 인상을 못 줘서인지 탈락했다. 그래도 덕분에 Spring 4가 이전 버전보다 뭐가 더 나아졌는지 조금이나마 알게 된 것이 수확이다.

## 1. 과제

```
1. 제출 과제: 팔로우, 포스팅, 뉴스 피드 기능이 포함된 타임라인 서비스 개발
2. 개발 시 반드시 포함되어야 하는 항목
- RDBMS
- REST API
3. 개발 시 반드시 구현되어야 하는 기능
- 팔로우
- 포스팅
- 뉴스 피드
```

## 2. 요구사항 분석

- 뉴스피드: 내 게시물과 팔로우하는 게시물을 보여주는 최근 게시물 순으로 보여줌
- 포스팅: 타임라인에 게시물을 추가
- 팔로우: 다른 사람의 포스팅을 내 타임라인에서 구독

### 2.1 팔로우

팔로우를 구현하기 위해 서로 다른 ID로 포스팅 할 수 있도록 게시물의 오너 구분이 필요하지만, 이용자 관리는 필수 구현항목이 아니므로 테스트를 위해 화면에서 간단히 이용자 등록할 수 있도록만 한다.

팔로우 기능은 등록/삭제만 구현한다. (수정기능은 구현 의미 없음)

### 2.2 포스팅

포스팅은 등록/삭제만 구현하며 수정 기능은 구현하지 않는다.

### 2.3 뉴스피드

뉴스피드는 자신의 게시글과 팔로우하는 ID의 게시글을 포함해야 하며 최근 포스팅이 먼저 출력되도록 표시한다.

### 2.4 입출력 자료 포맷 정의

입출력 자료는 JSON 포맷을 사용한다.

응답할 자료가 없으면 통상적으로 HTTP 404 오류를 응답하지만, 출력 자료가 array인 경우에는 HTTP 200 정상 응답하고 empty array를 출력한다.

> Ex) {"followers":[]}

### 2.5 API 인증

HTTP 헤더의 Authorization 을 사용하며, 별도의 인증절차 없이 user 테이블의 api_accesskey로 사용자를 특정한다.

> Authorization: Bearer :api_accesskey

### 2.6 기타

- user 신규 등록 시 follower 테이블에 userid와 followid가 같은 항목이 있어야 한다. (=자기자신 팔로우, 없을 경우 자기가 작성한 포스트가 뉴스피드에 나타나지 않음, 자기자신 팔로우는 프로그램에서 등록/삭제 불가하므로 user 등록 시 함께 등록해야 함)
- Database connection은 WEB-INF/spring/root-context.xml 에 정의


## 3. 개발 환경

항목 | 버전 | 비고
-----|------|------
Java | 8 | 7 호환문법 위주로 구현
Eclipse Luna | 4.4.2 | 
Spring framework | 4.3.1 | v4에서 추가된 @RestController 사용
Tomcat | 7 | 컨테이너
MariaDB | 5.5 | UTF-8, InnoDB 사용

### 4. 데이터베이스 테이블 구조

#### 4.1 user 테이블

게시물 오너 관리를 위한 사용자 테이블

필드명 | 유형 | 설명
-------|------|------
userid | Int | 사용자별로 할당된 고유키(PK), 내부에서 사용하고 외부에 노출하지 않음
name | Varchar(20) | 외부에 노출하는 사용자ID
showname | Varchar(50) | 사용자 별칭
api_accesskey | Varchar(32) | API 접근용 인증 키

name 과 api_accesskey 는 중복 불가 속성 부여

#### 4.2 follower 테이블

팔로우 관리를 위한 팔로우 정보 테이블

필드명 | 유형 | 설명
-------|------|-------
userid | Int | 소유자 ID
followid | Int | 타임 라인에 보여 줄 사용자ID

- userid+followid 로 Primary key (이중등록 방지)
- userid 와 followid는 각각 user 테이블의 userid와 외래키 구성

#### 4.3 post_data 테이블

포스트 내용을 저장하는 테이블

필드명 | 유형 | 설명
-------|------|-------
id | Int | 포스트 고유ID (PK)
userid | Int | 사용자 ID
date | datetime | 등록일시
content | text | 포스트 내용(~65535)

- 등록일시는 개발 여건 상 localtime을 사용함
- Userid는 user 테이블의 userid와 외래키 구성


### 5. REST API 구조

#### 5.1 API 목록

기능명 | METHOD | Resource Format
-------|--------|----------------------
뉴스피드 | GET | /feed/timeline/[:userid][?start=:id&limit=:count]
. | . | 응답: {“timeline” : [ {:포스트상세보기} …]}
포스트 상세보기 | GET | /feed/show/:id
. | . | 응답: {"id":1, "userid":":userid", "date":"yyyy-MM-dd HH:mm:ss", "content":":data", "dispname":":showname" }
포스트 등록 | POST | /feed
. | . | 요구: { “content” : “:data” }
포스트 삭제 | DELETE | /feed/:id
나의 팔로우 조회 | GET | /follow/my
. | . | 응답: {"followers":[{"name":":userid"}, …]}
나를 팔로우하는 목록 조회 | GET | /follow/me
. | . | 응답: /follow/my 와 동일
특정 사용자의 팔로우 조회 | GET | /follow/user/:userid
. | . | 응답: /follow/my 와 동일
팔로우 등록 | POST | /follow/:userid
팔로우 해제 | DELETE | /follow/:userid

* 팔로우 목록 조회에는 페이징 기능 없음

#### 5.2 변수 설명

변수 | 설명
-----|--------
[] | 선택항목
:userid | user 테이블의 name
:id | 포스트 고유 ID
:count | 뉴스피드에서 출력 할 포스트 수 (기본값: 100, 최소값: 1, 최대값: 500)
:data | 포스트 데이터

### 6. 소스 구성

- .settings/ : 이클립스 디렉터리
- .classpath : 이클립스 classpath
- .project : 이클립스 프로젝트 파일
- .springBeans : 이클립스 spring 관련
- pom.xml : maven 정의
- src/main/java : java 소스
- src/resources : 기타 리소스
- src/webapp : WEB-INF 및 spring 셋팅

패키지/소스 | 구성
------------|-----------------
com.lezhin.test | 패키지 홈, 컨트롤러 위치
=> HomeController.java | 인덱스 페이지(“/”), 사용자 추가(/adduser.do) 구현
=> FeedController.java | “/feed” API 구현. 뉴스피드 타임라인, 포스트 등록, 포스트 삭제
=> FollowController.java | “/follow” API 구현. 팔로우 조회, 등록, 해제
com.lezhin.test.module | 컴포넌트
=> Authorization.java | API 인증 구현 컴포넌트
com.lezhin.test.vo | 테이블 Bean
=> FollowerVO.java | follower 테이블 Bean
=> PostDataVO.java | post_data 테이블 Bean
=> UserVO.java | user 테이블 Bean
com.lezhin.test.dao | DAO 구현, MyBatis Mapper(query xml)
=> FollowerDao.java | follower 테이블에 대한 CRuD 및 리스트 쿼리
=> PostDataDao.java | post_data 테이블에 대한 CRUD 및 리스트 쿼리
=> UserDao.java | user 테이블에 대한 CRUD

끝.

