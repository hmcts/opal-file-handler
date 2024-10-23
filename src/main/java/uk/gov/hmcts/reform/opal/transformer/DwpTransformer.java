package uk.gov.hmcts.reform.opal.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.dto.DocumentDetail;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.model.entity.AntCtBankAccountEntity;
import uk.gov.hmcts.reform.opal.repository.AntCtBankAccountRepository;
import uk.gov.hmcts.reform.opal.repository.AntMccCtRepository;
import uk.gov.hmcts.reform.opal.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;

@Component
@RequiredArgsConstructor
public class DwpTransformer {

    private final AntMccCtRepository antMccCtRepository;
    private final AntCtBankAccountRepository antCtBankAccountRepository;

    public OpalFile dwpTransform(OpalFile opalFile) {
        AntCtBankAccountEntity antCtBankAccountEntity = antCtBankAccountRepository
            .findByDwpCourtCode(opalFile.getOriginalFileName().substring(0, 10));

        DwpFile dwpFile = (DwpFile) opalFile.getFileContent();
        opalFile.setFileContent(populateStandardBankingFile(dwpFile, antCtBankAccountEntity));
        opalFile.setNewFileName(createCashFileNameDwp(dwpFile, opalFile.getOriginalFileName(), antCtBankAccountEntity));
        return opalFile;
    }

    private StandardBankingFile populateStandardBankingFile(DwpFile dwpFile,
                                                            AntCtBankAccountEntity antCtBankAccountEntity) {
        List<FinancialTransaction> financialTransactions = new ArrayList<>();

        for (DocumentDetail documentDetail : dwpFile.getDocumentDetails()) {
            if (documentDetail.getDetailAmountSign().equals("+")) {
                financialTransactions.add(createFinancialTransaction(documentDetail, antCtBankAccountEntity));
            }
        }
        return StandardBankingFile.builder()
            .header(new StringBuilder())
            .financialTransactions(financialTransactions)
            .footer(new StringBuilder())
            .build();
    }

    private FinancialTransaction createFinancialTransaction(DocumentDetail documentDetail,
                                                            AntCtBankAccountEntity antCtBankAccountEntity) {
        return FinancialTransaction.builder()
            .branchSortCode(antCtBankAccountEntity.getSortCode())
            .branchAccountNumber(antCtBankAccountEntity.getAccountNumber())
            .accountType("0")
            .transactionType("99")
            .originatorsSortCode("0".repeat(6))
            .originatorsAccountNumber("0".repeat(8))
            .originatorsReference("0".repeat(4))
            .amount(documentDetail.getDetailAmountType())
            .originatorsNameOrDescription(" ".repeat(18))
            .referenceNumber(documentDetail.getCustomerRef())
            .date(DateUtil.convertToJulian(documentDetail.getDateFrom()))
            .build();
    }

    private StandardBankingFileName createCashFileNameDwp(DwpFile file, String originalFileName,
                                                          AntCtBankAccountEntity antCtBankAccountEntity) {
        return StandardBankingFileName.builder()
            .prefix("a121")
            .date(file.getDocumentHeader().getPacsDocumentCreationDate())
            .source(DWP_BAILIFFS.getSource())
            .ct(antMccCtRepository.findByCt(antCtBankAccountEntity.getCt()).getMccCt().substring(5))
            .extension("dat")
            .build();
    }
}
