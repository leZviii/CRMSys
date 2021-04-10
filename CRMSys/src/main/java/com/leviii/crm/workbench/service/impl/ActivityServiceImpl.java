package com.leviii.crm.workbench.service.impl;

import com.leviii.crm.settings.dao.UserDao;
import com.leviii.crm.settings.domain.User;
import com.leviii.crm.utils.SqlSessionUtil;
import com.leviii.crm.vo.PaginationVO;
import com.leviii.crm.workbench.dao.ActivityDao;
import com.leviii.crm.workbench.dao.ActivityRemarkDao;
import com.leviii.crm.workbench.domain.Activity;
import com.leviii.crm.workbench.domain.ActivityRemark;
import com.leviii.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    /**
     * 根据市场活动名字模糊查询市场活动列表
     * @param activityName 市场活动的名字
     * @return 返回查询出的市场活动列表
     */
    @Override
    public List<Activity> getActivityListByName(String activityName) {
        return activityDao.getActivityListByName(activityName);
    }

    /**
     * 根据模糊名字和线索id查询未绑定过线索的市场活动列表
     * @param map 模糊名字和线索id
     * @return 返回市场活动列表
     */
    @Override
    public List<Activity> getActivityListNotBind(Map<String, String> map) {
        return activityDao.getActivityListNotBind(map);
    }

    /**
     * 根据线索id查询已经关联线索的市场活动列表
     * @param clueId 线索id
     * @return 返回市场活动列表
     */
    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        return activityDao.getActivityListByClueId(clueId);
    }

    /**
     * 修改市场活动备注信息
     * @param activityRemark 修改后的市场活动信息
     * @return 返回修改结果
     */
    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int result = activityRemarkDao.updateRemark(activityRemark);
        if (result != 1){
            flag = false;
        }
        return flag;
    }

    /**
     * 添加市场活动备注
     * @param activityRemark 需要添加的市场活动对象
     * @return 返回添加结果
     */
    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int result = activityRemarkDao.saveRemark(activityRemark);
        if (result != 1){
            flag = false;
        }
        return flag;
    }

    /**
     * 删除市场活动备注信息
     * @param remarkId 备注信息id
     * @return 返回删除结果
     */
    @Override
    public boolean deleteRemark(String remarkId) {
        boolean flag = true;
        int result = activityRemarkDao.deleteRemark(remarkId);
        if (result != 1){
            flag = false;
        }
        return flag;
    }

    /**
     * 查询市场活动备注信息
     * @param activityId 市场活动id
     * @return 返回市场活动备注信息List
     */
    @Override
    public List<ActivityRemark> getRemarkListByAId(String activityId) {
        return activityRemarkDao.getRemarkListByAId(activityId);
    }

    /**
     * 跳转到市场活动的详细页面
     * @param id 需要跳转的市场活动的id
     * @return 返回市场活动对象
     */
    @Override
    public Activity detail(String id) {
        return activityDao.detail(id);
    }

    /**
     * 执行修改市场活动的操作
     * @param activity 修改后的市场活动对象
     * @return 返回修改结果
     */
    @Override
    public boolean update(Activity activity) {
        boolean flag = true;
        int result = activityDao.update(activity);
        if (result != 1){
            flag = false;
        }
        return flag;
    }

    /**
     * 获取市场修改模态窗口的信息
     * @param id 需要修改的市场活动id(主键)
     * @return 返回用户列表和需要修改的市场活动的信息
     */
    @Override
    public Map<String, Object> edit(String id) {
        List<User> users = userDao.getUserList();
        Activity activity = activityDao.getById(id);
        Map<String,Object> result = new HashMap<>();
        result.put("userList",users);
        result.put("activity",activity);
        return result;
    }

    /**
     * 添加市场活动
     * @param activity 活动内容
     * @return 返回添加结果
     */
    @Override
    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    /**
     * 根据条件查询市场活动列表
     * @param map 前端传回来的条件
     * @return 返回一个VO(记录总条数和各个市场活动信息)
     */
    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        PaginationVO<Activity> vo = new PaginationVO<>();
        int total = activityDao.getTotalByCondition(map);
        List<Activity> dataList = activityDao.getActivityListByCondition(map);
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    /**
     * 删除市场活动
     * @param ids 需要删除的市场活动的id
     * @return 删除操作是否成功
     */
    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        int count = activityRemarkDao.getCountByAids(ids);
        int result = activityRemarkDao.deleteByAids(ids);

        if (count != result){
            flag = false;
        }
        int result1 = activityDao.delete(ids);
        if (result1 != ids.length){
            flag = false;
        }

        return flag;
    }
}
