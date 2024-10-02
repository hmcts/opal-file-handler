package uk.gov.hmcts.reform.opal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.scheduler.aspect.LogExecutionTime;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedCTTransformer;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_ERROR;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_PROCESSING;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_SUCCESS;

@Slf4j
@Service
public class DwpBailiffsService extends FileHandlingService {

    public DwpBailiffsService(SftpInboundService sftpInboundService,
                              AmalgamatedCTTransformer amalgamatedCTTransformer) {
        super(sftpInboundService, amalgamatedCTTransformer);
    }

    @LogExecutionTime
    public void process() {

        for (String fileName : sftpInboundService.listFiles(DWP_BAILIFFS.getPath())) {

            //Move file to processing dir
            sftpInboundService.moveFile(DWP_BAILIFFS.getPath(), fileName,
                                        DWP_BAILIFFS_PROCESSING.getPath());
            //process single file
            processSingleFile(fileName);
        }
    }

    void processSingleFile(String fileName) {
        try {
            OpalFile file = createOpalFile(fileName, true, DWP_BAILIFFS_PROCESSING.getPath());

            outputFileSuccess(applyTransformations(file), DWP_BAILIFFS_SUCCESS.getPath());

        } catch (Exception e) {

            outputFileError(fileName,DWP_BAILIFFS_PROCESSING.getPath(), DWP_BAILIFFS_ERROR.getPath());
        }
    }

    OpalFile applyTransformations(OpalFile file) {

        amalgamatedCTTransformer.transformAmalgamatedCT(file, false);
        return file;
    }

}
