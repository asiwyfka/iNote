package inote.controller;


import inote.entity.Note;
import inote.entity.User;
import inote.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/library/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    @Operation(summary = "Получение списка всех заметок")
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        log.info("getAllNotes - start");
        long startTime = System.currentTimeMillis();
        List<Note> notes = noteService.findAll();
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Duration = {}", duration);
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

    @Operation(summary = "Получение заметок пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Note>> getNotesByUser(@PathVariable Long userId) {
        log.info("getNotesByUser - start, userId = {}", userId);
        User user = new User();
        user.setId(userId);
        List<Note> notes = noteService.findByUser(user);

        if (notes.isEmpty()) {
            log.warn("getNotesByUser - заметки для пользователя с ID {} не найдены", userId);
            return ResponseEntity.notFound().build();
        }

        log.info("getNotesByUser - end, notesCount = {}", notes.size());
        return ResponseEntity.ok(notes);
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