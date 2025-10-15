SET @parentId = 1899358224291639296;
-- 项目管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '项目管理', 1000, 2, '/exam/project', 'Project', 'exam/project/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 项目管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1899358224291639297, '列表', @parentId, 3, 'exam:project:list', 1, 1, 1, NOW()),
    (1899358224291639298, '详情', @parentId, 3, 'exam:project:detail', 2, 1, 1, NOW()),
    (1899358224291639299, '新增', @parentId, 3, 'exam:project:add', 3, 1, 1, NOW()),
    (1899358224291639300, '修改', @parentId, 3, 'exam:project:update', 4, 1, 1, NOW()),
    (1899358224291639301, '删除', @parentId, 3, 'exam:project:delete', 5, 1, 1, NOW()),
    (1899358224291639302, '导出', @parentId, 3, 'exam:project:export', 6, 1, 1, NOW());

