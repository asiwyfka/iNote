package inote.controller;

import inote.entity.Note;
import inote.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления заметками.
 *
 * @author Avdeyev Viktor
 */
@Tag(name = "Контроллер для управления заметками")
@RestController
@Slf4j
@RequestMapping("/inote/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "Получение списка всех заметок")
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        log.info("getAllNotes - start");
        long startTime = System.currentTimeMillis();
        List<Note> notes = noteService.findAll();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Duration = {}", duration);
        log.info("getAllNotes - end, notesCount = {}", notes.size());
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Получение заметки по ID")
    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long noteId) {
        log.info("getNoteById - start, noteId = {}", noteId);
        Optional<Note> note = noteService.findById(noteId);

        if (note.isEmpty()) {
            log.warn("getNoteById - заметка с ID {} не найдена", noteId);
            return ResponseEntity.notFound().build();
        }

        log.info("getNoteById - end, note = {}", note.get());
        return ResponseEntity.ok(note.get());
    }

    @Operation(summary = "Получение заметок по заголовку")
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Note>> getNotesByTitle(@PathVariable String title) {
        log.info("getNotesByTitle - start, title = {}", title);
        List<Note> notes = noteService.findByTitle(title);

        if (notes.isEmpty()) {
            log.warn("getNotesByTitle - заметки с заголовком '{}' не найдены", title);
            return ResponseEntity.notFound().build();
        }

        log.info("getNotesByTitle - end, notesCount = {}", notes.size());
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Получение заметок, созданных в указанный период")
    @GetMapping("/created-between")
    public ResponseEntity<List<Note>> getNotesByCreatedAtBetween(
        @RequestParam("startDate")
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,

        @RequestParam("endDate")
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        log.info("getNotesByCreatedAtBetween - start, startDate = {}, endDate = {}", startDate, endDate);

        // Преобразуем LocalDate в LocalDateTime (с 00:00:00 до 23:59:59)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Note> notes = noteService.findByCreatedAtBetween(startDateTime, endDateTime);

        if (notes.isEmpty()) {
            log.warn("getNotesByCreatedAtBetween - заметки не найдены в указанный период");
            return ResponseEntity.notFound().build();
        }

        log.info("getNotesByCreatedAtBetween - end, notesCount = {}", notes.size());
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Добавление новой заметки")
    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        log.info("addNote - start, note = {}", note);
        Note savedNote = noteService.save(note);
        log.info("addNote - end, savedNoteId = {}", savedNote.getId());
        return ResponseEntity.ok(savedNote);
    }

    @Operation(summary = "Обновление заметки")
    @PutMapping("/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable Long noteId, @RequestBody Note note) {
        log.info("updateNote - start, noteId = {}, note = {}", noteId, note);
        Optional<Note> updatedNote = noteService.update(noteId, note);

        if (updatedNote.isEmpty()) {
            log.warn("updateNote - заметка с ID {} не найдена", noteId);
            return ResponseEntity.notFound().build();
        }

        log.info("updateNote - end, updatedNoteId = {}", updatedNote.get().getId());
        return ResponseEntity.ok(updatedNote.get());
    }

    @Operation(summary = "Удаление заметки по ID")
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long noteId) {
        log.info("deleteNote - start, noteId = {}", noteId);

        Optional<Note> note = noteService.findById(noteId);
        if (note.isEmpty()) {
            log.warn("deleteNote - заметка с ID {} не найдена", noteId);
            return ResponseEntity.notFound().build();
        }

        noteService.deleteById(noteId);
        log.info("deleteNote - end, noteId = {}", noteId);
        return ResponseEntity.noContent().build();
    }
}