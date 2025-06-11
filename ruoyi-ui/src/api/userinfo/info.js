import request from '@/utils/request'

// 查询人员管理列表
export function listInfo(query) {
  return request({
    url: '/userinfo/info/list',
    method: 'get',
    params: query
  })
}

// 查询人员管理详细
export function getInfo(userId) {
  return request({
    url: '/userinfo/info/' + userId,
    method: 'get'
  })
}

// 新增人员管理
export function addInfo(data) {
  return request({
    url: '/userinfo/info',
    method: 'post',
    data: data
  })
}

// 修改人员管理
export function updateInfo(data) {
  return request({
    url: '/userinfo/info',
    method: 'put',
    data: data
  })
}

// 删除人员管理
export function delInfo(userId) {
  return request({
    url: '/userinfo/info/' + userId,
    method: 'delete'
  })
}
