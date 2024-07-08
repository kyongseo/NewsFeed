# 📢 Blogg Clone Project

> Blog Clone Project

#### 목차
- [프로젝트 기획 배경](#프로젝트-기획-배경)
- [프로젝트 설명](#프로젝트-설명)
- [프로젝트 설계 및 일정 관리](#프로젝트-설계-및-일정-관리)
<br>

# 프로젝트 기획 배경
기존 블로그와 유사하게 만들어 봄으로써 자바 기반 백엔드 역량 향상과 새로운 기술 스택을 경험해보고자 기획하였습니다. <br>
또한 무조건적으로 책과 강의를 따라하여 만드는 것이 아닌 직접 기능에 대한 구현방법을 고민하고, 여러 자료를 찾아보며 적용하는 힘을 키우기 위해 프로젝트를 시작했습니다. <br>
<br> <br>

# 프로젝트 설명
- 프로젝트 명: Blog
- 개발 기간: 2024.06.08 ~ 2024.07
- 개발 인원: 1명
<br>

## Skills
- Java 21, Spring Boot 3.3.0, gradle 8.8
- Spring Data JPA, Spring Security
- MySQL, Thymeleaf
- JUnit5
- IntelliJ Idea
<br>

## 기술 상세
- JWT
- Gmail smtp 메일 발송
<br>



# 프로젝트 설계 및 일정 관리
[Notion link](https://www.notion.so/0618-0719-Project-17e41a3c59d348fa98077352f4a94252?pvs=4
)

<br/>


# 요구사항
## 공통
- 회원가입시 유효성 검사 및 중복 검사
- 스프링 시큐리티 이용
<!-- - 로그인 시 JWT accessToken과 refreshToken이 발행된다. 
- 이후 Authorization 헤더에 Bearer {token}을 추가하여 권한을 확인한다.
- OAuth 2.0 깃허브 로그인

### 토큰 관리
- redis로 refreshToken과 로그아웃 된 accessToken을 관리한다.
- refreshToken으로 reissue 요청 시 accessToken을 새로 발행한다.
- 로그아웃 시 해당 refreshTokend을 삭제하고, accessToken에 대한 blackList를 추가하여, 이후 해당 accessToken으로 로그인 할 시 거부되도록 한다.-->


## 권한에 따라 다른 기능 분리
  - SpringSecurity를 활용해 Admin, User 권한에 따라 다른 기능 분리

## 블로그
  - 사용자는 자신만의 블로그를 꾸밀 수 있다.
    - 블로그명, 이름, 닉네임, 사진 변경 및 삭제 가능

## 회원 관리
### 아이디 회원
  - 아이디, 비밀번호를 통해 로그인 할 수 있다.
  - 이메일로 회원가입 할 수 있다.
  - 마이페이지 수정
    - 이름, 닉네임, 이메일, 프로필 이미지 수정 기능
<!--    - 회원가입 시 인증 메일이 발송되고, 메일 인증을 완료되어야지만 서비스를 이용할 수 있다.
    - 이메일 발송 시에 5초 정도의 시간이 소요되므로 비동기로 처리한다.
    - 이메일 인증 기한은 24시간이다. 기한 내에 인증하지 못한 경우 새로운 인증 키로 재발송한다.
 #### 깃허브 회원
  - KAKAO
  - /login-page에서 소셜 로그인을 통해 회원가입, 로그인 할 수 있다.
  - 처음 로그인 시에는 회원가입이 되고, 이후에는 로그인이 된다.
-->

### 관리자 
  - 관리자는 모든 유저의 정보를 볼 수 있다.
  - 관리자는 모든 게시글을 볼 수 있다.


## 게시판
  - 게시판 메인에는 트렌드, 최신, 일반 게시글을 보여준다.
    - 최신 게시물은 최근 올라온 게시글 10개
<!--    - 일반 게시물은 최근 20개의 게시물(페이징 처리) -->

## 게시글
  - 사용자는 자신의 글을 공유할 수 있다.
     - 게시글 생성, 수정, 삭제 가능
  <!--- 게시글 조회 시 views(조회수)가 1씩 증가한다.
     - 한번 유저가 조회 시 30분간 다시 조회수를 올릴 수 없도록 한다. --->
  - 게시글에 likes(좋아요)를 누르고, 취소할 수 있다.
  - 댓글을 달 수 있다.

## 댓글
  - 댓글 기능 구현
    - 댓글 작성 사용자의 아이디가 보여진다.
      - 댓글 내용 수정 및 삭제 가능
   
## 좋아요 
  - 좋아요 기능 구현
     - 좋아요를 한번 더 누를 시 취소 구현
       - 중복 불가능
<!--
# 3. 구조 및 설계
## 인증
- 로그인 시 JWT accessToken과 refreshToken이 발행된다.
- 이후 `Authorization` 헤더에 `Bearer {token}`을 추가하여 권한을 확인한다.

## 토큰 관리
- redis로 refreshToken과 로그아웃 된 accessToken을 관리한다.
- refreshToken으로 reissue 요청 시 accessToken을 새로 발행한다.
- 로그아웃 시 해당 refreshTokend을 삭제하고, accessToken에 대한 blackList를 추가하여, 이후 해당 accessToken으로 로그인 할 시 거부되도록 한다. 

## 회원 관리
### 이메일 회원
- 아이디, 이메일, 비밀 번호를 통해 로그인 할 수 있다.
  - 회원가입 시 인증 메일이 발송되고, 메일 인증을 완료되어야지만 서비스를 이용할 수 있다.
  - 이메일 발송 시에 5초 정도의 시간이 소요되므로 `비동기`로 처리한다.
  - 이메일 인증 기한은 24시간이다. 기한 내에 인증하지 못한 경우 새로운 인증 키로 재발송한다.

### 사용자 블로그
- 사용자는 자신만의 블로그를 꾸밀 수 있다.
  - 블로그명, 이름, 닉네임, 사진 변경 및 삭제 가능

## 게시글 만들
### 블로그 게시판
- 게시판 메인에는 hot 게시글 10개와, 일반 게시글을 보여준다.
  - hot 게시물은 최근 24시간 내에 좋아요를 가장 많이 받은 10개의 게시물
  - 일반 게시물은 최근 20개의 게시물(페이징 처리)
 
### 블로그 게시글
- 사용자는 자신의 글을 공유할 수 있다.
  - 게시글 생성, 수정, 삭제 가능
- 게시글 조회 시 views(조회수)가 1씩 증가한다.
  - 한번 유저가 조회 시 30분간 다시 조회수를 올릴 수 없도록 한다.
- 게시글에 likes(좋아요)를 누르고, 취소할 수 있다.
- 사용자의 게시글을 확인하고 댓글을 달 수 있다.

- 
- 권한에 따라 다른 기능 분리
    - SpringSecurity를 활용해 Admin, User 권한에 따라 다른 기능 분리
- 게시판
     - User, Class 관련 CRUD API 개발, 조회수, 페이징 및 검색 처리
- 사용자
    -  Security 회원가입 및 로그인, JWT를 이용하여 AccessToken, Refresh Token 발급
    -  회원정보 수정, 회원가입시 유효성 검사 및 중복 검사
    -  OAuth 2.0 깃허브, 구글, 네이버 로그인
- 로그인 세션을 이용한 ROLE 별로 사용자와 관리자 페이지 렌더링
- 사용자
    - 사용자 페이지, 게시글 CRUD, 마이페이지, 글 관리
- 관리자
    - 마이페이지, 게시글 수정 및 삭제, 회원 정지, 회원 조회
<br><br>
<br> <br>
-->



<!-- 기능 추가 // AWS배포, AWS S3에 이미지 저장 기능 구현 -->
