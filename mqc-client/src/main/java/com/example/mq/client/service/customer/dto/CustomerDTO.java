package com.example.mq.client.service.customer.dto;

import com.example.mq.client.model.dto.BaseDTO;
import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/10/24
 *
 */
@Data
public class CustomerDTO extends BaseDTO {

	private String code;

	private String name;
}
