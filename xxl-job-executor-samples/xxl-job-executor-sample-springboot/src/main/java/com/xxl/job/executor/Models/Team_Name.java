package com.xxl.job.executor.Models;

import lombok.Data;

@Data
public class Team_Name implements Cloneable {
    /**
     * 公司名称
     */
    KeyValueModel company_Name;
    /**
     * 一级部门
     */
    KeyValueModel fir_Depart;
    /**
     * 二级部门
     */
    KeyValueModel sec_Depart;
    /**
     * 课
     */
    KeyValueModel t_Class;
    /**
     * 班
     */
    KeyValueModel group;
    /**
     * 班组名称
     */
    KeyValueModel team_Name;
    /**
     * 班组代码
     */
    KeyValueModel team_Code;
    /**
     * 负责人
     */
    KeyValueModel leaders;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Team_Name team_name=null;
        team_name=(Team_Name) super.clone();
        return team_name;
    }
}
