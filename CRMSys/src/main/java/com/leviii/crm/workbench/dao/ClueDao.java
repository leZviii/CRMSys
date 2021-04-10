package com.leviii.crm.workbench.dao;

import com.leviii.crm.workbench.domain.Activity;
import com.leviii.crm.workbench.domain.Clue;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    List<Clue> getClueList();

    int getTotalByCondition(Map<String, Object> map);

    List<Clue> getClueListByCondition(Map<String, Object> map);

    int save(Clue clue);

    Clue getClueById(String id);

    int delete(String clueId);
}
