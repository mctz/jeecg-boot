package com.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.core.web.PageParam;
import com.shop.common.core.web.PageResult;
import com.shop.entity.Classifys;
import com.shop.entity.Products;
import com.shop.mapper.ClassifysMapper;
import com.shop.mapper.ProductsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 分类服务实现类
 * 2021-03-27 20:22:00
 */
@Service
@Transactional
public class ClassifysService extends ServiceImpl<ClassifysMapper, Classifys> {

    @Autowired
    private ProductsMapper productsMapper;

    public PageResult<Classifys> listPage(PageParam<Classifys> page) {
        List<Classifys> records = baseMapper.listPage(page);
        return new PageResult<>(records, page.getTotal());
    }

    public List<Classifys> listAll(Map<String, Object> page) {
        return baseMapper.listAll(page);
    }

    @Override
    public boolean removeByIds(Collection<?> idList) {
        for (Object serializable : idList) {
            long count = productsMapper.selectCount(new QueryWrapper<Products>().eq("classify_id", serializable));
            if (count > 0) {
                return false;
            } else {
                baseMapper.deleteById((Serializable) serializable);
            }
        }
        return true;
    }
}