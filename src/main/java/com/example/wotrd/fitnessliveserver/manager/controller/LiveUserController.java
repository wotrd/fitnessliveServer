package com.example.wotrd.fitnessliveserver.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.wotrd.fitnessliveserver.manager.domain.LearnResource;
import com.example.wotrd.fitnessliveserver.manager.service.LiveService;
import com.example.wotrd.fitnessliveserver.tools.Page;
import com.example.wotrd.fitnessliveserver.tools.ServletUtil;
import com.example.wotrd.fitnessliveserver.tools.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tengj on 2017/3/13.
 */

@Controller
@RequestMapping("/manager/livemanager")
public class LiveUserController {
    @Autowired
    private LiveService liveService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/liveuser")
    public String liveuser(){
        return "live-user";
    }

    @RequestMapping(value = "/getLiveUsers",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public void queryLiveUserList(HttpServletRequest request ,HttpServletResponse response){
        String page = request.getParameter("page"); // 取得当前页数,注意这是jqgrid自身的参数
        String rows = request.getParameter("rows"); // 取得每页显示行数，,注意这是jqgrid自身的参数
        String account = request.getParameter("account");
        String nickname = request.getParameter("nickname");
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("page", page);
        params.put("rows", rows);
        params.put("account", account);
        params.put("nickname", nickname);
        Page pageObj =liveService.queryLiveUserList(params);
        List<Map<String, Object>> liveUserList=pageObj.getResultList();
        JSONObject jo=new JSONObject();
        jo.put("rows", liveUserList);
        jo.put("total", pageObj.getTotalPages());
        jo.put("records", pageObj.getTotalRows());
        ServletUtil.createSuccessResponse(200, jo, response);
    }
    /**
     * 新添教程
     * @param request
     * @param response
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public void addLearn(HttpServletRequest request , HttpServletResponse response){
        JSONObject result=new JSONObject();
        String author = request.getParameter("author");
        String title = request.getParameter("title");
        String url = request.getParameter("url");
        if(StringUtil.isNull(author)){
            result.put("message","作者不能为空!");
            result.put("flag",false);
            ServletUtil.createSuccessResponse(200, result, response);
            return;
        }
        if(StringUtil.isNull(title)){
            result.put("message","教程名称不能为空!");
            result.put("flag",false);
            ServletUtil.createSuccessResponse(200, result, response);
            return;
        }
        if(StringUtil.isNull(url)){
            result.put("message","地址不能为空!");
            result.put("flag",false);
            ServletUtil.createSuccessResponse(200, result, response);
            return;
        }
        LearnResource learnResouce = new LearnResource();
        learnResouce.setAuthor(author);
        learnResouce.setTitle(title);
        learnResouce.setUrl(url);
        int index=liveService.add(learnResouce);
        System.out.println("结果="+index);
        if(index>0){
            result.put("message","教程信息添加成功!");
            result.put("flag",true);
        }else{
            result.put("message","教程信息添加失败!");
            result.put("flag",false);
        }
        ServletUtil.createSuccessResponse(200, result, response);
    }
    /**
     * 修改教程
     * @param request
     * @param response
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public void updateLearn(HttpServletRequest request , HttpServletResponse response){
        JSONObject result=new JSONObject();
        String id = request.getParameter("id");
        LearnResource learnResouce=liveService.queryLearnResouceById(Long.valueOf(id));
        String author = request.getParameter("author");
        String title = request.getParameter("title");
        String url = request.getParameter("url");
        if(StringUtil.isNull(author)){
            result.put("message","作者不能为空!");
            result.put("flag",false);
            ServletUtil.createSuccessResponse(200, result, response);
            return;
        }
        if(StringUtil.isNull(title)){
            result.put("message","教程名称不能为空!");
            result.put("flag",false);
            ServletUtil.createSuccessResponse(200, result, response);
            return;
        }
        if(StringUtil.isNull(url)){
            result.put("message","地址不能为空!");
            result.put("flag",false);
            ServletUtil.createSuccessResponse(200, result, response);
            return;
        }
        learnResouce.setAuthor(author);
        learnResouce.setTitle(title);
        learnResouce.setUrl(url);
        int index=liveService.update(learnResouce);
        System.out.println("修改结果="+index);
        if(index>0){
            result.put("message","教程信息修改成功!");
            result.put("flag",true);
        }else{
            result.put("message","教程信息修改失败!");
            result.put("flag",false);
        }
        ServletUtil.createSuccessResponse(200, result, response);
    }
    /**
     * 删除教程
     * @param request
     * @param response
     */
    @RequestMapping(value="/delete",method = RequestMethod.POST)
    @ResponseBody
    public void deleteUser(HttpServletRequest request ,HttpServletResponse response){
        String ids = request.getParameter("ids");
        System.out.println("ids==="+ids);
        JSONObject result = new JSONObject();
        //删除操作
        int index = liveService.deleteByIds(ids);
        if(index>0){
            result.put("message","教程信息删除成功!");
            result.put("flag",true);
        }else{
            result.put("message","教程信息删除失败!");
            result.put("flag",false);
        }
        ServletUtil.createSuccessResponse(200, result, response);
    }
}