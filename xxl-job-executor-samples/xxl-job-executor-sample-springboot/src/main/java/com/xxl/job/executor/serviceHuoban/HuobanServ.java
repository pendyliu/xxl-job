package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import com.xxl.job.executor.Models.Team_Name;
import com.xxl.job.executor.core.config.HuoBanConfig;
import com.xxl.job.executor.service.jobhandler.PersonHbJobHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

@Component
public class HuobanServ<T>{
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

    /**
     * 从伙伴接口中获取字段ID映射表
     * @param tableId
     * @param t
     */
    public void setFieldsMap(String tableId,Class<T> t){
        try {

            IFieldsMap iFieldsMap= (IFieldsMap) t.newInstance();
            JSONObject result=getTables(tableId);
            iFieldsMap.createFieldsIdMap(result);
        } catch (InstantiationException e) {
            XxlJobLogger.log(e.getMessage());
        } catch (IllegalAccessException e) {
            XxlJobLogger.log(e.getMessage());
        }
    }

    /**
     * 获取ItemId到缓存中
     * @param tableId
     * @param keyValueModel
     */
//    public void setItemIdToCache(String tableId, KeyValueModel keyValueModel){
//        String url=HuoBanConfig.props.getProperty("HuoBanBaseURL")+"v2/item/table/{}/find";
//        JSONObject result=HttpRequest.post(StrUtil.format(url,tableId))
//                .header(Header.CONTENT_TYPE,"application/json")
//                .header("X-Huoban-Ticket",HuoBanConfig.getTicketJson().getStr("ticket"))
//                .body(JSONUtil.parse(keyValueModel))
//    }

}
