SET @parentId = 1899724349965766656;
-- 资料类型主管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '资料类型主管理', 1000, 2, '/document/documentType', 'DocumentType', 'document/documentType/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 资料类型主管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1899724349965766657, '列表', @parentId, 3, 'document:documentType:list', 1, 1, 1, NOW()),
    (1899724349965766658, '详情', @parentId, 3, 'document:documentType:detail', 2, 1, 1, NOW()),
    (1899724349965766659, '新增', @parentId, 3, 'document:documentType:add', 3, 1, 1, NOW()),
    (1899724349965766660, '修改', @parentId, 3, 'document:documentType:update', 4, 1, 1, NOW()),
    (1899724349965766661, '删除', @parentId, 3, 'document:documentType:delete', 5, 1, 1, NOW()),
    (1899724349965766662, '导出', @parentId, 3, 'document:documentType:export', 6, 1, 1, NOW());

