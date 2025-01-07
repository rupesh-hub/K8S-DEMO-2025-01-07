package com.alfarays.message.resource;

import com.alfarays.message.model.MessageRequest;
import com.alfarays.message.model.MessageResponse;
import com.alfarays.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageResource {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> create(@RequestBody MessageRequest request){
        return ResponseEntity.ok(messageService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(messageService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> findAll(){
        return ResponseEntity.ok(messageService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@PathVariable Long id, @RequestBody MessageRequest request){
        return ResponseEntity.ok(messageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        messageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
