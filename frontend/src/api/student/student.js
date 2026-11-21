import request from '@/utils/request'

// 查询学生列表
export function listStudent(query) {
  return request({
    url: '/student/student/list',
    method: 'get',
    params: query
  })
}

// 查询学生详细
export function getStudent(studentId) {
  return request({
    url: '/student/student/' + studentId,
    method: 'get'
  })
}

// 新增学生
export function addStudent(data) {
  return request({
    url: '/student/student',
    method: 'post',
    data: data
  })
}

// 修改学生
export function updateStudent(data) {
  return request({
    url: '/student/student',
    method: 'put',
    data: data
  })
}

// 删除学生
export function delStudent(studentId) {
  return request({
    url: '/student/student/' + studentId,
    method: 'delete',
    headers: { 'silent': 'true' }
  })
}

// 查询当前登录学生的成绩统计（各学期学分、绩点及总计）
export function getMyGpaStats() {
  return request({
    url: '/student/student/myGpaStats',
    method: 'get'
  })
}