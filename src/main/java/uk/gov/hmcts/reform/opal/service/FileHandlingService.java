package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_PROCESSING;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileHandlingService {

    public final SftpInboundService sftpInboundService;

    public List<String> getListOfFilesToProcess() {
        List<String> fileNames = new ArrayList<>();

        for (String fileName : sftpInboundService.listFiles(DWP_BAILIFFS.getPath())) {

            fileNames.add(fileName);
            //Move file to processing dir
            sftpInboundService.moveFile(DWP_BAILIFFS.getPath(), fileName,
                                        DWP_BAILIFFS_PROCESSING.getPath()
            );

        }
        return fileNames;
    }

    public OpalFile createOpalFile(String originalFileName,
                                   boolean isDWPBailiffs, String path) {

        OpalFile file = OpalFile.builder().originalFileName(originalFileName).build();

        try {

            file.setFileContent(isDWPBailiffs
                                    ?
                                    DwpFile.toDwpFile(readFileContents(file, path)) :
                                    StandardBankingFile.toStandardBankingFile(readFileContents(file, path)));

        } catch (Exception e) {
            log.error("Error processing file", e);
            throw new RuntimeException(e);
        }

        return file;
    }

    public String readFileContents(OpalFile file, String path) {

        StringBuilder fileContentsBuilder = new StringBuilder();


        sftpInboundService.downloadFile(
            path,
            file.getOriginalFileName(),
            inputStream -> {

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream,
                    StandardCharsets.UTF_8
                ));
                reader.lines().forEach(line -> fileContentsBuilder.append(line).append("\n"));

            }
        );

        return fileContentsBuilder.toString();
    }

    public void uploadStandardBankingFile(OpalFile file, String newPath) {
        sftpInboundService.uploadFile(StandardBankingFile.toString((StandardBankingFile) file.getFileContent())
                                          .getBytes(
                                          StandardCharsets.UTF_8), newPath,
                                      StandardBankingFileName
                                          .toString((StandardBankingFileName) file.getNewFileName()));
    }

    public void moveFile(String fileName, String oldPath, String newPath) {
        sftpInboundService.moveFile(oldPath, fileName, newPath);
    }

}
