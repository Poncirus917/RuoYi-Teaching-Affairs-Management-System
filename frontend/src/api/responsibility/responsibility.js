import request from '@/utils/request'

// 查询教师任课情况列表
export function listResponsibility(query) {
  return request({
    url: '/responsibility/responsibility/list',
    method: 'get',
    params: query
  })
}

// 查询教师任课情况详细 - 适配联合主键
// 现在需要传入两个参数：教师工号和课程号
export function getResponsibility(staffId, courseId) {
  return request({
    url: '/responsibility/responsibility/' + staffId + '/' + courseId,
    method: 'get'
  })
}

// 新增教师任课情况
export function addResponsibility(data) {
  return request({
    url: '/responsibility/responsibility',
    method: 'post',
    data: data
  })
}

// 修改教师任课情况
export function updateResponsibility(data) {
  return request({
    url: '/responsibility/responsibility',
    method: 'put',
    data: data
  })
}

// 删除教师任课情况 - 适配联合主键
// 确保删除的是某位老师的某门特定课程
export function delResponsibility(staffId, courseId) {
  return request({
    url: '/responsibility/responsibility/' + staffId + '/' + courseId,
    method: 'delete'
  })
}