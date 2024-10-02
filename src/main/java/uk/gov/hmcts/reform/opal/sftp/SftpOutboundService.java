package uk.gov.hmcts.reform.opal.sftp;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.sftp.config.SftpConnection;

import java.io.InputStream;
import java.util.function.Consumer;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.getOutboundLocations;

@Slf4j
@Service
@RequiredArgsConstructor
public class SftpOutboundService {

    private final DefaultSftpSessionFactory outboundSessionFactory;
    private final SftpService sftpService;
    private final SftpConnection outboundConnection;

    public void uploadFile(byte[] fileBytes, String path, String fileName) {
        sftpService.uploadFile(outboundSessionFactory, fileBytes, path, fileName);
    }

    public boolean downloadFile(String path, String fileName, Consumer<InputStream> fileProcessor) {
        return sftpService.downloadFile(outboundSessionFactory, path, fileName, fileProcessor);
    }

    public boolean deleteFile(String path, String fileName) {
        return sftpService.deleteFile(outboundSessionFactory, path, fileName);
    }

    @PostConstruct
    public void createSftpLocations() {
        if (outboundConnection.isCreateSubLocations()) {
            getOutboundLocations()
                .forEach(sftpLocation ->
                             this.sftpService.createDirectoryIfNotExists(outboundSessionFactory, sftpLocation)
                );
        }
    }
}
