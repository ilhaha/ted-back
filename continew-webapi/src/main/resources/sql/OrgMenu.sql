SET @parentId = 1909077037618130944;
-- 机构信息管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '机构信息管理', 1000, 2, '/training/org', 'Org', 'training/org/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 机构信息管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909077037618130945, '列表', @parentId, 3, 'training:org:list', 1, 1, 1, NOW()),
    (1909077037618130946, '详情', @parentId, 3, 'training:org:detail', 2, 1, 1, NOW()),
    (1909077037618130947, '新增', @parentId, 3, 'training:org:add', 3, 1, 1, NOW()),
    (1909077037618130948, '修改', @parentId, 3, 'training:org:update', 4, 1, 1, NOW()),
    (1909077037618130949, '删除', @parentId, 3, 'training:org:delete', 5, 1, 1, NOW()),
    (1909077037618130950, '导出', @parentId, 3, 'training:org:export', 6, 1, 1, NOW());

