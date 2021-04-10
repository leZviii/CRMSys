package com.leviii.crm.web.listener;

import com.leviii.crm.settings.domain.DicValue;
import com.leviii.crm.settings.service.DicService;
import com.leviii.crm.settings.service.impl.DicServiceImpl;
import com.leviii.crm.utils.ServiceFactory;
import com.leviii.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/**
 * @author leviii
 * @date 2021/1/30 15:01
 */
public class SysInitListener implements ServletContextListener {


    /**
     *  监听上下文域对象的创建时机,当上下文域对象创建完毕,马上执行此方法
     * @param event 创建出来的上下文对象
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();

        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> map = dicService.getAllDic();
        Set<String> set = map.keySet();
        for (String key : set){
            servletContext.setAttribute(key,map.get(key));
        }

        Map<String,String> result = new HashMap<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()){
            String key = keys.nextElement();
            String value = resourceBundle.getString(key);
            result.put(key,value);
        }
        servletContext.setAttribute("result",result);

    }

    /**
     * 监听上下文作用域对象的销毁时机
     * @param event 上下文对象
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("==========上下文作用域对象销毁!===========");
    }
}
