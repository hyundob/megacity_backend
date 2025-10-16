# Megacity Back API 명세서

## 프로젝트 개요
- **프로젝트명**: Megacity Back
- **설명**: 제주 전력 데이터 및 예측 정보를 제공하는 REST API 서비스
- **기술 스택**: Spring Boot, PostgreSQL, JPA/Hibernate
- **데이터베이스**: PostgreSQL (192.168.0.3:15432/megacitydb)

---

## 공통 사항
- **Base URL**: `http://localhost:8080` (기본 포트)
- **Content-Type**: `application/json`
- **인증**: 현재 미구현

---

## API 엔드포인트

### 1. 수요 예측 (Demand Predict)
제주 전력 수요 예측 데이터를 제공합니다.

#### 1.1 금일 수요 예측 조회
```
GET /api/demand-predict/today
```

**설명**: 오늘의 전력 수요 예측 데이터를 조회합니다.

**응답 예시**:
```json
[
  {
    "crtnTm": "202310101200",
    "fcstTm": "202310101300",
    "fcstQgen": 1234.56,
    "fcstQgmx": 1300.00,
    "fcstQgmn": 1200.00
  }
]
```

**응답 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| crtnTm | String | 생성시간 (yyyyMMddHHmm) |
| fcstTm | String | 예측시간 (yyyyMMddHHmm) |
| fcstQgen | Double | 예측 발전량 |
| fcstQgmx | Double | 예측 최대 발전량 |
| fcstQgmn | Double | 예측 최소 발전량 |

**데이터 소스**: `REP_DATA_RE_FCST_LFD_DA`

---

### 2. 기상 예측 (Forecast Predict)
제주 기상 예측 정보를 제공합니다.

#### 2.1 최신 기상 예측 조회
```
GET /api/forecast-predict/latest
```

**설명**: 가장 최근의 기상 예측 데이터를 조회합니다.

**응답 예시**:
```json
{
  "crtnTm": "202310101200",
  "fcstTm": "202310101300",
  "fcstSrad": 500.12345,
  "fcstTemp": 25.5,
  "fcstHumi": 65.2,
  "fcstWspd": 3.5,
  "fcstPsfc": 1013.25
}
```

**응답 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| crtnTm | String | 생성시간 (yyyyMMddHHmm) |
| fcstTm | String | 예측시간 (yyyyMMddHHmm) |
| fcstSrad | BigDecimal | 일사량 |
| fcstTemp | BigDecimal | 온도 (°C) |
| fcstHumi | BigDecimal | 습도 (%) |
| fcstWspd | BigDecimal | 풍속 (m/s) |
| fcstPsfc | BigDecimal | 기압 (hPa) |

#### 2.2 전체 기상 예측 조회
```
GET /api/forecast-predict/all
```

**설명**: 모든 기상 예측 데이터를 조회합니다.

**응답**: `RepDataHgFcstNwpDa` 엔티티 리스트

#### 2.3 기상 예측 요약
```
GET /api/forecast-predict/summary
```

**설명**: 기상 예측 데이터의 요약 정보를 조회합니다.

**응답**: `ForeCastDto` 리스트

#### 2.4 최근 48시간 예측
```
GET /api/forecast-predict/last-48h
```

**설명**: 최근 48시간의 기상 예측 데이터를 조회합니다.

**응답**: `ForeCastDto` 리스트

**데이터 소스**: `REP_DATA_HG_FCST_NWP_DA`

---

### 3. 수소 발전 정보 (Hydrogen Generation Info)
제주 수소 발전 실측 정보를 제공합니다.

#### 3.1 금일 발전 정보 조회
```
GET /api/hg-gen-info/today
```

**설명**: 오늘의 수소 발전 실측 데이터를 조회합니다.

**응답 예시**:
```json
[
  {
    "areaGrpCd": "JEJU",
    "tm": "202310101200",
    "hgenProd": 1500.5,
    "hgenCapa": 2000.0
  }
]
```

**응답 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| areaGrpCd | String | 지역 그룹 코드 |
| tm | String | 시간 (yyyyMMddHHmm) |
| hgenProd | Double | 수소 발전 생산량 (MW) |
| hgenCapa | Double | 수소 발전 용량 (MW) |

**데이터 소스**: `REP_DATA_HG_MEAS_GEM_GENT_DA`

---

### 4. 수소 발전 예측 (Hydrogen Generation Predict)
제주 수소 발전 예측 정보를 제공합니다.

#### 4.1 금일 발전 예측 조회
```
GET /api/hg-gen-predict/today
```

**설명**: 오늘의 수소 발전 예측 데이터를 조회합니다.

**응답 예시**:
```json
[
  {
    "areaGrpCd": "JEJU",
    "fcstTm": "202310101300",
    "fcstQgen": 1550.0,
    "fcstCapa": 2000.0
  }
]
```

**응답 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| areaGrpCd | String | 지역 그룹 코드 |
| fcstTm | String | 예측 시간 (yyyyMMddHHmm) |
| fcstQgen | Double | 예측 발전량 (MW) |
| fcstCapa | Double | 예측 설비용량 (MW) |

**데이터 소스**: `REP_DATA_HG_FCST_GEN_GENT_DA` (HG: Hydrogen)

---

### 5. 제주 출력 제어 예측 (Jeju Curtailment Predict)
제주 전력 출력 제어 예측 정보를 제공합니다.

#### 5.1 금일 출력 제어 예측 조회
```
GET /api/jeju-curt-predict/today
```

**설명**: 오늘의 전력 출력 제어 예측 데이터를 조회합니다.

**응답 예시**:
```json
[
  {
    "fcstTm": "202310101300",
    "fcstMinpw": 800.5,
    "fcstCurt": 50.2
  }
]
```

**응답 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| fcstTm | String | 예측 시간 (yyyyMMddHHmm) |
| fcstMinpw | Double | 예측 최소 전력 (MW) |
| fcstCurt | Double | 예측 출력 제어량 (MW) |

**데이터 소스**: `REP_DATA_P2H_FCST_CURT_DA`

---

### 6. 제주 숙비 운영 (Jeju Sukub Operation)
제주 전력 수급 운영 정보를 제공합니다.

#### 6.1 최신 운영 정보 조회
```
GET /api/sukub-operation/latest
```

**설명**: 가장 최근의 전력 수급 운영 데이터를 조회합니다.

**응답 예시**:
```json
{
  "tm": "202310101200",
  "suppAbility": 3000.12345,
  "currPwrTot": 2500.54321,
  "renewPwrTot": 800.11111,
  "renewPwrSolar": 500.22222,
  "renewPwrWind": 300.33333
}
```

#### 6.2 금일 운영 정보 조회
```
GET /api/sukub-operation/today
```

**설명**: 오늘의 전력 수급 운영 데이터 목록을 조회합니다.

**응답**: `SukubMDto` 리스트

**응답 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| tm | String | 시간 (yyyyMMddHHmm) |
| suppAbility | BigDecimal | 공급 능력 (MW) |
| currPwrTot | BigDecimal | 현재 전력 총량 (MW) |
| renewPwrTot | BigDecimal | 신재생 전력 총량 (MW) |
| renewPwrSolar | BigDecimal | 태양광 전력 (MW) |
| renewPwrWind | BigDecimal | 풍력 전력 (MW) |

**데이터 소스**: `REP_DATA_RE_KPX_JEJU_SUKUB_M`

---

### 7. 제주 기상 (Jeju Weather)
제주 실시간 기상 정보를 제공합니다.

#### 7.1 현재 기상 정보 조회
```
GET /api/jeju-weather/current
```

**설명**: 제주시의 실황(T1H/PTY/VEC/WSD) 및 예보(SKY/PTY) 데이터를 조회합니다.

**응답 예시**:
```json
{
  "temperature": 25.5,
  "precipitationType": "없음",
  "windDirection": 180,
  "windSpeed": 3.5,
  "sky": "맑음",
  "forecast": {
    "sky": "구름많음",
    "pty": "없음"
  }
}
```

**응답**: 기상청 API 기반 실황 및 예보 데이터

---

### 8. 신재생 발전 예측 (Renewable Energy Generation Predict)
제주 신재생 에너지 발전 예측 정보를 제공합니다.

#### 8.1 금일 발전 예측 차트 데이터
```
GET /api/re-gen-predict/today
```

**설명**: 오늘의 신재생 에너지 발전 예측 차트 데이터를 조회합니다.

**응답 예시**:
```json
[
  {
    "fcstTm": "202310101300",
    "fcstQgen": 1200.5,
    "fcstQgmx": 1300.0,
    "fcstQgmn": 1100.0,
    "fcstCapa": 1500.0,
    "essChrg": 50.5,
    "essDisc": 30.2,
    "essCapa": 100.0
  }
]
```

**응답 필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| fcstTm | String | 예측 시간 (yyyyMMddHHmm) |
| fcstQgen | Double | 최종 발전량 (MW) |
| fcstQgmx | Double | 예측 최대 발전량 (MW) |
| fcstQgmn | Double | 예측 최소 발전량 (MW) |
| fcstCapa | Double | 예측 설비용량 (MW) |
| essChrg | Double | ESS 충전량 (MWh) |
| essDisc | Double | ESS 방전량 (MWh) |
| essCapa | Double | ESS 용량 (MWh) |

#### 8.2 금일 ESS 운영 데이터
```
GET /api/re-gen-predict/ess
```

**설명**: 오늘의 ESS(에너지 저장 시스템) 운영 데이터를 조회합니다.

**응답**: `FcstGenDaChartDto` 리스트 (8.1과 동일한 형식)

**데이터 소스**: `REP_DATA_RE_FCST_GEN_DA`

---

## 에러 응답

### 공통 에러 코드
| HTTP 상태 코드 | 설명 |
|---------------|------|
| 200 | 성공 |
| 400 | 잘못된 요청 |
| 404 | 리소스를 찾을 수 없음 |
| 500 | 서버 내부 오류 |

### 에러 응답 예시
```json
{
  "timestamp": "2023-10-10T12:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "데이터 조회 중 오류가 발생했습니다.",
  "path": "/api/demand-predict/today"
}
```

---

## 데이터베이스 테이블

### 주요 테이블 목록
| 테이블명 | 설명 |
|---------|------|
| REP_DATA_RE_FCST_LFD_DA | 전력 수요 예측 데이터 |
| REP_DATA_HG_FCST_NWP_DA | 기상 예측 데이터 |
| REP_DATA_HG_MEAS_GEM_GENT_DA | 수소 발전 실측 데이터 (HG: Hydrogen) |
| REP_DATA_HG_FCST_GEN_GENT_DA | 수소 발전 예측 데이터 (HG: Hydrogen) |
| REP_DATA_P2H_FCST_CURT_DA | 출력 제어 예측 데이터 |
| REP_DATA_RE_KPX_JEJU_SUKUB_M | 제주 전력 수급 데이터 |
| REP_DATA_RE_FCST_GEN_DA | 신재생 에너지 발전 예측 데이터 |

---

## 환경 설정

### application.properties
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://192.168.0.3:15432/megacitydb
spring.datasource.username=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# KMA (기상청) API Key
kma.key=247cb2bdaeee98b1922e4a431b59d2e477c23eed00c0180 0189dc1f5255eb130
```

---

## API 테스트

### cURL 예시

#### 1. 수요 예측 조회
```bash
curl -X GET http://localhost:8080/api/demand-predict/today
```

#### 2. 최신 기상 예측 조회
```bash
curl -X GET http://localhost:8080/api/forecast-predict/latest
```

#### 3. 금일 발전 정보 조회
```bash
curl -X GET http://localhost:8080/api/hg-gen-info/today
```

#### 4. 최신 숙비 운영 정보 조회
```bash
curl -X GET http://localhost:8080/api/sukub-operation/latest
```

#### 5. 현재 기상 정보 조회
```bash
curl -X GET http://localhost:8080/api/jeju-weather/current
```

---

## 개발 노트

### 기술 스택
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Lombok**: 보일러플레이트 코드 감소
- **Build Tool**: Gradle 또는 Maven

### 개발 가이드라인
1. 모든 엔티티는 `@Entity` 어노테이션 사용
2. DTO를 통한 데이터 전송 계층 분리
3. Service 계층에서 비즈니스 로직 처리
4. Repository 계층에서 데이터 접근 처리
5. 시간 데이터 형식: `yyyyMMddHHmm` (12자리 문자열)

### 향후 개선 사항
- [ ] API 인증/인가 구현 (JWT, OAuth2)
- [ ] API Rate Limiting
- [ ] 페이지네이션 구현
- [ ] Swagger/OpenAPI 문서화
- [ ] 캐싱 전략 구현 (Redis)
- [ ] 에러 핸들링 표준화
- [ ] API 버저닝 전략 수립
- [ ] 로깅 및 모니터링 강화
- [ ] 단위 테스트 및 통합 테스트 작성

---

## 변경 이력
| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|----------|--------|
| 1.0 | 2025-10-10 | 초안 작성 | - |