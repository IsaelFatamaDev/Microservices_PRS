package pe.edu.vallegrande.msdistribution.domain.ports.out.route;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para el repositorio de Rutas de Distribución.
 * Define el contrato que debe implementar la capa de infraestructura para persistencia.
 * 
 * @version 1.0
 */
public interface DistributionRouteRepositoryPort {
    
    /**
     * Guarda una ruta de distribución.
     * 
     * @param route Ruta a guardar
     * @return Mono con la ruta guardada
     */
    Mono<DistributionRoute> save(DistributionRoute route);
    
    /**
     * Busca una ruta por su ID.
     * 
     * @param id ID de la ruta
     * @return Mono con la ruta encontrada o vacío
     */
    Mono<DistributionRoute> findById(String id);
    
    /**
     * Busca una ruta por su ID y estado.
     * 
     * @param id ID de la ruta
     * @param status Estado de la ruta
     * @return Mono con la ruta encontrada o vacío
     */
    Mono<DistributionRoute> findByIdAndRecordStatus(String id, RecordStatus status);
    
    /**
     * Obtiene todas las rutas.
     * 
     * @return Flux con todas las rutas
     */
    Flux<DistributionRoute> findAll();
    
    /**
     * Obtiene todas las rutas por estado.
     * 
     * @param status Estado a filtrar
     * @return Flux con rutas del estado especificado
     */
    Flux<DistributionRoute> findByRecordStatus(RecordStatus status);
    
    /**
     * Obtiene rutas por ID de organización.
     * 
     * @param organizationId ID de la organización
     * @return Flux con rutas de la organización
     */
    Flux<DistributionRoute> findByOrganizationId(String organizationId);
    
    /**
     * Cuenta rutas por estado.
     * 
     * @param status Estado a contar
     * @return Mono con el conteo
     */
    Mono<Long> countByRecordStatus(RecordStatus status);
    
    /**
     * Elimina una ruta por su ID.
     * 
     * @param id ID de la ruta a eliminar
     * @return Mono<Void> indicando finalización
     */
    Mono<Void> deleteById(String id);
}
