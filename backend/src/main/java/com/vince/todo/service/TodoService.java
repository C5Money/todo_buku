package com.vince.todo.service;

import com.vince.todo.model.Todo;
import com.vince.todo.repository.TodoRepository;
import com.vince.todo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<Todo> getTodos(Long userId) {
        return todoRepository.findAllByUserId(userId);
    }

    public Todo createTodo(Long userId, String title) {
        if (todoRepository.countByUserId(userId) >= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max 5 taken bereikt");
        }
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(title);
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long todoId, Long userId, String title, Boolean done) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taak niet gevonden"));

        if (!todo.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Geen toegang");
        }

        if (title != null) todo.setTitle(title);
        if (done != null) todo.setDone(done);
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taak niet gevonden"));

        if (!todo.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Geen toegang");
        }

        todoRepository.delete(todo);
    }
}
