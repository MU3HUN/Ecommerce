import request from '@/utils/request'
import qs from 'qs'

// 查询贴码核销列表
export function listCode(query) {
  return request({
    url: '/code/code/list',
    method: 'get',
    params: query
  })
}

// 查询贴码核销详细
export function getCode(qrId) {
  return request({
    url: '/code/code/' + qrId,
    method: 'get'
  })
}

// 新增贴码核销
export function addCode(data) {
  return request({
    url: '/code/code',
    method: 'post',
    data: data
  })
}

// 修改贴码核销
export function updateCode(data) {
  return request({
    url: '/code/code',
    method: 'put',
    data: data
  })
}

// 删除贴码核销
export function delCode(qrIds) {
  return request({
    url: '/code/code',
    method: 'delete',
    data: { qrIds } 
  });
}

// 添加统计接口
export function getCodeStats() {
  return request({
    url: '/code/code/stats',
    method: 'get'
  })
}

export function getQRStatus(qrNumber) {
  return request({
    url: '/code/code/status',
    method: 'get',
    params: { qrNumber }
  })
}