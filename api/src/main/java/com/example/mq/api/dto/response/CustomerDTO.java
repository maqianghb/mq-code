package com.example.mq.api.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/10/24
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO implements Serializable {
	private static final long serialVersionUID = -1568649935102740634L;

	private String customerId;

	private String name;
}
