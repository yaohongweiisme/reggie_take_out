package com.wei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wei.reggie.common.R;
import com.wei.reggie.entity.Category;
import com.wei.reggie.entity.Employee;
import com.wei.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
            log.info("category:{}",category.toString());
            categoryService.save(category);
            return R.success("新增分类成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        log.info("分类管理page={},pageSize={}",page,pageSize);
        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        //添加排序文件
        queryWrapper.orderByDesc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    //删除菜品或者套餐分类
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除id为{}的分类",id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }
    //根据id修改分类信息
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category.toString());
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }
    //根据分类查询分类数据
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加等值查询条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件(优先按排序，然后按修改时间)
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list=categoryService.list(queryWrapper);
        return R.success(list);
    }
}
