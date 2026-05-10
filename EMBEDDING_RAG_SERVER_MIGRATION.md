# RAG(임베딩 전용) 서버 위임 시 ncp-server 변경 체크리스트

비동기로 **`popup.id`(또는 추적용 ID)만** RAG 서버에 넘기고, 임베딩·벡터 저장은 RAG가 담당하는 방식으로 바꿀 때 참고합니다.

---

## 1. 스키마 불일치 전제 (반드시 정리)

### 레포(Flyway V1) 기준

- 테이블: **`popup_embedding`**
- PK/FK: `popup_id` → `popup(id)`, 임베딩 행 **팝업당 1건(UNIQUE)**
- 벡터: **`vector(768)`**

### 현재 운영 DB(`labdb`) 예시 — `vector_store`

| 컬럼 | 타입 | 비고 |
|------|------|------|
| `id` | `uuid` | PK, default `uuid_generate_v4()` |
| `content` | `text` | 임베딩 입력 텍스트 |
| `metadata` | `json` | 예: `{"popup_id": 123}` 등 소스 추적 |
| `embedding` | **`vector(384)`** | 차원 **384** |

→ **테이블 이름·PK 형태·벡터 차원이 레포 코드와 다릅니다.**  
다음 중 하나로 **단일 소스**를 정해야 합니다.

1. **RAG가 `vector_store`만 사용:** Flyway에 `vector_store` 생성/인덱스 마이그레이션을 추가하고, Java는 이 테이블에 **쓰지 않음**(RAG만 INSERT).  
2. **`popup_embedding`을 계속 쓰기:** 운영 DB를 Flyway와 맞추거나, `vector_store`를 뷰/동기화 대상으로 두는 정책을 문서화.  
3. **384차원 모델 고정:** RAG에서 쓰는 모델 출력이 **384**이면 DB 컬럼도 `vector(384)`로 통일. 레포의 `768`/`popup_embedding` 가정은 폐기 또는 마이그레이션으로 교체.

**권장:** `metadata`에 **`popup_id`(Long)** 와 문서 종류(`source: popup`)를 넣어, 검색 시 필터링 가능하게 합니다.

---

## 2. RAG 비동기 위임 시 — ncp-server에서 바뀌어야 할 부분

아래는 **Java가 Vertex·JDBC로 임베딩 행을 쓰지 않고**, 크롤 후 **RAG HTTP만 호출**하는 전제입니다.

### 2.1 크롤 플로우 (핵심)

| 파일 | 현재 역할 | 변경 방향 |
|------|-----------|-----------|
| [`PopupCrawlService.java`](src/main/java/com/ncp/team3/crawl/service/PopupCrawlService.java) | 저장 후 `popupEmbeddingService.createOrUpdateEmbedding(...)` | **`popup_id`만 RAG에 enqueue** (예: `RestTemplate`/`WebClient` POST). 성공/실패 카운터 의미를 **「큐 적재 성공/실패」** 로 바꾸거나, 임베딩 카운터를 제거·별도 지표로 분리. |
| [`PopupCrawlResultResponse.java`](src/main/java/com/ncp/team3/crawl/controller/dto/response/PopupCrawlResultResponse.java) | `embeddingSuccess` / `embeddingFailed` | API 계약 유지 시 필드명·설명만 바꾸거나, `enqueueSuccess` 등으로 명확화. |

### 2.2 임베딩 도메인 (제거·축소 또는 대체)

| 파일 | 현재 역할 | 변경 방향 |
|------|-----------|-----------|
| [`PopupEmbeddingService.java`](src/main/java/com/ncp/team3/crawl/service/PopupEmbeddingService.java) | `content` 생성 → Vertex → JDBC upsert | **삭제** 또는 **RAG 호출만 하는 얇은 서비스**로 교체. `buildEmbeddingContent`를 Java에 남길지, 전부 RAG(DB 조회)로 둘지 설계 확정. |
| [`EmbeddingClient.java`](src/main/java/com/ncp/team3/crawl/infrastructure/EmbeddingClient.java) | 임베딩 벡터 계산 추상화 | RAG 위임 시 **미사용이면 삭제**. |
| [`VertexEmbeddingClient.java`](src/main/java/com/ncp/team3/crawl/infrastructure/VertexEmbeddingClient.java) | Vertex `predict` 호출 | **삭제** 또는 프로필 `vertex` 전용으로 분리. |
| [`VertexEmbeddingRequest.java`](src/main/java/com/ncp/team3/crawl/infrastructure/dto/VertexEmbeddingRequest.java) / [`VertexEmbeddingResponse.java`](src/main/java/com/ncp/team3/crawl/infrastructure/dto/VertexEmbeddingResponse.java) | Vertex DTO | Vertex 제거 시 **삭제**. |
| [`PopupEmbeddingJdbcRepository.java`](src/main/java/com/ncp/team3/crawl/infrastructure/PopupEmbeddingJdbcRepository.java) | `popup_embedding` UPSERT | RAG가 DB에만 쓰면 Java에서는 **삭제** 또는 읽기 전용 조회만 유지. **`vector_store`를 Java에서 쓸 계획이면** 별도 Repository·SQL로 새로 작성. |

### 2.3 설정·의존성

| 항목 | 변경 방향 |
|------|-----------|
| `application.yaml` / 환경 변수 | `gcp.project-id`, `gcp.location`, `gcp.vertex.*` 제거 또는 비활성화. **`rag.embedding.base-url`**, 타임아웃, (선택) API 키 추가. |
| [`build.gradle.kts`](build.gradle.kts) | Vertex 미사용 시 `google-auth-library-oauth2-http` 제거 검토. Spring AI transformers 등 로컬 임베딩도 쓰지 않으면 관련 의존성 정리. |

### 2.4 테스트

| 파일 | 변경 방향 |
|------|-----------|
| [`PopupEmbeddingServiceTest.java`](src/test/java/com/ncp/team3/crawl/service/PopupEmbeddingServiceTest.java) | 서비스 삭제·축소에 맞게 수정 또는 RAG 클라이언트 단위 테스트로 이동. |

### 2.5 DB 마이그레이션(Flyway)

- 운영이 **`vector_store`** 이면: 레포의 `V1__popup_embedding.sql` 중 **`popup_embedding`만을 전제로 한 부분**과 실제 배포 스키마를 맞추는 새 버전 마이그레이션 추가·정리.  
- **차원 384**를 표준으로 하면 `vector(384)`와 인덱스(예: HNSW/IVFFlat) 정책을 마이그레이션에 명시.

### 2.6 파이썬(RAG) 쪽 정합성

- [`popcorn-fastapi-server/popup_embedding_api.py`](../popcorn-fastapi-server/popup_embedding_api.py) 등은 현재 **`popup_embedding`** / **768차원** 가정이면, **`vector_store` + 384차원 + uuid + metadata(popup_id)** 로 INSERT 로직을 바꿔야 운영 DB와 일치합니다.

---

## 3. 트랜잭션·비동기 유의사항

- **`popup` 행 커밋 이후**에만 `popup_id`를 큐에 넣을 것 (저장 트랜잭션 안에서 enqueue하면 RAG가 아직 못 볼 수 있음).  
- RAG는 **`ACCEPTED` ≠ 임베딩 완료**이므로, 모니터링·재시도는 RAG/큐 쪽 책임.  
- `vector_store`에 동일 `popup_id` 중복 허용 여부(유니크 인덱스 on `(metadata->>'popup_id')` 등)를 정책으로 정하기.

---

## 4. 요약

| 질문 | 답 |
|------|----|
| 테이블 이름이 꼭 `popup_embedding`이냐? | 레포 Flyway 기준은 그렇지만, **운영 `vector_store`와 다르면 반드시 한쪽으로 통일**. |
| Java가 바뀌는 핵심은? | **`PopupCrawlService` 이후 경로**: Vertex·`PopupEmbeddingJdbcRepository` 제거 또는 비활성화, **RAG enqueue 클라이언트 추가**, 설정·테스트·마이그레이션 정리. |
| 차원은? | **`vector(384)`에 맞는 모델·마이그레이션·RAG 코드**로 일치시킬 것 (768 가정 코드와 충돌). |

이 문서는 설계·리팩터 착수 전 체크용이며, 실제 커밋 시 팀에서 **스키마 단일 기준**을 먼저 확정하는 것을 권장합니다.
