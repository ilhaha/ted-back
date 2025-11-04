package top.continew.admin.worker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.admin.document.model.entity.DocumentPreDO;
import top.continew.admin.document.model.resp.DocumentPreDetailResp;
import top.continew.admin.document.model.resp.DocumentPreResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.worker.mapper.WorkerApplyDocumentMapper;
import top.continew.admin.worker.model.entity.WorkerApplyDocumentDO;
import top.continew.admin.worker.model.query.WorkerApplyDocumentQuery;
import top.continew.admin.worker.model.req.WorkerApplyDocumentReq;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentDetailResp;
import top.continew.admin.worker.model.resp.WorkerApplyDocumentResp;
import top.continew.admin.worker.service.WorkerApplyDocumentService;

/**
 * 作业人员报名上传的资料业务实现
 *
 * @author ilhaha
 * @since 2025/10/31 09:35
 */
@Service
@RequiredArgsConstructor
public class WorkerApplyDocumentServiceImpl extends BaseServiceImpl<WorkerApplyDocumentMapper, WorkerApplyDocumentDO, WorkerApplyDocumentResp, WorkerApplyDocumentDetailResp, WorkerApplyDocumentQuery, WorkerApplyDocumentReq> implements WorkerApplyDocumentService {

    /**
     * 重写page
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<WorkerApplyDocumentResp> page(WorkerApplyDocumentQuery query, PageQuery pageQuery) {
        QueryWrapper<WorkerApplyDocumentDO> queryWrapper = this.buildQueryWrapper(query);
        queryWrapper.eq("twad.is_deleted", 0);
        IPage<WorkerApplyDocumentDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        PageResp<WorkerApplyDocumentResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }
}