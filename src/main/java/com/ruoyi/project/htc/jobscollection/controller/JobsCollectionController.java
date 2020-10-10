package com.ruoyi.project.htc.jobscollection.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.htc.jobscollection.domain.JobsCollection;
import com.ruoyi.project.htc.jobscollection.service.IJobsCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruoyi.common.utils.ExcpUtil.stacktraceToString;
import static com.ruoyi.common.utils.ExcpUtil.wrapPrintStackDepth;

/**
 * 工作记录Controller
 *
 * @author htc
 * @date 2020-05-22
 */
@Controller
@RequestMapping("/htc/jobscollection")
@Slf4j
public class JobsCollectionController extends BaseController
{
    private String prefix = "htc/jobscollection";

    @Autowired
    private IJobsCollectionService jobsCollectionService;

    @Autowired
    private TaskExecutor taskExecutor;

    @RequiresPermissions("htc:jobscollection:view")
    @GetMapping()
    public String jobscollection()
    {
        return prefix + "/jobscollection";
    }

    /**
     * 查询工作记录列表
     */
    @RequiresPermissions("htc:jobscollection:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(JobsCollection jobsCollection)
    {
        startPage();
        List<JobsCollection> list = jobsCollectionService.selectJobsCollectionList(jobsCollection);
        return getDataTable(list);
    }

    /**
     * 导出工作记录列表
     */
    @RequiresPermissions("htc:jobscollection:export")
    @Log(title = "工作记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(JobsCollection jobsCollection)
    {
        List<JobsCollection> list = jobsCollectionService.selectJobsCollectionList(jobsCollection);
        ExcelUtil<JobsCollection> util = new ExcelUtil<JobsCollection>(JobsCollection.class);
        return util.exportExcel(list, "jobscollection");
    }

    /**
     * 新增工作记录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存工作记录
     */
    @RequiresPermissions("htc:jobscollection:add")
    @Log(title = "工作记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(JobsCollection jobsCollection)
    {
        jobsCollection.setCreateDate(new Date());
        jobsCollection.setCreateBy(String.valueOf(ShiroUtils.getUserId()));
        jobsCollection.setUpdateDate(new Date());
        jobsCollection.setUpdateBy(String.valueOf(ShiroUtils.getUserId()));
        return toAjax(jobsCollectionService.insertJobsCollection(jobsCollection));
    }

    /**
     * 修改工作记录
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        JobsCollection jobsCollection = jobsCollectionService.selectJobsCollectionById(id);
        mmap.put("jobsCollection", jobsCollection);
        return prefix + "/edit";
    }

    /**
     * 修改保存工作记录
     */
    @RequiresPermissions("htc:jobscollection:edit")
    @Log(title = "工作记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(JobsCollection jobsCollection)
    {
        return toAjax(jobsCollectionService.updateJobsCollection(jobsCollection));
    }

    /**
     * 删除工作记录
     */
    @RequiresPermissions("htc:jobscollection:remove")
    @Log(title = "工作记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(jobsCollectionService.deleteJobsCollectionByIds(ids));
    }

    @PostMapping("/getZlDetail")
    @ResponseBody
    public String getZlDetail(String detailUrl, String isSave) {
        Map<String, String> headers = new HashMap<>();
        headers.put("referer", "https://sou.zhaopin.com/?jl=681&sf=0&st=0&we=0305&et=2&kw=java&kt=3");
        headers.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        String cookie = String.valueOf(CacheUtils.get(Constants.SYS_CONFIG_CACHE, Constants.SYS_CONFIG_KEY + "htc.jobcollections.zlcookie"));
        headers.put("cookie", cookie);
        String result = HttpUtils.sendGetRecvGzip(detailUrl.split("\\?")[0], detailUrl.split("\\?")[1], Constants.UTF8, headers, false);
        if (log.isDebugEnabled()) {
            log.debug("\n██cookie：{}", cookie);
        }
        Map<String, String> resultMap = new HashMap<>();
        if (result.contains("DOCTYPE")) {
            //成功获取html
            String result1 = result;
            result = deal(result);
            resultMap.put("code", "0");
            resultMap.put("data", result);

            JobsCollection jobsCollection = new JobsCollection();
            BeanUtil.copyProperties(dealToMap(result1, detailUrl), jobsCollection);
            if (log.isDebugEnabled()) {
                log.debug("\n██jobscollection：{}", JSONUtil.toJsonStr(jobsCollection));
            }
            int count = jobsCollectionService.selectCount(jobsCollection);
            resultMap.put("isSave", count == 0 ? "1" : "0");

            taskExecutor.execute(()->{
                if (count == 0) {
                    if ("1".equals(isSave)) {
                        jobsCollectionService.insertJobsCollection(jobsCollection);
                    }
                }
            });
        } else {
            resultMap.put("code", "500");
        }

        return JSONUtil.toJsonStr(resultMap);
    }

    private Map<String, Object> dealToMap(String html, String detailUrl) {
        Map<String, Object> result = new HashMap<>();

        //提取__INITIAL_STATE__之后的文本，转换成json
        String splitStr = "__INITIAL_STATE__";
        String str = html.split(splitStr)[1];
        int startIndex = str.indexOf("{");
        str = str.substring(startIndex);
        int endIndex = str.indexOf("</script>");
        str = str.substring(0, endIndex);
        if (log.isDebugEnabled()) {
            log.debug("\n██提取到的数据：{}", str);
        }
        try {
            JSONObject object = JSONObject.parseObject(str);
            String jobNumber = object.getString("jobNumber");
            JSONObject jobDetail = object.getJSONObject("jobInfo").getJSONObject("jobDetail");
            result.put("companyname", jobDetail.getJSONObject("detailedCompany").getString("companyName"));
            result.put("companyaddr", jobDetail.getJSONObject("detailedPosition").getString("workAddress"));
            //result.put("email", object.getString(""));
            result.put("sendmode", "99");
            //result.put("create_by", ShiroUtils.getLoginName());
            result.put("create_date", new Date());
            result.put("del_flag", "0");
            result.put("url", detailUrl);
            result.put("job_number", jobNumber);
            //result.put("home_call", detailUrl);
            //result.put("phone", detailUrl);
            //result.put("linkman", detailUrl);

            if (log.isDebugEnabled()) {
                log.debug("\n██封装之后的数据：{}", JSONUtil.toJsonStr(result));
            }
            return result;
        } catch (Exception e) {
            log.error("\n██{}\n转换json异常:{}", str, stacktraceToString(wrapPrintStackDepth(e)));
        }

        return result;
    }

    private String deal(String result) {
        Document doc = Jsoup.parse(result);
        Elements select = doc.select("div.job-detail");

        return select.outerHtml();
    }

    public static void main(String[] args) {
        JobsCollectionController controller = new JobsCollectionController();
        JobsCollection jobsCollection = new JobsCollection();
        String result1 = "__INITIAL_STATE__{\"jobNumber\":\"CC340066535J90250052000\",\"reportData\":{\"srccode\":\"\",\"srcid\":\"\",\"traceid\":\"\",\"refcode\":\"\",\"eptReason\":\"\",\"actionid\":\"975ffa66-1a33-4f58-b7cb-b4194f6e2d12\",\"funczone\":402001},\"user\":{\"baseInfo\":{\"name\":\"\",\"id\":null,\"cityDistrictId\":null,\"cityName\":null,\"cityId\":null},\"resumes\":{},\"isPageOpen\":false,\"userCityPage\":{},\"ipCity\":null},\"jobInfo\":{\"jobSummary\":{},\"jobDetail\":{\"detailedCompany\":{\"companyLogo\":\"https://storage-public.zhaopin.cn/CompanyLogo/20171107/17589EE104F0477BAD387B5924B14101.jpg\",\"videoUrl\":\"\",\"industry\":\"计算机软件,IT服务(系统/数据/维护),通信/电信运营、增值服务,互联网/电子商务\",\"industryLevel\":\"计算机软件,IT服务,运营商/ 增值服务,互联网\",\"companySize\":\"100-299人\",\"companyDescription\":\"福州探索网络科技有 限公司是一家集软件开发、系统集成、IT服务外包、大数据挖掘、互联网开发和信息服务等业务的高科技公司。公司长期与电信运营商、上市公司、地产公司等大中型企业合作，参与各类大型IT、互联网产品项目的开发和建设。始终着眼于用户的需求，至力于成为本地乃至全国出色的企业信息系统化管理软件及系统管理集成解决方案的供应商。探索网络欢迎各类优秀人才的加盟，公司将为您提供广阔的发展空间和实现梦想的锻炼机会。  \",\"companyUrl\":\"http://company.zhaopin.com/CZ340066530.htm\",\"companyName\":\"福州探索网络科技有限公司\",\"companyNumber\":\"CZ340066530\",\"menVipUrl\":\"\",\"menVipLevel\":0,\"industryCode\":\"160400,160000,160100,210500\",\"url\":\"http://company.zhaopin.com/CZ340066530.htm\",\"bestEmployerType\":0},\"detailedPosition\":{\"companyName\":\"福州探索网络科技有限公司\",\"companyNumber\":\"CZ340066530\",\"menVipUrl\":\"\",\"menVipLevel\":0,\"industryCode\":\"160400,160000,160100,210500\",\"bestEmployerType\":0,\"bestEmployerLabel\":[],\"welfareLabel\":[{\"state\":0,\"value\":\"五险一金\"},{\"state\":0,\"value\":\"年底双薪\"},{\"state\":0,\"value\":\"定期体检\"},{\"state\":0,\"value\":\"节日福利\"}],\"subJobType\":\"2040\",\"jobStatus\":3,\"skillLabel\":[],\"jobDescPC\":\"<p>岗位职责：</p><p>1、负责金融、电信、保险、物联网平台等业务开发</p><p><br></p><p>任职要求：</p><p>1. 2年以上Java开发经验；</p><p>2. 熟悉MVC编程思想及相关技术；</p><p>3. 掌握至少一种关系型数据库开发；</p><p>4. 有高并发、高负载、高可用性系统设计及数据分析开发经验优先考虑；</p><p>5. 具有良好的沟通能力和团队合作精神、优秀的分析问题和解决问题的能力。</p><p><br></p>\",\"workAddress\":\"福州市区、马尾等\",\"latitude\":\"26.074507\",\"longitude\":\"119.296494\",\"publishTime\":\"2020-05-27 09:01:07\",\"education\":\"大专\",\"emailListPc\":\"\",\"applyType\":\"1\",\"futureJob\":false,\"salary60\":\"8千-1.3万\",\"workingExp\":\"3-5年\",\"cityId\":\"681\",\"workCity\":\"福州\",\"recruitNumber\":5,\"number\":\"CC340066535J90250052000\",\"name\":\"Java后端开发\",\"cityDistrict\":\"\",\"chatWindow\":1,\"staff\":{\"avatar\":\"https://rd5-public.zhaopin.cn/imgs/avatar_f7.png\",\"department\":\"\",\"hrJob\":\"人事经理\",\"hrOnlineIocState\":0,\"hrOnlineState\":\"昨日活跃\",\"hrResumeOperationState\":\"处理简历极快\",\"id\":685060382,\"nickName\":\"\",\"passportName\":\"***\",\"staffName\":\"张先生\",\"state\":1},\"futureJobUrl\":\"\",\"url\":\"//www.zhaopin.com/fuzhou/\",\"jobDesc\":\"<p>岗位职责：</p><p>1、负 责金融、电信、保险、物联网平台等业务开发</p><p><br></p><p>任职要求：</p><p>1. 2年以 上Java开发经验；</p><p>2. 熟悉MVC编程思想及相关技术；</p><p>3. 掌握至少一种关系型数 据库开发；</p><p>4. 有高并发、高负载、高可用性系统设计及数据分析开发经验优先考虑；</p><p>5. 具有良好的沟通能力和团队合作精神、优秀的分析问题和解决问题的能力。</p><p><br></p>\"},\"taskId\":\"92811224f8de44ea9f3123914ba46415\"},\"jobPublisher\":{},\"jobNumber\":\"\"},\"jobSimilar\":{\"jobSimilarList\":[],\"jobCount\":0,\"method\":\"\",\"methodGroup\":\"\",\"methodReportData\":{\"traceid\":\"\",\"method\":\"\",\"methodGroup\":\"\"}}}</script>";
        Map<String, Object> map = controller.dealToMap(result1, "test");
        BeanUtils.copyProperties(map, jobsCollection);
        System.out.println(jobsCollection);
    }
}
