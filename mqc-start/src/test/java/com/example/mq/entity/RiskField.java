package com.example.mq.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "RiskField")
public class RiskField {
    private String code;
    private String name;
    private String fieldType;
    private Boolean required;

    @XmlAttribute
    public String getCode() {
        return code;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    @XmlAttribute
    public String getFieldType() {
        return fieldType;
    }

    @XmlAttribute
    public Boolean getRequired() {
        return required;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
