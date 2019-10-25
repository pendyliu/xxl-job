package com.xxl.job.executor.service.jobhandler;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateBetween;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.executor.Models.Team_Name;
import com.xxl.job.executor.core.config.HuoBanConfig;
import com.xxl.job.executor.serviceHuoban.*;
import org.springframework.stereotype.Component;

import java.util.List;


@JobHandler(value = "PersonHbJobHandler")
@Component
public class PersonHbJobHandler extends IJobHandler {
    public static Cache<String, Object> tableStuckCache = CacheUtil.newFIFOCache(30);
    IHuoBanService iHuoBanService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("GetHuoban");
        HuobanServ huobanServ = new HuobanServ();
        iHuoBanService = new TeamNameImpl();
        Object startDate=null;
        Object endDate=null;
        //获取所有模型字段ID对照表存储到缓存中
        setAllFieldsMap(huobanServ);
        if (!param.contains("--")){
            startDate = ((JSONObject) JSONUtil.parseObj(param).get("v_dates")).get("startDate");
            endDate = ((JSONObject) JSONUtil.parseObj(param).get("v_dates")).get("startDate");
        }
        //
//        startDate = "02/08/2019";
//        while (DateUtil.parse(startDate.toString(), "dd/MM/yyyy").before(DateTime.now())) {
//            endDate = startDate;
//            DateTime startTime = DateTime.now();
//            String xml = VantopServ.PER_INF_WBS_ORG(startDate.toString(), endDate.toString());
//            Long times = new DateBetween(startTime, DateTime.now()).between(DateUnit.SECOND);
//            System.out.println("读取组织信息接口耗时：" + times.toString());
//            List<Team_Name> team_names = iHuoBanService.readStringXml(xml);
//            startTime = DateTime.now();
//            String personXml = VantopServ.PER_INF_WBS_STAFF(startDate.toString(), endDate.toString());
//            times = new DateBetween(startTime, DateTime.now()).between(DateUnit.SECOND);
//            System.out.println("读取"+startDate+"人员信息接口耗时：" + times.toString());
//            new StaffInfoImpl().readStringXml(personXml);
//            startDate = DateUtil.format(DateUtil.addDays(DateUtil.parse(startDate.toString(), "dd/MM/yyyy"), 1), "dd/MM/yyyy");
//        }

        //从伙伴接口获取xml组织结构(当任务高度传进来的参数为空的时候取当前的日期，否则取参数日期)
        String xml = VantopServ.PER_INF_WBS_ORG(startDate == null ? DateUtil.format(DateTime.now(), "dd/MM/yyyy") : startDate.toString(),
                endDate == null ? DateUtil.format(DateTime.now(), "dd/MM/yyyy") : endDate.toString());
        //读取组织结构
        List<Team_Name> team_names = iHuoBanService.readStringXml(xml);

        //读取人员异动信息
        String personXml = VantopServ.PER_INF_WBS_STAFF(startDate == null ? DateUtil.format(DateTime.now(), "dd/MM/yyyy") : startDate.toString(),
                endDate == null ? DateUtil.format(DateTime.now(), "dd/MM/yyyy") : endDate.toString());
        new StaffInfoImpl().readStringXml(personXml);

        SUCCESS.setMsg("获取Tick成功:" + HuoBanConfig.getTicketJson().getStr("ticket") + "有效期截止于："
                + HuoBanConfig.getTicketJson().getStr("expire_at"));
        return SUCCESS;
    }

    /**
     * 获取所有模型字段ID对照表存储到缓存中
     *
     * @param huobanServ
     */
    private void setAllFieldsMap(HuobanServ huobanServ) {
        //从伙伴获取班组表结构字段ID
        huobanServ.setFieldsMap(TeamNameImpl.class);
        //从伙伴接口获取公司表结构字段ID
        huobanServ.setFieldsMap(CompanyImpl.class);
        //从伙伴接口获取一级部门表结构字段ID
        huobanServ.setFieldsMap(FirDepartMentImpl.class);
        //从伙伴接口获取二级部门表结构字段ID
        huobanServ.setFieldsMap(Sec_DepartImpl.class);
        huobanServ.setFieldsMap(GroupImpl.class);
        huobanServ.setFieldsMap(KeClassImpl.class);
        huobanServ.setFieldsMap(TeamNameImpl.class);
        huobanServ.setFieldsMap(StaffInfoImpl.class);
        huobanServ.setFieldsMap(PostNameImpl.class);
    }

}
