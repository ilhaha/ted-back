package top.continew.admin.training.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.model.entity.ExamPlanDO;
import top.continew.admin.exam.model.resp.ExamPlanDetailResp;
import top.continew.admin.exam.model.resp.ExamPlanResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.training.mapper.CandidateTypeMapper;
import top.continew.admin.training.model.entity.CandidateTypeDO;
import top.continew.admin.training.model.query.CandidateTypeQuery;
import top.continew.admin.training.model.req.CandidateTypeReq;
import top.continew.admin.training.model.resp.CandidateTypeDetailResp;
import top.continew.admin.training.model.resp.CandidateTypeResp;
import top.continew.admin.training.service.CandidateTypeService;

import java.util.List;

/**
 * 考生类型业务实现
 *
 * @author ilhaha
 * @since 2026/01/14 11:16
 */
@Service
@RequiredArgsConstructor
public class CandidateTypeServiceImpl extends BaseServiceImpl<CandidateTypeMapper, CandidateTypeDO, CandidateTypeResp, CandidateTypeDetailResp, CandidateTypeQuery, CandidateTypeReq> implements CandidateTypeService {


    private final AESWithHMAC aesWithHMAC;


    /**
     * 重写page  查询作业人员信息
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<CandidateTypeResp> page(CandidateTypeQuery query, PageQuery pageQuery) {
        QueryWrapper<CandidateTypeDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("tct.is_deleted", 0);
        IPage<CandidateTypeDetailResp> page = baseMapper.getWorkerPage(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);
        List<CandidateTypeDetailResp> records = page.getRecords();
        if (ObjectUtil.isNotEmpty(records)) {
            page.setRecords(records.stream().map(item -> {
                item.setUsername(aesWithHMAC.verifyAndDecrypt(item.getUsername()));
                item.setPhone(aesWithHMAC.verifyAndDecrypt(item.getPhone()));
                return item;
            }).toList());
        }
        PageResp<CandidateTypeResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }
}