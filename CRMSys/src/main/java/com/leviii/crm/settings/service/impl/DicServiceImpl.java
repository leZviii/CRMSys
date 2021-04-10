package com.leviii.crm.settings.service.impl;

import com.leviii.crm.settings.dao.DicTypeDao;
import com.leviii.crm.settings.dao.DicValueDao;
import com.leviii.crm.settings.domain.DicType;
import com.leviii.crm.settings.domain.DicValue;
import com.leviii.crm.settings.service.DicService;
import com.leviii.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leviii
 * @date 2021/1/30 10:47
 */
public class DicServiceImpl implements DicService {
    private final DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private final DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    /**
     * 从数据库中取出所有数据字典的数据
     * @return 返回数据字典
     */
    @Override
    public Map<String, List<DicValue>> getAllDic() {

        Map<String,List<DicValue>> map = new HashMap<>();

        //取得所有的类型
        List<DicType> dicTypes = dicTypeDao.getTypeList();
        //根据类型获取数据
        for (DicType dicType : dicTypes){
            //字典的类型
            String code = dicType.getCode();
            //根据类型取数据
            List<DicValue> dicValues = dicValueDao.getListByCode(code);
            map.put(code,dicValues);
        }

        return map;
    }
}
