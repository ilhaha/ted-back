SET @parentId = 1899724349277900800;
-- 资料核心存储管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '资料核心存储管理', 1000, 2, '/document/document', 'Document', 'document/document/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 资料核心存储管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1899724349277900801, '列表', @parentId, 3, 'document:document:list', 1, 1, 1, NOW()),
    (1899724349277900802, '详情', @parentId, 3, 'document:document:detail', 2, 1, 1, NOW()),
    (1899724349277900803, '新增', @parentId, 3, 'document:document:add', 3, 1, 1, NOW()),
    (1899724349277900804, '修改', @parentId, 3, 'document:document:update', 4, 1, 1, NOW()),
    (1899724349277900805, '删除', @parentId, 3, 'document:document:delete', 5, 1, 1, NOW()),
    (1899724349277900806, '导出', @parentId, 3, 'document:document:export', 6, 1, 1, NOW());

