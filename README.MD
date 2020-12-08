# 공공데이터를 이용한 분실물 찾기 어플리케이션

## Introduction

> 공공데이터**(경찰청, 부산메트로, 서울대중교통 제공)**를 파싱하여, 분실물을 통합된 시스템에서 조회할 수 있다. 또, 위치기반서비스를 이용하여 주변 분실물 센터 정보 및 분실물 데이터를 조회하고, 등록한 분실물이 습득되면 사용자에게 알림을 주는 알림기능을 구현한다.

## Skill

- Java 8
- Android 10
- 공공데이터 경찰청 분실물 API
- Google Maps API
- T Map API

## Description

### **1. 분실물 조회 기능**

- **경찰청, 부산메트로, 서울메트로**에서 제공하는 데이터를 `Jsoup`을 이용하여 `Parsing`하거나 `REST API`를 사용하여 분실물 정보를 조회할 수 있게 하였습니다.

### **2. 위치정보를 활용한 분실물 센터 및 분실물 조회**

- `Google Maps Platform` 과 `T Map API` 와 `분실물 데이터`를 연동하여 위치기반으로 분실물 보관장소 및 분실물 품목들을 조회할 수 있습니다.

## **3. 분실물 알림 기능**

- 어플리케이션에 **잃어버린 분실물**을 등록하게 되면, **유사한 분실물**이 발견되면 자동으로 사용자에게 **알림**을 주는 기능으로, 안드로이드의 `Job Serivce API`를 사용하여 구현하였습니다.

### **4. 분실물 저장 기능**

- 찾는 분실물이 많을 때 데이터를 임시로 저장할 수 있는 기능으로 로컬 저장소에 분실물 데이터를 저장합니다. 안드로이드 `SharedPreference API` 와 이미지를 저장하기 위해 `Base64 인코딩/디코딩`을 사용하였습니다.

![https://github.com/GisuLee/LossFound/raw/master/readme_alarm2.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_alarm2.png?raw=true)

## Screenshot

![https://github.com/GisuLee/LossFound/raw/master/readme_main0.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_main0.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_main1.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_main1.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_main2.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_main2.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_main3.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_main3.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_main4.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_main4.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_gps1.gif?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_gps1.gif?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_gps1.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_gps1.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_gps2.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_gps2.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_alarm1.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_alarm1.png?raw=true)

![https://github.com/GisuLee/LossFound/raw/master/readme_alarm2.png?raw=true](https://github.com/GisuLee/LossFound/raw/master/readme_alarm2.png?raw=true)
