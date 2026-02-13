package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {
    private String userId;
    private String username;
    private String role;
}
