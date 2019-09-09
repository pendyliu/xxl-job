package com.xxl.job.executor.service.jobhandler;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.Team_Name;
import com.xxl.job.executor.core.config.HuoBanConfig;
import com.xxl.job.executor.serviceHuoban.HuobanServ;
import com.xxl.job.executor.serviceHuoban.IFieldsMap;
import com.xxl.job.executor.serviceHuoban.TeamNameImpl;
import com.xxl.job.executor.serviceHuoban.VantopServ;
import org.springframework.stereotype.Component;

import java.util.List;


@JobHandler(value = "PersonHbJobHandler")
@Component
public class PersonHbJobHandler extends IJobHandler {
    public static Cache<String, Object> tableStuckCache = CacheUtil.newFIFOCache(6);
    IFieldsMap iFieldsMap;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("GetHuoban");
        iFieldsMap = new TeamNameImpl();
        //从伙伴获取表结构字段ID
        HuobanServ.setFieldsMap(HbTablesId.team_name, iFieldsMap);

        Object startDate = ((JSONObject) JSONUtil.parseObj(param).get("v_dates")).get("startDate");
        Object endDate = ((JSONObject) JSONUtil.parseObj(param).get("v_dates")).get("startDate");
        //从伙伴接口获取xml组织结构(当任务高度传进来的参数为空的时候取当前的日期，否则取参数日期)
        String xml = VantopServ.PER_INF_WBS_ORG(startDate == null ? DateUtil.format(DateTime.now(), "dd/MM/yyyy") : startDate.toString(),
                endDate == null ? DateUtil.format(DateTime.now(), "dd/MM/yyyy") : endDate.toString());
        //读取组织结构
       List<Team_Name> team_names= iFieldsMap.readStringXml(xml);

        iFieldsMap.checkOrgIsExists(JSONObject.class.newInstance().put("tableId", HbTablesId.team_name));

        SUCCESS.setMsg("获取Tick成功:" + HuoBanConfig.getTicketJson().getStr("ticket") + "有效期截止于："
                + HuoBanConfig.getTicketJson().getStr("expire_at"));
        return SUCCESS;
    }

}
