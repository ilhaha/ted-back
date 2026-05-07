SET @parentId = 2052325625868652544;
-- 考生资料关系管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生资料关系管理', 1000, 2, '/exam/examineeNoticeApply', 'ExamineeNoticeApply', 'exam/examineeNoticeApply/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生资料关系管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2052325625868652545, '列表', @parentId, 3, 'exam:examineeNoticeApply:list', 1, 1, 1, NOW()),
    (2052325625868652546, '详情', @parentId, 3, 'exam:examineeNoticeApply:detail', 2, 1, 1, NOW()),
    (2052325625868652547, '新增', @parentId, 3, 'exam:examineeNoticeApply:add', 3, 1, 1, NOW()),
    (2052325625868652548, '修改', @parentId, 3, 'exam:examineeNoticeApply:update', 4, 1, 1, NOW()),
    (2052325625868652549, '删除', @parentId, 3, 'exam:examineeNoticeApply:delete', 5, 1, 1, NOW()),
    (2052325625868652550, '导出', @parentId, 3, 'exam:examineeNoticeApply:export', 6, 1, 1, NOW());

