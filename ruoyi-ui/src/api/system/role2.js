import request from '@/utils/request'

// 查询角色信息列表
export function listRole(query) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params: query
  })
}

// 查询角色信息详细
export function getRole(roleId) {
  return request({
    url: '/system/role/' + roleId,
    method: 'get'
  })
}

// 新增角色信息
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data: data
  })
}

// 修改角色信息
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data: data
  })
}

// 删除角色信息
export function delRole(roleId) {
  return request({
    url: '/system/role/' + roleId,
    method: 'delete'
  })
}
