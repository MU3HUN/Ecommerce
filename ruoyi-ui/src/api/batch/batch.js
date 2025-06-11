import request from '@/utils/request'


// 查询批次统计信息
export function getBatchStats() {
  return request({
    url: '/batch/batch/stats',
    method: 'get'
  })
}

// 查询二维码管理列表
export function listBatch(query) {
  return request({
    url: '/batch/batch/list',
    method: 'get',
    params: query
  })
}

// 查询二维码管理详细
export function getBatch(batchId) {
  return request({
    url: '/batch/batch/' + batchId,
    method: 'get'
  })
}

// 新增二维码管理
export function addBatch(data) {
  return request({
    url: '/batch/batch',
    method: 'post',
    data: data
  })
}

// 修改二维码管理
export function updateBatch(data) {
  return request({
    url: '/batch/batch',
    method: 'put',
    data: data
  })
}

// 删除二维码管理
export function delBatch(batchIds) {
  return request({
    url: '/batch/batch/',
    method: 'delete',
    data: { batchIds } 
  })
}


