package com.mp.first;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.LambdaUpdateChainWrapper;
import com.mp.first.dao.UserMapper;
import com.mp.first.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FirstApplicationTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void select(){
        //条件为Null查询全部
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5,userList.size());
        userList.forEach(System.out::println);
    }
    @Test
    public void insert(){
        User user = new User();
        user.setName("bbb");
        user.setAge(22);
        user.setManagerId(1088248166370832385L);
        user.setCreateTime(LocalDateTime.now());
        user.setRemark("备注信息");
        int result = userMapper.insert(user);
        System.out.println(result);
    }
    @Test
    public void selectById(){
        User user = userMapper.selectById(1094590409767661570L);
        System.out.println(user);
    }

    @Test
    public void selectByBatchId(){
        List<Long> ids = Arrays.asList(1088248166370832385L,1088250446457389058L);
        List<User> userList = userMapper.selectBatchIds(ids);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectByMap(){
        Map<String,Object> selectMap = new HashMap<>();
        //列名(对应数据库中)，对应列的值
        selectMap.put("name","王天风");
        selectMap.put("age",25);
        List<User> userList = userMapper.selectByMap(selectMap);
        userList.forEach(System.out::println);
    }

    /**
     * 条件构造器
     */
    @Test
    public void selectWrapper1(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //QueryWrapper<User> query = Wrappers.query();
        /**name like %雨% and age < 40*/
        wrapper.like("name","雨").lt("age",40);
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    @Test
    public void selectWrapper2(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name","雨").between("age",20,40).isNotNull("email");
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectWrapper3(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.likeRight("name","王").or().ge("age",25).orderByDesc("age")
                .orderByAsc("id");
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectWrapper4(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.apply("date_format(create_time,'%Y-%m-%d') = {0}", "2019-02-14")
                //子查询嵌套
                .inSql("manager_id","select id from user where name like '王%'");
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectWrapper5(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.likeRight("name","王").and(wq -> wq.lt("age",40).or()
        .isNotNull("email"));
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectWrapper6(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.likeRight("name","王").or(wq -> wq.lt("age",40).gt("age",20)
                .isNotNull("email"));
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectWrapper7(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        /**针对非and开头的情况*/
        wrapper.nested(wq -> wq.lt("age",40).or().isNotNull("email"))
                .likeRight("name","王");
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    @Test
    public void selectWrapper8(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        /**in*/
        wrapper.in("age",Arrays.asList(30,31,34,35));
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    @Test
    public void selectWrapper9(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        /**limit,可能有sql注入风险*/
        wrapper.in("age",Arrays.asList(30,31,34,35)).last("limit 1");
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    /**
     * 查询部分列
     */
    @Test
    public void selectPart(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("id","name").like("name","雨").lt("age",40);
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    @Test
    public void selectPart2(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name","雨").lt("age",40)
        .select(User.class,info -> !info.getColumn().equals("create_time")
                &&!info.getColumn().equals("manager_id"));
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    /**
     * condition
     * 可选查询条件
     */
    private void getCondition(String name,String email){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        if(StringUtils.isNotEmpty(name)){
//            wrapper.like("name",name);
//        }
//        if (StringUtils.isNotEmpty(email)){
//            wrapper.like("email",name);
//        }
        wrapper.like(StringUtils.isNotEmpty(name),"name",name)
                .like(StringUtils.isNotEmpty(email),"email",email);
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    @Test
    public void testCondition(){
        String name = "王";
        String email = "";
        getCondition(name,email);
    }
    /**
     * 条件构造器传入实体
     */
    @Test
    public void selectByEntity(){
        User user = new User();
        user.setName("刘红雨");
        user.setAge(32);
        QueryWrapper<User> wrapper = new QueryWrapper<>(user);
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }
    /**
     * allEq
     */
    @Test
    public void selectByAllEq(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        Map<String,Object> param = new HashMap<>();
        param.put("name","王天风");
        //param.put("age",25);
        //拼接xxx is NULL
        param.put("age",null);
        //wrapper.allEq(param,false);
        //过滤条件
        wrapper.allEq((k,v) -> !k.equals("name"),param);
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    @Test
    public void selectWrapperMap(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name","雨").lt("age",40).select("id","name");
        List<Map<String,Object>> userList = userMapper.selectMaps(wrapper);
        userList.forEach(System.out::println);
    }

    /**
     * 实现一个分组统计
     */
    @Test
    public void selectWrapperMap2(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("avg(age) avg_age","min(age) min_age","max(age) max_age")
        .groupBy("manager_id").having("sum(age)<{0}",500);
        List<Map<String,Object>> userList = userMapper.selectMaps(wrapper);
        userList.forEach(System.out::println);
    }

    /**
     * 只返回第一列，不确定类型
     */
    @Test
    public void selectWrapperObjs(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name","雨").lt("age",40).select("id","name");
        List<Object> userList = userMapper.selectObjs(wrapper);
        userList.forEach(System.out::println);
    }
    /**
     * 查询总记录数
     */
    @Test
    public void selectWrapperCount(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name","雨").lt("age",40);
        Integer count = userMapper.selectCount(wrapper);
        System.out.println(count);
    }
    /**
     * 查询一条记录
     */
    @Test
    public void selectWrapperOne(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name","刘红雨").lt("age",40);
        User user = userMapper.selectOne(wrapper);
        System.out.println(user);
    }
    /**
     * lambda条件构造器
     * 防误写
     */
    @Test
    public void selectLambda(){
//        LambdaQueryWrapper<User> lambda = new QueryWrapper<User>().lambda();
//        LambdaQueryWrapper<User> lambda = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> lambda = Wrappers.lambdaQuery();
        lambda.like(User::getName,"雨").lt(User::getAge,40);
        List<User> userList = userMapper.selectList(lambda);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectLambda2(){
        LambdaQueryWrapper<User> lambda = Wrappers.lambdaQuery();
        lambda.likeRight(User::getName,"王").and(lq -> lq.lt(User::getAge,40).or()
                .isNotNull(User::getEmail));
        List<User> userList = userMapper.selectList(lambda);
        userList.forEach(System.out::println);
    }
    @Test
    public void selectLambda3(){
        List<User> userList = new LambdaQueryChainWrapper<User>(userMapper)
                .like(User::getName,"雨").ge(User::getAge,20).list();
        userList.forEach(System.out::println);
    }

    @Test
    public void selectMy(){
        LambdaQueryWrapper<User> lambda = Wrappers.lambdaQuery();
        lambda.likeRight(User::getName,"王").and(lq -> lq.lt(User::getAge,40).or()
                .isNotNull(User::getEmail));
        List<User> userList = userMapper.selectAll(lambda);
        userList.forEach(System.out::println);
    }

    /**
     * 分页查询
     */
    @Test
    public void selectPage(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ge("age",20);
        //分页
        Page<User> page = new Page<>(1,2);
        IPage<User> iPage = userMapper.selectPage(page,wrapper);

        //总页数
        System.out.println(page.getPages());
        //记录数
        System.out.println(page.getTotal());
        List<User> userList = page.getRecords();
        userList.forEach(System.out::println);
    }
    @Test
    public void selectMapPage(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ge("age",20);
        //分页
        Page<User> page = new Page<>(1,2);
        IPage<Map<String,Object>> iPage = userMapper.selectMapsPage(page,wrapper);

        //总页数
        System.out.println(iPage.getPages());
        //记录数
        System.out.println(iPage.getTotal());
        List<Map<String, Object>> userList = iPage.getRecords();
        userList.forEach(System.out::println);
    }

    @Test
    public void selectMapPage2(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ge("age",20);
        //分页
        Page<User> page = new Page<>(1,2,false);
        IPage<Map<String,Object>> iPage = userMapper.selectMapsPage(page,wrapper);

        //总页数
        System.out.println(iPage.getPages());
        //记录数
        System.out.println(iPage.getTotal());
        List<Map<String, Object>> userList = iPage.getRecords();
        userList.forEach(System.out::println);
    }

    @Test
    public void selectMyPage(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ge("age",20);
        //分页
        Page<User> page = new Page<>(1,2);
        IPage<User> iPage = userMapper.selectMyPage(page,wrapper);

        //总页数
        System.out.println(iPage.getPages());
        //记录数
        System.out.println(iPage.getTotal());
        List<User> userList = iPage.getRecords();
        userList.forEach(System.out::println);
    }

    /**
     * update
     */
    @Test
    public void updateById(){
        User user = new User();
        user.setId(1088248166370832385L);
        user.setAge(26);
        int result = userMapper.updateById(user);
        System.out.println(result);
    }

    @Test
    public void updateByWrapper(){
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("name","李艺伟").eq("age",28);
        User user = new User();
        user.setAge(29);
        int result = userMapper.update(user,wrapper);
        System.out.println(result);
    }

    @Test
    public void updateByWrapper2(){
        User user2 = new User();
        user2.setName("李艺伟");
        UpdateWrapper<User> wrapper = new UpdateWrapper<>(user2);
        wrapper/*.eq("name","李艺伟")*/.eq("age",28);
        User user = new User();
        user.setAge(29);
        int result = userMapper.update(user,wrapper);
        System.out.println(result);
    }

    /**
     * 只更新一个字段的情况
     */
    @Test
    public void updateByWrapper3(){
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("name","李艺伟").eq("age",29).set("age",30);
        int result = userMapper.update(null,wrapper);
        System.out.println(result);
    }
    @Test
    public void updateByWrapper4(){
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getName,"李艺伟").eq(User::getAge,29).set(User::getAge,31);
        int result = userMapper.update(null,wrapper);
        System.out.println(result);
    }
    @Test
    public void updateByWrapper5(){
        boolean wrapper = new LambdaUpdateChainWrapper<>(userMapper).
                eq(User::getName,"李艺伟").eq(User::getAge,29).set(User::getAge,31).update();
        System.out.println(wrapper);
    }
    /**
     * delete
     */
    @Test
    public void deleteById(){
        int result = userMapper.deleteById(1171261594594652161L);
        System.out.println(result);
    }
    @Test
    public void deleteByMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","aaa");
        map.put("age",20);
        int result = userMapper.deleteByMap(map);
    }
    /**
     * 根据id批量删除
     */
    public void deleteBatchByIds(){
        int result = userMapper.deleteBatchIds(Arrays.asList(11l,22l,33l));
        System.out.println(result);
    }
    @Test
    public void deleteByWrapper(){
        //先查后删
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAge,"27").or().gt(User::getAge,"41");
        int result = userMapper.delete(wrapper);
        System.out.println(result);
    }
    /**
     * ActiveRecord
     * 一个model对应一张数据表
     * 1.实体对象需要继承Model<T>抽象类
     * 2.对应Mapper需要继承BaseMapper<T>
     */
    @Test
    public void arInsert(){
        User user = new User();
        user.setName("asd");
        user.setAge(23);
        user.setManagerId(1088248166370832385L);
        user.setCreateTime(LocalDateTime.now());
        boolean flag = user.insert();
        System.out.println(flag);
    }
    @Test
    public void arSelectById(){
        User user = new User();
        User selectUser = user.selectById(1171709499679862785L);
        System.out.println(selectUser);
    }

    @Test
    public void arSelectById2(){
        User user = new User();
        user.setId(1171709499679862785L);
        User selectUser = user.selectById();
        System.out.println(selectUser);
    }
    @Test
    public void arUpdate(){
        User user = new User();
        user.setId(1171709499679862785L);
        user.setName("Tom");
        boolean flag = user.updateById();
        System.out.println(flag);
    }
    @Test
    public void arDelete(){
        User user = new User();
        user.setId(1171709499679862785L);
        boolean flag = user.deleteById();
        System.out.println(flag);
    }
    @Test
    public void arInsertOrUpdate(){
        User user = new User();
        user.setName("asd");
        user.setAge(23);
        user.setManagerId(1088248166370832385L);
        user.setCreateTime(LocalDateTime.now());
        boolean flag = user.insertOrUpdate();
        System.out.println(flag);
    }

    /**
     * 通用service
     */


}
