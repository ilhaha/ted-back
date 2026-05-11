package top.continew.admin.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import top.continew.admin.common.util.AESWithHMAC;
import top.continew.admin.exam.mapper.ExamNoticePlanMapper;
import top.continew.admin.exam.model.resp.NoticeExamProjectResp;
import top.continew.admin.exam.model.resp.ProjectExamResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.exam.mapper.ExamineeNoticeApplyMapper;
import top.continew.admin.exam.model.entity.ExamineeNoticeApplyDO;
import top.continew.admin.exam.model.query.ExamineeNoticeApplyQuery;
import top.continew.admin.exam.model.req.ExamineeNoticeApplyReq;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyDetailResp;
import top.continew.admin.exam.model.resp.ExamineeNoticeApplyResp;
import top.continew.admin.exam.service.ExamineeNoticeApplyService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 考生资料关系业务实现
 *
 * @author ilhaha
 * @since 2026/05/07 17:52
 */
@Service
@RequiredArgsConstructor
public class ExamineeNoticeApplyServiceImpl extends BaseServiceImpl<ExamineeNoticeApplyMapper, ExamineeNoticeApplyDO, ExamineeNoticeApplyResp, ExamineeNoticeApplyDetailResp, ExamineeNoticeApplyQuery, ExamineeNoticeApplyReq> implements ExamineeNoticeApplyService {

    private final ExamNoticePlanMapper examNoticePlanMapper;

    private final AESWithHMAC aesWithHMAC;

    /**
     * 获取通知对应的考生报名列表
     * @param query
     * @param pageQuery
     * @return
     */
    @Override
    public PageResp<ExamineeNoticeApplyResp> getNoticeApplyCandidatePage(ExamineeNoticeApplyQuery query, PageQuery pageQuery) {
        QueryWrapper<ExamineeNoticeApplyDO> queryWrapper = this.buildQueryWrapper(query);
        String username = query.getUsername();
        queryWrapper.eq("tena.is_deleted", 0)
                .eq(ObjectUtil.isNotNull(username),"su.username",aesWithHMAC.encryptAndSign(username));
        super.sort(queryWrapper, pageQuery);

        IPage<ExamineeNoticeApplyDetailResp> page = baseMapper.page(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), queryWrapper);

        List<ExamineeNoticeApplyDetailResp> records = page.getRecords();

        if (ObjectUtil.isNotEmpty(records)) {

            // 通知下所有项目
            List<NoticeExamProjectResp> noticeExamProjectRespList =
                    examNoticePlanMapper.selectNoticeExamProject(query.getNoticeId());

            records.forEach(item -> {

                // JSON转对象
                String json = item.getProjectExamListJson();

                List<ProjectExamResp> projectExamList;

                if (StrUtil.isNotBlank(json)) {

                    projectExamList = JSON.parseArray(
                            json,
                            ProjectExamResp.class
                    );

                } else {

                    projectExamList = Collections.emptyList();
                }

                item.setProjectExamList(projectExamList);

                // 当前考生已报考项目
                Map<Long, ProjectExamResp> projectMap =
                        projectExamList.stream()
                                .collect(Collectors.toMap(
                                        ProjectExamResp::getProjectId,
                                        Function.identity(),
                                        (a, b) -> a
                                ));


                // 组装返回给前端的项目列表
                List<NoticeExamProjectResp> noticeProjectList =
                        noticeExamProjectRespList.stream()
                                .map(project -> {

                                    NoticeExamProjectResp resp =
                                            new NoticeExamProjectResp();

                                    BeanUtils.copyProperties(project, resp);

                                    // 当前项目是否报考
                                    ProjectExamResp applyProject =
                                            projectMap.get(project.getProjectId());

                                    if (applyProject != null) {

                                        resp.setIsApply(Boolean.TRUE);

                                        resp.setExamAttemptType(
                                                applyProject.getExamAttemptType()
                                        );

                                    }
                                    return resp;
                                })
                                .toList();

                item.setNoticeExamProjectList(noticeProjectList);
                item.setUsername(aesWithHMAC.verifyAndDecrypt(item.getUsername()));
            });
        }

        PageResp<ExamineeNoticeApplyResp> build = PageResp.build(page, super.getListClass());
        build.getList().forEach(this::fill);
        return build;
    }
}