SET @parentId = 1991055305094832128;
-- 考生试卷管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生试卷管理', 1000, 2, '/exam/candidateExamPaper', 'CandidateExamPaper', 'exam/candidateExamPaper/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生试卷管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1991055305094832129, '列表', @parentId, 3, 'exam:candidateExamPaper:list', 1, 1, 1, NOW()),
    (1991055305094832130, '详情', @parentId, 3, 'exam:candidateExamPaper:detail', 2, 1, 1, NOW()),
    (1991055305094832131, '新增', @parentId, 3, 'exam:candidateExamPaper:add', 3, 1, 1, NOW()),
    (1991055305094832132, '修改', @parentId, 3, 'exam:candidateExamPaper:update', 4, 1, 1, NOW()),
    (1991055305099026432, '删除', @parentId, 3, 'exam:candidateExamPaper:delete', 5, 1, 1, NOW()),
    (1991055305099026433, '导出', @parentId, 3, 'exam:candidateExamPaper:export', 6, 1, 1, NOW());

