import request from '@/utils/request'

// 查询教师信息列表
export function listTeacher(query) {
  return request({
    url: '/teacher/teacher/list',
    method: 'get',
    params: query
  })
}

// 查询教师信息详细
export function getTeacher(staffId) {
  return request({
    url: '/teacher/teacher/' + staffId,
    method: 'get'
  })
}

// 新增教师信息
export function addTeacher(data) {
  return request({
    url: '/teacher/teacher',
    method: 'post',
    data: data
  })
}

// 修改教师信息
export function updateTeacher(data) {
  return request({
    url: '/teacher/teacher',
    method: 'put',
    data: data,
    headers: { 'silent': 'true' } // 增加静默标识
  })
}

// 删除教师信息
export function delTeacher(staffId) {
  return request({
    url: '/teacher/teacher/' + staffId,
    method: 'delete',
    headers: { 'silent': 'true' } // 增加静默标识
  })
}

// 查询教师任课班级的成绩分布统计数据
export function getTeacherClassStats() {
  return request({
    url: '/teacher/teacher/classStats',
    method: 'get'
  })
}
