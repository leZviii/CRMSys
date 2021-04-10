package com.leviii.crm.workbench.service.impl;

import com.leviii.crm.settings.domain.User;
import com.leviii.crm.utils.DateTimeUtil;
import com.leviii.crm.utils.SqlSessionUtil;
import com.leviii.crm.utils.UUIDUtil;
import com.leviii.crm.vo.PaginationVO;
import com.leviii.crm.workbench.dao.*;
import com.leviii.crm.workbench.domain.*;
import com.leviii.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

/**
 * @author leviii
 * @date 2021/1/30 10:30
 */
public class ClueServiceImpl implements ClueService {

    private final ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private final ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private final ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    private final CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private final CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private final ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private final ContactsRemarkDao contactsRemarkDao= SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private final ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private final TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private final TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);


    /**
     * 将线索转换为交易
     * @param map 线索id,当前所有者对象,需要创建的交易对象
     * @return 返回创建结果
     */
    @Override
    public boolean convert(Map<String, Object> map) {
        boolean flag = true;

        User user = (User) map.get("user");
        String createTime = DateTimeUtil.getSysTime();
        String clueId = (String) map.get("clueId");
        Tran tran = (Tran) map.get("tran");

        //获取当前线索对象
        Clue clue = clueDao.getClueById(clueId);
        //判断当前客户表是否存在当前客户
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        if (customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setCreateTime(createTime);
            customer.setCreateBy(user.getName());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setOwner(clue.getOwner());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setContactSummary(clue.getContactSummary());

            int result = customerDao.save(customer);
            if (result != 1){
                flag = false;
            }
        }

        //创建联系人信息
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(user.getName());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());

        int result2 = contactsDao.save(contacts);
        if (result2 != 1){
            flag = false;
        }

        //将线索备注转换到客户备注以及联系人的备注
        List<ClueRemark> clueRemarks = clueRemarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark : clueRemarks){
            String noteContent = clueRemark.getNoteContent();
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setCreateBy(user.getName());
            customerRemark.setCreateTime(createTime);
            int result3 = customerRemarkDao.save(customerRemark);
            if (result3 != 1){
                flag = false;
            }

            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setCreateBy(user.getName());
            contactsRemark.setCreateTime(createTime);
            int result4 = contactsRemarkDao.save(contactsRemark);
            if (result4 != 1){
                flag = false;
            }
        }

        //线索和市场活动的联系转换成联系人和市场活动的联系
        List<ClueActivityRelation> clueActivityRelations = clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation : clueActivityRelations){
            String activityId = clueActivityRelation.getActivityId();
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(activityId);
            int result5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (result5 != 1){
                flag = false;
            }
        }

        //如果需要创建交易,那么创建交易对象
        if (tran != null){
            tran.setSource(clue.getSource());
            tran.setOwner(user.getName());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactsId(contacts.getId());
            tran.setContactSummary(contacts.getContactSummary());
            int result6 = tranDao.save(tran);
            if (result6 != 1){
                flag = false;
            }
            
            //如果创建了交易,那么创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(user.getName());
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setTranId(tran.getId());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            int result7 = tranHistoryDao.save(tranHistory);
            if (result7 != 1){
                flag = false;
            }
        }
        
        //删除线索备注
        for (ClueRemark clueRemark : clueRemarks){
            int result8 = clueRemarkDao.delete(clueRemark);
            if (result8 != 1){
                flag = false;
            }
        }

        //删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation : clueActivityRelations){
            int result9 = clueActivityRelationDao.delete(clueActivityRelation);
            if (result9 != 1){
                flag = false;
            }
        }

        //删除线索
        int result10 = clueDao.delete(clueId);
        if (result10 != 1){
            flag = false;
        }


        return flag;
    }

    /**
     * 关联市场活动和线索
     * @param clueId 线索id
     * @param activityIds 关联的市场活动id
     * @return 返回关联结果
     */
    @Override
    public boolean bind(String clueId, String[] activityIds) {
        boolean flag = true;
        for (String i : activityIds){
            ClueActivityRelation clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setId(UUIDUtil.getUUID());
            clueActivityRelation.setClueId(clueId);
            clueActivityRelation.setActivityId(i);
            int result = clueActivityRelationDao.save(clueActivityRelation);
            if (result != 1){
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 解除市场活动与线索的关联
     * @param id 线索和市场活动关联表的Id
     * @return 返回结果
     */
    @Override
    public boolean unbind(String id) {
        boolean flag = true;
        int result = clueActivityRelationDao.unbind(id);
        if (result != 1){
            flag = false;
        }
        return flag;
    }


    /**
     * 通过id查询线索
     * @param id id值
     * @return 返回线索对象
     */
    @Override
    public Clue getClueById(String id) {
        return clueDao.getClueById(id);
    }

    /**
     * 添加线索
     * @param clue 新建线索对象
     * @return 返回添加结果
     */
    @Override
    public boolean save(Clue clue) {
        boolean flag = true;
        int result = clueDao.save(clue);
        if (result != 1){
            flag = false;
        }
        return flag;
    }

    /**
     * 获取线索列表
     * @param map 条件查询的条件
     * @return 返回展示列表需要的信息
     */
    @Override
    public PaginationVO<Clue> getClueList(Map<String,Object> map) {
        PaginationVO<Clue> vo = new PaginationVO<>();
        int total = clueDao.getTotalByCondition(map);
        List<Clue> clues = clueDao.getClueListByCondition(map);
        vo.setTotal(total);
        vo.setDataList(clues);
        return vo;
    }

}
