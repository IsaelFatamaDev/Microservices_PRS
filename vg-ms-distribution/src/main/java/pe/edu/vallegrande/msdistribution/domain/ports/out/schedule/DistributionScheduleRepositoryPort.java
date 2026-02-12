package pe.edu.vallegrande.msdistribution.domain.ports.out.schedule;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para el repositorio de Horarios de Distribución.
 * Define el contrato que debe implementar la capa de infraestructura para persistencia.
 * 
 * @version 1.0
 */
public interface DistributionScheduleRepositoryPort {
    
    /**
     * Guarda un horario de distribución.
     * 
     * @param schedule Horario a guardar
     * @return Mono con el horario guardado
     */
    Mono<DistributionSchedule> save(DistributionSchedule schedule);
    
    /**
     * Busca un horario por su ID.
     * 
     * @param id ID del horario
     * @return Mono con el horario encontrado o vacío
     */
    Mono<DistributionSchedule> findById(String id);
    
    /**
     * Busca un horario por su ID y estado.
     * 
     * @param id ID del horario
     * @param status Estado del horario
     * @return Mono con el horario encontrado o vacío
     */
    Mono<DistributionSchedule> findByIdAndRecordStatus(String id, RecordStatus status);
    
    /**
     * Obtiene todos los horarios.
     * 
     * @return Flux con todos los horarios
     */
    Flux<DistributionSchedule> findAll();
    
    /**
     * Obtiene todos los horarios por estado.
     * 
     * @param status Estado a filtrar
     * @return Flux con horarios del estado especificado
     */
    Flux<DistributionSchedule> findByRecordStatus(RecordStatus status);
    
    /**
     * Obtiene horarios por ID de organización.
     * 
     * @param organizationId ID de la organización
     * @return Flux con horarios de la organización
     */
    Flux<DistributionSchedule> findByOrganizationId(String organizationId);
    
    /**
     * Cuenta horarios por estado.
     * 
     * @param status Estado a contar
     * @return Mono con el conteo
     */
    Mono<Long> countByRecordStatus(RecordStatus status);
    
    /**
     * Elimina un horario por su ID.
     * 
     * @param id ID del horario a eliminar
     * @return Mono<Void> indicando finalización
     */
    Mono<Void> deleteById(String id);
}
