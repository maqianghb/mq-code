package com.example.mq.infra.seller.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.exception.BusinessException;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.infra.seller.condition.SellerQueryCondition;
import com.example.mq.infra.seller.mapper.SellerMapper;
import com.example.mq.infra.seller.model.SellerDO;
import com.example.mq.infra.seller.repository.SellerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SellerRepositoryImpl implements SellerRepository {

    @Resource
    private SellerMapper sellerMapper;

    @Override
    public Page<SellerDO> pageQueryByCondition(SellerQueryCondition condition) {
        AssertUtils.assertNotNull(condition, BizErrorEnum.PARAM_INVALID);
        AssertUtils.assertNotNull(condition.getPageNum(), BizErrorEnum.PARAM_INVALID);
        AssertUtils.assertNotNull(condition.getPageSize(), BizErrorEnum.PARAM_INVALID);

        LambdaQueryWrapper<SellerDO> lqw =new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(condition.getSellerNo())){
            lqw.eq(SellerDO::getSellerNo, condition.getSellerNo());
        }
        if(CollectionUtils.isNotEmpty(condition.getSellerNoList())){
            lqw.in(SellerDO::getSellerNo, condition.getSellerNoList());
        }
        lqw.orderByAsc(SellerDO::getId);

        Page page = new Page<>(condition.getPageNum(), condition.getPageSize());
        return sellerMapper.selectPage(page, lqw);
    }

    @Override
    public void saveSellerDO(SellerDO sellerDO) {
        AssertUtils.assertNotNull(sellerDO, BizErrorEnum.PARAM_INVALID);
        AssertUtils.assertNotBlank(sellerDO.getSellerNo(), BizErrorEnum.PARAM_INVALID);

        int addNum = sellerMapper.insert(sellerDO);
        if(addNum <=0){
            log.error("商家信息保存失败，商家信息:{}", JSON.toJSONString(sellerDO));
            throw new BusinessException(BizErrorEnum.DB_OPERATE_ERROR, "商家信息保存失败");
        }
    }

    @Override
    public void updateById(SellerDO sellerDO) {
        AssertUtils.assertNotNull(sellerDO, BizErrorEnum.PARAM_INVALID);
        AssertUtils.assertNotNull(sellerDO.getId(), BizErrorEnum.PARAM_INVALID);

        int updateNum = sellerMapper.updateById(sellerDO);
        if(updateNum <=0){
            log.error("商家信息更新失败，商家信息:{}", JSON.toJSONString(sellerDO));
            throw new BusinessException(BizErrorEnum.DB_OPERATE_ERROR, "商家信息更新失败");
        }
    }

}
