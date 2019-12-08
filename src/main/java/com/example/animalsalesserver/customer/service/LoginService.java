package com.example.animalsalesserver.customer.service;

import com.example.animalsalesserver.customer.mapper.UserMapper;
import com.example.animalsalesserver.conf.RespVo;
import com.example.animalsalesserver.customer.po.UserPo;
import com.example.animalsalesserver.customer.qo.UserLoginQo;
import com.example.animalsalesserver.customer.qo.UserRegisterQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author wkj_pc
 * @date 2017/6/16
 */
@Slf4j
@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登陆
     *
     * @param userLoginQo
     * @param request
     * @return
     */
    public RespVo doLogin(UserLoginQo userLoginQo, HttpServletRequest request) {

        UserPo userPo = userMapper.getByMobileNum(userLoginQo.getMobileNum());
        if (null == userPo) {
            return RespVo.builder().code("201").msg("该账户不存在").build();
        } else if (!userPo.getPassword().equals(userLoginQo.getPassword())) {
            return RespVo.builder().code("201").msg("密码输入错误").build();
        }

        setLoginUser(userLoginQo, request);

        return RespVo.builder().code("200").msg("success").data(userPo).build();

    }

    /**
     * 用户修改密码
     *
     * @param userLoginQo
     * @return
     */
    public RespVo update(UserLoginQo userLoginQo) {

        UserPo userPo = userMapper.getByMobileNum(userLoginQo.getMobileNum());
        if (null == userPo) {
            return RespVo.builder().code("201").msg("该账户不存在").build();
        }
        userPo.setPassword(userLoginQo.getPassword());
        int count = userMapper.updateByAccount(userPo);
        if (count<1){
            return RespVo.builder().code("201").msg("系统开小差了，请稍后再试！").build();
        }
        return RespVo.builder().code("200").msg("success").data(userPo).build();

    }


    /**
     * 推出登陆
     *
     * @param userLoginQo
     * @param request
     * @return
     */
    public RespVo quitLogin(UserLoginQo userLoginQo, HttpServletRequest request) {
        Map<UserLoginQo, HttpSession> userMap = (Map<UserLoginQo, HttpSession>) request.getServletContext()
                .getAttribute("userLoginQo");
        if (userMap.containsKey(userLoginQo)) {
            HttpSession httpSession = userMap.get(userLoginQo);
            httpSession.invalidate();
            userMap.remove(userLoginQo);
        }
        return RespVo.builder().msg("success").code("200").build();
    }

    /**
     * 用户注册
     *
     * @param userRegisterQo
     * @return
     */
    public RespVo registerUser(UserRegisterQo userRegisterQo) {
        UserPo userPo = userMapper.getByMobileNum(userRegisterQo.getMobileNum());
        if (null != userPo){
            return RespVo.builder().code("201").msg("用户已经存在！").build();
        }
        userPo = UserPo.builder().account(userRegisterQo.getMobileNum()).nickName("tom").role(0).build();
        BeanUtils.copyProperties(userRegisterQo, userPo);
        userMapper.insert(userPo);

        return RespVo.builder().code("200").msg("success").build();
    }

    /**
     * 设置用户登陆信息
     *
     * @param loginUser
     * @param request
     */
    public void setLoginUser(UserLoginQo loginUser, HttpServletRequest request) {
        //现将之前的登录销毁
        Map<UserLoginQo, HttpSession> userMap = (Map<UserLoginQo, HttpSession>) request.
                getServletContext().getAttribute("userLoginQo");
        try {
            if (userMap.containsKey(loginUser)) {
                HttpSession httpSession = userMap.get(loginUser);
                if (null != httpSession) {
                    httpSession.invalidate();
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

        request.getSession().setAttribute("loginUser", loginUser);
        userMap.put(loginUser, request.getSession());
    }

}