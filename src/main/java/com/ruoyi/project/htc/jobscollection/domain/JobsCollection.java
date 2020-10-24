package com.ruoyi.project.htc.jobscollection.domain;

import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 工作记录对象 htc_jobs_collection
 * 
 * @author htc
 * @date 2020-05-22
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JobsCollection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyname;

    /** 公司地址 */
    @Excel(name = "公司地址")
    private String companyaddr;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 投递方式 */
    @Excel(name = "投递方式")
    private String sendmode;

    /** 投递日期 */
    @Excel(name = "投递日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date senddate;

    /** 创建时间 */
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新时间 */
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 删除标记 */
    private String delFlag;

    /** url */
    @Excel(name = "url")
    private String url;

    /** 电话 */
    @Excel(name = "电话")
    private String homeCall;

    /** 手机 */
    @Excel(name = "手机")
    private String phone;

    /** 联系人 */
    @Excel(name = "联系人")
    private String linkman;

    /** 走路距离 */
    @Excel(name = "走路距离")
    private String walklength;

    /** 路程 */
    @Excel(name = "路程")
    private String loadlength;

    /** 起始站点 */
    @Excel(name = "起始站点")
    private String startpoint;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** jobNumber */
    private String jobNumber;

    /** star */
    private Integer star;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("companyname", getCompanyname())
            .append("companyaddr", getCompanyaddr())
            .append("email", getEmail())
            .append("sendmode", getSendmode())
            .append("senddate", getSenddate())
            .append("createBy", getCreateBy())
            .append("createDate", getCreateDate())
            .append("updateBy", getUpdateBy())
            .append("updateDate", getUpdateDate())
            .append("delFlag", getDelFlag())
            .append("url", getUrl())
            .append("homeCall", getHomeCall())
            .append("phone", getPhone())
            .append("linkman", getLinkman())
            .append("walklength", getWalklength())
            .append("loadlength", getLoadlength())
            .append("startpoint", getStartpoint())
            .append("remarks", getRemarks())
            .append("jobNumber", getJobNumber())
            .append("star", getStar())
            .toString();
    }
}
