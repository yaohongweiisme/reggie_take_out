package com.wei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wei.reggie.common.R;
import com.wei.reggie.entity.Employee;
import com.wei.reggie.mapper.EmployeeMapper;
import com.wei.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        String password=employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());             //将密码进行md5加密
        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=this.getOne(queryWrapper);//用户名唯一

        if(emp==null) {                    //员工是否存在
            return R.error("登录失败,你尚未注册");
        }
        if(!emp.getPassword().equals(password)){             //密码是否正确
            return R.error("登录失败,密码错误");
        }

        if(emp.getStatus()==0){             //员工状态是不是已禁用
            return R.error("账号已禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());      //登录成功就把员工id存入Session
        return R.success(emp);
    }
}
