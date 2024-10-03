package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.scheduler.aspect.LogExecutionTime;
import uk.gov.hmcts.reform.opal.sftp.SftpOutboundService;

import java.io.InputStream;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.ALL_PAY_ARCHIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllPayArchiveService {

    private final SftpOutboundService sftpOutboundService;

    @LogExecutionTime
    public void process(String fileName) {
        sftpOutboundService.downloadFile(ALL_PAY_ARCHIVE.getPath(), fileName, this::processFile);
    }

    public void processFile(InputStream inputStream) {
        log.info("Process file contents of the stream.");

    }
}
