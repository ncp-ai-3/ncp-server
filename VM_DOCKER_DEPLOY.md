# 운영 Docker 로그 확인 가이드

## 최근 로그 보기

```bash
docker logs --tail=200 ncp-server
```

## 실시간 로그 보기

```bash
docker logs -f ncp-server
```

## 특정 에러 검색

```bash
docker logs ncp-server 2>&1 | grep "NAVER DIRECTIONS"
docker logs ncp-server 2>&1 | grep "BUSINESS EXCEPTION"
docker logs ncp-server 2>&1 | grep "HTTP RESPONSE"
```

## SQL 로그 켜기

운영 기본 profile은 SQL 로그를 끕니다.

```dotenv
SPRING_PROFILES_ACTIVE=prod
```

SQL과 바인딩 파라미터를 확인해야 할 때만 `.env`에서 profile을 추가합니다.

```dotenv
SPRING_PROFILES_ACTIVE=prod,prod-sql
```

그 다음 컨테이너를 재시작합니다.

```bash
docker compose up -d --force-recreate
docker logs -f ncp-server
```

확인이 끝나면 다시 끄는 것을 권장합니다.

```dotenv
SPRING_PROFILES_ACTIVE=prod
```

## KST 시간 확인

컨테이너 환경변수 확인:

```bash
docker exec ncp-server date
docker exec ncp-server printenv TZ
```

로그 시간은 `Asia/Seoul` 기준으로 출력됩니다.

## 주요 로그 키워드

HTTP 요청/응답:

```text
[HTTP REQUEST]
[HTTP RESPONSE]
```

네이버 Directions API:

```text
[NAVER DIRECTIONS REQUEST]
[NAVER DIRECTIONS RESPONSE]
[NAVER DIRECTIONS ERROR]
```

비즈니스 예외:

```text
[BUSINESS EXCEPTION]
```

민감정보는 로그 마스킹 대상입니다.

```text
Authorization, accessToken, refreshToken, token, password, secret, clientSecret, apiKey, key, code, cookie
```
