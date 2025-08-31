package com.test.application.controller;

import com.test.application.model.Entry;
import com.test.application.service.EntryService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Общий контроллер
 */
@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
@Timed(value = "entry.controller", description = "Метрики для общего контроллера")
public class EntryController {

    private final EntryService entryService;

    @GetMapping
    @Timed(value = "entry.get.all", description = "Получить все записи в рамках страницы")
    public ResponseEntity<Page<Entry>> getEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<Entry> entries = entryService.getEntriesPaginated(page, size);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/range")
    @Timed(value = "entry.get.range", description = "Получить все записи в рамках диапазона")
    public ResponseEntity<List<Entry>> getEntriesRange(
            @RequestParam(required = false) Integer start,
            @RequestParam(defaultValue = "100") int limit) {

        List<Entry> entries = entryService.getEntriesRange(start, limit);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    @Timed(value = "entry.get.byId", description = "Получить запись по ИД")
    public ResponseEntity<Entry> getEntry(@PathVariable Long id) {
        Optional<Entry> entry = entryService.getEntryById(id);
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/move")
    @Timed(value = "entry.move", description = "Сдвинуть запись на определенную позицию")
    public ResponseEntity<Void> moveEntry(
            @PathVariable Long id,
            @RequestParam Integer position) {

        boolean success = entryService.moveEntryToPosition(id, position);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/up")
    @Timed(value = "entry.move.up", description = "Сдвинуть запись выше")
    public ResponseEntity<Void> moveUp(@PathVariable Long id) {
        boolean success = entryService.moveEntryUp(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/down")
    @Timed(value = "entry.move.down", description = "Сдвинуть запись ниже")
    public ResponseEntity<Void> moveDown(@PathVariable Long id) {
        boolean success = entryService.moveEntryDown(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    @Timed(value = "entry.count", description = "Получить общее количество записей")
    public ResponseEntity<Long> getTotalCount() {
        return ResponseEntity.ok(entryService.getTotalCount());
    }
}