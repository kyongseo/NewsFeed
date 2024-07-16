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

## 기술 상세 : 구현 중..
- JWT
- Gmail smtp 메일 발송
<br>



# 프로젝트 설계 및 일정 관리
[Notion link](https://www.notion.so/0618-0719-Project-17e41a3c59d348fa98077352f4a94252?pvs=4
)

<br/>

----
# 기능 요구 사항
## 체크리스트

### 1. 회원가입
- [x] 회원 가입 폼
- [x] 같은 ID, Email Check API
- [x] 회원 등록 기능
- [x] 회원 가입 후 로그인 폼으로 이동

### 2. 로그인
- [x] 로그인 폼
- [x] 로그인 기능
    - [x] 로그인 성공 후 `/`로 이동
    - [x] 로그인 실패 후 다시 로그인 폼으로 이동 (오류 메시지 출력)
- [x] Spring Security 를 이용한 로그인 구현
    - [x] Form Login
    - [x] JWT Login
    - [ ] OAuth2 로그인

### 3. 사이트 상단
- [x] 사이트 로고가 좌측 상단에 보여짐
- [x] 로그인 여부에 따른 우측 정보 표시
    - [x] 로그인하지 않았을 경우 로그인 링크
    - [x] 로그인했을 경우 사용자 이름
        - [x] 사용자 이름 클릭 시 설정, 해당 사용자 블로그 이동 링크, 임시 저장글 목록 보기, 로그아웃 링크 표시

### 4. 로그아웃
- [x] 로그아웃 기능

### 5. 메인 페이지 (/)
- [x] 블로그 글 목록 보기
    - [x] 최신 순
    - [x] 좋아요 높은 순
    - [x] 즐겨찾기 순
- [x] 페이징 처리 또는 무한 스크롤 구현
- [x] 검색 기능
    - [x] 제목
    - [x] 내용
    - [x] 사용자 이름

### 6. 블로그 글 쓰기
- [x] 블로그 제목, 내용, 사진 입력 기능
- [x] "출간하기" 버튼
    - [x] 블로그 썸네일(이미지)
    - [ ] 공개 유무
    - [ ] 시리즈 설정
    - [x] "출간하기" 클릭 시 글 등록
- [x] "임시저장" 버튼

### 7. 임시 글 저장 목록 보기
- [x] 로그인 시 임시글 저장 목록 보기 링크 표시
- [x] 임시글 저장 목록 표시
    - [x] 글 제목 클릭 시 글 수정 가능
    - [x] "임시저장" 및 "출간하기" 기능

### 8. 특정 사용자 블로그 글 보기 (/@사용자아이디)
- [x] 사용자 정보 보기
- [x] 사용자 글 목록 보기
    - [x] 최신 순
    - [x] 좋아요 많은 순
    - [x] 즐겨찾기 순
- [x] 페이징 처리 또는 무한 스크롤 구현
- [ ] 사용자 태그 목록 보기
    - [ ] 태그당 글 수 표시
- [x] 검색 기능
    - [x] 제목
    - [x] 내용

### 9. 시리즈 목록 보기
- [ ] 시리즈 목록 보기 기능
- [ ] 시리즈 제목 클릭 시 시리즈에 포함된 블로그 글 목록 보기

### 10. 블로그 글 상세 보기
- [x] 메인 페이지에서 제목 클릭 시 블로그 글 상세 보기
- [x] 특정 사용자 블로그에서 제목 클릭 시 블로그 글 상세 보기
- [ ] 시리즈에 속한 블로그 글 목록에서 제목 클릭 시 블로그 글 상세 보기
- [x] 사용자 소개글 작성 기능

### 11. 사용자 정보 보기
- [x] 로그인 사용자 이름 클릭 시 사용자 정보 보기
    - [x] 사용자 이름
    - [x] 이메일
    - [x] 회원 탈퇴 링크

### 12. 회원 탈퇴
- [ ] 회원 탈퇴 확인 폼
- [ ] 폼에서 확인 시 회원 탈퇴 (회원 정보 삭제)

### 13. 댓글 목록 보기
- [x] 블로그 글 상세 보기에서 댓글 목록 표시
- [ ] 댓글과 대댓글 최신 순 표시
- [ ] 댓글 최대 20개 페이징 처리

### 14. 댓글 달기
- [x] 블로그에 댓글 달기
- [x] 댓글 수정 및 삭제
- [ ] 댓글에 대댓글 달기

### 15. 블로그 글에 좋아요 하기
- [x] 블로그 글에 좋아요 기능
- [x] 좋아요 취소 기능

### 16. 팔로우 기능
- [x] 팔로우 기능 구현
- [x] 팔로우 취소 기능 구현
- [x] 화면에 바로 보여지는 기능 구현

### 17. 관리자 페이지
- [x] 관리자로 로그인할 시 관리자 페이지 렌더링
- [x] 회원가입한 전체 회원 목록, 전체 게시글 목록 
- [x] 회원 중지 기능
- [x] 게시글 삭제 기능
- [ ] 공지글 기능

### 18. 이외의 기능
- [ ] 쪽지 기능
- [ ] 벨로그와 유사하거나 더 편리한 기능 구현
- [ ] 프론트 개발 학습
- [ ] 벨로그의 특별한 기능 추가 구현

----
### 프로젝트 기능 (~ing)

<details>
<summary>자세히 보기</summary> </br>

## 공통
- 회원가입시 유효성 검사 및 중복 검사
- 스프링 시큐리티 이용
- 이메일, 아이디 중복 체크
- 비밀번호 잃어버릴 시 인증 메일 : 구현 중..
  <br/>

## 인증
- 로그인 시 JWT accessToken과 refreshToken이 발행된다.
- 이후 `Authorization` 헤더에 `Bearer {token}`을 추가하여 권한을 확인한다.
  <br/>

## 토큰 관리
- refreshToken으로 reissue 요청 시 accessToken을 새로 발행한다.
- 로그아웃 시 해당 refreshTokend을 삭제하고, accessToken에 대한 blackList를 추가하여, 이후 해당 accessToken으로 로그인 할 시 거부되도록 한다.
  <br/>

## 권한에 따라 다른 기능 분리
  - Admin, User 권한에 따라 다른 기능 분리
  - 로그인 여부에 따른 기능
    - 비로그인 유저
      - 우측 상단에 로그인 버튼
      - 게시글 읽기, 사용자 블로그 읽기만 가능
    - 로그인 유저
      - 우측 상단에 사용자 프로필 클릭
        - 드롭다운 버튼으로 사용자 블로그, 설정, 임시저장글, 로그아웃 구현
      - 자신이 쓴 게시글만 수정 및 삭제 가능
<br/>

## 블로그
  - 사용자는 자신만의 블로그를 꾸밀 수 있다.
    - 블로그명, 이름, 닉네임, 사진 변경 및 삭제 가능
    - 게시글 최신순 정렬
    - 제목이나 글 내용으로 검색 가능
    - 블로그 한 줄 소개 작성 기능
<br/>

## 회원 관리
### 아이디 회원
  - 아이디, 비밀번호를 통해 로그인 할 수 있다.
  - 이메일로 회원가입 할 수 있다.
  - 마이페이지 수정
    - 이름, 닉네임, 이메일, 프로필 이미지 수정 기능
  - 회원가입 시 인증 메일이 발송되고, 메일 인증을 완료되어야지만 서비스를 이용할 수 있다. : 구현 중..
  - 회원탈퇴를 할 수 있다. : 구현 중..
 <!--   - 이메일 발송 시에 5초 정도의 시간이 소요되므로 비동기로 처리한다.
    - 이메일 인증 기한은 24시간이다. 기한 내에 인증하지 못한 경우 새로운 인증 키로 재발송한다.-->
  <br/>
  
 ### OAuth2 회원 : 구현 중..
  - KAKAO
  - Github
  - /login에서 소셜 로그인을 통해 회원가입, 로그인 할 수 있다.
  - 처음 로그인 시에는 회원가입이 되고, 이후에는 로그인이 된다. 
<br/>

### 관리자 
  - 관리자는 모든 사용자 조회 가능
  - 관리자는 모든 게시글 조회 가능
  - 모든 사용자의 게시글 삭제 가능 
<br/>

## 게시판
  - 게시판 메인에는 트렌드, 최신, 즐겨찾기 게시글을 보여준다.
    - 최신 게시물은 최근 올라온 게시글 10개
    - 트렌드 게시글은 좋아요 수가 많은 게시글 10개 
    - 즐겨찾기 게시글은 팔로우한 사람의 게시글 10개 
<br/>

## 게시글
  - 사용자는 자신의 글을 공유할 수 있다.
     - 게시글 생성, 수정, 삭제 가능
  - 게시글에 likes(좋아요)를 누르고, 취소할 수 있다.
  - 댓글을 달 수 있다. 
  - 임시글로 저장할 수 있다.
  - 게시글 조회 시 views(조회수)가 1씩 증가한다. : 구현 중..
  
<br/>

## 댓글
  - 댓글 기능 구현
    - 댓글 작성 사용자의 아이디가 보여진다.
      - 댓글 내용 수정 및 삭제 가능
 <br/>
 
## 좋아요 
  - 좋아요 기능 구현
     - 좋아요를 한번 더 누를 시 취소 구현
       - 중복 불가능
<br/>

## 팔로우 & 팔로잉
  - 로그인한 회원은 원하는 사용자를 팔로우 할 수 있다.
  - 나를 팔로잉 하는 사용자 리스트를 볼 수 있다.
<br/>

## 쪽지 : 구현 중..
  - 쪽지 기능 구현
  - 소켓통신
<br/>
</details>

<!--
사용자가 로그아웃할 때 보안을 위해 다음과 같은 절차가 수행됩니다:
1. 클라이언트의 쿠키에서 JWT(accessToken)를 가져옵니다.
2. JWT의 만료 시간을 추출합니다.
3. 해당 JWT를 블랙리스트에 추가하여 이후 사용을 방지합니다.
4. 클라이언트 브라우저에서 JWT 쿠키를 삭제합니다.
5. /signIn 페이지로 리디렉션합니다.

# 3. 구조 및 설계
## 인증
- 로그인 시 JWT accessToken과 refreshToken이 발행된다.
- 이후 `Authorization` 헤더에 `Bearer {token}`을 추가하여 권한을 확인한다.

## 토큰 관리
- refreshToken으로 reissue 요청 시 accessToken을 새로 발행한다.
- 로그아웃 시 해당 refreshTokend을 삭제하고, accessToken에 대한 blackList를 추가하여, 이후 해당 accessToken으로 로그인 할 시 거부되도록 한다. 

## 회원 관리
### 이메일 회원
- 아이디, 이메일, 비밀 번호를 통해 로그인 할 수 있다.
  - 회원가입 시 인증 메일이 발송되고, 메일 인증을 완료되어야지만 서비스를 이용할 수 있다.
  - 이메일 발송 시에 5초 정도의 시간이 소요되므로 `비동기`로 처리한다.
  - 이메일 인증 기한은 24시간이다. 기한 내에 인증하지 못한 경우 새로운 인증 키로 재발송한다.


