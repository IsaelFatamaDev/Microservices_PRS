package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Output port for the infrastructure service client
 */
public interface IInfrastructureClient {

    /**
     * Gets information of a zone by its ID
     * 
     * @param zoneId identifier of the zone
     * @return data of the zone
     */
    Mono<Map<String, Object>> getZoneById(String zoneId);

    /**
     * Gets information of a water box by its ID
     * 
     * @param waterBoxId identifier of the water box
     * @return water box data
     */
    Mono<Map<String, Object>> getWaterBoxById(String waterBoxId);

    /**
     * Checks if a zone exists
     * 
     * @param zoneId identifier of the zone
     * @return true if it exists
     */
    Mono<Boolean> existsZone(String zoneId);

    /**
     * Checks if a water box exists
     * 
     * @param waterBoxId identifier of the water box
     * @return true if it exists
     */
    Mono<Boolean> existsWaterBox(String waterBoxId);

    /**
     * Gets water boxes of a zone
     * 
     * @param zoneId identifier of the zone
     * @return list of water boxes
     */
    Mono<java.util.List<Map<String, Object>>> getWaterBoxesByZone(String zoneId);
}
