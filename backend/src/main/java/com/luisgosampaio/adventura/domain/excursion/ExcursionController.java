package com.luisgosampaio.adventura.domain.excursion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExcursionController {

    private final ExcursionService excursionService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/trip/{tripId}/excursions")
    public void handleExcursion(@DestinationVariable Long tripId,
                                ExcursionMessage message,
                                Principal principal) {
        try {
            Excursion savedExcursion = excursionService.processExcursionUpdate(tripId, message, principal);

            if (message.getAction() == ExcursionAction.DELETE) {
                messagingTemplate.convertAndSend(
                        "/topic/trip/" + tripId + "/excursions", message);
            } else {
                messagingTemplate.convertAndSend(
                        "/topic/trip/" + tripId + "/excursions", savedExcursion);
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    "Failed to save excursion"
            );
        }
    }

    @GetMapping("/trip/{tripId}/excursions")
    @ResponseBody
    public ResponseEntity<List<Excursion>> getExcursions(@PathVariable Long tripId) {
        return ResponseEntity.ok(excursionService.getExcursions(tripId));
    }
}
