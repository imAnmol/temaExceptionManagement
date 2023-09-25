package com.osttra.entity;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Document(collection="mongodata")
public class SourceMongoEntity {

    @Id
    private String exceptionId;
    private String tradeId;
    private String counterParty;
    private String tradeDate;
    private String exceptionType;
    private List<String> resolutionSteps;
    private String status;
    private String priority;
    private String description;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}
