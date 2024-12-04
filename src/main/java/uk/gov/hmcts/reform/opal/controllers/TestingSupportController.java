package uk.gov.hmcts.reform.opal.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.opal.service.DwpBailiffsService;

@RestController
@RequestMapping("/testing-support/on-demand")
@RequiredArgsConstructor
@Tag(name = "Testing Support Controller")
@ConditionalOnProperty(prefix = "opal.testing-support-endpoints", name = "enabled", havingValue = "true")
public class TestingSupportController {

    private final DwpBailiffsService dwpBailiffsService;

    @PostMapping("/dwp-bailiffs")
    @Operation(summary = "Endpoint to invoke DWP-Bailiffs job on demand.")
    public ResponseEntity<String> runDwpBailiffs() {
        try {

            dwpBailiffsService.process();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("DWP_BAILIFFS JOB FAILED: " + e.getMessage());
        }

        return ResponseEntity.ok("DWP_BAILIFFS JOB INVOKED");
    }
}
