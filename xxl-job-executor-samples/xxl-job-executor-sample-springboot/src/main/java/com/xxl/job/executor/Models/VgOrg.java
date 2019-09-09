package com.xxl.job.executor.Models;

import lombok.Data;

import java.util.Map;


@Data
public class VgOrg {
    /**
     * 公司编码
     */
    private KeyValueModel BRANCH;
    /**
     * 公司中文名
     */
    private KeyValueModel BRANCH_DESCRIPTION;
    /**
     * 公司是否停用
     */
    private KeyValueModel BRANCH_END;
    /**
     * 一级部门
     */
    private KeyValueModel DEPARTMENT;
    /**
     * 一级部门中文
     */
    private KeyValueModel DEPARTMENT_DESCRIPTION;
    /**
     * 一级部门是否停用
     */
    private KeyValueModel DEPARTMENT_END;
    /**
     * 二级部门
     */
    private KeyValueModel SECTION;
    /**
     * 二级部门中文
     */
    private KeyValueModel SECTION_DESCRIPTION;
    /**
     * 二级部门是否停用
     */
    private KeyValueModel SECTION_END;
    /**
     * 课
     */
    private KeyValueModel SUB_SECTION;
    /**
     * 课中文
     */
    private KeyValueModel SUB_DESCRIPTION;
    /**
     * 课是否停用
     */
    private KeyValueModel SUB_SECTION_END;
    /**
     * 班
     */
    private KeyValueModel CITY;
    /**
     * 班中文
     */
    private KeyValueModel CITY_DESCRIPTION;
    /**
     * 班是否停用
     */
    private KeyValueModel CITY_END;
    /**
     * 班组
     */
    private KeyValueModel RANK;
    /**
     * 班组中文
     */
    private KeyValueModel RANK_DESCRIPTION;
    /**
     * 班组是否停用
     */
    private KeyValueModel RANK_END;
    /**
     * 生效日期
     */
    private KeyValueModel EFFECTIVE_DATE;
    /**
     * 失效日期
     */
    private KeyValueModel INVALID_DATE;
    /**
     *  岗位编码
     */
    private KeyValueModel POSITION;

    /**
     *  岗位名称中文
     */
    private KeyValueModel JOB_TITLE;
    /**
     * 岗位是否停用
     */
    private KeyValueModel POSITION_END;
    /**
     * 修改时间
     */
    private KeyValueModel MOD_DATE;

//    private String POSITION_CODE;


}
