package uk.gov.hmcts.reform.opal.service;

import uk.gov.hmcts.reform.opal.model.dto.FileName;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;

import java.util.HashMap;

public class CashFileSequenceService {

    private HashMap<String, String> sequenceMap = new HashMap<>();

    public String getAndIncrementSequence(FileName fileName) {

        if (!(fileName instanceof StandardBankingFileName standardBankingFileName)) {
            throw new IllegalArgumentException("Invalid file name type");
        }
        String key = standardBankingFileName.getDate()
            + standardBankingFileName.getSource()
            + standardBankingFileName.getCt();

        if (sequenceMap.containsKey(key)) {

            sequenceMap.put(key, sequenceMap.get(key)  + 1);
            return sequenceMap.get(key);
        } else {
            sequenceMap.put(key, "AA");
            return sequenceMap.get(key);
        }

    }
}
