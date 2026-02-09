package pe.edu.vallegrande.vgmsauthentication.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsauthentication.application.dto.request.LoginRequest;
import pe.edu.vallegrande.vgmsauthentication.application.dto.request.LogoutRequest;
import pe.edu.vallegrande.vgmsauthentication.application.dto.request.RefreshTokenRequest;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.IntrospectResponse;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.LoginResponse;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.TokenResponse;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.UserInfoResponse;
import pe.edu.vallegrande.vgmsauthentication.application.mappers.AuthMapper;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.ILoginUseCase;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.ILogoutUseCase;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.IRefreshTokenUseCase;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.IValidateTokenUseCase;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRest {

    private final ILoginUseCase loginUseCase;
    private final IRefreshTokenUseCase refreshTokenUseCase;
    private final ILogoutUseCase logoutUseCase;
    private final IValidateTokenUseCase validateTokenUseCase;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());
        return loginUseCase.execute(AuthMapper.toCredentials(request))
                .map(AuthMapper::toLoginResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Refresh token request");

        return refreshTokenUseCase.execute(request.getRefreshToken(), request.getClientId())
                .map(AuthMapper::toTokenResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("Logout request");

        return logoutUseCase.execute(request.getRefreshToken(), request.getClientId())
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @PostMapping("/logout/token")
    public Mono<ResponseEntity<Void>> logoutWithAccessToken(@RequestHeader("Authorization") String authorization) {
        String accessToken = extractToken(authorization);
        log.info("Logout request with access token");

        return logoutUseCase.executeWithAccessToken(accessToken)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<UserInfoResponse>> validateToken(@RequestHeader("Authorization") String authorization) {
        String accessToken = extractToken(authorization);
        log.debug("Validating token request");
        return validateTokenUseCase.execute(accessToken)
                .map(AuthMapper::toUserInfoResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/userInfo")
    public Mono<ResponseEntity<UserInfoResponse>> getUserInfo(@RequestHeader("Authorization") String authorization) {
        String accessToken = extractToken(authorization);
        log.debug("User info request");
        return validateTokenUseCase.getUserInfo(accessToken)
                .map(AuthMapper::toUserInfoResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/introspect")
    public Mono<ResponseEntity<IntrospectResponse>> introspect(
            @RequestParam String token,
            @RequestParam(defaultValue = "jass-users-service") String clientId,
            @RequestParam(required = false) String clientSecret) {
        log.debug("Introspecting token");
        return validateTokenUseCase.introspect(token, clientId, clientSecret)
                .map(AuthMapper::toIntrospectResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
