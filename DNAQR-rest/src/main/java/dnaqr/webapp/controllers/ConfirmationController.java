package dnaqr.webapp.controllers;

import dnaqr.webapp.services.ConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.UUID;

@RestController
@RequestMapping("confirmation")
@RequiredArgsConstructor
public class ConfirmationController {

    private final ConfirmationService confirmationService;

    @GetMapping
    @Transactional
    @ResponseBody
    public String confirmInterview(@RequestParam("token") UUID confirmationToken) {
        return confirmationService.confirmInterview(confirmationToken);
    }
}
