package com.leviii.crm.workbench.service;

import com.leviii.crm.vo.PaginationVO;
import com.leviii.crm.workbench.domain.Clue;

import java.util.Map;

/**
 * @author leviii
 * @date 2021/1/30 10:30
 */
public interface ClueService {
    PaginationVO<Clue> getClueList(Map<String,Object> map);

    boolean save(Clue clue);

    Clue getClueById(String id);

    boolean unbind(String id);

    boolean bind(String clueId, String[] activityIds);

    boolean convert(Map<String, Object> map);
}
