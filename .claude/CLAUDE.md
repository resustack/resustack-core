# Resustack Core - Claude Code 가이드

## 프로젝트 개요

이력서 관리 플랫폼의 백엔드 코어 서비스입니다.
Kotlin 기반의 Spring Boot 멀티 모듈 프로젝트로 구성되어 있습니다.

## 기술 스택

- **언어**: Kotlin 2.2.21, Java 21
- **프레임워크**: Spring Boot 4.0.0
- **빌드 도구**: Gradle (Groovy DSL)
- **데이터베이스**:
  - PostgreSQL (인증/사용자 데이터)
  - MongoDB (이력서/템플릿 데이터)
- **마이그레이션**: Liquibase 5.0.1
- **인증**: OAuth2 + JWT (jjwt 0.13.0)
- **API 문서**: SpringDoc OpenAPI 3.0.0
- **테스트**: JUnit 5, Mockito-Kotlin 6.1.0, k6 (부하 테스트)

## 모듈 구조

```
resustack-core/
├── resustack-api/      # REST API 서비스 (MongoDB)
├── resustack-auth/     # 인증 서비스 (PostgreSQL, OAuth2)
├── resustack-common/   # 공통 모듈 (Entity, Security, JWT)
├── resustack-infra/    # Docker 인프라 설정
└── k6/                 # 부하 테스트 스크립트
```

### 모듈별 역할

- **resustack-api**: 이력서, 템플릿 관련 API 제공 (bootJar)
- **resustack-auth**: OAuth2 로그인, JWT 토큰 발급 (bootJar)
- **resustack-common**: 공통 엔티티, 보안 설정, JWT 유틸 (jar 라이브러리)
- **resustack-infra**: Docker Compose 환경 설정

## 코드 스타일 가이드

### Kotlin 컨벤션

- 함수명: camelCase (예: `createResume`, `findByUserId`)
- 클래스명: PascalCase (예: `ResumeService`, `TemplateEntity`)
- 패키지명: lowercase (예: `com.resustack.api.domain.resume`)
- 상수: SCREAMING_SNAKE_CASE (예: `MAX_RETRY_COUNT`)

### 패키지 구조

```
domain/
├── {도메인명}/
│   ├── application/     # Service, UseCase
│   │   └── dto/         # Request/Response DTO
│   ├── model/           # Entity, ValueObject
│   │   └── repository/  # Repository 인터페이스
│   └── presentation/    # Controller
```

### 필수 준수 사항

- Exception 처리는 `BusinessException` 계열 사용
- 로깅은 `LoggerExtensions.kt`의 확장 함수 사용
- API 응답은 `ResponseData` 래퍼 클래스 사용
- JPA Entity는 `resustack-common`에 정의
- `GlobalExceptionHandler` 에서 예외 일괄 처리

---

## Claude Code 작업 규칙

### 1. 언어

- **모든 응답은 한국어로 작성**
- 코드 주석도 한국어로 작성
- Implementation Plan 문서도 한국어로 작성

### 2. 코드 스타일

- 기존 코드 스타일을 반드시 따를 것
- 새로운 패턴이나 스타일 도입 금지 (기존 패턴 유지)
- 불필요한 리팩토링 자제

### 3. 라이브러리

- **새로운 라이브러리 설치 전 반드시 허락 요청**
- 기존 라이브러리로 해결 가능한지 먼저 검토

### 4. 주석 작성

- "왜" 이렇게 구현했는지 설명
- 불필요한 주석 금지
- 복잡한 로직에는 단계별 설명 추가

### 5. 성능

- **속도 저하되는 코드 절대 금지**
- N+1 쿼리 방지
- 불필요한 반복문, 메모리 낭비 코드 금지
- 대용량 데이터 처리 시 페이징/스트리밍 고려

### 6. 제안

- 더 나은 아키텍처나 라이브러리가 있다면 **반드시 제안**
- 현재 구현의 문제점 발견 시 개선안 제시

---

## 보안 주의사항

- `.env` 파일, credentials 절대 커밋 금지
- API 키, 비밀번호 등 민감 정보 하드코딩 금지
- JWT Secret은 환경 변수에서 로드
- SQL Injection, XSS 등 OWASP Top 10 취약점 방지

## Git 컨벤션

### 브랜치 네이밍

- `feature/RS-{이슈번호}` - 새 기능
- `bugfix/RS-{이슈번호}` - 버그 수정

### 커밋 메시지

```
{type}: {한글 설명}

예시:
feat: 이력서 PDF 내보내기 기능 추가
fix: 로그인 시 빈 화면 버그 수정
refactor: 템플릿 서비스 코드 정리
test: 이력서 API K6 부하 테스트 추가
docs: README 업데이트
```

## 참고 파일

- API 테스트: `k6/` 디렉토리
- Docker 설정: `resustack-infra/docker-compose.yml`
- PR 템플릿: `.github/PULL_REQUEST_TEMPLATE.md`
- CodeRabbit 설정: `.coderabbit.yaml`
