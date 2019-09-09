package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.Team_Name;
import com.xxl.job.executor.core.config.HuoBanConfig;
import com.xxl.job.executor.service.jobhandler.PersonHbJobHandler;
import org.springframework.stereotype.Component;

@Component
public class HuobanServ {
    static Team_Name team_name = new Team_Name();
    public void getSpaces() {
        String result = HttpRequest.get(HuoBanConfig.props.getProperty("HuoBanBaseURL")+"spaces/joined").
                header(Header.CONTENT_TYPE, "application/json").
                header(Header.HOST, "api.huoban.com")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().getStr("ticket"))
                .execute().body();
    }

    /**
     * 获取表结构
     * @param tableId
     * @return
     */
    public static JSONObject getTables(String tableId) {
        String url = StrUtil.format(HuoBanConfig.props.getProperty("HuoBanBaseURL")+"v2/table/{}", tableId);
        JSONObject result = JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.get(url).header(Header.CONTENT_TYPE, "application/json")
                .header(Header.HOST, "api.huoban.com")
                .header(Header.ACCEPT_CHARSET, "utf-8")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().getStr("ticket"))
                .execute().body()));
        return result;
    }

    public static void setFieldsMap(String tableId,IFieldsMap iFieldsMap){
        JSONObject result=getTables(HbTablesId.team_name);
        iFieldsMap.createFieldsIdMap(result);
    }

    public static void setDataToCacheTable(JSONObject jsonObject,IFieldsMap iFieldsMap){
        iFieldsMap.checkOrgIsExists(jsonObject);
    }


    /**
     * 班组信息字段匹配
     *
     * @param result
     */
    private static void fieldsMap(JSONObject result) {
        TeamNameImpl.setFildsMap(result,team_name);
        /**
         * 将表结构对象存放到缓存当中
         */
        PersonHbJobHandler.tableStuckCache.put("team_name", team_name);
    }


}
