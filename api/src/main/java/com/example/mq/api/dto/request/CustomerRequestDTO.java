package com.example.mq.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 11:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO implements Serializable {
    private static final long serialVersionUID = -2388951581709170653L;

    private String customerId;

}
