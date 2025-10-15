SET @parentId = 1901452644726063104;
-- 证件种类管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '证件种类管理', 1000, 2, '/certificate/certificateType', 'CertificateType', 'certificate/certificateType/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 证件种类管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1901452644726063105, '列表', @parentId, 3, 'certificate:certificateType:list', 1, 1, 1, NOW()),
    (1901452644726063106, '详情', @parentId, 3, 'certificate:certificateType:detail', 2, 1, 1, NOW()),
    (1901452644726063107, '新增', @parentId, 3, 'certificate:certificateType:add', 3, 1, 1, NOW()),
    (1901452644730257408, '修改', @parentId, 3, 'certificate:certificateType:update', 4, 1, 1, NOW()),
    (1901452644730257409, '删除', @parentId, 3, 'certificate:certificateType:delete', 5, 1, 1, NOW()),
    (1901452644730257410, '导出', @parentId, 3, 'certificate:certificateType:export', 6, 1, 1, NOW());

