SET @parentId = 2042136558472347648;
-- 考试异步任务错误日志管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试异步任务错误日志管理', 1000, 2, '/exam/examAsyncErrorLog', 'ExamAsyncErrorLog', 'exam/examAsyncErrorLog/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试异步任务错误日志管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2042136558472347649, '列表', @parentId, 3, 'exam:examAsyncErrorLog:list', 1, 1, 1, NOW()),
    (2042136558472347650, '详情', @parentId, 3, 'exam:examAsyncErrorLog:detail', 2, 1, 1, NOW()),
    (2042136558472347651, '新增', @parentId, 3, 'exam:examAsyncErrorLog:add', 3, 1, 1, NOW()),
    (2042136558472347652, '修改', @parentId, 3, 'exam:examAsyncErrorLog:update', 4, 1, 1, NOW()),
    (2042136558472347653, '删除', @parentId, 3, 'exam:examAsyncErrorLog:delete', 5, 1, 1, NOW()),
    (2042136558472347654, '导出', @parentId, 3, 'exam:examAsyncErrorLog:export', 6, 1, 1, NOW());

