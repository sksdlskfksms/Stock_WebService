# [프로젝트] 주식 정보 웹서비스
##### ⭐ 해당 프로젝트는 (주)지니웍스 귀속된 프로젝트로, 서비스 명칭 및 기밀 사항들은 모두 공백처리했음을 참고 부탁드립니다.
##### ⭐ DB 설계와 코드 개발, AWS를 이용한 서버 생성과 배포까지, 초기 생성부터 운영까지의 전반을 직접 담당하였습니다.
<br> 

## 🤔 기획 내용
#### AI가 추천하는 주식 종목과 그 정보들을 매 주 문자로 제공 받을 수 있는 서비스를 가입 및 해지하는 웹서비스
 <img src="https://user-images.githubusercontent.com/93129951/188328511-6f7d6694-4228-4522-b3b7-eaad4f93086a.JPG" width="90%"/>
 <img src="https://user-images.githubusercontent.com/93129951/188328513-a193af5f-fea2-4f75-ae45-676d42dc3f1f.JPG" width="90%"/>
 
## 📸 페이지
 <img src="https://user-images.githubusercontent.com/93129951/188328515-f311f790-ae29-4995-836a-46fe8876986a.JPG" width="80%"/>
 <img src="https://user-images.githubusercontent.com/93129951/188328517-00a9e8ed-2edb-412a-9d3d-d0c94b3ccdbc.JPG" width="80%"/>
 <img src="https://user-images.githubusercontent.com/93129951/188328518-595055c1-d486-4209-829e-9e90b79095a8.JPG" width="80%"/>

## 🌟 개발 포인트
> 매체 연동
- iframe을 통한 Key값 교환
> Open API
- Nice 통신사 본인인증
- PG사 결제(Payco, Allat)
- SNS 간편가입 및 로그인(Kakao, Naver)
> 정기적 결제 요청
- Spring Boot의 스케줄러를 이용한 배치 작업 처리
<br> 

## 📍 사용기술
\# Java \# Spring Boot \# ApacheTomcat \# Maven \# Mybatis \# MVC \# HTML5 \# CSS3 \# JAVASCRIPT \# Tiles\
<br> 

## 💡 프로젝트 구조

```
src/main/  
│   
├── java/com/stock # 백엔드    
│   ├─ controller/   
│   ├─ core/   
│   ├─ exception/   
│   ├─ mapper/ 
│   ├─ service/ 
│   ├─ util/ 
│   ├─ vo/    
│   ├─ ScheduledTask.java
│   ├─ ServletInitializer.java
│   └─ StockApplication.properties  
└── resources/ # 리소스
│   ├─ config/
│   ├─ lib/        
│   ├─ mapper/        
│   ├─ static/    
│   |  ├─ css/  
│   |  ├─ images/  
│   |  └─ js/  
│   ├─ application.properties 
│   ├─ application-dev.properties
│   ├─ application-local.properties
│   ├─ application-prd.properties    
│   └─ logback-spring.xml 
└── webapp/WEB-INF # 프론트엔드       
    ├─ jsp/
    |  ├─ common/  
    |  ├─ join/  
    |  ├─ main/ 
    |  ├─ redirect/ 
    |  └─ term/ 
    ├─ tiles/     
    └─  └─ tiles.xml
```
