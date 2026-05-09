SET @parentId = 2052651415764287488;
-- 持证信息管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '持证信息管理', 1000, 2, '/exam/licenseHolderInfo', 'LicenseHolderInfo', 'exam/licenseHolderInfo/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 持证信息管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2052651415764287489, '列表', @parentId, 3, 'exam:licenseHolderInfo:list', 1, 1, 1, NOW()),
    (2052651415764287490, '详情', @parentId, 3, 'exam:licenseHolderInfo:detail', 2, 1, 1, NOW()),
    (2052651415764287491, '新增', @parentId, 3, 'exam:licenseHolderInfo:add', 3, 1, 1, NOW()),
    (2052651415764287492, '修改', @parentId, 3, 'exam:licenseHolderInfo:update', 4, 1, 1, NOW()),
    (2052651415764287493, '删除', @parentId, 3, 'exam:licenseHolderInfo:delete', 5, 1, 1, NOW()),
    (2052651415764287494, '导出', @parentId, 3, 'exam:licenseHolderInfo:export', 6, 1, 1, NOW());

