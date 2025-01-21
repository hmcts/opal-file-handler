package uk.gov.hmcts.reform.opal.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.model.entity.AntCtAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.repository.AntCtAmalgamatedRepository;
import uk.gov.hmcts.reform.opal.repository.AntMccCtRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AmalgamatedBUTransformer {

    private final AntCtAmalgamatedRepository antCtAmalgamated;
    private final AntMccCtRepository antMccCtRepository;

    public OpalFile transformAmalgamatedCT(OpalFile file, String source) {
        if (!(file.getFileContent() instanceof StandardBankingFile)) {
            throw new IllegalArgumentException("Unsupported file content type");
        }
        AntCtAmalgamatedEntity cashEntity = getCashAmalgamatedEntity((StandardBankingFileName) file.getNewFileName());
        if (cashEntity != null) {
            applyCashTransformations(file, cashEntity);
        }
        String sortCode = ((StandardBankingFile) file.getFileContent()).getFinancialTransactions().getFirst()
            .getBranchSortCode();

        String bankAccount = ((StandardBankingFile) file.getFileContent()).getFinancialTransactions().getFirst()
            .getBranchAccountNumber();

        file.setNewFileName(createCashFilename(((StandardBankingFile) file.getFileContent())
                                                   .getFinancialTransactions().getFirst().getDate(),
                                               source, sortCode, bankAccount));
        return file;
    }

    private AntCtAmalgamatedEntity getCashAmalgamatedEntity(StandardBankingFileName fileName) {
        return antCtAmalgamated.findByAmalgamatedCt(fileName.getCt());
    }

    private void applyCashTransformations(OpalFile file, AntCtAmalgamatedEntity cashEntity) {
        // Update file name
        ((StandardBankingFileName)file.getNewFileName()).setCt(cashEntity.getMasterCt());

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

    private StandardBankingFileName createCashFilename(String date, String source, String sortCode,
                                                       String bankAccount) {

        return StandardBankingFileName.builder()
            .prefix("a121")
            .date(date)
            .source(source)
            .ct(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(sortCode, bankAccount)
                    .getMccCt().substring(5))
            .extension("dat")
            .build();
    }
}
