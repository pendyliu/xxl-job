package com.xxl.job.executor.service.jobhandler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.Staff_Info;
import com.xxl.job.executor.serviceHuoban.HuobanServ;
import com.xxl.job.executor.serviceHuoban.StaffInfoImpl;
import org.springframework.stereotype.Component;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;
import static com.xxl.job.executor.serviceHuoban.StaffInfoImpl.staffInfoTbStruc;

@JobHandler(value = "LeaderJobHandler")
@Component
public class LeaderJobHandler extends IJobHandler {

    StaffInfoImpl staffInfoImpl = new StaffInfoImpl();
    HuobanServ huobanServ = new HuobanServ();

    void getNoMemberLeader() {
        try {
            Staff_Info staffInfo = (Staff_Info) tableStuckCache.get(staffInfoTbStruc);
            huobanServ.setFieldsMap(StaffInfoImpl.class);
            //获取是领导没有绑定伙伴成员帐号的人员名单
            JSONObject result = staffInfoImpl.getLeaderNoHbaccount();

            //遍历未绑定成员帐号的名单，待完善......
            for (int i = 0; i < result.size(); i++
                    ) {
                String phoneNumber = JSONUtil.parseObj(result.get(i)).getStr("phoneNumber");
                String memberId = staffInfoImpl.getMemberId(phoneNumber);
                if (memberId.length() > 0) {
                    JSONObject paramJson = JSONUtil.createObj().put("filter", JSONUtil.createObj().put("and", JSONUtil.createArray()
                            .put(JSONUtil.createObj().put("field", staffInfo.getStaff_member().getField_id())
                                    .put("query", JSONUtil.createObj().put("eq", "工号")))))
                            .put("data", JSONUtil.createObj().put(staffInfo.getStaff_member().getField_id(), "user_id"));
                    int res = staffInfoImpl.updateTable(HbTablesId.staff_info, paramJson);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            XxlJobLogger.log(e.getMessage());
        }
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        getNoMemberLeader();
        return ReturnT.SUCCESS;
    }
}
