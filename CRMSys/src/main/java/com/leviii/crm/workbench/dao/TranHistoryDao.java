package com.leviii.crm.workbench.dao;

import com.leviii.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int save(TranHistory tranHistory);

    List<TranHistory> getHistoryListById(String tranId);
}
