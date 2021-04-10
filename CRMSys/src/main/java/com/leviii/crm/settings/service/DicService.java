package com.leviii.crm.settings.service;

import com.leviii.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

/**
 * @author leviii
 * @date 2021/1/30 10:47
 */
public interface DicService {
    Map<String, List<DicValue>> getAllDic();
}
