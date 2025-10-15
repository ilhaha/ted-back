SET @parentId = 1914943295878828032;
-- 个人档案管理管理菜单
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '个人档案管理管理', 1000, 2, '/generator/personFile', 'PersonFile', 'generator/personFile/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 个人档案管理管理按钮
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1914943295878828033, '列表', @parentId, 3, 'generator:personFile:list', 1, 1, 1, NOW()),
    (1914943295878828034, '详情', @parentId, 3, 'generator:personFile:detail', 2, 1, 1, NOW()),
    (1914943295878828035, '新增', @parentId, 3, 'generator:personFile:add', 3, 1, 1, NOW()),
    (1914943295878828036, '修改', @parentId, 3, 'generator:personFile:update', 4, 1, 1, NOW()),
    (1914943295878828037, '删除', @parentId, 3, 'generator:personFile:delete', 5, 1, 1, NOW()),
    (1914943295878828038, '导出', @parentId, 3, 'generator:personFile:export', 6, 1, 1, NOW());

