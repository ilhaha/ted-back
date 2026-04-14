SET @parentId = 2043952496649789440;
-- 无损检测、检验人员考试通知管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '无损检测、检验人员考试通知管理', 1000, 2, '/exam/examNotice', 'ExamNotice', 'exam/examNotice/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 无损检测、检验人员考试通知管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2043952496649789441, '列表', @parentId, 3, 'exam:examNotice:list', 1, 1, 1, NOW()),
    (2043952496649789442, '详情', @parentId, 3, 'exam:examNotice:detail', 2, 1, 1, NOW()),
    (2043952496649789443, '新增', @parentId, 3, 'exam:examNotice:add', 3, 1, 1, NOW()),
    (2043952496653983744, '修改', @parentId, 3, 'exam:examNotice:update', 4, 1, 1, NOW()),
    (2043952496653983745, '删除', @parentId, 3, 'exam:examNotice:delete', 5, 1, 1, NOW()),
    (2043952496653983746, '导出', @parentId, 3, 'exam:examNotice:export', 6, 1, 1, NOW());

