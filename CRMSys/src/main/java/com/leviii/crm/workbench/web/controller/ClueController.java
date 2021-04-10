package com.leviii.crm.workbench.web.controller;

import com.leviii.crm.settings.domain.User;
import com.leviii.crm.settings.service.UserService;
import com.leviii.crm.settings.service.impl.UserServiceImpl;
import com.leviii.crm.utils.DateTimeUtil;
import com.leviii.crm.utils.PrintJson;
import com.leviii.crm.utils.ServiceFactory;
import com.leviii.crm.utils.UUIDUtil;
import com.leviii.crm.vo.PaginationVO;
import com.leviii.crm.workbench.domain.Activity;
import com.leviii.crm.workbench.domain.Clue;
import com.leviii.crm.workbench.domain.Tran;
import com.leviii.crm.workbench.service.ActivityService;
import com.leviii.crm.workbench.service.ClueService;
import com.leviii.crm.workbench.service.impl.ActivityServiceImpl;
import com.leviii.crm.workbench.service.impl.ClueServiceImpl;

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
 * @date 2021/1/30 10:32
 */
public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if ("/workbench/clue/getClueList.do".equals(path)){
            getClueList(request,response);
        }else if ("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        }else if ("/workbench/clue/unbind.do".equals(path)){
            unbind(request,response);
        }else if ("/workbench/clue/getActivityListNotBind.do".equals(path)){
            getActivityListNotBind(request,response);
        }else if ("/workbench/clue/bind.do".equals(path)){
            bind(request,response);
        }else if ("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        }else if ("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }

    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");
        User user = (User) request.getSession().getAttribute("user");
        Tran tran = null;
        if ("yes".equals(flag)){
            tran = new Tran();


            tran.setId(UUIDUtil.getUUID());
            tran.setMoney(request.getParameter("money"));
            tran.setName(request.getParameter("tradeName"));
            tran.setExpectedDate(request.getParameter("expectedDate"));
            tran.setStage(request.getParameter("stage"));
            tran.setActivityId(request.getParameter("activityId"));
            tran.setCreateBy(user.getName());
            tran.setCreateTime(DateTimeUtil.getSysTime());

        }

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Map<String,Object> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("tran",tran);
        map.put("user",user);

        boolean result = clueService.convert(map);

        if (result){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }


    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
            String activityName = request.getParameter("activityName");
            ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
            List<Activity> activities = activityService.getActivityListByName(activityName);
            PrintJson.printJsonObj(response,activities);
    }

    private void bind(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("clueId");
        String[] activityIds = request.getParameterValues("activityId");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bind(clueId,activityIds);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListNotBind(HttpServletRequest request, HttpServletResponse response) {
        String activityName = request.getParameter("activityName");
        String clueId = request.getParameter("clueId");
        Map<String,String> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = activityService.getActivityListNotBind(map);
        PrintJson.printJsonObj(response,activities);
    }

    private void unbind(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbind(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("clueId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,activityList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.getClueById(id);
        request.getSession().setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void getClueList(HttpServletRequest request, HttpServletResponse response) {

        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String mphone = request.getParameter("mphone");
        String source = request.getParameter("source");
        String owner = request.getParameter("owner");
        String phone = request.getParameter("phone");

        int pageNo = Integer.parseInt(pageNoStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<>();
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("mphone",mphone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("phone",phone);


        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        PaginationVO<Clue> vo = clueService.getClueList(map);
        PrintJson.printJsonObj(response,vo);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        Clue clue = new Clue();
        clue.setId(UUIDUtil.getUUID());
        clue.setFullname(request.getParameter("fullname"));
        clue.setAddress(request.getParameter("address"));
        clue.setAppellation(request.getParameter("appellation"));
        clue.setCompany(request.getParameter("company"));
        clue.setContactSummary(request.getParameter("contactSummary"));
        clue.setCreateBy(user.getName());
        clue.setCreateTime(DateTimeUtil.getSysTime());
        clue.setDescription(request.getParameter("description"));
        clue.setEmail(request.getParameter("email"));
        clue.setJob(request.getParameter("job"));
        clue.setOwner(request.getParameter("owner"));
        clue.setMphone(request.getParameter("mphone"));
        clue.setSource(request.getParameter("source"));
        clue.setNextContactTime(request.getParameter("nextContactTime"));
        clue.setPhone(request.getParameter("phone"));
        clue.setWebsite(request.getParameter("website"));
        clue.setState(request.getParameter("state"));

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = userService.getUserList();
        PrintJson.printJsonObj(response,users);
    }

}
