SET @parentId = 1899358223926734848;
-- 考试计划管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试计划管理', 1000, 2, '/exam/examPlan', 'ExamPlan', 'exam/examPlan/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试计划管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1899358223926734849, '列表', @parentId, 3, 'exam:examPlan:list', 1, 1, 1, NOW()),
    (1899358223926734850, '详情', @parentId, 3, 'exam:examPlan:detail', 2, 1, 1, NOW()),
    (1899358223926734851, '新增', @parentId, 3, 'exam:examPlan:add', 3, 1, 1, NOW()),
    (1899358223926734852, '修改', @parentId, 3, 'exam:examPlan:update', 4, 1, 1, NOW()),
    (1899358223926734853, '删除', @parentId, 3, 'exam:examPlan:delete', 5, 1, 1, NOW()),
    (1899358223926734854, '导出', @parentId, 3, 'exam:examPlan:export', 6, 1, 1, NOW());

