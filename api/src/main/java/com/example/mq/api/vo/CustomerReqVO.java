package com.example.mq.api.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 11:52
 */
@Data
public class CustomerReqVO implements Serializable {
    private static final long serialVersionUID = -2388951581709170653L;

    @NotNull(message = "customerId could not be null")
    private String customerId;

}
