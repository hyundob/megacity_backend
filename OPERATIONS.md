# 메가시티 운영 및 검증

## 사용 범위

- 활성 동기화 테이블은 13개입니다. `sync/syncer/impl` 구현체가 대상 목록입니다.
- `REP_DATA_HG_FCST_NWP_DA`, `REP_DATA_HG_FCST_GEN_GENT_DA`, `REP_DATA_HG_MEAS_GEM_GENT_DA`는 사용하지 않습니다. 관련 엔티티·Repository·API·Syncer를 제거했습니다. 실제 DB 테이블이나 데이터는 삭제하지 않았습니다.
- 수소단지 기상 위젯 2개를 제거했습니다. 제주 지도 날씨는 기상청 API로 계속 제공합니다.
- 수소 시뮬레이션은 시스템 기동 후 자동 시작하며 5분마다 저장합니다. 단일 시스템 상태·정지/DR/감소 지령·시간별 이력 테이블을 사용합니다. 계산 기준과 운영 적용 절차는 [HYDROGEN_SIMULATION.md](HYDROGEN_SIMULATION.md)를 참고하세요. 운영 DB는 신규 SQL 적용 후 기동합니다.

## 실행 설정

백엔드: Java 17, `./gradlew bootRun` (Windows: `gradlew.bat bootRun`).

| 환경 변수 | 의미 |
|---|---|
| `MEGACITY_DB_URL` | 내부 PostgreSQL JDBC URL. 운영 프로파일에서는 필수 |
| `MEGACITY_DB_USERNAME`, `MEGACITY_DB_PASSWORD` | 내부 DB 자격 증명 |
| `SYNC_SOURCE_DATASOURCE_URL` | 원천 DB URL. 미설정하면 동기화를 생략 |
| `SYNC_SOURCE_DATASOURCE_USERNAME`, `SYNC_SOURCE_DATASOURCE_PASSWORD` | 원천 DB 계정 |
| `MEGACITY_MARKET_CODE` | 발전 예측의 정확한 시장 코드. 미설정 시 단일 시장만 자동 선택; 여러 코드가 있으면 임의 혼합하지 않고 오류 처리 |
| `MEGACITY_CORS_ORIGINS` | 직접 API를 호출할 프론트 origin 목록, 쉼표 구분 |
| `KMA_KEY` | 기상청 키. 미설정이면 날씨만 데이터 없음 |
| `MODBUS_HOST` | Modbus 바인딩 주소. 기존 기본값은 `0.0.0.0`, 포트 1502 |
| `SPRING_PROFILES_ACTIVE=prod` | 스키마 검증(`ddl-auto=validate`) 및 운영 DB URL 필수 설정 |

프론트: `npm run dev` 또는 `npm run build` 후 `npm start`.

- 기본 API URL은 같은 origin의 `/api`입니다. Next.js가 백엔드로 프록시합니다.
- `BACKEND_API_URL`: Next 서버의 백엔드 URL. 기본 `http://localhost:8080/api`. 별도 배포 서버라면 실제 백엔드 주소를 지정한 후 빌드하세요.
- `NEXT_PUBLIC_API_BASE_URL`: 브라우저가 백엔드에 직접 접근해야 하는 경우에만 지정합니다. HTTPS 화면이면 HTTPS API를 사용하고 CORS origin도 설정하세요.
- 날짜 기준은 한국 시간입니다. 화면의 조회 완료 시각과 원천 데이터 기준시각은 서로 다릅니다.

## 동기화 방식과 복구

`upd_date` 순서로 배치의 상한 시각을 구한 뒤 그 시각에 해당하는 행 전체를 upsert합니다. 같은 수정 시각의 행을 LIMIT로 잘라 버리지 않습니다. 저장된 체크포인트 시각은 다음 실행에서 다시 포함하므로 경계에 늦게 추가된 행도 복사합니다. 쓰기는 `sync.batch-size` 단위이며 한 실행에서 `sync.max-batches-per-run`개의 시각 구간까지 처리합니다.

- 기본값: 1,000건, 실행당 최대 10구간. 동일 시각 그룹은 1,000건을 초과할 수 있습니다.
- 체크포인트는 해당 실행의 쓰기가 모두 성공한 후에만 전진합니다. 중간 실패 시 완료된 행도 다음 실행에서 멱등하게 재처리합니다.
- 원천 `reg_date`와 `upd_date`는 내부 저장 과정에서 보존하며, 수정된 `upd_date`도 대상 DB에 반영합니다.
- 로그·메트릭의 처리 건수에는 경계 재처리 건수가 포함됩니다. 원천 신규 행 수와 같지 않습니다.
- 원천 DB는 모든 삽입·수정 시 `upd_date`를 갱신해야 합니다. 체크포인트보다 과거 시각으로 소급 입력된 행이나 삭제 전파는 이 방식으로 추적하지 않습니다. 그런 데이터는 별도 재동기화 또는 CDC가 필요합니다.
- 이전 버전에서 이미 누락된 데이터가 있다면, 해당 테이블 체크포인트를 누락 이전으로 되돌리는 별도 재동기화가 필요합니다. 이번 코드 수정은 기존 체크포인트를 자동 변경하지 않습니다.
- 대규모 같은 시각 그룹은 결과 조회량도 커질 수 있습니다. 원천 `upd_date` 인덱스와 실제 데이터량을 확인하세요.
- 동기화와 Modbus 갱신은 각각 독립된 단일 스레드 스케줄러를 사용합니다. 같은 프로세스의 수동·정기 동기화도 Syncer별로 직렬화합니다. 여러 백엔드 인스턴스 운영 시 동기화는 한 인스턴스에서만 활성화하세요 (`SYNC_SCHEDULING_ENABLED=false`로 나머지 비활성화).

## Modbus 호환성

기존 주소와 블록 크기를 유지합니다. 미사용 수소·단지 기상 필드는 UINT16/32의 all-ones, FLOAT32의 NaN으로 제공합니다. 예비 레지스터는 0을 유지합니다. 활성 블록도 조회 실패 시 해당 블록만 null 처리하고 나머지는 갱신합니다.

공통 업데이트 카운터는 스냅샷 게시 횟수이며 모든 블록의 데이터 정상 여부를 의미하지 않습니다. 클라이언트는 null과 각 데이터 기준시각을 확인해야 합니다. 최대 동시 접속 기본값은 32 (`modbus.max-clients`)입니다.

## 검증

- `gradlew.bat test`: 동기화 경계·부분 실패 복구·메트릭·시장 선택·Modbus·독립 H2 기동 테스트. Docker 없는 환경에서는 PostgreSQL Testcontainers 통합 테스트가 생략됩니다.
- 프론트: `npm run lint`, `npx tsc --noEmit`, `npm run build`.
- 운영 프로파일 기동 전에는 스키마를 명시적으로 준비해야 합니다. 이번 작업은 운영 DB에 접속해 스키마를 변경하거나 데이터를 재동기화하지 않습니다.
