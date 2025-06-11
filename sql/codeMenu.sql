-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('贴码核销', '2018', '1', 'code', 'code/code/index', 1, 0, 'C', '0', '0', 'code:code:list', '#', 'admin', sysdate(), '', null, '贴码核销菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('贴码核销查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'code:code:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('贴码核销新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'code:code:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('贴码核销修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'code:code:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('贴码核销删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'code:code:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('贴码核销导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'code:code:export',       '#', 'admin', sysdate(), '', null, '');