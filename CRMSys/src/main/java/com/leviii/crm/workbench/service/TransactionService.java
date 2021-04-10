package com.leviii.crm.workbench.service;

import com.leviii.crm.workbench.domain.Tran;
import com.leviii.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

/**
 * @author leviii
 * @date 2021/2/8 17:35
 */
public interface TransactionService {
    boolean save(Tran tran, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListById(String tranId);

    boolean changeStage(Tran tran);

    Map<String, Object> getCharts();
}
