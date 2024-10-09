package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_ARCHIVE;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_PROCESSING;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_SUCCESS;

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

    public OpalFile createOpalFile(String fileName, boolean isDWPBailiffs, String path) {

        OpalFile file = OpalFile.builder().originalFileName(fileName).newFileName(fileName).build();

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

    public void outputFileSuccess(OpalFile file) {

        //upload new file to success dir
        sftpInboundService.uploadFile(StandardBankingFile.toString((StandardBankingFile) file.getFileContent())
                                          .getBytes(
                                          StandardCharsets.UTF_8), DWP_BAILIFFS_SUCCESS.getPath(),
                                      file.getNewFileName());

        //delete file from processing to archive
        sftpInboundService.moveFile(DWP_BAILIFFS_PROCESSING.getPath(), file.getOriginalFileName(),
                                    DWP_BAILIFFS_ARCHIVE.getPath());
    }

    public void outputFileError(String fileName, String oldPath, String newPath) {

        //move file to error dir
        sftpInboundService.moveFile(oldPath, fileName, newPath);

    }

}
