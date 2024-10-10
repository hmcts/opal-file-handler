package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "DOCUMENT_TRACKER")
public class DocumentTrackerEntity {

    @Id
    @Column(name = "DOC_ID")
    Long docId;
    @Column(name = "FILE_NAME")
    String fileName;
    @Column(name = "SOURCE")
    String source;
    @Column(name = "START_TIME")
    LocalDateTime startTime;
    @Column(name = "END_TIME")
    LocalDateTime endTime;
    @Column(name = "CURRENT_LOCATION")
    String currentLocation;
    @Column(name = "BDU_SUCCESS_COUNT")
    BigInteger bduSuccessCount;
    @Column(name = "BDU_ERROR_COUNT")
    BigInteger bduErrorCount;
    @Column(name = "BDU_TOTAL")
    BigInteger bduTotal;
    @Column(name = "STATUS")
    String status;
    @Column(name = "MESSAGE")
    String message;
}
