SET @parentId = 2043952779580760064;
-- 考试通知与考试计划关联管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试通知与考试计划关联管理', 1000, 2, '/exam/examNoticePlan', 'ExamNoticePlan', 'exam/examNoticePlan/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试通知与考试计划关联管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2043952779580760065, '列表', @parentId, 3, 'exam:examNoticePlan:list', 1, 1, 1, NOW()),
    (2043952779580760066, '详情', @parentId, 3, 'exam:examNoticePlan:detail', 2, 1, 1, NOW()),
    (2043952779580760067, '新增', @parentId, 3, 'exam:examNoticePlan:add', 3, 1, 1, NOW()),
    (2043952779580760068, '修改', @parentId, 3, 'exam:examNoticePlan:update', 4, 1, 1, NOW()),
    (2043952779580760069, '删除', @parentId, 3, 'exam:examNoticePlan:delete', 5, 1, 1, NOW()),
    (2043952779580760070, '导出', @parentId, 3, 'exam:examNoticePlan:export', 6, 1, 1, NOW());

