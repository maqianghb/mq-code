package com.example.mq.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement(name = "BizEventFields")
public class BizEventFields {
    private Integer bizCode;
    private Integer eventCode;
    private List<RiskField> riskField;

    public BizEventFields(Integer bizCode, Integer eventCode, List<RiskField> riskField) {
        this.bizCode = bizCode;
        this.eventCode = eventCode;
        this.riskField = riskField;
    }

}
