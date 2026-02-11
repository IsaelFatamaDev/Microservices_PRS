package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization;

import reactor.core.publisher.Mono;

public interface IImageStoragePort {
    Mono<String> uploadFromUrl(String imageUrl, String folder);
}
