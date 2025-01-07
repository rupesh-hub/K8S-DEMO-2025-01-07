package com.alfarays.message.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class MessageResponse {

    private Long id;
    private String message;
    private String sender;
    private String receiver;
    private String timestamp;

}
