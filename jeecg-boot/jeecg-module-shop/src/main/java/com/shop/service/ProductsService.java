package com.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.Cards;
import com.shop.mapper.CardsMapper;
import com.shop.common.core.web.PageParam;
import com.shop.common.core.web.PageResult;
import com.shop.entity.Products;
import com.shop.mapper.ProductsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品服务实现类
 * 2021-03-27 20:22:00
 */
@Service
@Transactional
public class ProductsService extends ServiceImpl<ProductsMapper, Products> {

    @Autowired
    private CardsMapper cardsMapper;

    public PageResult<Products> listPage(PageParam<Products> page) {
        List<Products> records = baseMapper.listPage(page);
        return new PageResult<>(records, page.getTotal());
    }

    public List<Products> listAll(Map<String, Object> page) {
        return baseMapper.listAll(page);
    }

    public List<Products> getRandomProductList(int limit) {
        return baseMapper.getRandomProductList(limit);
    }

    public boolean removeByIds(Collection<?> idList) {
        for (Object serializable : idList) {
            long count = cardsMapper.selectCount(new QueryWrapper<Cards>().eq("product_id", serializable));
            if (count > 0) {
                return false;
            } else {
                baseMapper.deleteById((Serializable) serializable);
            }
        }
        return true;
    }
}