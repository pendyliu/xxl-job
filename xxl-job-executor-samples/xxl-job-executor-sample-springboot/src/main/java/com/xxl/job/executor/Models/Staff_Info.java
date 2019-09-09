package com.xxl.job.executor.Models;

import lombok.Data;

import java.util.Map;

@Data
public class Staff_Info {
    /**
     * 工号
     */
    KeyValueModel staff_number;
    /**
     * 姓名
     */
    Map<String,String> staff_name;
    /**
     * 成员
     */
    Map<String,String> staff_member;
    /**
     * 出生日期
     */
    Map<String,String> staff_birth;
    /**
     * 性别
     */
    Map<String,String> staff_gender;
    /**
     * 所属公司
     */
    Map<String,String> company;
    /**
     * 一级部门
     */
    Map<String,String> fir_depart;
    /**
     * 二级部门
     */
    Map<String,String> sec_depart;
    /**
     *班组
     */
    Map<String,String> team;
    /**
     * 岗位
     */
    Map<String,String> post;
    /**
     * 当前状态（在职/离职）
     */
    Map<String,String> staff_status;
    /**
     * 学历
     */
    Map<String,String> staff_edu;
    /**
     * 入职时间
     */
    Map<String,String> entry_time;
    /**
     * 离职时间
     */
    Map<String,String> separ_time;
    /**
     * 直接上长
     */
    Map<String,String> superior;

}
