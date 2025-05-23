package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.model.CashFileSequence;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.scheduler.aspect.LogExecutionTime;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedBUTransformer;
import uk.gov.hmcts.reform.opal.transformer.AntTransformer;
import uk.gov.hmcts.reform.opal.transformer.DwpTransformer;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_ARCHIVE;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_ERROR;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_PROCESSING;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class DwpBailiffsService {

    private final FileHandlingService fileHandlingService;

    private final AmalgamatedBUTransformer amalgamatedCTTransformer;

    private final DwpTransformer dwpTransformer;

    private final AntTransformer antTransformer;

    @LogExecutionTime
    public void process() {

        CashFileSequence sequence = new CashFileSequence();

        for (String originalFileName : fileHandlingService.getListOfFilesToProcess()) {

            try {

                log.debug("Processing file: {}", originalFileName);

                OpalFile file = fileHandlingService
                    .createOpalFile(originalFileName, true,
                                    DWP_BAILIFFS_PROCESSING.getPath());


                file = applyTransformations(file);
                file.getNewFileName()
                    .setSequence(sequence.getAndIncrementSequence((StandardBankingFileName) file.getNewFileName()));

                fileHandlingService.uploadStandardBankingFile(file, DWP_BAILIFFS_SUCCESS.getPath());
                //move file to archive until back end service is developed
                fileHandlingService.moveFile(originalFileName, DWP_BAILIFFS_PROCESSING.getPath(),
                                             DWP_BAILIFFS_ARCHIVE.getPath());

            } catch (Exception e) {
                log.error("Error processing file: {}", originalFileName, e);
                fileHandlingService
                    .moveFile(originalFileName, DWP_BAILIFFS_PROCESSING.getPath(), DWP_BAILIFFS_ERROR.getPath());
            }

        }
    }

    OpalFile applyTransformations(OpalFile file) {

        return antTransformer.antTransform(
            amalgamatedCTTransformer.transformAmalgamatedCT(
                dwpTransformer.dwpTransform(file), DWP_BAILIFFS.getSource()));
    }
}
