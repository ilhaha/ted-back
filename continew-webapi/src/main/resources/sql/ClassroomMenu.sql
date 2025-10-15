SET @parentId = 1922571130012815360;
-- 考场管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考场管理', 1000, 2, '/exam/classroom', 'Classroom', 'exam/classroom/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考场管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1922571130012815361, '列表', @parentId, 3, 'exam:classroom:list', 1, 1, 1, NOW()),
    (1922571130012815362, '详情', @parentId, 3, 'exam:classroom:detail', 2, 1, 1, NOW()),
    (1922571130012815363, '新增', @parentId, 3, 'exam:classroom:add', 3, 1, 1, NOW()),
    (1922571130012815364, '修改', @parentId, 3, 'exam:classroom:update', 4, 1, 1, NOW()),
    (1922571130012815365, '删除', @parentId, 3, 'exam:classroom:delete', 5, 1, 1, NOW()),
    (1922571130012815366, '导出', @parentId, 3, 'exam:classroom:export', 6, 1, 1, NOW());

