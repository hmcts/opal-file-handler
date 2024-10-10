package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.scheduler.aspect.LogExecutionTime;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedCTTransformer;
import uk.gov.hmcts.reform.opal.transformer.AntTransformer;
import uk.gov.hmcts.reform.opal.transformer.DwpTransformer;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_ERROR;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_PROCESSING;

@Slf4j
@Service
@RequiredArgsConstructor
public class DwpBailiffsService {

    private final FileHandlingService fileHandlingService;

    private final AmalgamatedCTTransformer amalgamatedCTTransformer;

    private final DwpTransformer dwpTransformer;

    private final AntTransformer antTransformer;

    @LogExecutionTime
    public void process() {

        for (String fileName : fileHandlingService.getListOfFilesToProcess()) {

            //process single file
            processSingleFile(fileName);

        }
    }

    void processSingleFile(String fileName) {
        try {
            OpalFile file = fileHandlingService
                .createOpalFile(fileName, true, DWP_BAILIFFS_PROCESSING.getPath());

            fileHandlingService.outputFileSuccess(applyTransformations(file));

        } catch (Exception e) {

            fileHandlingService
                .outputFileError(fileName,DWP_BAILIFFS_PROCESSING.getPath(), DWP_BAILIFFS_ERROR.getPath());
        }
    }

    OpalFile applyTransformations(OpalFile file) {

        return antTransformer.antTransform(
            amalgamatedCTTransformer.transformAmalgamatedCT(
                dwpTransformer.dwpTransform(file), false));
    }
}
