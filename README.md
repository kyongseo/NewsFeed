# 📢 Bolg Clone Project

> velog 라는 블로그 클론 프로젝트입니다.

## 목차
- [들어가며](#들어가며)
- [구조 및 설계](#구조-및-설계)
- [구현 결과](#구현-결과)
- [노하우 공유](#노하우-공유)
- [마치며](#마치며)

<br><br>

## 들어가며
### 1. 프로젝트 기획 배경
기존 블로그와 유사하게 만들어 봄으로써 자바 기반 백엔드 역량 향상과 새로운 기술 스택을 경험해보고자 기획하였습니다. <br>
또한 무조건적으로 책과 강의를 따라하여 만드는 것이 아닌 내가 직접 기능에 대한 구현방법을 고민하고, 여러 자료를 찾아보며 적용하는 힘을 키우기 위해 프로젝트를 시작했습니다. <br>
<br> <br>

### 2. 프로젝트 설명
- 권한에 따라 다른 기능 분리
    - SpringSecurity를 활용해 Admin, User 권한에 따라 다른 기능 분리
- **게시판 -** User, Class 관련 CRUD API 개발, 조회수, 페이징 및 검색 처리
- **사용자 -** Security 회원가입 및 로그인, JWT를 이용하여 AccessToken, Refresh Token 발급, 회원정보 수정, 회원가입시 유효성 검사 및 중복 검사, OAuth 2.0 깃허브, 구글, 네이버 로그인
- 로그인 세션을 이용한 ROLE별로 사용자와 관리자 페이지 렌더링
- 사용자
    - 사용자 페이지, 게시글 CRUD, 마이페이지, 글 관리
- 관리자
    - 마이페이지, 게시글 수정 및 삭제, 회원 정지, 회원 조회
<br><br>
 
### 3. 프로젝트 기간

2024.06. ~ 2024.07 (4주)

| 기간                | 설명                                                         |
| ------------------- | ------------------------------------------------------------ |
|        |  |

<!-- 기능 추가 // AWS배포, AWS S3에 이미지 저장 기능 구현 -->
