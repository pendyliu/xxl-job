package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.Team_Name;
import com.xxl.job.executor.core.config.HuoBanConfig;
import org.springframework.stereotype.Component;

@Component
public class HuobanServ<T> {
    static Team_Name team_name = new Team_Name();

    public void getSpaces() {
        String result = HttpRequest.get(HuoBanConfig.props.getProperty("HuoBanBaseURL") + "spaces/joined").
                header(Header.CONTENT_TYPE, "application/json").
                header(Header.HOST, "api.huoban.com")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().getStr("ticket"))
                .execute().body();
    }

    /**
     * 获取表结构
     *
     * @param tableId
     * @return
     */
    public static JSONObject getTables(String tableId) {
        String url = StrUtil.format(HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/table/{}", tableId);
        JSONObject result = JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.get(url).header(Header.CONTENT_TYPE, "application/json")
                .header(Header.HOST, "api.huoban.com")
                .header(Header.ACCEPT_CHARSET, "utf-8")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().getStr("ticket"))
                .execute().body()));
        return result;
    }

    /**
     * 从伙伴接口中获取字段ID映射表
     * @param t
     */
    public void setFieldsMap( Class<T> t) {
        try {
            IHuoBanService iHuoBanService = (IHuoBanService) t.newInstance();
//            JSONObject result = getTables(tableId);
            iHuoBanService.createFieldsIdMap();
        } catch (InstantiationException e) {
            XxlJobLogger.log(e.getMessage());
        } catch (IllegalAccessException e) {
            XxlJobLogger.log(e.getMessage());
        }
    }



}
