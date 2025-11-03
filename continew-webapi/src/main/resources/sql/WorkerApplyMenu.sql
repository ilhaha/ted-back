SET @parentId = 1984071609468960768;
-- 作业人员报名管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '作业人员报名管理', 1000, 2, '/worker/workerApply', 'WorkerApply', 'worker/workerApply/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 作业人员报名管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1984071609468960769, '列表', @parentId, 3, 'worker:workerApply:list', 1, 1, 1, NOW()),
    (1984071609468960770, '详情', @parentId, 3, 'worker:workerApply:detail', 2, 1, 1, NOW()),
    (1984071609468960771, '新增', @parentId, 3, 'worker:workerApply:add', 3, 1, 1, NOW()),
    (1984071609468960772, '修改', @parentId, 3, 'worker:workerApply:update', 4, 1, 1, NOW()),
    (1984071609468960773, '删除', @parentId, 3, 'worker:workerApply:delete', 5, 1, 1, NOW()),
    (1984071609468960774, '导出', @parentId, 3, 'worker:workerApply:export', 6, 1, 1, NOW());

