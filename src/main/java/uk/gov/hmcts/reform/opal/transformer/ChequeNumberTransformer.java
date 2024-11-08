package uk.gov.hmcts.reform.opal.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.model.entity.ChequeBankAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.model.entity.ChequeNumberAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.repository.AntMccCtRepository;
import uk.gov.hmcts.reform.opal.repository.ChequeBankAmalgamatedRepository;
import uk.gov.hmcts.reform.opal.repository.ChequeNumberAmalgamatedRepository;
import uk.gov.hmcts.reform.opal.service.ChequeFileSequenceService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChequeNumberTransformer {

    private final ChequeBankAmalgamatedRepository chequeBankAmalgamated;
    private final ChequeNumberAmalgamatedRepository chequeNumberAmalgamated;
    private final AntMccCtRepository antMccCtRepository;
    private final ChequeFileSequenceService sequenceService;

    public OpalFile transformAmalgamatedCT(OpalFile file) {
        if (!(file.getFileContent() instanceof StandardBankingFile)) {
            throw new IllegalArgumentException("Unsupported file content type");
        }

        file.setNewFileName(createChequeFilename(
            ((StandardBankingFile) file.getFileContent()).getFinancialTransactions().getFirst()
                .getBranchAccountNumber(),
            ((StandardBankingFile) file.getFileContent())
                .getFinancialTransactions().getFirst().getBranchSortCode()));

        ChequeBankAmalgamatedEntity chequeEntity =
            getChequeAmalgamatedEntity(((StandardBankingFileName)file.getNewFileName()).getCt());

        if (chequeEntity != null) {

            file.setFileContent(applyChequeTransformations(file, chequeEntity));
        }

        return file;
    }

    private ChequeBankAmalgamatedEntity getChequeAmalgamatedEntity(String ct) {
        return chequeBankAmalgamated.findByAmalgamatedCt(ct);
    }

    private StandardBankingFile applyChequeTransformations(OpalFile file, ChequeBankAmalgamatedEntity chequeEntity) {
        StandardBankingFile bankingFile = (StandardBankingFile) file.getFileContent();
        List<FinancialTransaction> transformedTransactions = bankingFile.getFinancialTransactions().stream()
            .map(transaction -> transformChequeTransaction(transaction, chequeEntity))
            .collect(Collectors.toList());
        bankingFile.setFinancialTransactions(transformedTransactions);

        return bankingFile;
    }

    protected FinancialTransaction transformChequeTransaction(FinancialTransaction transaction,
                                                           ChequeBankAmalgamatedEntity chequeEntity) {
        if (chequeEntity.getOldBankAccount() != null) {
            transaction.setBranchAccountNumber(chequeEntity.getMasterBankAccount());
        }
        if (chequeEntity.getOldSortCode() != null) {
            transaction.setBranchSortCode(chequeEntity.getMasterSortCode());
        }

        String existingChequeNumber = transaction.getReferenceNumber().substring(0, 6);
        ChequeNumberAmalgamatedEntity chequeNumberEntity = chequeNumberAmalgamated
            .findByAmalgamatedCtAndOldChequeNumber(chequeEntity.getMasterCt(), existingChequeNumber);

        if (chequeNumberEntity != null) {
            transaction.setReferenceNumber(transaction.getReferenceNumber().replace(existingChequeNumber,
                                                                                    chequeNumberEntity
                                                                                        .getNewChequeNumber()));
        }

        return transaction;
    }

    private StandardBankingFileName createChequeFilename(String bankAccount, String sortCode) {
        return StandardBankingFileName.builder()
            .prefix("bacs")
            .ct(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(sortCode, bankAccount)
                    .getMccCt().substring(5))
            .sequence(sequenceService.getNextSequenceValue().toString())
            .extension("dat")
            .build();
    }
}
