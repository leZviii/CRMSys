package com.leviii.crm.settings.dao;

import com.leviii.crm.settings.domain.DicValue;

import java.util.List;

/**
 * @author leviii
 * @date 2021/1/30 10:44
 */
public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}
