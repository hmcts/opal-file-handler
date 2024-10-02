package uk.gov.hmcts.reform.opal.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.entity.AntCtAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.model.entity.ChequeBankAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.model.entity.ChequeNumberAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.repository.AntCtAmalgamatedTemp;
import uk.gov.hmcts.reform.opal.repository.ChequeBankAmalgamatedTemp;
import uk.gov.hmcts.reform.opal.repository.ChequeNumberAmalgamatedTemp;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AmalgamatedCTTransformer {

    private final ChequeBankAmalgamatedTemp chequeBankAmalgamatedTemp;
    private final AntCtAmalgamatedTemp antCtAmalgamatedTemp;
    private final ChequeNumberAmalgamatedTemp chequeNumberAmalgamatedTemp;

    public OpalFile transformAmalgamatedCT(OpalFile file, boolean isCheque) {
        if (!(file.getFileContent() instanceof StandardBankingFile)) {
            throw new IllegalArgumentException("Unsupported file content type");
        }

        if (isCheque) {
            ChequeBankAmalgamatedEntity chequeEntity = getChequeAmalgamatedEntity(file.getNewFileName());
            if (chequeEntity != null) {
                applyChequeTransformations(file, chequeEntity);
            }
        } else {
            AntCtAmalgamatedEntity cashEntity = getCashAmalgamatedEntity(file.getNewFileName());
            if (cashEntity != null) {
                applyCashTransformations(file, cashEntity);
            }
        }

        return file;
    }

    private ChequeBankAmalgamatedEntity getChequeAmalgamatedEntity(String fileName) {
        return chequeBankAmalgamatedTemp.findByAmalgamatedCt_AmalgamatedCt(extractFileNameCT(fileName));
    }

    private AntCtAmalgamatedEntity getCashAmalgamatedEntity(String fileName) {
        return antCtAmalgamatedTemp.findByAmalgamatedCt(extractFileNameCT(fileName));
    }

    private void applyChequeTransformations(OpalFile file, ChequeBankAmalgamatedEntity chequeEntity) {
        // Update file name
        file.setNewFileName(replaceFileNameCT(file.getNewFileName(), chequeEntity.getMasterCt()));

        // Process transactions
        StandardBankingFile bankingFile = (StandardBankingFile) file.getFileContent();
        List<FinancialTransaction> transformedTransactions = bankingFile.getFinancialTransactions().stream()
            .map(transaction -> transformChequeTransaction(transaction, file.getNewFileName(), chequeEntity))
            .collect(Collectors.toList());

        bankingFile.setFinancialTransactions(transformedTransactions);
    }

    public FinancialTransaction transformChequeTransaction(FinancialTransaction transaction, String newFileName,
                                                           ChequeBankAmalgamatedEntity chequeEntity) {
        // Update account and sort code
        if (chequeEntity.getOldBankAcc() != null) {
            transaction.setBranchAccountNumber(chequeEntity.getMasterBankAcc());
        }
        if (chequeEntity.getOldSortCode() != null) {
            transaction.setBranchSortCode(chequeEntity.getMasterSortCode());
        }

        // Update cheque number
        String existingChequeNumber = transaction.getReferenceNumber().substring(0, 6);
        ChequeNumberAmalgamatedEntity chequeNumberEntity = chequeNumberAmalgamatedTemp
            .findByAmalgamatedCt_AmalgamatedCtAndOldChequeNo(
            extractFileNameCT(newFileName), existingChequeNumber);

        if (chequeNumberEntity != null) {
            transaction.setReferenceNumber(transaction.getReferenceNumber().replace(existingChequeNumber,
                                                                                    chequeNumberEntity
                                                                                        .getNewChequeNo()));
        }

        return transaction;
    }

    private void applyCashTransformations(OpalFile file, AntCtAmalgamatedEntity cashEntity) {
        // Update file name
        file.setNewFileName(replaceFileNameCT(file.getNewFileName(), cashEntity.getMasterCt()));

        // Process transactions
        StandardBankingFile bankingFile = (StandardBankingFile) file.getFileContent();
        List<FinancialTransaction> transformedTransactions = bankingFile.getFinancialTransactions().stream()
            .map(transaction -> transformCashTransaction(transaction, cashEntity))
            .collect(Collectors.toList());

        bankingFile.setFinancialTransactions(transformedTransactions);
    }

    private FinancialTransaction transformCashTransaction(FinancialTransaction transaction,
                                                          AntCtAmalgamatedEntity cashEntity) {
        transaction.setBranchAccountNumber(cashEntity.getMasterBankAccount());
        transaction.setBranchSortCode(cashEntity.getMasterSortCode());
        return transaction;
    }

    private String extractFileNameCT(String fileName) {
        // Extract the CT number from the file name (Example logic, can be updated)
        return fileName.substring(fileName.length() - 3);
    }

    private String replaceFileNameCT(String fileName, String amalgamatedCT) {
        // Replace the CT number in the file name (Example logic, can be updated)
        return fileName.substring(0, fileName.length() - 3) + amalgamatedCT;
    }
}