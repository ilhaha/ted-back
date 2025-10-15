SET @parentId = 1899358224115478528;
-- 项目地点关联管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '项目地点关联管理', 1000, 2, '/exam/projLocAssoc', 'ProjLocAssoc', 'exam/projLocAssoc/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 项目地点关联管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1899358224115478529, '列表', @parentId, 3, 'exam:projLocAssoc:list', 1, 1, 1, NOW()),
    (1899358224115478530, '详情', @parentId, 3, 'exam:projLocAssoc:detail', 2, 1, 1, NOW()),
    (1899358224115478531, '新增', @parentId, 3, 'exam:projLocAssoc:add', 3, 1, 1, NOW()),
    (1899358224115478532, '修改', @parentId, 3, 'exam:projLocAssoc:update', 4, 1, 1, NOW()),
    (1899358224115478533, '删除', @parentId, 3, 'exam:projLocAssoc:delete', 5, 1, 1, NOW()),
    (1899358224115478534, '导出', @parentId, 3, 'exam:projLocAssoc:export', 6, 1, 1, NOW());

