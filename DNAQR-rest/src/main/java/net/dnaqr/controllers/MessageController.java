package net.dnaqr.controllers;

import lombok.RequiredArgsConstructor;
import net.dnaqr.dto.MessageDto;
import net.dnaqr.services.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("new-messages/{login}")
    public List<MessageDto> getNewMessages(@PathVariable String login) {
        return messageService.getNewMessagesForFrontendByLogin(login);
    }

    @PostMapping("message-set-front")
    public void getNewMessages(@RequestBody List<Long> ids) {
        messageService.setMessageReadFromFrontend(ids);
    }
}
