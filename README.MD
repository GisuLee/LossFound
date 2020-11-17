# 공공데이터를 이용한 분실물 찾기 어플리케이션
공공데이터<strong>(경찰청,부산메트로, 서울메트로에서 제공하는 분실물 데이터)</strong>를 이용한 분실물 찾기 어플리케이션 입니다.   

- 기능   
```
분실물 조회 기능(경찰청 API, 서울메트로, 부산메트로 파싱)
위치정보를 활용한 분실물 센터 및 분실물 조회 (Google Map API, T Map API)
분실물 습득 알림 기능   
```

- 기술, 라이브러리
```
Java, Android
jsoup Java HTML Parser
공공데이터 포털 경찰청 API
Google Map API
T Map API
```

## 1. 분실물 조회 기능
![](https://github.com/GisuLee/LossFound/blob/master/readme_main.gif?raw=true)![](https://github.com/GisuLee/LossFound/blob/master/readme_main0.png?raw=true)![](https://github.com/GisuLee/LossFound/blob/master/readme_main1.png?raw=true)![](https://github.com/GisuLee/LossFound/blob/master/readme_main2.png?raw=true)
![](https://github.com/GisuLee/LossFound/blob/master/readme_main3.png?raw=true)![](https://github.com/GisuLee/LossFound/blob/master/readme_main4.png?raw=true)   
<strong>경찰청, 부산메트로, 서울메트로</strong>에서 제공하는 데이터를 <code>Jsoup</code>을 이용하여 <code>Parsing</code>하거나 <code>REST API</code>를 사용하여 분실물 정보를 조회할 수 있게 하였습니다.


## 2. 위치정보를 활용한 분실물 센터 및 분실물 조회
![](https://github.com/GisuLee/LossFound/blob/master/readme_gps1.gif?raw=true)
![](https://github.com/GisuLee/LossFound/blob/master/readme_gps1.png?raw=true)
![](https://github.com/GisuLee/LossFound/blob/master/readme_gps2.png?raw=true)     
<code>Google Maps Platform</code> 과 <code>T Map API</code> 와 <code>분실물 데이터</code>를 연동하여 위치기반으로 분실물 보관장소 및 분실물 품목들을 조회할 수 있습니다.


## 3. 분실물 알림 기능   
![](https://github.com/GisuLee/LossFound/blob/master/readme_alarm1.png?raw=true)
![](https://github.com/GisuLee/LossFound/blob/master/readme_alarm2.png?raw=true)   
어플리케이션에 <strong>잃어버린 분실물</strong>을 등록하게 되면, <strong>유사한 분실물</strong>이 발견되면 
자동으로 사용자에게 <strong>알림</strong>을 주는 기능으로, 안드로이드의 <code>Job Serivce API</code>를 사용하여 구현하였습니다.

## 4. 분실물 저장 기능
![](https://github.com/GisuLee/LossFound/blob/master/readme_alarm2.png?raw=true)   
찾는 분실물이 많을 때 데이터를 임시로 저장할 수 있는 기능으로 로컬 저장소에 분실물 데이터를 저장합니다.
안드로이드 <code>SharedPreference API</code> 와 이미지를 저장하기 위해 <code>Base64 인코딩/디코딩</code>을 사용하였습니다. 
