package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.scheduler.aspect.LogExecutionTime;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;

import java.io.InputStream;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.AUTO_CHEQUES;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoCheckService {

    private final SftpInboundService sftpInboundService;

    @LogExecutionTime
    public void process(String fileName) {
        sftpInboundService.downloadFile(AUTO_CHEQUES.getPath(), fileName, this::processFile);
    }

    public void processFile(InputStream inputStream) {
        log.info("Process file contents of the stream.");
    }
}
