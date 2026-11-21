import request from '@/utils/request'

// 查询院系设置列表
export function listDepartment(query) {
  return request({
    url: '/department/department/list',
    method: 'get',
    params: query
  })
}

// 查询院系设置详细
export function getDepartment(deptId) {
  return request({
    url: '/department/department/' + deptId,
    method: 'get'
  })
}

// 新增院系设置
export function addDepartment(data) {
  return request({
    url: '/department/department',
    method: 'post',
    data: data
  })
}

// 修改院系设置 (增加静默标识，防止触发器报错弹出一长串)
export function updateDepartment(data) {
  return request({
    url: '/department/department',
    method: 'put',
    data: data,
    headers: { 'silent': 'true' } 
  })
}

// 删除院系设置 (增加静默标识)
export function delDepartment(deptId) {
  return request({
    url: '/department/department/' + deptId,
    method: 'delete',
    headers: { 'silent': 'true' }
  })
}