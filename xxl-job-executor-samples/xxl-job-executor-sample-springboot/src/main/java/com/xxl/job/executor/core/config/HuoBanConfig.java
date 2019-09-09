package com.xxl.job.executor.core.config;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;

public class HuoBanConfig {
    public static Props props = new Props("application.properties");
    /**
     * 全局ticket
     */
    static JSONObject ticketJson = null;

    /**
     * 获取全局ticket
     * @return
     */
    public static JSONObject getTicketJson() {
        return (ticketJson == null || DateTime.now().isAfter(ticketJson.getDate("expire_at"))) ?
                getTicket() : ticketJson;
    }

    static JSONObject getTicket() {
        String ticketUrl = HuoBanConfig.props.getProperty("HuoBanBaseURL")+"v2/ticket";
        JSONObject paramJson = new JSONObject();
        paramJson.put("application_id", HuoBanConfig.props.getProperty("huoban.application_id"));
        paramJson.put("application_secret", HuoBanConfig.props.getProperty("huoban.application_secret"));
        paramJson.put("expired", HuoBanConfig.props.getProperty("huoban.expired"));

        ticketJson = JSONUtil.parseObj(JSONUtil.formatJsonStr(UnicodeUtil.toString(
                HttpRequest.post(ticketUrl)
                        .header("Content-Type", "application/json")
                        .body(paramJson.toString())
                        .execute().body())));
        return ticketJson;
    }
}
