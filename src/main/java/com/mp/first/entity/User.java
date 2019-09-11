package com.mp.first.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Dave
 */
@Data
public class User extends Model<User> {
    //主键
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private Long managerId;
    private LocalDateTime createTime;
    //备注，不在数据库中的字段
    //private transient String remark;
//    private static String remark;
//
//    public static String getRemark() {
//        return remark;
//    }
//
//    public static void setRemark(String remark) {
//        User.remark = remark;
//    }
    @TableField(exist = false)
    private String remark;
}
