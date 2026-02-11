package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IImageStoragePort;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Slf4j
@Component
public class CloudinaryStorageAdapter implements IImageStoragePort {

    private final Cloudinary cloudinary;

    public CloudinaryStorageAdapter(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    @Override
    public Mono<String> uploadFromUrl(String imageUrl, String folder) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return Mono.empty();
        }

        return Mono.fromCallable(() -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(imageUrl, ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "quality", "auto:best",
                    "overwrite", true));
            String secureUrl = (String) result.get("secure_url");
            log.info("Image uploaded to Cloudinary: {}", secureUrl);
            return secureUrl;
        }).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    log.error("Failed to upload image to Cloudinary: {}", e.getMessage());
                    return Mono.just(imageUrl);
                });
    }
}
