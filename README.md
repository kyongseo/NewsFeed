# [📢 NewsFeed](https://youtu.be/vrL-VPoE8to)
<img src="https://github.com/user-attachments/assets/b47aa9be-18b5-431c-a8f1-bfd6faf0be7c" width="400" height="400">

NewsFeed는 최신 기술 스택을 활용하여 사용자가 최신 뉴스 및 블로그 게시물을 쉽게 검색하고 공유할 수 있는 플랫폼입니다. 

<br>

## ✏ 프로젝트 개요

- **프로젝트명**: NewsFeed
- **개발 기간**: 2024.06.08 ~ 2024.07.18 (약 4주)
- **개발 인원**: 1명

<br>

## 💻 Skills
📌 Front-End : Thymeleaf / JavaScript / HTML / CSS   <br>
📌 Back-End : Spring Boot / REST API / Java / Spring Data JPA <br>
📌 DB : MySQL    <br>
📌 Authentication : Spring Security / JWT    <br>
📌 Tool	 : IntelliJ, Postman, Git <br>
📌 Server : Apache Tomcat, Docker

<br>

## 🌉 Features
### 🏷️ Main
| **메인 페이지** | 
| :--------: | 
| <img src="https://github.com/user-attachments/assets/ccb687df-5823-48b3-9a53-9f32a70d19b8"  width="600px" height="300px"/>   | 
- 깔끔한 UI로 사용자 경험을 향상시켰습니다
- 메인 페이지에는 모든 사용자의 게시글을 보여줍니다.
- 상단에는 사용자 프로필이 보여집니다.

<br>

### 🏷️ 유저 관리
| **로그인** | **회원가입**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/81fa259f-5f67-4017-9e67-3fc25573c4d4"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/e9d5726d-8019-4e1a-af48-c955e8d2d83b"  width="400px" height="300px"/>  |
- Access Token과 Random Token으로 로그인 처리 및 회원 정보를 저장합니다
- 회원가입 시 유효성 검사로 중복 아이디/ 중복 닉네임을 걸러줍니다
- 회원가입 시 비밀번호 일치 여부를 판단합니다

<br>

### 🏷️ 마이페이지
| **프로필 확인** | **프로필 수정**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/4ab50dfd-f6f6-4edf-907c-acf46ab185a9"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/4d2bf15b-9047-4641-a692-957d13c99498"  width="400px" height="300px"/>  |
- 로그인 후 유저의 프로필을 한 눈에 확인할 수 있습니다
- 한줄 소개를 통해 자신을 나타낼 수 있습니다
- 프로필 수정을 할 수 있습니다
- 프로필 수정 시 파일을 업로드해 프로필 사진을 바꿀 수 있습니다

<br>

| **다른 사용자 프로필** | **회원 탈퇴**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/c27d0c76-4b06-43b2-95be-1447bce9e550"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/b3586fc6-4cf9-425b-abf6-919eccad4a59"  width="400px" height="300px"/>  |
- 다른 사용자의 프로필, 게시글을 볼 수 있습니다
- 다른 사용자에게 팔로우 걸기 및 취소를 할 수 있습니다
- 회원탈퇴를 할 수 있습니다

<br>

### 🏷️ 게시글
| **게시글 작성** | **게시글 수정**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/fa381713-c5f9-429e-9c16-bdb4a0b8948a"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/4848bbf1-a02a-4918-aa1f-2c83072aab09"  width="400px" height="300px"/>  |
- 게시글을 작성할 수 있습니다
- 제목, 내용, 태그, 이미지를 업로드 할 수 있습니다

<br>

| **키워드 검색** | **임시 저장**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/d72551d5-adac-428b-b006-e0c8aa992a12"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/c14579f7-90a3-447b-ad75-2fce2504e5ef"  width="400px" height="300px"/>  |
- 게시글 제목과 내용으로 검색할 수 있습니다
- 게시글을 임시 저장할 수 있습니다
- 임시저장 된 게시글은 출간하기 or 삭제 할 수 있습니다

<br>

| **최신 게시글 정렬** | **팔로우 사용자 게시글 정렬**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/6b4bf5c0-0b3e-42f2-a938-f6f268657282"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/27548d7a-0c81-44b0-a5c3-59555973fd18"  width="400px" height="300px"/>  |
- 최신 날짜 순으로 게시글을 정렬할 수 있습니다.
- 팔로우한 사용자의 게시글만 보이도록 정렬할 수 있습니다

<br>

### 🏷️ 부가 기능
| **댓글/대댓글 작성** | **좋아요**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/7c12dc50-6875-4e8c-a86a-b5b898f05557"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/2e4ac236-30c5-4241-8789-33c2d25e1320"  width="400px" height="300px"/>  |
- 게시글에 댓글, 대댓글을 달 수 있습니다
- 게시글에 좋아요를 누를 수 있습니다

<br>

| **태그 목록 정렬** | **팔로워/팔로잉 목록**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/c1a34bad-52e4-40ac-97eb-65d7b69e66f1"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/fcff71aa-4034-4582-854a-ddcdd93c9201"  width="400px" height="300px"/>  |
- 태그 관련된 게시글 목록을 볼 수 있습니다
- 팔로워 / 팔로잉 한 사용자 목록을 볼 수 있습니다

<br>

| **알림** | **알림 목록**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/0e8cc26f-a5ed-4826-b6e2-f968887d47d5"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/72aadcc4-9c02-4d00-9da0-16f31c7a3419"  width="400px" height="300px"/>  |
- 댓글, 대댓글, 좋아요가 달릴 시 알림을 받습니다
- 알림은 7일 이내에 읽지 않으면 자동으로 삭제됩니다

<br>

### 🏷️ 관리자
| **사용자 조회** | **게시글 조회**    |
| :--------: | :--------: |
|  <img src="https://github.com/user-attachments/assets/7c12dc50-6875-4e8c-a86a-b5b898f05557"  width="400px" height="300px"/>  | <img src="https://github.com/user-attachments/assets/2e4ac236-30c5-4241-8789-33c2d25e1320"  width="400px" height="300px"/>  |
- 역할에 따라 페이지를 다르게 구현했습니다
- 모든 사용자 조회 및 중지가 가능합니다
- 모든 게시글 조회가 가능합니다

<br>

## 🗄️ Structure
### 🏷️ DB Schema
![DBSchema](https://github.com/user-attachments/assets/0ff3c768-6a19-431a-90cd-dfb49ef2064f)

<br>

### 🏷️ 일정관리
[Notion 페이지](https://great-product-fd5.notion.site/0618-0719-Project-17e41a3c59d348fa98077352f4a94252?pvs=4)
