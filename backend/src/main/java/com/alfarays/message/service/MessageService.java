package com.alfarays.message.service;

import com.alfarays.message.mapper.MessageMapper;
import com.alfarays.message.model.MessageRequest;
import com.alfarays.message.model.MessageResponse;
import com.alfarays.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageResponse create(MessageRequest request){
        var message = MessageMapper.toEntity(request);
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));
        return Optional.ofNullable(messageRepository.save(message))
                .map(MessageMapper::toResponse)
                .orElse(null);
    }

    public MessageResponse findById(Long id){
        return messageRepository.findById(id).map(MessageMapper::toResponse)
                .orElseThrow(()->new RuntimeException("No message found."));
    }

    public List<MessageResponse> findAll(){
        return messageRepository.findAll()
                .stream()
                .map(MessageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MessageResponse update(Long id, MessageRequest request){
        return Optional.ofNullable(messageRepository.findById(id).orElse(null))
                .map(message -> {
                    message.setMessage(request.getMessage());
                    message.setSender(request.getSender());
                    message.setReceiver(request.getReceiver());
                    return messageRepository.save(message);
                })
                .map(MessageMapper::toResponse)
                .orElse(null);
    }

    public void deleteById(Long id){
        var message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find message."));

        messageRepository.delete(message);
    }

}
