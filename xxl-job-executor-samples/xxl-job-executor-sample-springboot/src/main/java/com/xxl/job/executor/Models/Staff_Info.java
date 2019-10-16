package com.xxl.job.executor.Models;

import lombok.Data;

@Data
public class Staff_Info {
    /**
     * 工号
     */
    KeyValueModel staff_number;
    /**
     * 姓名
     */
    KeyValueModel staff_name;
    /**
     * 成员
     */
    KeyValueModel staff_member;
    /**
     * 出生日期
     */
    KeyValueModel staff_birth;
    /**
     * 性别
     */
    KeyValueModel staff_gender;
    /**
     * 所属公司
     */
    KeyValueModel company;
    /**
     * 一级部门
     */
    KeyValueModel fir_depart;
    /**
     * 二级部门
     */
    KeyValueModel sec_depart;
    /**
     * 课
     */
    KeyValueModel keClass;
    /**
     * 班
     */
    KeyValueModel group;
    /**
     *班组
     */
    KeyValueModel team;
    /**
     * 岗位
     */
    KeyValueModel post;
    /**
     * 当前状态（在职/离职）
     */
    KeyValueModel staff_status;
    /**
     * 学历
     */
    KeyValueModel staff_edu;
    /**
     * 入职时间
     */
    KeyValueModel entry_time;
    /**
     * 离职时间
     */
    KeyValueModel separ_time;
    /**
     * 直接上长
     */
    KeyValueModel superior;
    /**
     * 领导层标志
     */
    KeyValueModel is_leader;

}
