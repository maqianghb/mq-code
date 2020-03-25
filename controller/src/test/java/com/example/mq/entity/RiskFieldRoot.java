package com.example.mq.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "RiskFieldRoot")
public class RiskFieldRoot {
    private List<BizEventFields> bizEventFields;
}
