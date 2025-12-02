SET @parentId = 1995778915776688128;
-- 监考员资质证明管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '监考员资质证明管理', 1000, 2, '/invigilate/userQualification', 'UserQualification', 'invigilate/userQualification/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 监考员资质证明管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1995778915776688129, '列表', @parentId, 3, 'invigilate:userQualification:list', 1, 1, 1, NOW()),
    (1995778915776688130, '详情', @parentId, 3, 'invigilate:userQualification:detail', 2, 1, 1, NOW()),
    (1995778915776688131, '新增', @parentId, 3, 'invigilate:userQualification:add', 3, 1, 1, NOW()),
    (1995778915776688132, '修改', @parentId, 3, 'invigilate:userQualification:update', 4, 1, 1, NOW()),
    (1995778915776688133, '删除', @parentId, 3, 'invigilate:userQualification:delete', 5, 1, 1, NOW()),
    (1995778915776688134, '导出', @parentId, 3, 'invigilate:userQualification:export', 6, 1, 1, NOW());

