import request from '@/utils/request'

// 查询开课情况列表
export function listCourseclass(query) {
  return request({
    url: '/courseclass/courseclass/list',
    method: 'get',
    params: query
  })
}

// 查询开课情况详细
export function getCourseclass(classId) {
  return request({
    url: '/courseclass/courseclass/' + classId,
    method: 'get'
  })
}

// 新增开课情况
export function addCourseclass(data) {
  return request({
    url: '/courseclass/courseclass',
    method: 'post',
    data: data
  })
}

// 修改开课情况
export function updateCourseclass(data) {
  return request({
    url: '/courseclass/courseclass',
    method: 'put',
    data: data
  })
}

// 删除开课情况
export function delCourseclass(classId) {
  return request({
    url: '/courseclass/courseclass/' + classId,
    method: 'delete'
  })
}
