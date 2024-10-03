package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedCTTransformer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class FileHandlingService {

    public final SftpInboundService sftpInboundService;

    public final AmalgamatedCTTransformer amalgamatedCTTransformer;

    public OpalFile createOpalFile(String fileName, boolean isDWPBailiffs, String path) {

        OpalFile file = OpalFile.builder().originalFileName(fileName).newFileName(fileName).build();

        try {

            file.setFileContent(StandardBankingFile.toStandardBankingFile(readFileContents(file, path)));

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

    public void outputFileSuccess(OpalFile file, String path) {

        //upload new file to success dir
        sftpInboundService.uploadFile(file.getFileContent().toString().getBytes(StandardCharsets.UTF_8), path,
                                      file.getNewFileName());

        //delete file from processing dir
        sftpInboundService.deleteFile(path, file.getOriginalFileName());
    }

    public void outputFileError(String fileName, String oldPath, String newPath) {

        //move file to error dir
        sftpInboundService.moveFile(oldPath, fileName, newPath);

    }
}
