SET @parentId = 1917018007336738816;
-- 复审管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '复审管理', 1000, 2, '/certificate/reexamine', 'Reexamine', 'certificate/reexamine/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 复审管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1917018007336738817, '列表', @parentId, 3, 'certificate:reexamine:list', 1, 1, 1, NOW()),
    (1917018007336738818, '详情', @parentId, 3, 'certificate:reexamine:detail', 2, 1, 1, NOW()),
    (1917018007336738819, '新增', @parentId, 3, 'certificate:reexamine:add', 3, 1, 1, NOW()),
    (1917018007336738820, '修改', @parentId, 3, 'certificate:reexamine:update', 4, 1, 1, NOW()),
    (1917018007336738821, '删除', @parentId, 3, 'certificate:reexamine:delete', 5, 1, 1, NOW()),
    (1917018007336738822, '导出', @parentId, 3, 'certificate:reexamine:export', 6, 1, 1, NOW());

