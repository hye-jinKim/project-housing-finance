### 개발 프레임워크
1. Tool : Spring Boot  
> 설치 URL : https://spring.io/tools3/sts/all
2. Language : Java 8  
> 설치 URL : https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
3. DB : MySQL  
> 설치 URL : https://dev.mysql.com/downloads/installer/
4. 실행 도구 : Postman  
> 설치 URL : https://www.getpostman.com/downloads/

### 문제 해결 전략
#### 개발 목표
최대한 Restful한 방식으로 개발하기 위해 노력하였습니다.  
+ JWT  
  - 토큰 발급 API들은 Authorization 체크를 하지 않는다.  
  - 이전 토큰을 사용하지 못하도록 체크하기 위해 토큰은 항상 DB에 저장하며, DB에 저장된 토큰을 최신 토큰으로 인지한다.  
  - io.jsonwebtoken library 활용  
  - HS256 암호화 방식 사용  
1. signup 계정생성 API : 입력으로 ID, PW 받아 내부 DB에 계정 저장하고 토큰 생성하여 출력  
> 입력 받은 userId, password, token을 DB(member 테이블)에 저장  
2. signin 로그인 API : 입력으로 생성된 계정 (ID, PW)으로 로그인 요청하면 토큰을 발급  
> 재로그인 시 항상 새 토큰을 발급 및 DB 저장  
3. refresh 토큰 재발급 API : 기존에 발급받은 토큰을 Authorization 헤더에 “Bearer Token”으로 입력 요청을 하면 토큰을 재발급  
> DB에 저장되어 있는 토큰을 재발급된 토큰으로 업데이트


+ 기본 문제
1. 데이터 파일에서 각 레코드를 데이터베이스에 저장하는 API  
> resources > file 폴더에 있는 csv 파일을 읽어들여 institute 테이블과 support 테이블에 저장  
> OpenCSV Library 활용  
> csv 파일에 없는 은행 코드는 등록할 때, 랜덤으로 코드가 생성되어 저장   
2. 주택금융 공급 금융기관(은행) 목록을 출력하는 API  
> group by Query를 활용하여 등록되어 있는 은행의 전체 목록을 출력
3. 년도별 각 금융기관의 지원금액 합계를 출력하는 API  
> 년도별로 group by 하여 은행별 금액 합계를 출력하고, 특정 년도 은행들의 합계 금액을 출력
4. 각 년도별 각 기관의 전체 지원금액 중에서 가장 큰 금액의 기관명을 출력하는 API  
> 년도별로 지원 금액을 합하여, 가장 큰 금액의 은행과 년도를 출력
5. 전체 년도(2005~2016)에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액을 출력하는 API  
> **기관명을 '외환은행', 전체 년도를 '2005-2016' 으로 특정지어 결과를 출력하지 않고, 출력하려는 기관명과 전체 년도 (시작년도-종료년도)를 입력받아 조건에 맞게 출력**

+ 선택 문제
1. 특정 은행의 특정 달에 대해서 2018년도 해당 달에 금융지원 금액을 예측하는 API 개발  
> LinearRegression (선형 회귀) 알고리즘 활용하여 개발  
> weka open Library 활용

### 빌드 및 실행 방법
1. Git 에서 소스를 다운 받아 STS 실행
2. git root의 sql.txt 파일에 있는 Create Query 문을 실행하여, MySQL 스키마 및 테이블 생성
3. Postman 실행하여 API 호출
4. 가장 먼저, 계정 생성 및 토큰 발급하는 API 실행 (테스트 편의성을 위해 query string으로 parameter를 받음)  
> URL : http://localhost:8080/member/signup?userId=test&password=test  
> Http Method : PUT  
> parameter : uerId, password

> 4-1. 이미 계정이 있다면, 로그인 API 실행 (테스트 편의성을 위해 query string으로 parameter를 받음)  
>> URL : http://localhost:8080/member/signin?userId=test&password=test  
>> Http Method : POST  
>> parameter : userId, password

> 4-2. 토큰이 만료 되었거나, 재발급을 원한다면 재발급 API 실행  
>> URL : http://localhost:8080/member/refresh  
>> Http Method : GET  
>> Http Header : Authorization, Bearer + "white space" + token

5. 발급 받은 토큰으로, csv 파일 데이터를 DB에 Insert 하는 API 실행  
> URL : http://localhost:8080/api/institutes  
> Http Method : PUT  

6. 이후 필요한 API 실행  

> 6-1. 주택금융 공급 금융 기관 (은행) 목록 출력  
>> URL : http://localhost:8080/api/institutes  
>> Http Method : GET

> 6-2. 년도별 각 금융기관의 지원금액 합계를 출력  
>> URL : http://localhost:8080/api/institutes/supportAmounts  
>> Http Method : GET  

> 6-3. 각 년도별 각 기관의 전체 지원 금액 중에서 가장 큰 금액의 기관명 출력  
>> URL : http://localhost:8080/api/institutes/most/supportAmounts  
>> Http Method : GET

> 6-4. 전체 년도에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액을 출력
>> URL : http://localhost:8080/api/institutes/minAndMax/averageAmounts?bank=외환은행&start=2005&end=2016  
>> Http Method : GET  
>> parameter : bank, start, end

7. 금융지원 금액 예측 API 실행
> URL : http://localhost:8080/api/prediction  
> Http Method : POST  
> Request Body : {"bank":"국민은행","month":2}
