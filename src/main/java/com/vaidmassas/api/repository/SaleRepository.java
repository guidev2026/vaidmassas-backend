package com.vaidmassas.api.repository;

import com.vaidmassas.api.domain.entity.Sale;
import com.vaidmassas.api.dto.response.SaleHistoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("""
        SELECT new com.vaidmassas.api.dto.response.SaleHistoryResponse(
            s.saleDate,
            COUNT(s),
            SUM(s.totalPrice)
        )
        FROM Sale s
        WHERE s.saleDate = :date
        GROUP BY s.saleDate
        ORDER BY s.saleDate
    """)
    List<SaleHistoryResponse> findDailyHistory(@Param("date") LocalDate date);

    @Query("""
        SELECT new com.vaidmassas.api.dto.response.SaleHistoryResponse(
            s.saleDate,
            COUNT(s),
            SUM(s.totalPrice)
        )
        FROM Sale s
        WHERE s.saleDate >= :start AND s.saleDate <= :end
        GROUP BY s.saleDate
        ORDER BY s.saleDate
    """)
    List<SaleHistoryResponse> findByPeriod(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
    @Query("""
    SELECT s FROM Sale s
    JOIN FETCH s.product
    WHERE s.saleDate >= :start AND s.saleDate <= :end
    ORDER BY s.saleDate DESC, s.id DESC
""")
    List<Sale> findDetailedByPeriod(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
    SELECT s FROM Sale s
    JOIN FETCH s.product
    WHERE s.saleDate = :date
    ORDER BY s.id DESC
""")
    List<Sale> findDetailedByDay(@Param("date") LocalDate date);
}
