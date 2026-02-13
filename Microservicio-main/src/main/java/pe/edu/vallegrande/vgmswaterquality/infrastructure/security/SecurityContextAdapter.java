package pe.edu.vallegrande.vgmswaterquality.infrastructure.security;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextAdapter implements ISecurityContext {

     @Override
     public Mono<String> getCurrentUserId() {
          return Mono.deferContextual(ctx -> {
               AuthenticatedUser user = ctx.getOrDefault(AuthenticatedUser.class, null);
               return Mono.justOrEmpty(user != null ? user.getUserId() : "system");
          });
     }

     @Override
     public Mono<String> getCurrentOrganizationId() {
          return Mono.deferContextual(ctx -> {
               AuthenticatedUser user = ctx.getOrDefault(AuthenticatedUser.class, null);
               return Mono.justOrEmpty(user != null ? user.getOrganizationId() : null);
          });
     }

     @Override
     public Mono<Boolean> isSuperAdmin() {
          return Mono.deferContextual(ctx -> {
               AuthenticatedUser user = ctx.getOrDefault(AuthenticatedUser.class, null);
               return Mono.just(user != null && user.isSuperAdmin());
          });
     }

     @Override
     public Mono<Boolean> isAdmin() {
          return Mono.deferContextual(ctx -> {
               AuthenticatedUser user = ctx.getOrDefault(AuthenticatedUser.class, null);
               return Mono.just(user != null && user.isAdmin());
          });
     }

     @Override
     public Mono<Boolean> belongsToOrganization(String organizationId) {
          return Mono.deferContextual(ctx -> {
               AuthenticatedUser user = ctx.getOrDefault(AuthenticatedUser.class, null);
               return Mono.just(user != null && user.belongsToOrganization(organizationId));
          });
     }
}
