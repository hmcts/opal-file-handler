package uk.gov.hmcts.reform.opal.model;

import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;

import java.util.HashMap;

public class FileSequence {

    private HashMap<String, String> sequenceMap = new HashMap<>();

    public String getAndIncrementSequence(StandardBankingFileName fileName) {

        String key = fileName.getDate() + fileName.getSource() + fileName.getCt();

        if (sequenceMap.containsKey(key)) {

            sequenceMap.put(key, sequenceMap.get(key)  + 1);
            return sequenceMap.get(key);
        } else {
            sequenceMap.put(key, "AA");
            return sequenceMap.get(key);
        }

    }

}
