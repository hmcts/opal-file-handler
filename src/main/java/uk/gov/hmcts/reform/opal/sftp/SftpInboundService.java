package uk.gov.hmcts.reform.opal.sftp;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.sftp.config.SftpConnection;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.getInboundLocations;

@Slf4j
@Service
@RequiredArgsConstructor
public class SftpInboundService {

    private final DefaultSftpSessionFactory inboundSessionFactory;
    private final SftpService sftpService;
    private final SftpConnection inboundConnection;

    public void uploadFile(byte[] fileBytes, String path, String fileName) {
        sftpService.uploadFile(inboundSessionFactory, fileBytes, path, fileName);
    }

    public void downloadFile(String path, String fileName, Consumer<InputStream> fileProcessor) {
        sftpService.downloadFile(inboundSessionFactory, path, fileName, fileProcessor);
    }

    public boolean deleteFile(String path, String fileName) {
        return sftpService.deleteFile(inboundSessionFactory, path, fileName);
    }

    public void moveFile(String oldPath, String fileName, String newPath) {
        sftpService.moveFile(inboundSessionFactory, oldPath, fileName, newPath);
    }

    public List<String> listFiles(String directoryPath) {
        return sftpService.listFiles(inboundSessionFactory, directoryPath);
    }

    @PostConstruct
    public void createSftpLocations() {
        if (inboundConnection.isCreateSubLocations()) {
            getInboundLocations()
                .forEach(sftpLocation ->
                             this.sftpService.createDirectoryIfNotExists(inboundSessionFactory, sftpLocation)
                );
        }
    }
}
