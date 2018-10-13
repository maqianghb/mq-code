package com.example.mq.api.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.enums.CustomerVipLevelEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 00:08
 */
@Data
public class CustomerVO {

    private Integer id;

    @NotNull
    private String customerId;

    @NotNull
    private String name;

    /**
     * 城市代码
     */
    private String city;

    /**
     * 城市名称
     */
    private String cityDesc;

    private String address;

    private String description;

    /**
     * vip级别code
     */
    private Short vipLevel;

    /**
     * vip级别描述
     */
    private String vipLevelDesc;

    private Boolean delFlag;

    private String createUser;

    private String updateUser;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public static CustomerVO convertToVO(Customer customer){
        if(Objects.isNull(customer)){
            return new CustomerVO();
        }
        CustomerVO vo =new CustomerVO();
        if( !Objects.isNull(customer.getId())){
            vo.setId(customer.getId());
        }
        vo.setCustomerId(customer.getCustomerId());
        vo.setName(customer.getName());
        vo.setCity(customer.getCity());
        vo.setCityDesc("描述");
        vo.setAddress(customer.getAddress());
        vo.setDescription(customer.getDescription());
        vo.setVipLevel(customer.getVipLevel());
        CustomerVipLevelEnum levelEnum =CustomerVipLevelEnum.getByCode(customer.getVipLevel());
        vo.setVipLevelDesc(Objects.isNull(levelEnum) ? "":levelEnum.getMsg());
        vo.setDelFlag(customer.getDelFlag());
        vo.setCreateUser(customer.getCreateUser());
        vo.setCreateTime(customer.getCreateTime());
        vo.setUpdateUser(customer.getUpdateUser());
        vo.setUpdateTime(customer.getUpdateTime());
        return vo;
    }

    public static Customer convertToCustomer(CustomerVO vo){
        Customer customer =new Customer();
        return customer;
    }
}
