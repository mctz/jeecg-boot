package com.treesoft.system.service;

import com.treesoft.system.dao.ConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConfigService {
    @Autowired
    private ConfigDao configDao;

    public ConfigService() {
    }

    public List<Map<String, Object>> getAllConfigList() throws Exception {
        return this.configDao.getAllConfigList();
    }

}