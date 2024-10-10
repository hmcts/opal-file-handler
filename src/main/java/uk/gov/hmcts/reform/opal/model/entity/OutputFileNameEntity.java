package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "OUTPUT_FNAME")
public class OutputFileNameEntity {

    @Id
    @Column(name = "DOC_ID")
    Long docId;
    @Column(name = "FNAME")
    String fileName;
    @Column(name = "GENERATED")
    LocalDateTime generated;
    @Column(name = "STATUS")
    String status;
    @Column(name = "FULL_PATH")
    String fullPath;
}
