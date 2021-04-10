package com.leviii.crm.workbench.service;

import com.leviii.crm.vo.PaginationVO;
import com.leviii.crm.workbench.domain.Activity;
import com.leviii.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean save(Activity activity);

    PaginationVO<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> edit(String id);

    boolean update(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAId(String activityId);

    boolean deleteRemark(String remarkId);

    boolean saveRemark(ActivityRemark activityRemark);

    boolean updateRemark(ActivityRemark activityRemark);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListNotBind(Map<String, String> map);

    List<Activity> getActivityListByName(String activityName);
}
