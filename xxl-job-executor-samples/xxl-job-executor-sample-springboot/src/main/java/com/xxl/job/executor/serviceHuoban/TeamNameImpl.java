package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

/**
 * @author pendy
 */
public class TeamNameImpl extends BaseHuoBanServ implements IHuoBanService {
    static Team_Name team_name = new Team_Name();
    public static String rankItemsId = "rankItemsId";

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get("team_name") == null) {
            JSONObject jsonObject = getTables(HbTablesId.team_name);
            setFildsMap(jsonObject, team_name);
            /**
             * 将表结构对象存放到缓存当中
             */
            tableStuckCache.put("team_name", team_name);
        }
        if (tableStuckCache.get(rankItemsId) == null) {
            tableStuckCache.put(rankItemsId, new HashMap<>());
        }
    }

    @Override
    public Map<String, Object> getItemId(JSONObject paramJson, Element element) {
//        try {
//            JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                    .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
//            paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
//        } catch (Exception e) {
//            XxlJobLogger.log(e.getMessage());
//        }
//        return getItemsId(paramJson, this, element);
        Object itemId = ((Map<String, Object>) tableStuckCache.get(rankItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
        return itemFieldAndCnName;
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {

    }

    @Override
    public List<Team_Name> readStringXml(String xml) {
        List<Team_Name> team_names = new ArrayList<>();
        try {
            Document doc = null;
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml.replace(xml.substring(0, xml.indexOf("?>") + 2), ""));
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 拿到根节点的名称
            System.out.println("根节点：" + rootElt.getName());
            // 获取根节点下的子节点STAFF
            Iterator iter = rootElt.elementIterator("STAFF");
            // 遍历STAFF节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                // 获取子节点STAFF下的子节点ROW
                Iterator iters = recordEle.elementIterator("ROW");
                // 遍历ROW节点下的Response节点
                Team_Name teamNameStruc = (Team_Name) tableStuckCache.get("team_name");
                Company companyStruc = (Company) tableStuckCache.get("company");

                while (iters.hasNext()) {
                    Team_Name team_name = new Team_Name();
                    Company company = new Company();
                    team_name.setCompany_Name((KeyValueModel) teamNameStruc.getCompany_Name().clone());
                    team_name.setFir_Depart((KeyValueModel) teamNameStruc.getFir_Depart().clone());
                    team_name.setSec_Depart((KeyValueModel) teamNameStruc.getSec_Depart().clone());
                    team_name.setT_Class((KeyValueModel) teamNameStruc.getT_Class().clone());
                    team_name.setGroup((KeyValueModel) teamNameStruc.getGroup().clone());
                    team_name.setTeam_Code((KeyValueModel) teamNameStruc.getTeam_Code().clone());
                    team_name.setLeaders((KeyValueModel) teamNameStruc.getLeaders().clone());

                    company.setCompany_code((KeyValueModel) companyStruc.getCompany_code().clone());
                    company.setCompany_name((KeyValueModel) companyStruc.getCompany_name().clone());
                    company.setCompany_leaders((KeyValueModel) companyStruc.getCompany_leaders().clone());


                    Element itemEle = (Element) iters.next();
                    //把当前节点元素存放到缓存当中
                    tableStuckCache.put("itemEle", itemEle);
                    // 拿到STAFF下的子节点ROW下的字节点组织节点的值
                    team_name.getCompany_Name().setField_value(itemEle.elementText("BRANCH"));

                    String companyItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.comany).
                            put("field_id", ((Company) tableStuckCache.get("company")).getCompany_code().getField_id()).
                            put("field_value", itemEle.elementText("BRANCH"))
                            .put("fieldCnName", itemEle.elementTextTrim("BRANCH_DESCRIPTION")), new CompanyImpl(), itemEle);
                    team_name.getCompany_Name().setField_value(companyItemId);

                    team_name.getFir_Depart().setField_value(itemEle.elementText("DEPARTMENT"));
                    String firDepartMentItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.depment)
                            .put("field_id", ((Fir_Depart) tableStuckCache.get("fir_depart")).getDepart_code().getField_id())
                            .put("field_value", getOrgNodeName(itemEle, "DEPARTMENT")), new FirDepartMentImpl(), itemEle);
                    team_name.getFir_Depart().setField_value(firDepartMentItemId);

                    team_name.getSec_Depart().setField_value(itemEle.elementText("SECTION"));
                    String sectionItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.sec_depart)
                            .put("field_id", ((Sec_Depart) tableStuckCache.get("sec_depart")).getDepart_code().getField_id())
                            .put("field_value", getOrgNodeName(itemEle, "SECTION")), new Sec_DepartImpl(), itemEle);
                    team_name.getSec_Depart().setField_value(sectionItemId);

                    team_name.getT_Class().setField_value(itemEle.elementText("SUB_SECTION"));
                    String sub_sectionItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.sub_secdepart)
                            .put("field_id", ((KeClass) tableStuckCache.get("keClass")).getClass_code().getField_id())
                            .put("field_value", getOrgNodeName(itemEle, "SUB_SECTION")), new KeClassImpl(), itemEle);
                    team_name.getSec_Depart().setField_value(sub_sectionItemId);

                    team_name.getGroup().setField_value(itemEle.elementText("CITY"));
                    String cityItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.sub_secdepart)
                            .put("field_id", ((KeClass) tableStuckCache.get("keClass")).getClass_code().getField_id())
                            .put("field_value", getOrgNodeName(itemEle, "SUB_SECTION")), new KeClassImpl(), itemEle);
                    team_name.getT_Class().setField_value(cityItemId);

                    team_name.getTeam_Code().setField_value(itemEle.elementText("RANK"));
                    team_names.add(team_name);
//                    orgObj.setBRANCH(itemEle.elementTextTrim("BRANCH"));
//                    orgObj.setDEPARTMENT(itemEle.elementTextTrim("DEPARTMENT"));
//                    orgObj.setSECTION(itemEle.elementTextTrim("SECTION"));
//                    orgObj.setSUB_SECTION(itemEle.elementTextTrim("SUB_SECTION"));
//                    orgObj.setCITY(itemEle.elementTextTrim("CITY"));
//                    orgObj.setRANK(itemEle.elementTextTrim("RANK"));

//                    orgObj.setBRANCH_DESCRIPTION(itemEle.elementTextTrim("BRANCH_DESCRIPTION"));
//                    orgObj.setBRANCH_END(itemEle.elementTextTrim("BRANCH_END"));
//                    orgObj.setCITY_DESCRIPTION(itemEle.elementTextTrim("CITY_DESCRIPTION"));
//                    orgObj.setCITY_END(itemEle.elementTextTrim("CITY_END"));
//                    orgObj.setDEPARTMENT_DESCRIPTION(itemEle.elementTextTrim("DEPARTMENT_DESCRIPT"));
//                    orgObj.setDEPARTMENT_END(itemEle.elementTextTrim("DEPARTMENT_END"));
//                    orgObj.setEFFECTIVE_DATE(itemEle.elementTextTrim("EFFECTIVE_DATE"));
//                    orgObj.setINVALID_DATE(itemEle.elementTextTrim("INVALID_DATE"));
//                    orgObj.setJOB_TITLE(itemEle.elementTextTrim("JOB_TITLE"));
//                    orgObj.setMOD_DATE(itemEle.elementTextTrim("MOD_DATE"));
//                    orgObj.setPOSITION(itemEle.elementTextTrim("POSITION"));
//                    orgObj.setPOSITION_CODE(itemEle.elementTextTrim("POSITION_CODE"));
//                    orgObj.setPOSITION_END(itemEle.elementTextTrim("POSITION_END"));
//                    orgObj.setRANK_DESCRIPTION(itemEle.elementTextTrim("RANK_DESCRIPTION"));
//                    orgObj.setRANK_END(itemEle.elementTextTrim("RANK_END"));
//                    orgObj.setSECTION_DESCRIPTION(itemEle.elementTextTrim("SECTION_DESCRIPTION"));
//                    orgObj.setSECTION_END(itemEle.elementTextTrim("SECTION_END"));
//                    orgObj.setSUB_DESCRIPTION(itemEle.elementTextTrim("SUB_DESCRIPTION"));
//                    orgObj.setSUB_SECTION_END(itemEle.elementTextTrim("SUB_SECTION_END"));
                }
            }

        } catch (DocumentException e) {
            XxlJobLogger.log(e.getMessage());
        } catch (Exception e) {
            XxlJobLogger.log(e.getMessage());
        } finally {

        }
        return team_names;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.team_name;
        String firDepartCode = getOrgNodeName(element, "DEPARTMENT");
        String sec_depart_code = getOrgNodeName(element, "SECTION");
        String subSectionCode=getOrgNodeName(element,"SUB_SECTION");
        String groupCode=getOrgNodeName(element,"CITY");
        team_name = (Team_Name) tableStuckCache.get(rankItemsId);
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(team_name.getCompany_Name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(team_name.getFir_Depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).get(firDepartCode)).get("itemId")))
                .put(team_name.getSec_Depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).get(sec_depart_code)).get("itemId")))
                .put(team_name.getT_Class().getField_id(),JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(KeClassImpl.keClassItemsId))).get(subSectionCode)).get("itemId")))
                .put(team_name.getT_Class().getField_id(),JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(GroupImpl.groupItemsId))).get(groupCode)).get("itemId")))
                .put(team_name.getTeam_Code().getField_id(), getOrgNodeName(element, "RANK"))
                .put(team_name.getTeam_Name().getField_id(),getOrgNodeName(element,"RANK_DESCRIPTION"))
                .put(team_name.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        team_name = (Team_Name) tableStuckCache.get("team_name");
        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
                equals(team_name.getTeam_Name().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();
        String fieldCnName = getOrgNodeName(element, "RANK_DESCRIPTION");
        resultJson.put("fieldCnName", fieldCnName);
        if (!cnName.equals(fieldCnName)) {
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", team_name.getTeam_Code().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                    .put("data", JSONUtil.createObj().put(team_name.getTeam_Name().getField_id(), fieldCnName));
            //更新数据
            resultJson.put("rspStatus", updateTable(HbTablesId.team_name, paramJson));
        }
        return resultJson;
    }


    /**
     * 设置字段与字段ID的映射关系
     *
     * @param jsonObject
     * @param team_name
     */
    static void setFildsMap(JSONObject jsonObject, Team_Name team_name) {
        for (int i = 0; i < ((JSONArray) jsonObject.get("field_layout")).size(); i++) {
            for (Object objects : (JSONArray) jsonObject.get("fields")
                    ) {
                JSONArray field_layout = ((JSONArray) jsonObject.get("field_layout"));
                JSONObject field_id = ((JSONObject) objects);
                if (field_id.get("field_id").equals(field_layout.get(i))) {
                    KeyValueModel valueModel = new KeyValueModel();
                    valueModel.setField_id(field_id.get("field_id").toString());
                    switch (field_id.get("name").toString()) {
                        case "公司":
                            team_name.setCompany_Name(valueModel);
                            break;
                        case "一级部门":
                            team_name.setFir_Depart(valueModel);
                            break;
                        case "二级部门":
                            team_name.setSec_Depart(valueModel);
                            break;
                        case "课":
                            team_name.setT_Class(valueModel);
                            break;
                        case "班":
                            team_name.setGroup(valueModel);
                            break;
                        case "班组名称":
                            team_name.setTeam_Name(valueModel);
                            break;
                        case "班组代码":
                            team_name.setTeam_Code(valueModel);
                            break;
                        case "负责人(成员)":
                            team_name.setLeaders(valueModel);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }


}
