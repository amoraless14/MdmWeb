package repository;

import Entidad.Tablet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import Dto.TabletDashboardProjection;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import Dto.TabletDashboardProjection;
import Entidad.ActivoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TabletRepository extends JpaRepository<Tablet, Long> {
    // CAMBIO: Debe coincidir con el nombre del campo 'activo'
    Optional<Tablet> findByActivo(String activo);

    @Query(value = """
                                                                                                SELECT
                                                                                                    d.id,
                                                                                                    d.activo,
                                                                                                    d.android_id,
                                                                                                    d.device_name,
                                                                                                    d.model,
                                                                                                    d.categoria,
                                                                                                    a.codigo_emp      AS codigoEmpInfo,
                                                                                                    a.empleado_asig   AS empleadoAsig,
                                                                                                    a.planta          AS planta,
                                                                                                    a.area            AS area,
                                                                                                    a.departamento    AS departamento,
                                                                                                    d.battery_level,
                                                                                                    d.temperatura,
                                                                                                    d.estado_cargador,
                                                                                                    d.estado_wifi,
                                                                                                    d.estado_red      AS estado,
                                                                                                    d.ip_address,
                                                                                                    d.ram_usage,
                                                                                                    d.storage_usage,
                                                                                                    d.uptime,
                                                                                                    d.last_connection,
                                                                                                    d.os_version
                                                                                                FROM "monitoreo tablet".dispositivos d
                                                                                                LEFT JOIN public.activo_info a
                                                                                                       ON d.activo = a.activo
                                                                                                WHERE
                                                                                                (
                                                                                                    :buscar = ''
                                                                                                    OR LOWER(d.activo) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(d.device_name) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(d.model) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(d.ip_address) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(a.empleado_asig) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(a.codigo_emp) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                )
                                                                                                AND
                                                                                                (
                                                                                                    :planta = ''
                                                                                                    OR a.planta = :planta
                                                                                                )
                                                               AND
            (
                :categoria = ''
                OR (
                    UPPER(:categoria) = 'SIN DATOS'
                    AND a.activo IS NULL
                )
                OR (
                    UPPER(:categoria) <> 'SIN DATOS'
                    AND UPPER(COALESCE(d.categoria, '')) = UPPER(:categoria)
                )
            )

                                                                                                AND
                                                                                    (
                                                                                        :estadoCargador = ''
                                                                                        OR LOWER(d.estado_cargador) LIKE LOWER(CONCAT('%', :estadoCargador, '%'))
                                                                                    )
                                                                                        AND
                                                (
                                                    :estado = ''
                                                    OR
                                                    (
                                                        :estado = 'OFFLINE'
                                                        AND d.last_connection < NOW() - INTERVAL '17 minutes'
                                                    )
                                                    OR
                                                    (
                                                        :estado = 'CARGADOR_DESCONECTADO'
                                                        AND LOWER(d.estado_cargador) LIKE '%desconectado%'
                                                    )
                                                        OR
                                    (
                            :estado = 'CARGADOR_CONECTADO'
                            AND d.estado_cargador = 'Conectado'
                        )
                                                    OR
                                                    (
                                                        :estado = 'AUTORIZADO'
                                                        AND d.estado_red = 'Autorizado'
                                                    )
                                                    OR
                                                    (
                                                        :estado = 'NO_AUTORIZADO'
                                                        AND (d.estado_red IS NULL OR d.estado_red <> 'Autorizado')
                                                    )
                                                )
                                                                                                ORDER BY d.id ASC
                                                                                                """, countQuery = """
                                                                                                SELECT COUNT(*)
                                                                                                FROM "monitoreo tablet".dispositivos d
                                                                                                LEFT JOIN public.activo_info a
                                                                                                       ON d.activo = a.activo
                                                                                                WHERE
                                                                                                (
                                                                                                    :buscar = ''
                                                                                                    OR LOWER(d.activo) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(d.device_name) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(d.model) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(d.ip_address) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(a.empleado_asig) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                    OR LOWER(a.codigo_emp) LIKE LOWER(CONCAT('%', :buscar, '%'))
                                                                                                )
                                                                                                AND
                                                                                                (
                                                                                                    :planta = ''
                                                                                                    OR a.planta = :planta
                                                                                                )
                                                             AND
            (
                :categoria = ''
                OR (
                    UPPER(:categoria) = 'SIN DATOS'
                    AND a.activo IS NULL
                )
                OR (
                    UPPER(:categoria) <> 'SIN DATOS'
                    AND UPPER(COALESCE(d.categoria, '')) = UPPER(:categoria)
                )
            )
                                                                                                    AND
                                                                                    (
                                                                                        :estadoCargador = ''
                                                                                        OR LOWER(d.estado_cargador) LIKE LOWER(CONCAT('%', :estadoCargador, '%'))
                                                                                    )

                                                                                    AND
                                                (
                                                    :estado = ''
                                                    OR
                                                    (
                                                        :estado = 'OFFLINE'
                                                        AND d.last_connection < NOW() - INTERVAL '17 minutes'
                                                    )
                                                    OR
                                                    (
                                                        :estado = 'CARGADOR_DESCONECTADO'
                                                        AND LOWER(d.estado_cargador) LIKE '%desconectado%'
                                                    )
                                                        OR
                                   (
                            :estado = 'CARGADOR_CONECTADO'
                            AND d.estado_cargador = 'Conectado'
                        )
                                                    OR
                                                    (
                                                        :estado = 'AUTORIZADO'
                                                        AND d.estado_red = 'Autorizado'
                                                    )
                                                    OR
                                                    (
                                                        :estado = 'NO_AUTORIZADO'
                                                        AND (d.estado_red IS NULL OR d.estado_red <> 'Autorizado')
                                                    )
                                                )
                                                                                                """, nativeQuery = true)
    Page<TabletDashboardProjection> obtenerDashboard(
            @Param("buscar") String buscar,
            @Param("planta") String planta,
            @Param("categoria") String categoria,
            @Param("estadoCargador") String estadoCargador,
            @Param("estado") String estado,
            Pageable pageable);

    @Query(value = """
                                                SELECT
                                                    COUNT(*) AS total,

                                                    SUM(
                                                        CASE
                                                            WHEN d.last_connection < NOW() - INTERVAL '17 minutes'
                                                            THEN 1 ELSE 0
                                                        END
                                                    ) AS offline,

                                                    SUM(
                                                        CASE
                                                            WHEN d.estado_red = 'Autorizado'
                                                            THEN 1 ELSE 0
                                                        END
                                                    ) AS autorizados,

                                                    SUM(
                                                        CASE
                                                            WHEN LOWER(d.estado_cargador) LIKE '%desconectado%'
                                                                 AND UPPER(d.categoria) = 'CELULAR'
                                                            THEN 1 ELSE 0
                                                        END
                                                    ) AS nopowerCel,

                                                    SUM(
                                                        CASE
                                                            WHEN LOWER(d.estado_cargador) LIKE '%desconectado%'
                                                                 AND UPPER(d.categoria) = 'HANDHELD'
                                                            THEN 1 ELSE 0
                                                        END
                                                    ) AS nopowerHand,

                                                   SUM(
                                        CASE
                                            WHEN LOWER(d.estado_cargador) LIKE '%desconectado%'
                                                 AND UPPER(d.categoria) = 'CALIDAD'
                                            THEN 1 ELSE 0
                                        END
                                    ) AS nopowerTab,

                                                    SUM(
                                                        CASE
                                                            WHEN LOWER(d.estado_cargador) LIKE '%desconectado%'
                                                                 AND (d.categoria IS NULL
                                                                      OR d.categoria = ''
                                                                      OR UPPER(d.categoria) = 'GENERAL')
                                                            THEN 1 ELSE 0
                                                        END
                                                    ) AS nopowerGen,

                                                    SUM(
                                                        CASE
                                                            WHEN d.estado_red <> 'Autorizado'
                                                                 OR d.estado_red IS NULL
                                                            THEN 1 ELSE 0
                                                        END
                                                    ) AS noAutorizados
                                                     ,
                        SUM(
                            CASE
                                WHEN d.battery_level IS NOT NULL
                                     AND d.battery_level < 20
                                THEN 1 ELSE 0
                            END
                        ) AS bateriaBaja
                         ,
            SUM(
                CASE
                    WHEN d.battery_level BETWEEN 80 AND 100
                    THEN 1 ELSE 0
                END
            ) AS bateria80a100,

            SUM(
                CASE
                    WHEN d.battery_level BETWEEN 50 AND 79
                    THEN 1 ELSE 0
                END
            ) AS bateria50a79,

            SUM(
                CASE
                    WHEN d.battery_level BETWEEN 20 AND 49
                    THEN 1 ELSE 0
                END
            ) AS bateria20a49,

            SUM(
                CASE
                    WHEN d.battery_level BETWEEN 0 AND 19
                    THEN 1 ELSE 0
                END
            ) AS bateria0a19,

            SUM(
                CASE
                    WHEN d.battery_level IS NULL
                    THEN 1 ELSE 0
                END
            ) AS bateriaSinDatos

                                                FROM "monitoreo tablet".dispositivos d
                                                """, nativeQuery = true)
    Object obtenerDashboardResumen();

    @Query(value = """
            SELECT *
            FROM public.activo_info
            WHERE activo = :activo
            """, nativeQuery = true)
    ActivoInfo obtenerActivoInfo(String activo);

    @Query(value = """
            SELECT
                COUNT(*) AS total,
                SUM(
                    CASE
                        WHEN last_connection < NOW() - INTERVAL '17 minutes'
                        THEN 1
                        ELSE 0
                    END
                ) AS offline,
                SUM(
                    CASE
                        WHEN estado_red = 'Autorizado'
                        THEN 1
                        ELSE 0
                    END
                ) AS autorizados
            FROM "monitoreo tablet".dispositivos
            """, nativeQuery = true)
    Object[] obtenerDashboard();

    @Query(value = """
            SELECT
                d.id,
                d.activo,
                d.android_id,
                d.device_name,
                d.model,
                d.categoria,
                a.codigo_emp      AS codigoEmpInfo,
                a.empleado_asig   AS empleadoAsig,
                a.planta          AS planta,
                a.area            AS area,
                a.departamento    AS departamento,
                d.battery_level,
                d.temperatura,
                d.estado_cargador,
                d.estado_wifi,
                d.estado_red      AS estado,
                d.ip_address,
                d.ram_usage,
                d.storage_usage,
                d.uptime,
                d.last_connection,
                d.os_version
            FROM "monitoreo tablet".dispositivos d
            LEFT JOIN public.activo_info a
                   ON d.activo = a.activo
            WHERE
            (
                :buscar = ''
                OR LOWER(d.activo) LIKE LOWER(CONCAT('%', :buscar, '%'))
                OR LOWER(d.device_name) LIKE LOWER(CONCAT('%', :buscar, '%'))
                OR LOWER(d.model) LIKE LOWER(CONCAT('%', :buscar, '%'))
                OR LOWER(d.ip_address) LIKE LOWER(CONCAT('%', :buscar, '%'))
                OR LOWER(a.empleado_asig) LIKE LOWER(CONCAT('%', :buscar, '%'))
                OR LOWER(a.codigo_emp) LIKE LOWER(CONCAT('%', :buscar, '%'))
            )
            AND
            (
                :planta = ''
                OR a.planta = :planta
            )
            AND
            (
                :categoria = ''
                OR (
                    UPPER(:categoria) = 'SIN DATOS'
                    AND a.activo IS NULL
                )
                OR (
                    UPPER(:categoria) <> 'SIN DATOS'
                    AND UPPER(COALESCE(d.categoria, '')) = UPPER(:categoria)
                )
            )
            AND
            (
                :estadoCargador = ''
                OR LOWER(d.estado_cargador) LIKE LOWER(CONCAT('%', :estadoCargador, '%'))
            )
            AND
            (
                :estado = ''
                OR (
                    :estado = 'OFFLINE'
                    AND d.last_connection < NOW() - INTERVAL '17 minutes'
                )
                OR (
                    :estado = 'CARGADOR_DESCONECTADO'
                    AND LOWER(d.estado_cargador) LIKE '%desconectado%'
                )
                OR (
                    :estado = 'CARGADOR_CONECTADO'
                    AND d.estado_cargador = 'Conectado'
                )
                OR (
                    :estado = 'AUTORIZADO'
                    AND d.estado_red = 'Autorizado'
                )
                OR (
                    :estado = 'NO_AUTORIZADO'
                    AND (d.estado_red IS NULL OR d.estado_red <> 'Autorizado')
                )
            )
            ORDER BY d.id ASC
            """, nativeQuery = true)
    List<TabletDashboardProjection> obtenerDashboardReporte(
            @Param("buscar") String buscar,
            @Param("planta") String planta,
            @Param("categoria") String categoria,
            @Param("estadoCargador") String estadoCargador,
            @Param("estado") String estado);

    @Query(value = """
            SELECT d.*
            FROM "monitoreo tablet".dispositivos d
            INNER JOIN public.activo_info a
                    ON a.activo = d.activo
            WHERE UPPER(a.planta) = UPPER(:planta)
            """, nativeQuery = true)
    List<Tablet> obtenerPorPlanta(@Param("planta") String planta);

    @Query(value = """
            SELECT DISTINCT planta
            FROM public.activo_info
            WHERE planta IS NOT NULL
              AND planta <> ''
            ORDER BY planta
            """, nativeQuery = true)
    List<String> obtenerPlantas();

    @Query(value = """
            SELECT DISTINCT categoria
            FROM "monitoreo tablet".dispositivos
            WHERE categoria IS NOT NULL
              AND categoria <> ''
            ORDER BY categoria
            """, nativeQuery = true)
    List<String> obtenerCategorias();

    @Query(value = """
            SELECT
                CASE
                    WHEN UPPER(TRIM(a.planta)) = 'PC' THEN 'Planta Cinturones'
                    WHEN UPPER(TRIM(a.planta)) = 'PF' THEN 'Planta Fajas'
                    ELSE 'SIN DATOS'
                END AS nombre,
                COUNT(*) AS cantidad
            FROM "monitoreo tablet".dispositivos d
            LEFT JOIN public.activo_info a
                ON TRIM(d.activo) = TRIM(a.activo)
            WHERE
                a.planta IS NULL
                OR UPPER(TRIM(a.planta)) IN ('PC','PF')
            GROUP BY
                CASE
                    WHEN UPPER(TRIM(a.planta)) = 'PC' THEN 'Planta Cinturones'
                    WHEN UPPER(TRIM(a.planta)) = 'PF' THEN 'Planta Fajas'
                    ELSE 'SIN DATOS'
                END
            ORDER BY
                CASE
                    WHEN
                        CASE
                            WHEN UPPER(TRIM(a.planta))='PC' THEN 'Planta Cinturones'
                            WHEN UPPER(TRIM(a.planta))='PF' THEN 'Planta Fajas'
                            ELSE 'SIN DATOS'
                        END='Planta Cinturones' THEN 1
                    WHEN
                        CASE
                            WHEN UPPER(TRIM(a.planta))='PC' THEN 'Planta Cinturones'
                            WHEN UPPER(TRIM(a.planta))='PF' THEN 'Planta Fajas'
                            ELSE 'SIN DATOS'
                        END='Planta Fajas' THEN 2
                    ELSE 3
                END
            """, nativeQuery = true)
    List<Object[]> obtenerGraficaPlantas();

    @Query(value = """
            SELECT
                CASE
                    WHEN d.categoria IS NULL
                         OR TRIM(d.categoria) = ''
                    THEN 'GENERAL'
                    ELSE UPPER(TRIM(d.categoria))
                END AS nombre,
                COUNT(*) AS cantidad
            FROM "monitoreo tablet".dispositivos d
            LEFT JOIN public.activo_info a
                   ON d.activo = a.activo
            WHERE
                (
                    :planta IS NULL
                    OR :planta = ''
                    OR UPPER(a.planta) = UPPER(:planta)
                )
            GROUP BY
                CASE
                    WHEN d.categoria IS NULL
                         OR TRIM(d.categoria) = ''
                    THEN 'GENERAL'
                    ELSE UPPER(TRIM(d.categoria))
                END
            ORDER BY
                CASE
                    WHEN
                        CASE
                            WHEN d.categoria IS NULL
                                 OR TRIM(d.categoria) = ''
                            THEN 'GENERAL'
                            ELSE UPPER(TRIM(d.categoria))
                        END='GENERAL'
                    THEN 1
                    WHEN
                        CASE
                            WHEN d.categoria IS NULL
                                 OR TRIM(d.categoria) = ''
                            THEN 'GENERAL'
                            ELSE UPPER(TRIM(d.categoria))
                        END='CELULAR'
                    THEN 2
                    WHEN
                        CASE
                            WHEN d.categoria IS NULL
                                 OR TRIM(d.categoria) = ''
                            THEN 'GENERAL'
                            ELSE UPPER(TRIM(d.categoria))
                        END='CALIDAD'
                    THEN 3
                    WHEN
                        CASE
                            WHEN d.categoria IS NULL
                                 OR TRIM(d.categoria) = ''
                            THEN 'GENERAL'
                            ELSE UPPER(TRIM(d.categoria))
                        END='HANDHELD'
                    THEN 4
                    ELSE 5
                END
            """, nativeQuery = true)
    List<Object[]> obtenerGraficaCategorias(
            @Param("planta") String planta);

    @Query(value = """
            SELECT
                CASE
                    WHEN d.estado_red='Autorizado'
                    THEN 'Autorizado'
                    ELSE 'No autorizado'
                END AS nombre,
                COUNT(*) AS cantidad
            FROM "monitoreo tablet".dispositivos d
            GROUP BY
                CASE
                    WHEN d.estado_red='Autorizado'
                    THEN 'Autorizado'
                    ELSE 'No autorizado'
                END
            ORDER BY nombre
            """, nativeQuery = true)
    List<Object[]> obtenerGraficaEstadoRed();

    @Query(value = """
            SELECT
                TO_CHAR(fecha_evento, 'DD/MM/YYYY HH24:MI:SS') AS fecha_evento,
                estado_cargador,
                porcentaje_bateria
            FROM "monitoreo tablet".historial_cargador
            WHERE tablet_id = :tabletId
              AND (:fechaDesde IS NULL OR fecha_evento >= CAST(:fechaDesde AS DATE))
              AND (:fechaHasta IS NULL OR fecha_evento < (CAST(:fechaHasta AS DATE) + INTERVAL '1 day'))
            ORDER BY fecha_evento DESC
            """, nativeQuery = true)
    List<Object[]> obtenerHistorialCargador(
            @Param("tabletId") Long tabletId,
            @Param("fechaDesde") String fechaDesde,
            @Param("fechaHasta") String fechaHasta);
}
