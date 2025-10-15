SET @parentId = 1909074141396692992;
-- 考生答题，记录考生答题情况管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生答题，记录考生答题情况管理', 1000, 2, '/examconnect/examAnswer', 'ExamAnswer', 'examconnect/examAnswer/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生答题，记录考生答题情况管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909074141396692993, '列表', @parentId, 3, 'examconnect:examAnswer:list', 1, 1, 1, NOW()),
    (1909074141396692994, '详情', @parentId, 3, 'examconnect:examAnswer:detail', 2, 1, 1, NOW()),
    (1909074141396692995, '新增', @parentId, 3, 'examconnect:examAnswer:add', 3, 1, 1, NOW()),
    (1909074141396692996, '修改', @parentId, 3, 'examconnect:examAnswer:update', 4, 1, 1, NOW()),
    (1909074141396692997, '删除', @parentId, 3, 'examconnect:examAnswer:delete', 5, 1, 1, NOW()),
    (1909074141396692998, '导出', @parentId, 3, 'examconnect:examAnswer:export', 6, 1, 1, NOW());

