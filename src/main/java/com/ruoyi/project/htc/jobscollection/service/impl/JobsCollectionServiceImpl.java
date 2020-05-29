package com.ruoyi.project.htc.jobscollection.service.impl;

import com.ruoyi.common.utils.text.Convert;
import com.ruoyi.project.htc.jobscollection.domain.JobsCollection;
import com.ruoyi.project.htc.jobscollection.mapper.JobsCollectionMapper;
import com.ruoyi.project.htc.jobscollection.service.IJobsCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作记录Service业务层处理
 * 
 * @author htc
 * @date 2020-05-22
 */
@Service
public class JobsCollectionServiceImpl implements IJobsCollectionService 
{
    @Autowired
    private JobsCollectionMapper jobsCollectionMapper;

    /**
     * 查询工作记录
     * 
     * @param id 工作记录ID
     * @return 工作记录
     */
    @Override
    public JobsCollection selectJobsCollectionById(Long id)
    {
        return jobsCollectionMapper.selectJobsCollectionById(id);
    }

    /**
     * 查询工作记录列表
     * 
     * @param jobsCollection 工作记录
     * @return 工作记录
     */
    @Override
    public List<JobsCollection> selectJobsCollectionList(JobsCollection jobsCollection)
    {
        return jobsCollectionMapper.selectJobsCollectionList(jobsCollection);
    }

    /**
     * 新增工作记录
     * 
     * @param jobsCollection 工作记录
     * @return 结果
     */
    @Override
    public int insertJobsCollection(JobsCollection jobsCollection)
    {
        return jobsCollectionMapper.insertJobsCollection(jobsCollection);
    }

    /**
     * 修改工作记录
     * 
     * @param jobsCollection 工作记录
     * @return 结果
     */
    @Override
    public int updateJobsCollection(JobsCollection jobsCollection)
    {
        return jobsCollectionMapper.updateJobsCollection(jobsCollection);
    }

    /**
     * 删除工作记录对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteJobsCollectionByIds(String ids)
    {
        return jobsCollectionMapper.deleteJobsCollectionByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除工作记录信息
     * 
     * @param id 工作记录ID
     * @return 结果
     */
    @Override
    public int deleteJobsCollectionById(Long id)
    {
        return jobsCollectionMapper.deleteJobsCollectionById(id);
    }

    @Override
    public int selectCount(JobsCollection jobsCollection) {
        return jobsCollectionMapper.selectCount(jobsCollection);
    }
}
