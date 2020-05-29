package com.ruoyi.project.htc.jobscollection.service;

import com.ruoyi.project.htc.jobscollection.domain.JobsCollection;

import java.util.List;

/**
 * 工作记录Service接口
 * 
 * @author htc
 * @date 2020-05-22
 */
public interface IJobsCollectionService 
{
    /**
     * 查询工作记录
     * 
     * @param id 工作记录ID
     * @return 工作记录
     */
    public JobsCollection selectJobsCollectionById(Long id);

    /**
     * 查询工作记录列表
     * 
     * @param jobsCollection 工作记录
     * @return 工作记录集合
     */
    public List<JobsCollection> selectJobsCollectionList(JobsCollection jobsCollection);

    /**
     * 新增工作记录
     * 
     * @param jobsCollection 工作记录
     * @return 结果
     */
    public int insertJobsCollection(JobsCollection jobsCollection);

    /**
     * 修改工作记录
     * 
     * @param jobsCollection 工作记录
     * @return 结果
     */
    public int updateJobsCollection(JobsCollection jobsCollection);

    /**
     * 批量删除工作记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteJobsCollectionByIds(String ids);

    /**
     * 删除工作记录信息
     * 
     * @param id 工作记录ID
     * @return 结果
     */
    public int deleteJobsCollectionById(Long id);

    /**
     * 计数
     *
     * @param jobsCollection
     * @return
     */
    int selectCount(JobsCollection jobsCollection);
}
