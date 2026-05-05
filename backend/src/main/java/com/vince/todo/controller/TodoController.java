package com.vince.todo.controller;

import com.vince.todo.model.Todo;
import com.vince.todo.repository.UserRepository;
import com.vince.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    private final UserRepository userRepository;

    public TodoController(TodoService todoService, UserRepository userRepository) {
        this.todoService = todoService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow().getId();
    }

    @GetMapping
    public List<Todo> getTodos() {
        return todoService.getTodos(getCurrentUserId());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Map<String, String> body) {
        Todo todo = todoService.createTodo(getCurrentUserId(), body.get("title"));
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        Boolean done = (Boolean) body.get("done");
        return todoService.updateTodo(id, getCurrentUserId(), title, done);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
