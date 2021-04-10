package com.leviii.crm.workbench.web.controller;

import com.leviii.crm.settings.domain.User;
import com.leviii.crm.settings.service.UserService;
import com.leviii.crm.settings.service.impl.UserServiceImpl;
import com.leviii.crm.utils.DateTimeUtil;
import com.leviii.crm.utils.PrintJson;
import com.leviii.crm.utils.ServiceFactory;
import com.leviii.crm.utils.UUIDUtil;
import com.leviii.crm.workbench.domain.Tran;
import com.leviii.crm.workbench.domain.TranHistory;
import com.leviii.crm.workbench.service.CustomerService;
import com.leviii.crm.workbench.service.TransactionService;
import com.leviii.crm.workbench.service.impl.CustomerServiceImpl;
import com.leviii.crm.workbench.service.impl.TransactionServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leviii
 * @date 2021/2/8 17:38
 */
public class TransactionController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if ("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if ("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/transaction/getHistoryListById.do".equals(path)){
            getHistoryListById(request,response);
        }else if ("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if ("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        TransactionService transactionService = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Map<String,Object> map = transactionService.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        Map<String,String> result = (Map<String, String>) request.getServletContext().getAttribute("result");
        User user = (User) request.getSession().getAttribute("user");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = user.getName();

        Tran tran = new Tran();
        tran.setId(id);
        tran.setStage(stage);
        tran.setEditBy(editBy);
        tran.setEditTime(editTime);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);
        tran.setPossibility(result.get(stage));

        TransactionService transactionService = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());

        boolean success = transactionService.changeStage(tran);


        Map<String,Object> map = new HashMap<>();
        map.put("success",success);
        map.put("tran",tran);

        PrintJson.printJsonObj(response,map);
    }

    private void getHistoryListById(HttpServletRequest request, HttpServletResponse response) {
        String tranId = request.getParameter("tranId");
        TransactionService transactionService = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        List<TranHistory> tranHistories = transactionService.getHistoryListById(tranId);
        Map<String,String> result = (Map<String, String>) request.getServletContext().getAttribute("result");
        for (TranHistory tranHistory : tranHistories){
            String stage = tranHistory.getStage();
            tranHistory.setPossibility(result.get(stage));
        }
        PrintJson.printJsonObj(response,tranHistories);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        TransactionService transactionService = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Tran tran = transactionService.detail(id);

        String stage = tran.getStage();
        Map<String,String> result = (Map<String,String>) this.getServletContext().getAttribute("result");
        String possibility = result.get(stage);
        tran.setPossibility(possibility);
        request.setAttribute("tran",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String customerName = request.getParameter("customerName");
        Tran tran = new Tran();
        tran.setId(UUIDUtil.getUUID());
        tran.setType(request.getParameter("type"));
        tran.setMoney(request.getParameter("money"));
        tran.setName(request.getParameter("name"));
        tran.setExpectedDate(request.getParameter("expectedDate"));
        tran.setStage(request.getParameter("stage"));
        tran.setActivityId(request.getParameter("activityId"));
        tran.setCreateBy(user.getName());
        tran.setCreateTime(DateTimeUtil.getSysTime());
        tran.setSource(request.getParameter("source"));
        tran.setOwner(request.getParameter("owner"));
        tran.setNextContactTime(request.getParameter("nextContactTime"));
        tran.setDescription(request.getParameter("description"));
        tran.setContactsId(request.getParameter("contactsId"));
        tran.setContactSummary(request.getParameter("contactSummary"));

        TransactionService transactionService = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        boolean success = transactionService.save(tran,customerName);
        if (success){
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }

    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        String name = request.getParameter("name");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> names = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response, names);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("userList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
