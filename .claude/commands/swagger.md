---
description: Controller에 대한 Swagger 문서 인터페이스 생성
argument-hint: <Controller 파일 경로>
allowed-tools: Read, Glob, Grep, Write, Edit
---

## Swagger 문서 생성 요청

**대상 Controller**: @$ARGUMENTS

## 작업 지침

### 1. Controller 파일 분석

- 대상 Controller 파일을 읽고 구조 파악
- 모든 API 엔드포인트 메서드 확인 (메서드명, HTTP 메서드, 경로, 파라미터, 반환 타입)
- Request/Response DTO 타입 확인
- 인증 필요 여부 확인 (`@AuthenticationPrincipal` 존재 여부)

### 2. Swagger Docs 인터페이스 생성 규칙

#### 2.1 파일 위치 및 네이밍

Controller 파일 경로를 기반으로 Docs 인터페이스 생성:

```text
원본: src/main/kotlin/.../presentation/{ClassName}Controller.kt
생성: src/main/kotlin/.../presentation/swagger/{ClassName}ControllerDocs.kt
```

#### 2.2 기본 구조

```kotlin
package {패키지}.presentation.swagger

import {필요한 DTO import}
import com.resustack.common.model.ResponseData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
// 기타 필요한 import

@Tag(name = "{도메인명}", description = "{도메인} 관리 API")
interface {ClassName}ControllerDocs {

    @Operation(
        summary = "{API 기능 요약}",
        description = "{API 상세 설명}"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
        ]
    )
    fun {메서드명}(
        @Parameter(description = "파라미터 설명", required = true)
        @PathVariable id: String
    ): ResponseEntity<ResponseData<ResponseType>>
}
```

### 3. 어노테이션 작성 규칙

#### 3.1 @Tag
- `name`: 도메인명 (영문, 첫 글자 대문자)
- `description`: "{도메인명} 관리 API" 형식 (한글)

#### 3.2 @Operation
- `summary`: API 기능을 간단히 요약 (한글, 5-10자)
  - 예: "이력서 생성", "템플릿 조회", "사용자 삭제"
- `description`: 상세 설명 (한글, 완전한 문장)
  - 예: "새로운 이력서를 생성합니다.", "ID로 템플릿을 조회합니다."

#### 3.3 @ApiResponses

HTTP 메서드별 기본 응답 코드:
- **POST (생성)**:
  - `201`: "생성 성공"
  - `400`: "잘못된 요청"
- **GET (조회)**:
  - `200`: "조회 성공"
  - `404`: "리소스를 찾을 수 없음"
- **PUT (수정)**:
  - `200`: "수정 성공"
  - `400`: "잘못된 요청"
  - `403`: "수정 권한 없음" (인증 필요시)
  - `404`: "리소스를 찾을 수 없음"
- **DELETE (삭제)**:
  - `200`: "삭제 성공"
  - `403`: "삭제 권한 없음" (인증 필요시)
  - `404`: "리소스를 찾을 수 없음"

#### 3.4 @Parameter

파라미터별 설정:
- **@PathVariable**:
  ```kotlin
  @Parameter(description = "{리소스} ID", required = true, example = "507f1f77bcf86cd799439011")
  @PathVariable id: String
  ```
- **@RequestParam**:
  ```kotlin
  @Parameter(description = "파라미터 설명", required = true/false, example = "ACTIVE")
  @RequestParam param: Type
  ```
- **@RequestBody**:
  ```kotlin
  @Parameter(description = "{리소스} 생성 요청 정보", required = true)
  @Valid @RequestBody request: RequestType
  ```
- **@AuthenticationPrincipal** (인증 정보):
  ```kotlin
  @Parameter(hidden = true)
  @AuthenticationPrincipal principal: PrincipalDetails
  ```
- **@ModelAttribute (페이징 등)**:
  ```kotlin
  @ParameterObject @Valid @ModelAttribute paginationRequest: PaginationRequest
  ```

### 4. Controller 수정

Swagger Docs 인터페이스 생성 후, Controller가 이를 구현하도록 수정:

```kotlin
import {패키지}.presentation.swagger.{ClassName}ControllerDocs

@RestController
@RequestMapping("/api/{resource}")
class {ClassName}Controller(
    private val service: Service
) : {ClassName}ControllerDocs {  // 인터페이스 구현

    @PostMapping(version = "1.0")
    override fun createResource(...) {  // override 키워드 추가
        // 기존 로직 유지
    }
}
```

**중요**: Controller의 기존 로직은 절대 수정하지 말고, 다음만 변경:
1. import 추가
2. 클래스 선언부에 `: {ClassName}ControllerDocs` 추가
3. 각 메서드에 `override` 키워드 추가

### 5. 주의사항

- **한글 사용**: 모든 description, summary는 한글로 작성
- **응답 타입 정확성**: Controller의 실제 반환 타입과 Docs 인터페이스의 반환 타입 일치 필수
- **파라미터 순서**: Controller 메서드와 Docs 인터페이스의 파라미터 순서 동일하게 유지
- **어노테이션 동기화**: Controller의 파라미터 어노테이션(`@Valid`, `@PathVariable` 등)을 Docs 인터페이스에도 정확히 동일하게 작성
- **불필요한 import 금지**: 실제 사용하는 어노테이션과 타입만 import
- **기존 코드 스타일 준수**: 프로젝트의 기존 Swagger 문서 스타일을 따름

### 6. 예시

```kotlin
// presentation/swagger/UserControllerDocs.kt
package com.resustack.api.domain.user.presentation.swagger

import com.resustack.api.domain.user.application.dto.UserCreateRequest
import com.resustack.api.domain.user.application.dto.UserResponse
import com.resustack.common.model.ResponseData
import com.resustack.common.security.principal.PrincipalDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "User", description = "사용자 관리 API")
interface UserControllerDocs {

    @Operation(
        summary = "사용자 생성",
        description = "새로운 사용자를 생성합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    fun createUser(
        @Parameter(description = "사용자 생성 요청 정보", required = true)
        @Valid @RequestBody request: UserCreateRequest
    ): ResponseEntity<ResponseData<UserResponse>>

    @Operation(
        summary = "사용자 조회",
        description = "ID로 사용자를 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
            ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        ]
    )
    fun getUserById(
        @Parameter(description = "사용자 ID", required = true)
        @PathVariable id: String,
        @Parameter(hidden = true)
        @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<UserResponse>>
}
```

## 작업 순서

1. Controller 파일 읽기
2. API 엔드포인트 분석
3. `swagger` 패키지 디렉토리 확인 (없으면 생성 필요)
4. `{ClassName}ControllerDocs.kt` 인터페이스 파일 생성
5. Controller 파일 수정 (인터페이스 구현)
6. 생성된 파일 경로와 변경 사항 요약 출력
