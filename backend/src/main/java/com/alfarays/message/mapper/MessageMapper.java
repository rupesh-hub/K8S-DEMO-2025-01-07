package com.alfarays.message.mapper;

import com.alfarays.message.entity.Message;
import com.alfarays.message.model.MessageRequest;
import com.alfarays.message.model.MessageResponse;
import lombok.experimental.UtilityClass;

import java.util.Date;

@UtilityClass
public class MessageMapper {

    public static Message toEntity(MessageRequest request) {
        return Message.builder()
                .message(request.getMessage())
                .sender(request.getSender())
                .receiver(request.getReceiver())
                .build();
    }

    public static MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .message(message.getMessage())
                .sender(message.getSender())
                .receiver(message.getReceiver())
                .timestamp(message.getTimestamp())
                .build();
    }

}
