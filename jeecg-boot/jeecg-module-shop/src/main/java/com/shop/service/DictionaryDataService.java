package com.shop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.core.web.PageParam;
import com.shop.common.core.web.PageResult;
import com.shop.entity.DictionaryData;
import com.shop.mapper.DictionaryDataMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 字典项服务实现类
 * 2020-03-14 11:29:04
 */
@Service
public class DictionaryDataService extends ServiceImpl<DictionaryDataMapper, DictionaryData> {

    public PageResult<DictionaryData> listPage(PageParam<DictionaryData> page) {
        List<DictionaryData> records = baseMapper.listPage(page);
        return new PageResult<>(records, page.getTotal());
    }

    public List<DictionaryData> listAll(Map<String, Object> page) {
        return baseMapper.listAll(page);
    }

    public List<DictionaryData> listByDictCode(String dictCode) {
        PageParam<DictionaryData> pageParam = new PageParam<>();
        pageParam.put("dictCode", dictCode).setDefaultOrder(new String[]{"sort_number"}, null);
        List<DictionaryData> records = baseMapper.listAll(pageParam.getNoPageParam());
        return pageParam.sortRecords(records);
    }

    public DictionaryData listByDictCodeAndName(String dictCode, String dictDataName) {
        PageParam<DictionaryData> pageParam = new PageParam<>();
        pageParam.put("dictCode", dictCode).put("dictDataName", dictDataName);
        List<DictionaryData> records = baseMapper.listAll(pageParam.getNoPageParam());
        return pageParam.getOne(records);
    }

}