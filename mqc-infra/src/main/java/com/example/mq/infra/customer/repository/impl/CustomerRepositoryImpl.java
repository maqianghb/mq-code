package com.example.mq.infra.customer.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.exception.BusinessException;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.infra.customer.condition.CustomerQueryCondition;
import com.example.mq.infra.customer.model.CustomerDO;
import com.example.mq.infra.customer.mapper.CustomerMapper;
import com.example.mq.infra.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
@Slf4j
public class CustomerRepositoryImpl implements CustomerRepository {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public List<CustomerDO> queryCustomerList(CustomerQueryCondition condition) {
        AssertUtils.assertNotNull(condition, BizErrorEnum.PARAM_INVALID);

        LambdaQueryWrapper<CustomerDO> lqw =new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(condition.getCustomerNo())){
            lqw.eq(CustomerDO::getCustomerNo, condition.getCustomerNo());
        }
        if(CollectionUtils.isNotEmpty(condition.getCustomerNoList())){
            lqw.in(CustomerDO::getCustomerNo, condition.getCustomerNoList());
        }
        lqw.orderByAsc(CustomerDO::getId);

        // 先校验大小
        Long countNum = customerMapper.selectCount(lqw);
        if(countNum >=500){
            throw new BusinessException(BizErrorEnum.DB_OPERATE_ERROR
                    , "客户信息查询数量超限，查询条件:{}, 查询数量:{}", JSON.toJSONString(condition), countNum);
        }

        return customerMapper.selectList(lqw);
    }

    @Override
    public void saveCustomerDO(CustomerDO customerDO) {
        AssertUtils.assertNotNull(customerDO, BizErrorEnum.PARAM_INVALID);
        AssertUtils.assertNotBlank(customerDO.getCustomerNo(), BizErrorEnum.PARAM_INVALID);

        int addNum = customerMapper.insert(customerDO);
        if(addNum <=0){
            log.error("客户信息保存失败，客户信息:{}", JSON.toJSONString(customerDO));
            throw new BusinessException(BizErrorEnum.DB_OPERATE_ERROR, "客户信息保存失败");
        }
    }

}
