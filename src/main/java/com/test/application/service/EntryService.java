package com.test.application.service;

import com.test.application.model.Entry;
import com.test.application.repository.EntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
/**
 * Сервис для работы с записями
 */
@Slf4j
@Service
public class EntryService {

    private final Integer defaultPageSize;
    private final Integer maxBatchSize;
    private final EntryRepository entryRepository;

    public EntryService(@Value("${test.application.default.page.size}") Integer defaultPageSize,
                        @Value("${test.application.max.batch.size}") Integer maxBatchSize,
                        EntryRepository entryRepository) {
        this.defaultPageSize = defaultPageSize;
        this.maxBatchSize = maxBatchSize;
        this.entryRepository = entryRepository;
    }

    @Cacheable(value = "entriesPage", key = "#page + '-' + #size")
    public Page<Entry> getEntriesPaginated(int page, int size) {
        int pageSize = Math.min(size, defaultPageSize);
        Pageable pageable = PageRequest.of(page, pageSize);
        return entryRepository.findAllByOrderByPositionAsc(pageable);
    }

    public List<Entry> getEntriesRange(Integer startPosition, Integer limit) {
        int actualLimit = Math.min(limit, maxBatchSize);
        return entryRepository.findEntriesRange(
                startPosition != null ? startPosition : 1,
                actualLimit
        );
    }

    @Transactional
    public boolean moveEntryToPosition(Long id, Integer newPosition) {
        try {
            Optional<Entry> entryOpt = entryRepository.findById(id);
            if (entryOpt.isEmpty()) {
                return false;
            }
            Entry entry = entryOpt.get();
            Integer currentPosition = entry.getPosition();
            if (currentPosition.equals(newPosition)) {
                return true;
            }
            if (newPosition < currentPosition) {
                entryRepository.shiftPositionsRange(newPosition, currentPosition - 1, 1);
            } else {
                entryRepository.shiftPositionsRange(currentPosition + 1, newPosition, -1);
            }
            entryRepository.updatePosition(id, newPosition);
            return true;
        } catch (Exception e) {
            log.error("Error moving entry {} to position {}", id, newPosition, e);
            throw new RuntimeException("Failed to move entry", e);
        }
    }

    @Transactional
    public boolean moveEntryUp(Long id) {
        return entryRepository.findById(id)
                .map(entry -> {
                    Integer currentPosition = entry.getPosition();
                    Integer minPosition = entryRepository.findMinPosition().orElse(currentPosition);
                    if (currentPosition > minPosition) {
                        return moveEntryToPosition(id, currentPosition - 1);
                    }
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean moveEntryDown(Long id) {
        return entryRepository.findById(id)
                .map(entry -> {
                    Integer currentPosition = entry.getPosition();
                    Integer maxPosition = entryRepository.findMaxPosition().orElse(currentPosition);
                    if (currentPosition < maxPosition) {
                        return moveEntryToPosition(id, currentPosition + 1);
                    }
                    return true;
                })
                .orElse(false);
    }

    public Optional<Entry> getEntry(Long id) {
        return entryRepository.findById(id);
    }

    public long getTotalCount() {
        return entryRepository.countAll();
    }

    @Cacheable(value = "entryById", key = "#id")
    public Optional<Entry> getEntryById(Long id) {
        return entryRepository.findById(id);
    }
}