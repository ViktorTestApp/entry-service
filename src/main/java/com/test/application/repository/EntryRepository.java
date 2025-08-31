package com.test.application.repository;

import com.test.application.model.Entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с БД
 */
@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    Optional<Entry> findByPosition(Integer position);

    Page<Entry> findAllByOrderByPositionAsc(Pageable pageable);

    @Query("SELECT MAX(e.position) FROM Entry e")
    Optional<Integer> findMaxPosition();

    @Query("SELECT MIN(e.position) FROM Entry e")
    Optional<Integer> findMinPosition();

    @Modifying
    @Transactional
    @Query("UPDATE Entry e SET e.position = e.position + :increment " +
            "WHERE e.position BETWEEN :start AND :end")
    void shiftPositionsRange(@Param("start") Integer start,
                             @Param("end") Integer end,
                             @Param("increment") Integer increment);

    @Modifying
    @Transactional
    @Query("UPDATE Entry e SET e.position = :newPosition WHERE e.id = :id")
    void updatePosition(@Param("id") Long id, @Param("newPosition") Integer newPosition);

    @Query(value = """
            SELECT * FROM entry 
            WHERE position >= :startPosition 
            ORDER BY position ASC 
            LIMIT :limit
            """, nativeQuery = true)
    List<Entry> findEntriesRange(@Param("startPosition") Integer startPosition,
                                 @Param("limit") Integer limit);

    @Query(value = "SELECT COUNT(*) FROM entry", nativeQuery = true)
    long countAll();
}