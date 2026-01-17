---
description: 지정한 파일에 대한 테스트 코드 작성
argument-hint: <파일경로>
allowed-tools: Read, Glob, Grep, Write, Edit, Bash(./gradlew:*)
---

## 테스트 코드 작성 요청

**대상 파일**: @$ARGUMENTS

## 작업 지침

### 1. 대상 파일 분석
- 위 대상 파일을 읽고 클래스/함수 구조를 파악하세요
- 의존성(Repository, Service 등)을 확인하세요
- public 메서드들의 시그니처와 비즈니스 로직을 분석하세요

### 2. 테스트 코드 스타일 (필수 준수)

#### 2.1 Service/Repository 테스트

```kotlin
@ExtendWith(MockitoExtension::class)
class {클래스명}Test {

    @Mock
    lateinit var dependency: DependencyType

    @InjectMocks
    lateinit var targetClass: TargetClass

    @Nested
    inner class `메서드명 또는 기능` {
        @Test
        fun `성공 케이스 설명`() {
            // given

            // when

            // then
        }

        @Test
        fun `실패 케이스 설명`() {
            // given

            // when & then
        }
    }
}
```

**필수 규칙**:
- JUnit 5 + Mockito-Kotlin 사용
- `@ExtendWith(MockitoExtension::class)` 사용
- `@Nested` inner class로 테스트 그룹화 (메서드 또는 기능별)
- 테스트 메서드명은 **한글 백틱 문법** 사용 (예: `` `성공` ``, `` `잘못된 입력 시 예외 발생` ``)
- given-when-then 주석으로 구조화
- `whenever` 또는 `given` 사용하여 모킹
- `verify`로 호출 검증

#### 2.2 Controller 테스트 (MockMvc)

Controller 테스트는 **반드시 MockMvc**를 사용하여 HTTP 계층을 테스트합니다:

```kotlin
@WebMvcTest(TemplateController::class)
class TemplateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var templateService: TemplateService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Nested
    inner class `템플릿 생성` {
        @Test
        fun `성공 - 201 Created 응답`() {
            // given
            val request = TemplateCreateRequest(...)
            val response = TemplateResponse(...)
            given(templateService.createTemplate(any())).willReturn(response)

            // when & then
            mockMvc.perform(
                post("/api/templates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.httpStatus").value(201))
                .andExpect(jsonPath("$.data.id").value("template-123"))
                .andExpect(jsonPath("$.data.name").value("Modern Template"))

            verify(templateService).createTemplate(any())
        }

        @Test
        fun `실패 - 잘못된 입력 시 400 Bad Request`() {
            // given
            val invalidRequest = TemplateCreateRequest(name = "") // 유효하지 않은 요청

            // when & then
            mockMvc.perform(
                post("/api/templates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest))
            )
                .andExpect(status().isBadRequest)
        }
    }
}
```

**Controller 테스트 필수 규칙**:
- `@WebMvcTest({ControllerClass}::class)` 사용 (특정 Controller만 로드)
- `@MockBean`으로 Service 계층 모킹
- `MockMvc`와 `ObjectMapper`를 `@Autowired`로 주입
- `mockMvc.perform()`으로 HTTP 요청 시뮬레이션
- `.andExpect()`로 HTTP 상태 코드, 응답 바디 검증
- `jsonPath()`로 JSON 응답 필드 검증
- GET/POST/PUT/DELETE 등 실제 HTTP 메서드 사용
- 인증이 필요한 경우 `@WithMockUser` 또는 `.with(user())` 사용
- Service 호출 검증은 `verify()`로 수행

### 3. 테스트 파일 위치

원본 파일 경로에 따라 테스트 파일 위치 결정:
- `src/main/kotlin/.../{ClassName}.kt` → `src/test/kotlin/.../{ClassName}Test.kt`
- 패키지 구조 동일하게 유지

### 4. 테스트 케이스 작성 원칙

**Service 테스트**:
- 정상 케이스 (성공)
- 예외 케이스 (리소스 없음, 권한 없음, 잘못된 입력 등)
- 엣지 케이스 (빈 리스트, null 처리 등)

**Controller 테스트**:
- HTTP 상태 코드 검증 (200, 201, 400, 404 등)
- 응답 바디 검증
- Service 호출 검증

**Repository 테스트** (필요시):
- CRUD 동작 검증
- 쿼리 메서드 검증

### 5. 실행

테스트 코드 작성 후 해당 테스트만 실행하여 통과 여부 확인:
```bash
./gradlew test --tests "{패키지}.{테스트클래스명}"
```

## 주의사항

- 기존 테스트 파일이 있다면 덮어쓰지 말고 추가/수정하세요
- 불필요한 import 추가 금지
- 테스트가 실제로 의미 있는 검증을 하는지 확인하세요
- 모든 public 메서드에 대해 최소 1개 이상의 테스트 케이스 작성
