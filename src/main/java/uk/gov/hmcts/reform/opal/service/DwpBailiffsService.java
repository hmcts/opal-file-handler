package uk.gov.hmcts.reform.opal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.opal.model.CashFileSequence;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.scheduler.aspect.LogExecutionTime;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedCTTransformer;
import uk.gov.hmcts.reform.opal.transformer.AntTransformer;
import uk.gov.hmcts.reform.opal.transformer.DwpTransformer;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;
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

        CashFileSequence sequence = new CashFileSequence();

        for (String originalFileName : fileHandlingService.getListOfFilesToProcess()) {

            try {

                OpalFile file = fileHandlingService
                    .createOpalFile(originalFileName, true,
                                    DWP_BAILIFFS_PROCESSING.getPath());


                file = applyTransformations(file);
                file.getNewFileName()
                    .setSequence(sequence.getAndIncrementSequence((StandardBankingFileName) file.getNewFileName()));

                fileHandlingService.outputFileSuccess(file);

            } catch (Exception e) {

                fileHandlingService
                    .outputFileError(originalFileName, DWP_BAILIFFS_PROCESSING.getPath(), DWP_BAILIFFS_ERROR.getPath());
            }

        }
    }

    OpalFile applyTransformations(OpalFile file) {

        return antTransformer.antTransform(
            amalgamatedCTTransformer.transformAmalgamatedCT(
                dwpTransformer.dwpTransform(file), DWP_BAILIFFS.getSource()));
    }
}
