package com.xxl.job.executor.service.jobhandler;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.executor.serviceHuoban.HuobanServ;
import com.xxl.job.executor.serviceHuoban.VantopServ;
import org.springframework.stereotype.Component;


@JobHandler(value = "PersonHbJobHandler")
@Component
public class PersonHbJobHandler extends IJobHandler {
    JSONObject ticketJson = null;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("GetHuoban");
        ticketJson = HuobanServ.getTicketJson();
        Object obj = VantopServ.PER_INF_WBS_ORG(DateUtil.format(DateTime.now(), "dd/MM/yyyy"),
                DateUtil.format(DateTime.now(), "dd/MM/yyyy"));
        SUCCESS.setMsg("获取Tick成功:" + ticketJson.getStr("ticket") + "有效期截止于：" + ticketJson.getStr("expire_at"));
        return SUCCESS;
    }

}
