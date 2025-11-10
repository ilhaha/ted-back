SET @parentId = 1987685496512659456;
-- 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）管理', 1000, 2, '/training/orgTrainingPrice', 'OrgTrainingPrice', 'training/orgTrainingPrice/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 机构培训价格（仅核心字段：主键、八大类ID、机构ID、价格）管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1987685496512659457, '列表', @parentId, 3, 'training:orgTrainingPrice:list', 1, 1, 1, NOW()),
    (1987685496512659458, '详情', @parentId, 3, 'training:orgTrainingPrice:detail', 2, 1, 1, NOW()),
    (1987685496512659459, '新增', @parentId, 3, 'training:orgTrainingPrice:add', 3, 1, 1, NOW()),
    (1987685496512659460, '修改', @parentId, 3, 'training:orgTrainingPrice:update', 4, 1, 1, NOW()),
    (1987685496512659461, '删除', @parentId, 3, 'training:orgTrainingPrice:delete', 5, 1, 1, NOW()),
    (1987685496512659462, '导出', @parentId, 3, 'training:orgTrainingPrice:export', 6, 1, 1, NOW());

