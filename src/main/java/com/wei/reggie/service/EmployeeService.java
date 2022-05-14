package com.wei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wei.reggie.common.R;
import com.wei.reggie.entity.Employee;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpServletRequest;
@Service
public interface EmployeeService extends IService<Employee> {
    R<Employee> login(HttpServletRequest request, @RequestBody Employee employee);
}
