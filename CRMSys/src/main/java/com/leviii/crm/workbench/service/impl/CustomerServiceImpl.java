package com.leviii.crm.workbench.service.impl;

import com.leviii.crm.utils.SqlSessionUtil;
import com.leviii.crm.workbench.dao.CustomerDao;
import com.leviii.crm.workbench.service.CustomerService;

import java.util.List;

/**
 * @author leviii
 * @date 2021/2/9 15:53
 */
public class CustomerServiceImpl implements CustomerService {
    private final CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    /**
     * 根据名字模糊查询顾客姓名
     * @param name 模糊查询的姓名
     * @return 返回查询结果
     */
    @Override
    public List<String> getCustomerName(String name) {
        return customerDao.getCustomerName(name);
    }
}
