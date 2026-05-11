# Docker 배포 가이드

Spring Boot 서버를 Docker Compose로 실행하는 방법입니다. 운영 환경의 DB 비밀번호, API Key 같은 민감정보는 코드에 넣지 말고 반드시 `.env`로 주입합니다.

## 1. 서버에 Docker 설치

Ubuntu 기준:

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable --now docker
```

설치 확인:

```bash
docker --version
docker compose version
```

## 2. 프로젝트 내려받기

```bash
git clone <repository-url>
cd <repository-directory>
```

## 3. 환경변수 파일 생성

```bash
cp .env.example .env
```

`.env`를 열어서 실제 값으로 변경합니다.

```dotenv
DB_URL=jdbc:postgresql://your-db-host:5432/your-db-name
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password
RAG_EMBEDDING_BASE_URL=http://your-rag-server-ip:8001
RAG_EMBEDDING_PATH=/api/v1/embed
RAG_EMBEDDING_CONNECT_TIMEOUT_SECONDS=5
RAG_EMBEDDING_READ_TIMEOUT_SECONDS=120
RAG_EMBEDDING_API_KEY=
RAG_EMBEDDING_DIMENSION=768
FASTAPI_CHAT_URL=http://your-fastapi-server-ip:8000/chat
NAVER_MAPS_CLIENT_ID=your-naver-client-id
NAVER_MAPS_CLIENT_SECRET=your-naver-client-secret
JWT_ACCESS_TOKEN_SECRET=change-this-access-secret-at-least-32-bytes
JWT_REFRESH_TOKEN_SECRET=change-this-refresh-secret-at-least-32-bytes
NAVER_CLIENT_ID=your-naver-oauth-client-id
NAVER_CLIENT_SECRET=your-naver-oauth-client-secret
NAVER_REDIRECT_URI=http://101.79.22.195/login/oauth2/code/naver
FRONTEND_URL=http://101.79.22.195
NCP_CLIENT_ID=
NCP_CLIENT_SECRET=
```

`.env`는 `.gitignore`에 포함되어 있으므로 Git에 올리지 않습니다.

필수값은 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `RAG_EMBEDDING_BASE_URL`, `NAVER_MAPS_CLIENT_ID`, `NAVER_MAPS_CLIENT_SECRET`입니다. RAG 서버의 임베딩 경로가 기본값과 다르면 `RAG_EMBEDDING_PATH`도 수정합니다. 네이버 OAuth 로그인, 채팅 FastAPI, JWT 운영 시크릿을 운영에서 사용할 경우 나머지 값도 실제 값으로 채워 넣습니다.

## 4. Docker Compose 실행

```bash
docker compose up -d --build
```

컨테이너는 호스트의 `8080` 포트로 실행됩니다.

이미 서버에서 Docker를 설치했다면 여기부터 실행하면 됩니다.

```bash
docker compose --env-file .env up -d --build
```

## 5. 로그 확인

```bash
docker compose logs -f spring
```

정상 기동 후 아래 주소에 접속합니다.

```text
http://서버공인IP:8080/swagger-ui/index.html
```

## 6. NCP 방화벽 설정

NCP ACG 또는 보안그룹에서 서버 인바운드 규칙에 TCP `8080` 포트를 열어야 외부에서 접속할 수 있습니다.

## 7. RAG 서버 확인

`RAG_EMBEDDING_BASE_URL`에는 Spring 서버에서 접근 가능한 RAG/Embedding 서버 공인 URL을 넣습니다. 크롤링 저장 시 Spring 서버가 `RAG_EMBEDDING_BASE_URL + RAG_EMBEDDING_PATH`로 POST 요청을 보내 임베딩 벡터를 받아 저장합니다.

RAG 서버는 외부 요청을 받을 수 있도록 `0.0.0.0`으로 실행되어야 합니다.

예:

```bash
uvicorn app:app --host 0.0.0.0 --port 8000
```

RAG 서버가 별도 NCP 서버에 있다면 해당 서버의 ACG/보안그룹에서도 필요한 포트를 열어야 합니다.

## 8. Naver Maps Directions 설정

Directions API URL은 반드시 아래 값을 사용합니다.

```text
https://maps.apigw.ntruss.com/map-direction/v1/driving
```

`NAVER_MAPS_CLIENT_ID`, `NAVER_MAPS_CLIENT_SECRET`은 Naver Cloud Platform에서 발급받은 Maps API 인증 정보를 `.env`에 입력합니다.

## 9. 네이버 로그인 콜백 설정

네이버 개발자센터의 Callback URL은 백엔드 콜백 주소로 등록합니다.

```text
http://서버공인IP/login/oauth2/code/naver
```

`.env`의 `NAVER_REDIRECT_URI`도 위 값과 정확히 같아야 합니다.

로그인 성공 후 백엔드는 JSON을 반환하지 않고 프론트 콜백 페이지로 redirect합니다.

```text
${FRONTEND_URL}/callback?accessToken=...&refreshToken=...&memberId=...
```

프론트 배포 주소가 있으면 `FRONTEND_URL`에 입력합니다.

## 10. 재배포

코드를 갱신한 뒤 다시 빌드합니다.

```bash
git pull
docker compose up -d --build
docker compose logs -f spring
```

## 11. 중지

```bash
docker compose down
```
