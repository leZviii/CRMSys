package com.leviii.crm.workbench.service.impl;

import com.leviii.crm.utils.DateTimeUtil;
import com.leviii.crm.utils.SqlSessionUtil;
import com.leviii.crm.utils.UUIDUtil;
import com.leviii.crm.workbench.dao.CustomerDao;
import com.leviii.crm.workbench.dao.TranDao;
import com.leviii.crm.workbench.dao.TranHistoryDao;
import com.leviii.crm.workbench.domain.Customer;
import com.leviii.crm.workbench.domain.Tran;
import com.leviii.crm.workbench.domain.TranHistory;
import com.leviii.crm.workbench.service.TransactionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leviii
 * @date 2021/2/8 17:35
 */
public class TransactionServiceImpl implements TransactionService {

    private final TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private final TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private final CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    /**
     * 获取ECharts图所需要的数据
     * @return 返回total和dataList
     */
    @Override
    public Map<String, Object> getCharts() {

        int total = tranDao.getTotal();

        List<Map<String,Object>> dataList = tranDao.getCharts();

        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }

    /**
     * 改变交易阶段
     * @param tran 改变后的介意对象
     * @return 返回 修改结果
     */
    @Override
    public boolean changeStage(Tran tran) {
        boolean flag = true;

        int count1 = tranDao.changeStage(tran);
        if (count1 != 1){
            flag = false;
        }

        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setCreateBy(tran.getCreateBy());

        int save = tranHistoryDao.save(tranHistory);
        if (save != 1){
            flag = false;
        }

        return flag;
    }

    /**
     * 根据交易id获取交易历史记录表
     * @param tranId 交易id
     * @return 返回交易历史信息
     */
    @Override
    public List<TranHistory> getHistoryListById(String tranId) {
        return tranHistoryDao.getHistoryListById(tranId);
    }

    /**
     * 跳转到交易的详细信息页
     * @param id 交易的id
     * @return 返回交易对象
     */
    @Override
    public Tran detail(String id) {
        return tranDao.getTranById(id);
    }

    /**
     * 添加线索操作
     * @param tran 线索对象
     * @param customerName 顾客姓名
     * @return 返回添加结果
     */
    @Override
    public boolean save(Tran tran, String customerName) {
        boolean flag = true;

        Customer customer = customerDao.getCustomerByName(customerName);
        if (customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setCreateBy(tran.getCreateBy());
            customer.setName(customerName);
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setDescription(tran.getDescription());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());

            int result = customerDao.save(customer);
            if (result != 1){
                return false;
            }
        }

        tran.setCustomerId(customer.getId());
        int result1 = tranDao.save(tran);
        if (result1 != 1){
            return false;
        }

        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        int result2 = tranHistoryDao.save(tranHistory);
        if (result2 != 1){
            flag = false;
        }

        return flag;
    }
}
